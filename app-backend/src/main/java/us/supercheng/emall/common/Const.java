package us.supercheng.emall.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Const {
    public static final String CURRENT_USER = "currentUser";
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String USERNAME = "USERNAME";
    public static final String E_MAIL = "EMAIL";
    public static final int ROLE_CUSTOMER = 0;
    public static final int ROLE_ADMIN = 1;

    @Value("${app.passwd.salt.prefix}")
    public static String SALT_PASSWD_PREFIX;

    @Value("${app.passwd.salt.suffix}")
    public static String SALT_PASSWD_SUFFIX;
}