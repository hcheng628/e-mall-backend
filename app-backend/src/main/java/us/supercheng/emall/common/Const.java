package us.supercheng.emall.common;

import org.springframework.context.annotation.Configuration;
import us.supercheng.emall.util.PropHelper;

@Configuration
public class Const {
    public static final String APP_PROP_FILE = "emall.properties";
    public static final String APP_DEFAULT_ENCODING = "UTF-8";
    public static final String APP_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final int APP_MONEY_SCALE = 2;

    public static final String USERNAME = "USERNAME";
    public static final String E_MAIL = "EMAIL";
    public static final String CURRENT_USER = "currentUser";

    public static final int ROLE_CUSTOMER = 0;
    public static final int ROLE_ADMIN = 1;

    public static final String APP_QR_PATH = "/upload/Images/QR/";


    public static final boolean APP_USE_HTTP_PROXY_FLAG = true;

    // Auth
    public static String SALT_PASSWD_PREFIX;
    public static String SALT_PASSWD_SUFFIX;

    // FTP
    public static String FTP_IP;
    public static Integer FTP_PORT;
    public static String FTP_USER;
    public static String FTP_PASS;
    public static String FTP_HTTP_PREFIX;

    static {
        System.out.println("AppConfig Init Enter");
        FTP_IP = PropHelper.getValue("ftp.server.ip");
        FTP_PORT = Integer.parseInt(PropHelper.getValue("ftp.server.port"));
        FTP_USER = PropHelper.getValue("ftp.server.user");
        FTP_PASS = PropHelper.getValue("ftp.server.pass");
        FTP_HTTP_PREFIX = PropHelper.getValue("ftp.server.http.prefix");

        SALT_PASSWD_PREFIX = PropHelper.getValue("app.passwd.salt.prefix");
        SALT_PASSWD_SUFFIX = PropHelper.getValue("app.passwd.salt.suffix");

        System.out.println("FTP Info: \r\nFTP_IP: " + FTP_IP + " FTP_PORT: "+ FTP_PORT +
                "\r\nFTP_USER: " + FTP_USER + " FTP_PASS: " + FTP_PASS + "\r\nFTP_HTTP_PREFIX: " + FTP_HTTP_PREFIX +
                "\r\nPASSWD Salt Info: \r\nSALT_PASSWD_PREFIX: " + SALT_PASSWD_PREFIX + " SALT_PASSWD_SUFFIX: " + SALT_PASSWD_SUFFIX);
        System.out.println("AppConfig Init Exit");
    }





    public interface PaymentSystem {
        int ALIPAY = 1;

        enum OrderStatusEnum {
            CANCELED(0, "CANCELED"),
            UNPAID(10, "UNPAID"),
            PAID(20, "PAID"),
            SHIPPED(40, "SHIPPED"),
            FINISHED(50, "FINISHED"),
            CLOSED(60, "CLOSED");

            private int code;
            private String val;

            OrderStatusEnum(int code, String val) {
                this.code = code;
                this.val = val;
            }

            public int getCode() {
                return this.code;
            }

            public String getVal() {
                return this.val;
            }

            public static OrderStatusEnum codeOf(int code) {
                for (OrderStatusEnum orderStatusEnum : values()) {
                    if (orderStatusEnum.getCode() == code) {
                        return orderStatusEnum;
                    }
                }
                throw new RuntimeException("No OrderStatusEnum Found");
            }
        }
    }

    public interface CartConst {
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        int PRODUCT_CHECKED = 1;
        int PRODUCT_UNCHECKED = 0;
    }

    public interface FTPConst {
        int BUFFER_SIZE = 1024;
        String ENCODING = Const.APP_DEFAULT_ENCODING;
    }

    public interface ProductConst {
        int PRODUCT_STATUS_1 = 1;
    }
}