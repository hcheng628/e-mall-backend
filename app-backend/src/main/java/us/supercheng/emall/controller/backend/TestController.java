package us.supercheng.emall.controller.backend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.util.FTPHelper;
import java.io.File;

@Controller
@RequestMapping("/test/")
public class TestController {

    @RequestMapping("ftp_upload_test.do")
    @ResponseBody
    public ServerResponse ftpUploadTest() {
        String testFilepath = this.getClass().getResource("/logback.xml").getPath();
        System.out.println(testFilepath);
        return ServerResponse.createServerResponseSuccess(FTPHelper.doUpload("/", new File(testFilepath)));
    }
}
