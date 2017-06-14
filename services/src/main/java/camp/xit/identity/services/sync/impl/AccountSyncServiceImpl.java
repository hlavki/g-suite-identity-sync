package camp.xit.identity.services.sync.impl;

import camp.xit.identity.services.config.AppConfiguration;
import camp.xit.identity.services.config.Configuration;
import camp.xit.identity.services.google.GSuiteDirectoryService;
import camp.xit.identity.services.google.model.GSuiteGroup;
import camp.xit.identity.services.google.model.GroupList;
import camp.xit.identity.services.google.model.GroupMember.Status;
import camp.xit.identity.services.google.model.GroupMembership;
import camp.xit.identity.services.ldap.LdapAccountService;
import camp.xit.identity.services.ldap.model.LdapGroup;
import camp.xit.identity.services.model.AccountInfo;
import camp.xit.identity.services.sync.AccountSyncService;
import camp.xit.identity.services.util.AccountUtil;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.cxf.rs.security.oidc.common.UserInfo;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountSyncServiceImpl implements AccountSyncService, EventHandler {

    private static final Logger log = LoggerFactory.getLogger(AccountSyncServiceImpl.class);
    private final AppConfiguration config;
    private final LdapAccountService ldapService;
    private final GSuiteDirectoryService gsuiteDirService;


    public AccountSyncServiceImpl(AppConfiguration config, LdapAccountService ldapService,
            GSuiteDirectoryService gsuiteDirService) {
        this.config = config;
        this.ldapService = ldapService;
        this.gsuiteDirService = gsuiteDirService;
    }


    private static void configure() {
        log.info("Configuring AccountSyncService...");
    }


    @Override
    public void synchronizeUserGroups(UserInfo userInfo) throws LDAPException {
        String accountDN = ldapService.getAccountDN(userInfo.getSubject());
        GroupList gsuiteGroups = gsuiteDirService.getGroups(userInfo.getSubject());
        Map<String, LdapGroup> ldapGroups = ldapService.getAccountGroups(accountDN);

        Set<String> asIs = ldapGroups.values().stream()
                .filter(g -> g.getMembersDn().contains(accountDN))
                .map(g -> g.getDn())
                .collect(Collectors.toSet());

        Set<String> toBe = gsuiteGroups.getGroups().stream()
                .map(group -> group.getEmail())
                .map(email -> AccountUtil.getLdapGroupName(email))
                .collect(Collectors.toSet());

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
    public void synchronizeAllGroups() throws LDAPException {
        Map<GSuiteGroup, GroupMembership> gsuiteGroups = gsuiteDirService.getAllGroupMembership();
        Set<String> ldapGroups = ldapService.getAllGroupNames();

        Map<String, AccountInfo> emailAccountMap = new HashMap<>();
        for (AccountInfo info : ldapService.getAllAccounts()) {
            info.getEmails().forEach(email -> emailAccountMap.put(email, info));
        }
        log.info("Email Account map: {}", emailAccountMap);

        Set<String> syncedGroups = new HashSet<>();
        for (Map.Entry<GSuiteGroup, GroupMembership> entry : gsuiteGroups.entrySet()) {
            GSuiteGroup gsuiteGroup = entry.getKey();
            GroupMembership gsuiteMembership = entry.getValue();
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
            Map<String, AccountInfo> emailAccountMap) throws LDAPException {
        LdapGroup ldapGroup = new LdapGroup();
        ldapGroup.setName(AccountUtil.getLdapGroupName(gsuiteGroup.getEmail()));
        ldapGroup.setDescription(gsuiteGroup.getName());
        Set<String> members = gsuiteMembership.getMembers().stream().
                filter(m -> m.getStatus() == Status.ACTIVE).filter(m -> emailAccountMap.containsKey(m.getEmail())).
                map(m -> AccountUtil.getAccountDN(emailAccountMap.get(m.getEmail()).getUsername(), config)).
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
    public void handleEvent(Event event) {
        if (Configuration.TOPIC_CHANGE.equals(event.getTopic())) {
            configure();
        }
    }
}
