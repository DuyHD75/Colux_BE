package com.dcode.identity_service.enumeration;


import static com.dcode.identity_service.constant.Constants.AuthorityConstant.*;

public enum Authority {
    USER(USER_AUTHORITIES),
    ADMIN(ADMIN_AUTHORITIES),
    MANAGER(MANAGER_AUTHORITIES),
    EMPLOYEE(EMPLOYEE_AUTHORITIES);

    private final String authorityValue;

    Authority(String authorityValue) {
        this.authorityValue = authorityValue;
    }

    public String getAuthorityValue() {
        return this.authorityValue;
    }
}
