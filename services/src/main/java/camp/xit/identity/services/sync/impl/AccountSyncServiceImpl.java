package camp.xit.identity.services.sync.impl;

import camp.xit.identity.services.config.AppConfiguration;
import camp.xit.identity.services.config.Configuration;
import camp.xit.identity.services.google.GSuiteDirectoryService;
import camp.xit.identity.services.google.model.GSuiteGroup;
import camp.xit.identity.services.google.model.GroupMembership;
import camp.xit.identity.services.ldap.LdapAccountService;
import camp.xit.identity.services.ldap.model.LdapGroup;
import camp.xit.identity.services.sync.AccountSyncService;
import camp.xit.identity.services.util.AccountUtil;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.Collections;
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
        Map<GSuiteGroup, GroupMembership> gsuiteGroups = gsuiteDirService.getAllGroupMembership();
        Map<String, Set<String>> groupMapping = config.getLdapGroupMapping();
        Map<String, LdapGroup> ldapGroups = ldapService.getAllLdapGroups();
        String accountDN = ldapService.getAccountDN(userInfo.getSubject());

        Set<String> asIs = ldapGroups.values().stream()
                .filter(g -> g.getMembers().contains(accountDN))
                .map(g -> g.getDn())
                .collect(Collectors.toSet());

        Set<String> toBe = gsuiteGroups.entrySet().stream()
                .filter(e -> e.getValue().isMember(userInfo.getSubject()))
                .map(e -> e.getKey().getEmail())
                .flatMap(g -> (groupMapping.get(g) != null ? groupMapping.get(g).stream() : Collections.<String>emptySet().stream()))
                .collect(Collectors.toSet());

        // Hack for implicit group mapping
        if (AccountUtil.isAccountInternal(config, userInfo)) {
            toBe.addAll(groupMapping.get(config.getGSuiteImplicitGroup()));
        }

        Set<String> toRemove = new HashSet<>(asIs);
        toRemove.removeAll(toBe);

        Set<String> toAdd = new HashSet<>(toBe);
        toAdd.removeAll(asIs);

        log.info("Remove membership for user {} from groups {}", userInfo.getEmail(), toRemove);
        log.info("Add membership for user {} to groups {}", userInfo.getEmail(), toAdd);

        for (String groupDN : toRemove) {
            ldapService.removeGroupMember(accountDN, groupDN);
        }
        for (String groupDN : toAdd) {
            ldapService.addGroupMember(accountDN, groupDN);
        }
    }


    @Override
    public void handleEvent(Event event) {
        if (Configuration.TOPIC_CHANGE.equals(event.getTopic())) {
            configure();
        }
    }
}
