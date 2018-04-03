package us.supercheng.emall.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.supercheng.emall.common.Const;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeHelper {
    private static final Logger logger = LoggerFactory.getLogger(DateTimeHelper.class);
    private static SimpleDateFormat simpleDateFormat;
    static {
        simpleDateFormat = new SimpleDateFormat(Const.APP_DATETIME_FORMAT);
    }
    public static String toAppDateTimeString(Date date) {
        logger.debug("Enter toAppDateTimeString date: " + date);
        return simpleDateFormat.format(date);
    }
    public static Date toAppDateTime(String date) {
        Date d = null;
        try {
            d = simpleDateFormat.parse(date);
        } catch (Exception ex) {
            logger.error("toAppDateTime String -> Date Fail \r\n" + ex);
            ex.printStackTrace();
        }
        logger.debug("Enter toAppDateTime date: " + d);
        return d;
    }
}