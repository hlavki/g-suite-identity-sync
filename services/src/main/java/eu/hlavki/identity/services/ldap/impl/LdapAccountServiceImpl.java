package eu.hlavki.identity.services.ldap.impl;

import eu.hlavki.identity.services.config.AppConfiguration;
import eu.hlavki.identity.services.model.AccountInfo;
import eu.hlavki.identity.services.config.Configuration;
import com.unboundid.ldap.sdk.*;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.hlavki.identity.services.ldap.LdapAccountService;
import eu.hlavki.identity.services.ldap.model.LdapAccount;
import eu.hlavki.identity.services.ldap.model.LdapGroup;
import eu.hlavki.identity.services.util.AccountUtil;
import java.util.*;
import java.util.stream.Collectors;
import org.osgi.service.event.EventHandler;

public class LdapAccountServiceImpl implements LdapAccountService, EventHandler {

    private static final Logger log = LoggerFactory.getLogger(LdapAccountServiceImpl.class);

    private static final String GROUP_OCLASS = "groupOfNames";
    private static final String GROUP_MEMBER_ATTR = "member";
    public static final String GROUP_NAME_ATTR = "cn";
    private static final String GROUP_DESC_ATTR = "description";

    private final LDAPConnectionPool ldapPool;
    private final AppConfiguration config;


    public LdapAccountServiceImpl(Configuration config, LDAPConnectionPool ldapPool) {
        this.config = config;
        this.ldapPool = ldapPool;
    }


