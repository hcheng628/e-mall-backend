package us.supercheng.emall.test.util;

import org.junit.Test;
import us.supercheng.emall.util.QRImageHelper;

public class QRImageHelperTest {

    @Test
    public void generateQRBase64Str_test() throws Exception{
        String imageBase64 = QRImageHelper.getQRCodeImage256("https:\\/\\/qr.alipay.com\\/bax00506dvm9gizal5ao0079");
        System.out.println("imageBase64: " + imageBase64);
    }
}