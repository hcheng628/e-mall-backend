package us.supercheng.emall.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.supercheng.emall.common.Const;
import java.math.BigDecimal;

public class BigDecimalHelper {
    private static final Logger logger = LoggerFactory.getLogger(BigDecimalHelper.class);

    private BigDecimalHelper(){}
    public static BigDecimal add(double a, double b) {
        logger.debug("add " + a + " " + b);
        return new BigDecimal(a+"").add(new BigDecimal(b + ""));
    }
    public static BigDecimal sub(double a, double b) {
        logger.debug("sub " + a + " " + b);
        return new BigDecimal(a+"").subtract(new BigDecimal(b + ""));
    }
    public static BigDecimal mul(double a, double b) {
        logger.debug("mul " + a + " " + b);
        return new BigDecimal(a+"").multiply(new BigDecimal(b + ""));
    }
    public static BigDecimal div(double a, double b) {
        logger.debug("div " + a + " " + b);
        return new BigDecimal(a+"").divide(new BigDecimal(b + ""), Const.APP_MONEY_SCALE, BigDecimal.ROUND_HALF_UP);
    }
}