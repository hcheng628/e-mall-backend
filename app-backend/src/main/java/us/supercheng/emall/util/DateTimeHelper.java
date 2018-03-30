package us.supercheng.emall.util;

import us.supercheng.emall.common.Const;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeHelper {
    private static SimpleDateFormat simpleDateFormat;
    static {
        simpleDateFormat = new SimpleDateFormat(Const.APP_DATETIME_FORMAT);
    }
    public static String toAppDateTimeString(Date date) {
        return simpleDateFormat.format(date);
    }
    public static Date toAppDateTime(String date) {
        Date d = null;
        try {
            d = simpleDateFormat.parse(date);
        } catch (Exception ex) {
            System.err.println("toAppDateTime String -> Date Fail");
            ex.printStackTrace();
        }
        return d;
    }
}