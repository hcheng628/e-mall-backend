package us.supercheng.emall.test.util;

import org.junit.Test;
import us.supercheng.emall.util.BigDecimalHelper;

public class BigDecimalHelperTest {

    @Test
    public void testAll() {
        System.out.println("Double: " + (0.05+0.01));
        System.out.println("BigDecimalHelper: " + BigDecimalHelper.add(0.05, 0.01));

        System.out.println("Double: " + (1.0-0.42));
        System.out.println("BigDecimalHelper: " + BigDecimalHelper.sub(1.0, 0.42));

        System.out.println("Double: " + (4.015*100));
        System.out.println("BigDecimalHelper: " + BigDecimalHelper.mul(4.015, 100));

        System.out.println("Double: " + (123.3/100));
        System.out.println("BigDecimalHelper: " + BigDecimalHelper.div(123.3, 100));
    }
}