package eu.hlavki.identity.services.sync.impl;

import eu.hlavki.identity.services.google.GSuiteDirectoryService;
import eu.hlavki.identity.services.google.model.GroupMember.Status;
import eu.hlavki.identity.services.ldap.LdapAccountService;
import eu.hlavki.identity.services.ldap.model.LdapAccount;
import eu.hlavki.identity.services.ldap.model.LdapGroup;
import eu.hlavki.identity.services.sync.AccountSyncService;
import static eu.hlavki.identity.services.sync.impl.AccountUtil.isInternalAccount;
import eu.hlavki.identity.services.google.model.*;
import eu.hlavki.identity.services.ldap.LdapSystemException;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.cxf.rs.security.oidc.common.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountSyncServiceImpl implements AccountSyncService {

    private static final Logger log = LoggerFactory.getLogger(AccountSyncServiceImpl.class);
    private final LdapAccountService ldapService;
    private final GSuiteDirectoryService gsuiteDirService;


    public AccountSyncServiceImpl(LdapAccountService ldapService, GSuiteDirectoryService gsuiteDirService) {
        this.ldapService = ldapService;
        this.gsuiteDirService = gsuiteDirService;
    }


    @Override
    public void synchronizeUserGroups(UserInfo userInfo) throws LdapSystemException {
        String accountDN = ldapService.getAccountDN(userInfo.getSubject());
        GroupList gsuiteGroups = gsuiteDirService.getUserGroups(userInfo.getSubject());
        Map<String, LdapGroup> ldapGroups = ldapService.getAccountGroups(accountDN);

        Set<String> asIs = ldapGroups.values().stream()
            .filter(g -> g.getMembersDn().contains(accountDN))
            .map(g -> g.getDn())
            .collect(Collectors.toSet());

        List<GSuiteGroup> gg = gsuiteGroups.getGroups() != null ? gsuiteGroups.getGroups() : Collections.emptyList();
        Set<String> toBe = gg.stream()
            .map(group -> group.getEmail())
            .map(email -> AccountUtil.getLdapGroupName(email))
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

        log.info("Remove membership for user {} from groups {}", userInfo.getEmail(), toRemove);
        log.info("Add membership for user {} to groups {}", userInfo.getEmail(), toAdd);

        for (String group : toRemove) {
            ldapService.removeGroupMember(accountDN, group);
        }
        for (String group : toAdd) {
            ldapService.addGroupMember(accountDN, group);
        }
    }


    @Override
    public void synchronizeAllGroups() throws LdapSystemException {
        Map<GSuiteGroup, GroupMembership> gsuiteGroups = gsuiteDirService.getAllGroupMembership(false);
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
        log.info("Removing groups from LDAP {}", toRemove);
        for (String groupName : toRemove) {
            ldapService.removeGroup(groupName);
        }
    }


    private LdapGroup synchronizeGroup(GSuiteGroup gsuiteGroup, GroupMembership gsuiteMembership,
        Map<String, LdapAccount> emailAccountMap) throws LdapSystemException {
        log.info("Starting to synchronize group {}", gsuiteGroup.getEmail());
        LdapGroup ldapGroup = new LdapGroup();
        ldapGroup.setName(AccountUtil.getLdapGroupName(gsuiteGroup.getEmail()));
        ldapGroup.setDescription(gsuiteGroup.getName());
        Set<String> members = gsuiteMembership.getMembers().stream().
            filter(m -> m.getStatus() == Status.ACTIVE).filter(m -> emailAccountMap.containsKey(m.getEmail())).
            map(m -> ldapService.getAccountDNFromEmail(emailAccountMap.get(m.getEmail()).getUsername())).
            collect(Collectors.toSet());
        LdapGroup result = ldapGroup;
        if (!members.isEmpty()) {
            ldapGroup.setMembersDn(members);
            log.info("Synchronizing GSuite group {} as LDAP group {} with {} members",
                gsuiteGroup.getEmail(), ldapGroup.getName(), ldapGroup.getMembersDn().size());
            result = ldapService.createOrUpdateGroup(ldapGroup);
        } else {
            log.info("Removing group {} from LDAP. No active members!", ldapGroup.getName());
            ldapService.removeGroup(ldapGroup.getName());
        }
        return result;
    }


    @Override
    public void synchronizeGSuiteUsers() throws LdapSystemException {
        GSuiteUsers users = gsuiteDirService.getAllUsers();
        for (GSuiteUser user : users.getUsers()) {
            if (ldapService.accountExists(user.getId())) {
                ldapService.updateAccount(AccountUtil.toLdapAccount(user));
                log.info("User {} successfully updated.", user.getPrimaryEmail());
            } else {
                log.info("User {} does not exists in LDAP.", user.getPrimaryEmail());
            }
        }
    }
}
