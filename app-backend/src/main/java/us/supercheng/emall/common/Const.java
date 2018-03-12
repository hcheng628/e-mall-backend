package us.supercheng.emall.common;

import org.springframework.beans.factory.annotation.Value;

public class Const {
    public static final String APP_PROP_FILE = "emall.properties";
    public static final String APP_DEFAULT_ENCODING = "UTF-8";
    public static final String APP_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String USERNAME = "USERNAME";
    public static final String E_MAIL = "EMAIL";
    public static final String CURRENT_USER = "currentUser";

    public static final int ROLE_CUSTOMER = 0;
    public static final int ROLE_ADMIN = 1;

    @Value("${app.passwd.salt.prefix}")
    public static String SALT_PASSWD_PREFIX;

    @Value("${app.passwd.salt.suffix}")
    public static String SALT_PASSWD_SUFFIX;

    public interface FTPConst {
        int BUFFER_SIZE = 1024;
        String ENCODING = Const.APP_DEFAULT_ENCODING;
    }

    /* */
    @Value("${ftp.server.ip}")
    public static String FTP_IP;
    @Value("${ftp.server.port}")
    public static Integer FTP_PORT;
    @Value("${ftp.server.user}")
    public static String FTP_USER;

    @Value("${ftp.server.pass}")
    public static String FTP_PASS;
    @Value("${ftp.server.http.prefix}")
    public static String FTP_HTTP_PREFIX;

}