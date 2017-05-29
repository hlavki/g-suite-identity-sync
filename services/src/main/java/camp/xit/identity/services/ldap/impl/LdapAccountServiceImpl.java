package camp.xit.identity.services.ldap.impl;

import camp.xit.identity.services.config.AppConfiguration;
import camp.xit.identity.services.model.AccountInfo;
import camp.xit.identity.services.config.Configuration;
import camp.xit.identity.services.model.CreateAccountData;
import camp.xit.identity.services.model.PrepareAccountData.Role;
import com.unboundid.ldap.sdk.*;
import org.apache.cxf.rs.security.oidc.common.UserInfo;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import camp.xit.identity.services.ldap.LdapAccountService;
import camp.xit.identity.services.ldap.model.LdapGroup;
import camp.xit.identity.services.model.UpdateAccountData;
import java.util.*;
import org.osgi.service.event.EventHandler;

public class LdapAccountServiceImpl implements LdapAccountService, EventHandler {

    private static final Logger log = LoggerFactory.getLogger(LdapAccountServiceImpl.class);

    private final LDAPConnectionPool ldapPool;
    private final AppConfiguration config;


    public LdapAccountServiceImpl(Configuration config, LDAPConnectionPool ldapPool) {
        this.config = config;
        this.ldapPool = ldapPool;
    }


    private static void configure() {
        log.info("Configuring UserLdapService...");
    }


    @Override
    public boolean accountExists(String subject) throws LDAPException {
        return getAccountDN(subject) != null;
    }


    @Override
    public String getAccountDN(String subject) throws LDAPException {
        try (LDAPConnection conn = ldapPool.getConnection()) {
            String baseDn = config.getLdapUserBaseDN();
            SearchResultEntry entry = conn.searchForEntry(baseDn, SearchScope.ONE, "(employeeNumber=" + subject + ")", "uid");
            return entry != null ? entry.getDN() : null;
        }
    }


    @Override
    public AccountInfo getAccountInfo(String subject) throws LDAPException {
        AccountInfo result = null;
        try (LDAPConnection conn = ldapPool.getConnection()) {
            String baseDn = config.getLdapUserBaseDN();
            SearchResultEntry entry = conn.searchForEntry(baseDn, SearchScope.ONE, "(employeeNumber=" + subject + ")");
            if (entry != null) {
                AccountInfo info = new AccountInfo();
                info.setUsername(entry.getAttributeValue("uid"));
                info.setEmail(entry.getAttributeValue("mail"));
                info.setGivenName(entry.getAttributeValue("givenName"));
                info.setFamilyName(entry.getAttributeValue("sn"));
                info.setSubject(entry.getAttributeValue("employeeNumber"));
                info.setName(entry.getAttributeValue("cn"));
                info.setRole(Role.valueOf(entry.getAttributeValue("employeeType")));
                result = info;
            }
        }
        return result;
    }


    @Override
    public void createAccount(UserInfo userInfo, CreateAccountData createData) throws LDAPException {
        try (LDAPConnection conn = ldapPool.getConnection()) {
            DN baseDN = new DN(config.getLdapUserBaseDN());
            DN entryDN = new DN(new RDN("uid", createData.getEmail()), baseDN);
            Entry entry = new Entry(entryDN);
            entry.addAttribute("objectClass", "inetOrgPerson");
            entry.addAttribute("mail", userInfo.getEmail());
            entry.addAttribute("givenName", userInfo.getGivenName());
            entry.addAttribute("sn", userInfo.getFamilyName());
            entry.addAttribute("cn", userInfo.getName());
            entry.addAttribute("employeeNumber", userInfo.getSubject());
            entry.addAttribute("userPassword", createData.getPassword());
            entry.addAttribute("employeeType", String.valueOf(createData.getRole()));
            conn.add(entry);
        }
    }


    @Override
    public void updateAccount(UserInfo userInfo, UpdateAccountData createData) throws LDAPException {
        try (LDAPConnection conn = ldapPool.getConnection()) {
            String entryDN = getAccountDN(userInfo.getSubject());
            Modification mod = new Modification(ModificationType.REPLACE, "userPassword", createData.getPassword());
            conn.modify(new ModifyRequest(entryDN, mod));
        }
    }


