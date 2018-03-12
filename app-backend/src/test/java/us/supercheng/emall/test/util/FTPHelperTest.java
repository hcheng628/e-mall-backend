package us.supercheng.emall.test.util;

import org.junit.Test;
import us.supercheng.emall.util.FTPHelper;
import java.io.File;

public class FTPHelperTest {
    @Test
    public void main_upload() {
        String testFilepath = this.getClass().getResource("/logback.xml").getPath();
        System.out.println(testFilepath);
        FTPHelper.doUpload("/", new File(testFilepath));
    }
}