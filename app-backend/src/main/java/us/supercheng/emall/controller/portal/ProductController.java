package us.supercheng.emall.controller.portal;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import us.supercheng.emall.common.Const;
import us.supercheng.emall.common.ResponseCode;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.pojo.Product;
import us.supercheng.emall.pojo.User;
import us.supercheng.emall.service.IProductService;
import us.supercheng.emall.service.IUserService;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/product")
public class ProductController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(Integer categoryId, String keyword, HttpSession session,
                                         @RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                         @RequestParam(defaultValue = "") String orderBy) {
        User user = this.iUserService.getCurrentUser(session);
        if (user != null) {
            PageInfo pageInfo = this.iProductService.findProductsByKeywordsOrCategoryId(pageNum, pageSize, keyword, categoryId, orderBy);
            return ServerResponse.createServerResponseSuccess(pageInfo);
        }
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<Product> detail(Integer productId, HttpSession session) {
        User user = this.iUserService.getCurrentUser(session);
        if (user != null) {
            Product product = this.iProductService.findProductById(productId);
            if (product != null) {
                if (product.getStatus() == Const.ProductConst.PRODUCT_STATUS_1) {
                    return ServerResponse.createServerResponseSuccess(product);
                } else {
                    return ServerResponse.createServerResponseError("Such ProductID: " + productId + " is not Available");
                }
            } else {
                return ServerResponse.createServerResponseError("No Such ProductID: " + productId);
            }
        }
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }
}