package camp.xit.identity.services.ldap.impl;

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

public class LdapAccountServiceImpl implements LdapAccountService {

    private static final Logger log = LoggerFactory.getLogger(LdapAccountServiceImpl.class);
    private static final String LDAP_USERS_BASE_DN_PROP = "ldap.users.baseDN";

    private final LDAPConnectionPool ldapPool;
    private final Configuration config;


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


    private String getAccountDN(String subject) throws LDAPException {
        try (LDAPConnection conn = ldapPool.getConnection()) {
            String baseDn = config.get(LDAP_USERS_BASE_DN_PROP);
            SearchResultEntry entry = conn.searchForEntry(baseDn, SearchScope.ONE, "(employeeNumber=" + subject + ")", "uid");
            return entry != null ? entry.getDN() : null;
        }
    }


    @Override
    public AccountInfo getAccountInfo(String subject) throws LDAPException {
        AccountInfo result = null;
        try (LDAPConnection conn = ldapPool.getConnection()) {
            String baseDn = config.get(LDAP_USERS_BASE_DN_PROP);
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
            DN baseDN = new DN(config.get(LDAP_USERS_BASE_DN_PROP));
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
    public void updateAccount(UserInfo userInfo, CreateAccountData createData) throws LDAPException {
        try (LDAPConnection conn = ldapPool.getConnection()) {
            String entryDN = getAccountDN(userInfo.getSubject());
            Modification mod = new Modification(ModificationType.REPLACE, "userPassword", createData.getPassword());
            conn.modify(new ModifyRequest(entryDN, mod));
        }
    }


    @Override
    public void synchronizeUserGroups(UserInfo userInfo) throws LDAPException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public void handleEvent(Event event) {
        if (Configuration.TOPIC_CHANGE.equals(event.getTopic())) {
            configure();
        }
    }
}
