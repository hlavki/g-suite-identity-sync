package eu.hlavki.identity.services.sync.impl;

import eu.hlavki.identity.services.config.AppConfiguration;
import eu.hlavki.identity.services.google.GSuiteDirectoryService;
import eu.hlavki.identity.services.google.ResourceNotFoundException;
import eu.hlavki.identity.services.google.model.*;
import eu.hlavki.identity.services.ldap.LdapAccountService;
import eu.hlavki.identity.services.ldap.model.LdapAccount;
import eu.hlavki.identity.services.ldap.model.LdapGroup;
import eu.hlavki.identity.services.sync.AccountSyncService;
import static eu.hlavki.identity.services.sync.impl.AccountUtil.isInternalAccount;
import java.util.*;
import static java.util.Collections.emptySet;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toSet;
import org.apache.cxf.rs.security.oidc.common.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountSyncServiceImpl implements AccountSyncService {

    private static final Logger LOG = LoggerFactory.getLogger(AccountSyncServiceImpl.class);
    private final LdapAccountService ldapService;
    private final GSuiteDirectoryService gsuiteDirService;
    private final AppConfiguration appConfig;


    public AccountSyncServiceImpl(LdapAccountService ldapService, GSuiteDirectoryService gsuiteDirService,
            AppConfiguration appConfig) {
        this.ldapService = ldapService;
        this.gsuiteDirService = gsuiteDirService;
        this.appConfig = appConfig;
    }


    @Override
    public void synchronizeUserGroups(UserInfo userInfo) {
        String accountDN = ldapService.getAccountDN(userInfo.getSubject());
        GroupList gsuiteGroups = gsuiteDirService.getUserGroups(userInfo.getSubject());
        List<LdapGroup> ldapGroups = ldapService.getAccountGroups(accountDN);

        Set<String> asIs = ldapGroups.stream()
                .filter(g -> g.getMembersDn().contains(accountDN))
                .map(g -> g.getDn())
                .collect(Collectors.toSet());

        List<GSuiteGroup> gg = gsuiteGroups.getGroups() != null ? gsuiteGroups.getGroups() : Collections.emptyList();
        Set<String> toBe = gg.stream()
                .map(group -> AccountUtil.getLdapGroupName(group))
                .collect(Collectors.toSet());

        // Workaround for implicit group mapping
        boolean implicitGroup = gsuiteDirService.getImplicitGroup() != null;
        if (isInternalAccount(userInfo, gsuiteDirService.getDomainName()) && implicitGroup) {
            toBe.add(AccountUtil.getLdapGroupName(gsuiteDirService.getImplicitGroup()));
        }

        Set<String> toRemove = new HashSet<>(asIs);
        toRemove.removeAll(toBe);

        Set<String> toAdd = new HashSet<>(toBe);
        toAdd.removeAll(asIs);

        LOG.info("Remove membership for user {} from groups {}", userInfo.getEmail(), toRemove);
        LOG.info("Add membership for user {} to groups {}", userInfo.getEmail(), toAdd);

        for (String group : toRemove) {
            ldapService.deleteGroupMember(accountDN, group);
        }
        for (String group : toAdd) {
            ldapService.addGroupMember(accountDN, group);
        }
    }


    @Override
    public void synchronizeAllGroups() {
        Map<GSuiteGroup, GroupMembership> gsuiteGroups = gsuiteDirService.getAllGroupMembership();
        Set<String> ldapGroups = ldapService.getAllGroupNames();
        GSuiteUsers allGsuiteUsers = gsuiteDirService.getAllUsers();

        Map<String, LdapAccount> emailAccountMap = new HashMap<>();
        for (LdapAccount info : ldapService.getAllAccounts()) {
            info.getEmails().forEach(email -> emailAccountMap.put(email, info));
        }

        Set<String> syncedGroups = new HashSet<>();
        for (Map.Entry<GSuiteGroup, GroupMembership> entry : gsuiteGroups.entrySet()) {
            GSuiteGroup gsuiteGroup = entry.getKey();
            GroupMembership gsuiteMembership = entry.getValue();

            // Workaround for implicit group mapping
            if (gsuiteGroup.getEmail().equals(gsuiteDirService.getImplicitGroup())) {
                List<GroupMember> allMembers = allGsuiteUsers.getUsers().stream().map(u -> u.toMember()).collect(Collectors.toList());
                gsuiteMembership.getMembers().addAll(allMembers);
            }
            LdapGroup syncGroup = synchronizeGroup(gsuiteGroup, gsuiteMembership, emailAccountMap);
            if (syncGroup != null) {
                syncedGroups.add(syncGroup.getName());
            }
        }

        Set<String> toRemove = new HashSet<>(ldapGroups);
        toRemove.removeAll(syncedGroups);
        LOG.info("Removing groups from LDAP {}", toRemove);
        for (String groupName : toRemove) {
            ldapService.deleteGroup(groupName);
        }
    }


    @Override
    public void synchronizeGroup(String groupEmail) throws ResourceNotFoundException {
        GroupMembership gsuiteMembership = gsuiteDirService.getGroupMembers(groupEmail);
        GSuiteGroup gsuiteGroup = gsuiteDirService.getGroup(groupEmail);

        Map<String, LdapAccount> emailAccountMap = new HashMap<>();
        for (LdapAccount info : ldapService.getAllAccounts()) {
            info.getEmails().forEach(email -> emailAccountMap.put(email, info));
        }
        synchronizeGroup(gsuiteGroup, gsuiteMembership, emailAccountMap);
    }


    @Override
    public void removeGroup(String groupEmail) {
        ldapService.deleteGroup(AccountUtil.getLdapGroupName(groupEmail));
    }


    @Override
    public void removeUserByEmail(String email) {
        ldapService.deleteUserByEmail(email);
    }


    private LdapGroup synchronizeGroup(GSuiteGroup gsuiteGroup, GroupMembership gsuiteMembership,
            Map<String, LdapAccount> emailAccountMap) {
        LOG.info("Starting to synchronize group {}", gsuiteGroup.getEmail());
        LdapGroup ldapGroup = new LdapGroup();
        ldapGroup.setName(AccountUtil.getLdapGroupName(gsuiteGroup));
        ldapGroup.setDescription(gsuiteGroup.getName());
        Set<String> members = gsuiteMembership.getMembers().stream().
                filter(m -> emailAccountMap.containsKey(m.getEmail())).
                map(m -> ldapService.getAccountDN(emailAccountMap.get(m.getEmail()))).
                collect(Collectors.toSet());
        LdapGroup result = ldapGroup;
        if (!members.isEmpty()) {
            ldapGroup.setMembersDn(members);
            LOG.info("Synchronizing GSuite group {} as LDAP group {} with {} members",
                    gsuiteGroup.getEmail(), ldapGroup.getName(), ldapGroup.getMembersDn().size());
            result = ldapService.createOrUpdateGroup(ldapGroup);
        } else {
            LOG.info("Removing group {} from LDAP. No active members!", ldapGroup.getName());
            ldapService.deleteGroup(ldapGroup.getName());
        }
        return result;
    }


    @Override
    public void synchronizeGSuiteUsers() {
        List<LdapAccount> accounts = ldapService.getAllAccounts();
        for (LdapAccount account : accounts) {
            GSuiteUser gsuiteUser = gsuiteDirService.getUser(account.getSubject());
            ldapService.updateAccount(AccountUtil.toLdapAccount(gsuiteUser));
            LOG.info("User {} successfully updated.", account.getUsername());
        }
    }


    @Override
    public void cleanExternalUsers() {
        Optional<String> externalGroup = appConfig.getExternalAccountsGroup();
        Set<LdapAccount> toRemove = externalGroup.map(extGroup -> {
            List<LdapAccount> ldapExt = ldapService.searchByRole(LdapAccount.Role.EXTERNAL);
            GroupMembership gsuiteExt = gsuiteDirService.getGroupMembers(extGroup);
            Set<String> gsuiteSubjects = gsuiteExt.getMembers().stream().map(u -> u.getId()).collect(toSet());
            return ldapExt.stream().filter(acc -> !gsuiteSubjects.contains(acc.getSubject())).collect(toSet());
        }).orElse(emptySet());
        LOG.info("Cleaning {} external users", toRemove.size());
        toRemove.forEach(ldapService::deleteUser);
    }
}
