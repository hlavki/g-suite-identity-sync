package camp.xit.identity.services.util;

import camp.xit.identity.services.config.AppConfiguration;
import camp.xit.identity.services.model.PrepareAccountData;
import camp.xit.identity.services.model.PrepareAccountData.Role;
import org.apache.cxf.rs.security.oidc.common.UserInfo;

public final class AccountUtil {

    private AccountUtil() {
    }


    public static boolean isAccountInternal(AppConfiguration cfg, UserInfo info) {
        return cfg.getGSuiteDomain().equals(info.getProperty("hd"));
    }


    public static Role getAccountRole(AppConfiguration cfg, UserInfo userInfo) {
        return isAccountInternal(cfg, userInfo) ? Role.INTERNAL : Role.EXTERNAL;
    }
}
