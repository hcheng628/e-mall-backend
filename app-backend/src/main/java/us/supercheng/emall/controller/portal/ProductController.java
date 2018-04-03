package us.supercheng.emall.controller.portal;

import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

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
        logger.info("Enter list pageNum: " + pageNum + " pageSize: "+ pageSize + " orderBy: " +
                orderBy + " keyword: " + keyword + " categoryId: " + categoryId);
        User user = this.iUserService.getCurrentUser(session);
        if (user != null) {
            PageInfo pageInfo = this.iProductService.findProductsByKeywordsOrCategoryId(pageNum, pageSize, keyword, categoryId, orderBy);
            logger.info("Exit list");
            return ServerResponse.createServerResponseSuccess(pageInfo);
        }
        logger.info("Exit list --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<Product> detail(Integer productId, HttpSession session) {
        logger.info("Enter detail productId: " + productId);
        User user = this.iUserService.getCurrentUser(session);
        if (user != null) {
            Product product = this.iProductService.findProductById(productId);
            if (product != null) {
                if (product.getStatus() == Const.ProductConst.PRODUCT_STATUS_1) {
                    logger.info("Exit detail");
                    return ServerResponse.createServerResponseSuccess(product);
                } else {
                    logger.error("Exit detail --- Such ProductID: " + productId + " is not Available");
                    return ServerResponse.createServerResponseError("Such ProductID: " + productId + " is not Available");
                }
            } else {
                logger.error("Exit detail --- No Such ProductID: " + productId);
                return ServerResponse.createServerResponseError("No Such ProductID: " + productId);
            }
        }
        logger.error("Exit detail --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }
}