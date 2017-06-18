package camp.xit.identity.services.config;

import camp.xit.identity.services.google.NoPrivateKeyException;
import java.security.PrivateKey;
import java.util.Set;

public interface AppConfiguration {

    static final String CLIENT_ID_PROP = "oauth2.serviceAccount.clientId";
    static final String SUBJECT_PROP = "oauth2.serviceAccount.subject";
    static final String PRIVATE_KEY_PROP = "oauth2.serviceAccount.privateKey.file";
    static final String PRIVATE_KEY_PASS_PROP = "oauth2.serviceAccount.privateKey.passphrase";
    static final String GSUITE_DOMAIN_PROP = "gsuite.domain";
    static final String GSUITE_IMPLICIT_GROUP = "gsuite.implicit.group";
    static final String TOKEN_LIFETIME_PROP = "gsuite.serviceAccount.tokenLifetime";
    static final long TOKEN_LIFETIME_DEFAULT = 3600;
    static final String LDAP_USERS_BASE_DN_PROP = "ldap.users.baseDN";
    static final String LDAP_BASE_DN_PROP = "ldap.baseDN";
    static final String LDAP_GROUPS_BASE_DN_PROP = "ldap.groups.baseDN";
    static final String ADMINS_PROP = "admins";
    static final String GSUITE_SYNC_PASSWORD_PROP = "gsuite.sync.password";
    static final boolean GSUITE_SYNC_PASSWORD_DEFAULT = false;
    static final String EXTERNAL_USER_GROUP_PROP = "gsuite.external.accounts.group";


    String getGSuiteDomain();


    String getGSuiteImplicitGroup();


    String getServiceAccountClientId();


    String getServiceAccountSubject();


    PrivateKey getServiceAccountKey() throws NoPrivateKeyException;


    long getServiceAccountTokenLifetime();


    String getBaseDN();


    String getLdapUserBaseDN();


    String getLdapGroupsBaseDN();


    Set<String> getAdmins();


    boolean isGsuiteSyncPassword();


    String getExternalAccountsGroup();
}
