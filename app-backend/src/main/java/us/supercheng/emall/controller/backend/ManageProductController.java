package us.supercheng.emall.controller.backend;

import com.github.pagehelper.PageInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import us.supercheng.emall.common.ResponseCode;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.pojo.Product;
import us.supercheng.emall.pojo.User;
import us.supercheng.emall.service.IProductService;
import us.supercheng.emall.service.IUserService;
import us.supercheng.emall.util.FTPHelper;
import us.supercheng.emall.util.PropHelper;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/manage/product")

public class ManageProductController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Value("${ftp.server.ip}")
    private String testIp;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                               @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                               HttpSession session) {
        User user = this.iUserService.getCurrentUser(session);
        ServerResponse serverResponse = this.iUserService.checkAdminUser(user);
        if (serverResponse.getStatus() == ResponseCode.SUCCESS.getCode() || 0 == 0) {
            return ServerResponse.createServerResponseSuccess(iProductService.manageList(pageNum, pageSize));
        }
        return serverResponse;
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> search(String productName, Integer productId, HttpSession session,
                                           @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                           @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        if (!StringUtils.isNotBlank(productName) && productId == null) {
            return ServerResponse.createServerResponse(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        }
        return ServerResponse.createServerResponseSuccess(this.iProductService.manageFindProductsByNameOrId(pageNum,
                pageSize, productName, productId));
    }

    @RequestMapping(value = "upload.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map> upload(@RequestParam("upload_file") MultipartFile file, HttpSession session, @Value("${ftp.server.ip}") String prop) {
        Map map = new HashMap();
        try {
            File f = new File(file.getOriginalFilename());
            FileUtils.writeByteArrayToFile(f, file.getBytes());
            String filename = FTPHelper.doUpload("/", f);
            System.out.println("Upload Filename: " + filename);
            if (filename != null) {
                map.put("uri", filename);
                map.put("url", PropHelper.getValue("ftp.server.ip") + "/" + filename);
            } else {
                return ServerResponse.createServerResponseError("Upload File Failed");
            }
        } catch (IOException ex) {
            return ServerResponse.createServerResponseError("Upload File Failed");
        }
        return ServerResponse.createServerResponseSuccess(map);
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<Product> detail(Integer productId) {
        Product product = this.iProductService.manageDetail(productId);
        return ServerResponse.createServerResponseSuccess(product);
    }

    @RequestMapping(value = "set_sale_status.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        Product product = this.iProductService.findProductById(productId);
        if (product != null) {
            return this.iProductService.manageSetSaleStatus(product, status);
        } else {
            return ServerResponse.createServerResponseError("No Such Product ProductID: " + productId);
        }
    }
}