    @Override
    public Map<String, LdapGroup> getAllGroups() throws LDAPException {
        Map<String, LdapGroup> result = new HashMap<>();
        try (LDAPConnection conn = ldapPool.getConnection()) {
            String baseDN = config.getLdapAppsBaseDN();
            log.info("Group base DN: " + baseDN);
            SearchResult searchResult = conn.search(baseDN, SearchScope.SUB, "(objectClass=groupOfUniqueNames)", "cn", "uniqueMember");
            for (SearchResultEntry entry : searchResult.getSearchEntries()) {
                String dn = entry.getDN();
                Set<String> members = new HashSet<>(Arrays.asList(entry.getAttributeValues("uniqueMember")));
                result.put(dn, new LdapGroup(dn, members));
            }
        }
        return result;
    }


    @Override
    public Map<String, LdapGroup> getAccountGroups(String accountDN) throws LDAPException {
        Map<String, LdapGroup> result = new HashMap<>();
        try (LDAPConnection conn = ldapPool.getConnection()) {
            String baseDN = config.getLdapAppsBaseDN();
            log.info("Group base DN: " + baseDN);
            String filter = "(&(objectClass=groupOfUniqueNames)(uniqueMember=" + accountDN + "))";
            SearchResult searchResult = conn.search(baseDN, SearchScope.SUB, filter, "cn", "uniqueMember");
            for (SearchResultEntry entry : searchResult.getSearchEntries()) {
                String dn = entry.getDN();
                Set<String> members = new HashSet<>(Arrays.asList(entry.getAttributeValues("uniqueMember")));
                result.put(dn, new LdapGroup(dn, members));
            }
        }
        return result;
    }


    @Override
    public void addGroupMember(String accountDN, String groupDN) throws LDAPException {
        try (LDAPConnection conn = ldapPool.getConnection()) {
            LdapGroup group = getGroup(groupDN, conn);
            if (group != null && group.getMembers().contains(accountDN)) {
                log.info("Nothing to do. Account {} is already member of group {}", accountDN, groupDN);
            } else {
                if (group == null) {
                    log.debug("Creating group {}", groupDN);
                    Entry groupEntry = new Entry(groupDN);
                    groupEntry.addAttribute("objectClass", "groupOfUniqueNames");
                    groupEntry.addAttribute("uniqueMember", accountDN);
                    conn.add(groupEntry);
                    log.info("Group {} added", groupDN);
                } else {
                    Modification mod = new Modification(ModificationType.ADD, "uniqueMember", accountDN);
                    conn.modify(new ModifyRequest(groupDN, mod));
                    log.info("Added membership {} to {}", accountDN, groupDN);
                }
            }
        }
    }


    @Override
    public void removeGroupMember(String accountDN, String groupDN) throws LDAPException {
        try (LDAPConnection conn = ldapPool.getConnection()) {
            LdapGroup group = getGroup(groupDN, conn);
            if (group == null || !group.getMembers().contains(accountDN)) {
                log.info("Nothing to do. Account {} is not member of group {}", accountDN, groupDN);
            } else {
                Modification mod = new Modification(ModificationType.DELETE, "uniqueMember", accountDN);
                conn.modify(new ModifyRequest(groupDN, mod));
                log.info("Remove membership {} from {}", accountDN, groupDN);
                if (group.getMembers().size() == 1) {
                    log.info("Removing group {}", groupDN);
                    conn.delete(groupDN);
                }
            }
        }
    }


    protected LdapGroup getGroup(String groupDN, LDAPConnection conn) throws LDAPException {
        LdapGroup result = null;
        SearchResultEntry entry = conn.getEntry(groupDN, "cn", "uniqueMember");
        if (entry != null) {
            String dn = entry.getDN();
            Set<String> members = new HashSet<>(Arrays.asList(entry.getAttributeValues("uniqueMember")));
            result = new LdapGroup(dn, members);
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
