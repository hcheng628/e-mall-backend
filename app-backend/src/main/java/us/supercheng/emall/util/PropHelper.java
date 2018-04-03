package us.supercheng.emall.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.supercheng.emall.common.Const;
import java.util.Properties;

public class PropHelper {
    private static final Logger logger = LoggerFactory.getLogger(PropHelper.class);
    private static Properties appProp;

    static {
        appProp = new Properties();
        try {
            appProp.load(PropHelper.class.getClassLoader().getResourceAsStream(Const.APP_PROP_FILE));
        } catch (Exception ex) {
            logger.error("Load App Prop Error\r\n" + ex);
        }
    }

    public static String getValue(String key) {
        logger.debug("Enter getValue key: " + key);
        return appProp.getProperty(key);
    }

    public static String getValue(String fileName, String key) {
        logger.debug("Enter getValue FileName: " + fileName + " Key: " + key);
        Properties p = new Properties();
        try {
            p.load(PropHelper.class.getClassLoader().getResourceAsStream(Const.ALIPAY_PROP_FILE));
        } catch (Exception ex) {
            logger.error("Load \" + fileName + \" Prop Error\r\n" + ex);
        }
        return p.getProperty(key);
    }
}