    private void configure() {
        log.info("Configuring UserLdapService...");
        try (LDAPConnection conn = ldapPool.getConnection()) {
            creatOrgUnits(conn);
        } catch (LDAPException e) {
            log.error("Can't create org units!", e);
        }
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
                result = AccountUtil.fromLdap(entry);
                result.setSyncGsuitePassword(config.isGsuiteSyncPassword());
            }
        }
        return result;
    }


    @Override
    public List<AccountInfo> getAllAccounts() throws LDAPException {
        try (LDAPConnection conn = ldapPool.getConnection()) {
            String baseDn = config.getLdapUserBaseDN();
            SearchResult searchResult = conn.search(baseDn, SearchScope.ONE, "(objectClass=inetOrgPerson)");
            return searchResult.getSearchEntries().stream().map(entry -> AccountUtil.fromLdap(entry)).collect(Collectors.toList());
        }
    }


    @Override
    public void createAccount(LdapAccount account) throws LDAPException {
        try (LDAPConnection conn = ldapPool.getConnection()) {
            String entryDN = AccountUtil.getAccountDN(account.getUsername(), config);
            log.info("Creating user with DN {}", entryDN);
            Entry entry = new Entry(entryDN);
            entry.addAttribute("objectClass", "inetOrgPerson");
            for (String email : account.getEmails()) {
                entry.addAttribute("mail", email);
            }
            entry.addAttribute("givenName", account.getGivenName());
            entry.addAttribute("sn", account.getFamilyName());
            entry.addAttribute("cn", account.getName());
            entry.addAttribute("employeeNumber", account.getSubject());
            entry.addAttribute("userPassword", account.getPassword());
            entry.addAttribute("employeeType", account.getRole().toString());
            conn.add(entry);
        }
    }


    @Override
    public void updateAccount(LdapAccount account) throws LDAPException {
        try (LDAPConnection conn = ldapPool.getConnection()) {
            String entryDN = getAccountDN(account.getSubject());
            List<Modification> mods = new ArrayList<>();
            if (account.getPassword() != null) {
                mods.add(new Modification(ModificationType.REPLACE, "userPassword", account.getPassword()));
            }
            mods.add(new Modification(ModificationType.REPLACE, "givenName", account.getGivenName()));
            mods.add(new Modification(ModificationType.REPLACE, "sn", account.getFamilyName()));
            mods.add(new Modification(ModificationType.REPLACE, "cn", account.getName()));
            mods.add(new Modification(ModificationType.REPLACE, "mail", account.getEmails().toArray(new String[0])));
            conn.modify(new ModifyRequest(entryDN, mods));
        }
    }


    @Override
    public LdapGroup getGroup(String groupName) throws LDAPException {
        try (LDAPConnection conn = ldapPool.getConnection()) {
            return getGroup(groupName, conn);
        }
    }


    @Override
    public Set<String> getAllGroupNames() throws LDAPException {
        Set<String> result = new HashSet<>();
        try (LDAPConnection conn = ldapPool.getConnection()) {
            String baseDN = config.getLdapGroupsBaseDN();
            log.info("Group base DN: " + baseDN);
            String filter = "(objectClass=" + GROUP_OCLASS + ")";
            SearchResult searchResult = conn.search(baseDN, SearchScope.SUB, filter, GROUP_NAME_ATTR);
            for (SearchResultEntry entry : searchResult.getSearchEntries()) {
                String name = entry.getAttributeValue(GROUP_NAME_ATTR);
                result.add(name);
            }
        }
        return result;
    }


    @Override
    public LdapGroup createOrUpdateGroup(LdapGroup group) throws LDAPException {
        try (LDAPConnection conn = ldapPool.getConnection()) {
            LdapGroup current = getGroup(group.getName(), conn);
            if (current != null) {
                Modification[] mods = new Modification[]{
                    new Modification(ModificationType.REPLACE, GROUP_DESC_ATTR, group.getDescription()),
                    new Modification(ModificationType.REPLACE, GROUP_MEMBER_ATTR, group.getMembersDn().toArray(new String[0]))
                };
                conn.modify(current.getDn(), mods);
                current.setDescription(group.getDescription());
                current.setMembersDn(group.getMembersDn());
            } else {
                String dn = AccountUtil.getGroupDN(group.getName(), config);
                Entry entry = new Entry(dn);
                entry.setAttribute(GROUP_NAME_ATTR, group.getName());
                entry.setAttribute(GROUP_DESC_ATTR, group.getDescription());
                entry.setAttribute(GROUP_MEMBER_ATTR, group.getMembersDn());
                entry.setAttribute("objectClass", GROUP_OCLASS);
                conn.add(entry);
                current = new LdapGroup(dn, group.getName(), group.getDescription(), group.getMembersDn());
            }
            return current;
        }
    }


    @Override
    public Map<String, LdapGroup> getAccountGroups(String accountDN) throws LDAPException {
        Map<String, LdapGroup> result = new HashMap<>();
        try (LDAPConnection conn = ldapPool.getConnection()) {
            String baseDN = config.getLdapGroupsBaseDN();
            log.info("Group base DN: " + baseDN);
            Filter filter = Filter.createANDFilter(Filter.createEqualityFilter("objectClass", GROUP_OCLASS),
                    Filter.createEqualityFilter(GROUP_MEMBER_ATTR, accountDN));
            SearchResult searchResult = conn.search(baseDN, SearchScope.SUB, filter,
                    GROUP_NAME_ATTR, GROUP_MEMBER_ATTR, GROUP_DESC_ATTR);
            for (SearchResultEntry entry : searchResult.getSearchEntries()) {
                String dn = entry.getDN();
                String name = entry.getAttributeValue(GROUP_NAME_ATTR);
                String description = entry.getAttributeValue(GROUP_DESC_ATTR);
                Set<String> members = new HashSet<>(Arrays.asList(entry.getAttributeValues(GROUP_MEMBER_ATTR)));
                result.put(dn, new LdapGroup(name, dn, description, members));
            }
        }
        return result;
    }


    @Override
    public void addGroupMember(String accountDN, String groupName) throws LDAPException {
        try (LDAPConnection conn = ldapPool.getConnection()) {
            LdapGroup group = getGroup(groupName, conn);
            if (group != null && group.getMembersDn().contains(accountDN)) {
                log.info("Nothing to do. Account {} is already member of group {}", accountDN, group.getName());
            } else {
                if (group == null) {
                    log.debug("Creating group {}", groupName);
                    DN groupDN = new DN(new RDN(GROUP_NAME_ATTR, groupName), new DN(config.getLdapGroupsBaseDN()));
                    Entry groupEntry = new Entry(groupDN);
                    groupEntry.addAttribute("objectClass", GROUP_OCLASS);
                    groupEntry.addAttribute(GROUP_MEMBER_ATTR, accountDN);
                    conn.add(groupEntry);
                    log.info("Group {} added", groupDN);
                } else {
                    Modification mod = new Modification(ModificationType.ADD, GROUP_MEMBER_ATTR, accountDN);
                    conn.modify(new ModifyRequest(group.getDn(), mod));
                    log.info("Added membership {} to {}", accountDN, group.getName());
                }
            }
        }
    }


    @Override
    public void removeGroupMember(String accountDN, String groupName) throws LDAPException {
        try (LDAPConnection conn = ldapPool.getConnection()) {
            LdapGroup group = getGroup(groupName, conn);
            if (group == null || !group.getMembersDn().contains(accountDN)) {
                log.info("Nothing to do. Account {} is not member of group {}", accountDN, groupName);
            } else {
                Modification mod = new Modification(ModificationType.DELETE, GROUP_MEMBER_ATTR, accountDN);
                conn.modify(new ModifyRequest(group.getDn(), mod));
                log.info("Remove membership {} from {}", accountDN, group.getName());
                if (group.getMembersDn().size() == 1) {
                    log.info("Removing group {}", group.getName());
                    conn.delete(group.getDn());
                }
            }
        }
    }


    @Override
    public void removeGroup(String groupName) throws LDAPException {
        try (LDAPConnection conn = ldapPool.getConnection()) {
            String groupDN = AccountUtil.getGroupDN(groupName, config);
            SearchResultEntry entry = conn.getEntry(groupDN, GROUP_NAME_ATTR);
            if (entry != null) {
                conn.delete(groupDN);
            }
        }
    }


    /**
     * Read group from LDAP. If there is no group it returns NULL.
     *
     * @param groupName name of gruop
     * @param conn ldap connection
     * @return LDAP group or null if there is no group
     * @throws LDAPException
     */
    protected LdapGroup getGroup(String groupName, LDAPConnection conn) throws LDAPException {
        LdapGroup result = null;
        String baseDN = config.getLdapGroupsBaseDN();
        Filter groupFilter = Filter.createEqualityFilter(GROUP_NAME_ATTR, groupName);
        SearchResultEntry entry = conn.searchForEntry(baseDN, SearchScope.ONE, groupFilter,
                GROUP_NAME_ATTR, GROUP_MEMBER_ATTR, GROUP_DESC_ATTR);
        if (entry != null) {
            String dn = entry.getDN();
            String name = entry.getAttributeValue(GROUP_NAME_ATTR);
            String description = entry.getAttributeValue(GROUP_DESC_ATTR);
            Set<String> members = new HashSet<>(Arrays.asList(entry.getAttributeValues(GROUP_MEMBER_ATTR)));
            result = new LdapGroup(name, dn, description, members);
        }
        return result;
    }


    @Override
    public void handleEvent(Event event) {
        if (Configuration.TOPIC_CHANGE.equals(event.getTopic())) {
            configure();
        }
    }


    private void creatOrgUnits(LDAPConnection conn) throws LDAPException {
        String peopleDn = config.getLdapUserBaseDN();
        if (conn.getEntry(peopleDn) == null) {
            Entry entry = new Entry(peopleDn);
            entry.addAttribute("objectClass", "top");
            entry.addAttribute("objectClass", "organizationalUnit");
            conn.add(entry);
        }
        String groupDn = config.getLdapGroupsBaseDN();
        if (conn.getEntry(groupDn) == null) {
            Entry entry = new Entry(groupDn);
            entry.addAttribute("objectClass", "top");
            entry.addAttribute("objectClass", "organizationalUnit");
            conn.add(entry);
        }
    }
}
