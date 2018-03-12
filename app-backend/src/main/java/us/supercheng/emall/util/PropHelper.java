package us.supercheng.emall.util;

import us.supercheng.emall.common.Const;
import java.util.Properties;

public class PropHelper {

    private static Properties appProp;

    static {
        appProp = new Properties();
        try {
            appProp.load(PropHelper.class.getClassLoader().getResourceAsStream(Const.APP_PROP_FILE));
        } catch (Exception ex) {
            System.out.println("Load App Prop Error");
            ex.printStackTrace();
        }
    }

    public static String getValue(String key) {
        return appProp.getProperty(key);
    }
}