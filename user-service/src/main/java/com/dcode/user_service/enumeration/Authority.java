package com.dcode.user_service.enumeration;


import static com.dcode.user_service.constant.Constants.AuthorityConstant.*;

public enum Authority {
    USER(USER_AUTHORITIES),
    ADMIN(ADMIN_AUTHORITIES),
    MANAGER(MANAGER_AUTHORITIES);


    private final String authorityValue;

    Authority(String authorityValue) {
        this.authorityValue = authorityValue;
    }

    public String getAuthorityValue() {
        return this.authorityValue;
    }
}
