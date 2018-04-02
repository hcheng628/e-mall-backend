package us.supercheng.emall.controller.backend;

import com.github.pagehelper.PageInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import us.supercheng.emall.common.Const;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/manage/product")

public class ManageProductController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                         @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                         HttpSession session) {
        User user = this.iUserService.getCurrentUser(session);
        ServerResponse serverResponse = this.iUserService.checkAdminUser(user);
        if (serverResponse.getStatus() == ResponseCode.SUCCESS.getCode()) {
            return ServerResponse.createServerResponseSuccess(iProductService.manageList(pageNum, pageSize));
        }
        return serverResponse;
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> search(String productName, Integer productId, HttpSession session,
                                           @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                           @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        User user = this.iUserService.getCurrentUser(session);
        ServerResponse serverResponse = this.iUserService.checkAdminUser(user);
        if (serverResponse.getStatus() == ResponseCode.SUCCESS.getCode()) {
            if (!StringUtils.isNotBlank(productName) && productId == null) {
                return ServerResponse.createServerResponse(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                        ResponseCode.ILLEGAL_ARGUMENT.getDesc());
            }
            return ServerResponse.createServerResponseSuccess(this.iProductService.manageFindProductsByNameOrId(pageNum,
                    pageSize, productName, productId));
        }
        return serverResponse;
    }

    @RequestMapping(value = "upload.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map> upload(@RequestParam("upload_file") MultipartFile file, HttpSession session) {
        User user = this.iUserService.getCurrentUser(session);
        user = new User();
        user.setId(1);
        user.setRole(1);
        ServerResponse serverResponse = this.iUserService.checkAdminUser(user);
        if (serverResponse.getStatus() == ResponseCode.SUCCESS.getCode()) {
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
        return serverResponse;
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<Product> detail(Integer productId, HttpSession session) {
        User user = this.iUserService.getCurrentUser(session);
        ServerResponse serverResponse = this.iUserService.checkAdminUser(user);
        if (serverResponse.getStatus() == ResponseCode.SUCCESS.getCode()) {
            Product product = this.iProductService.manageDetail(productId);
            return ServerResponse.createServerResponseSuccess(product);
        }
        return serverResponse;
    }

    @RequestMapping(value = "set_sale_status.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status, HttpSession session) {
        User user = this.iUserService.getCurrentUser(session);
        ServerResponse serverResponse = this.iUserService.checkAdminUser(user);
        if (serverResponse.getStatus() == ResponseCode.SUCCESS.getCode()) {
            Product product = this.iProductService.findProductById(productId);
            if (product != null) {
                return this.iProductService.manageSetSaleStatus(product, status);
            } else {
                return ServerResponse.createServerResponseError("No Such Product ProductID: " + productId);
            }
        }
        return serverResponse;
    }

    @RequestMapping(value = "save.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> upsert(Product product, HttpSession session) {
        User user = this.iUserService.getCurrentUser(session);
        ServerResponse serverResponse = this.iUserService.checkAdminUser(user);
        if (serverResponse.getStatus() == ResponseCode.SUCCESS.getCode()) {
            return this.iProductService.upsert(product);
        }
        return serverResponse;
    }

    @RequestMapping(value = "richtext_img_upload.do", method = RequestMethod.POST)
    @ResponseBody
    public Map richtextImgupload(@RequestParam("upload_file") MultipartFile file, HttpSession session) {
        Map<String, String> map = new HashMap<>();
        User user = this.iUserService.getCurrentUser(session);
        user = new User();
        user.setId(1);
        user.setRole(1);
        ServerResponse<String> serverResponse = this.iUserService.checkAdminUser(user);
        try {
            if (serverResponse.getStatus() == ResponseCode.SUCCESS.getCode()) {
                File f = new File(file.getOriginalFilename());
                FileUtils.writeByteArrayToFile(f, file.getBytes());
                String filename = FTPHelper.doUpload("/", f);
                System.out.println("Upload Filename: " + filename);
                if (filename != null) {
                    map.put("file_path", PropHelper.getValue("ftp.server.ip") + "/" + filename);
                    map.put("msg", "Upload Rich Text Image Success");
                    map.put("success", "true");
                } else {
                    map.put("file_path", "[real file path]");
                    map.put("msg", "Upload Rich Text Image Failed");
                    map.put("success", "false");
                }
            } else {
                map.put("file_path", "[real file path]");
                map.put("msg", serverResponse.getData());
                map.put("success", "false");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            map.put("file_path", "[real file path]");
            map.put("msg", ex.getMessage());
            map.put("success", "false");
        }
        return map;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat(Const.APP_DATETIME_FORMAT), true));
    }
}