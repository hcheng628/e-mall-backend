package us.supercheng.emall.controller.portal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import us.supercheng.emall.common.ResponseCode;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.pojo.User;
import us.supercheng.emall.service.ICartService;
import us.supercheng.emall.service.IUserService;
import us.supercheng.emall.vo.CartProductVo;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/cart/")
public class CartController {
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICartService iCartService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartProductVo> list(HttpSession session) {
        logger.info("Enter list");
        User user = this.iUserService.getCurrentUser(session);
        user = new User();
        user.setId(21);
        if (user != null) {
            logger.info("Exit list");
            return ServerResponse.createServerResponseSuccess(this.iCartService.list(21));
        }
        logger.error("Exit list --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("upsert.do")
    @ResponseBody
    public ServerResponse<String> upsert(Integer productId, Integer count, HttpSession session) {
        logger.info("Enter upsert productId: " + productId + " count: " + count);
        User user = this.iUserService.getCurrentUser(session);
        user = new User();
        user.setId(21);
        if (user != null) {
            Map map = this.iCartService.upsert(productId, count, user.getId());
            if (map.get("count").toString().equalsIgnoreCase("0")) {
                logger.error("Exit upsert --- No Such Product ProductID: " + productId);
                return ServerResponse.createServerResponseError("No Such Product ProductID: " + productId);
            }
            if (map.get("countDb").toString().equalsIgnoreCase("1")) {
                logger.info("Exit upsert");
                return ServerResponse.createServerResponseSuccess("Add Product to Cart Success");
            }
        }
        logger.error("Exit upsert --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse<CartProductVo> deleteCartProducts(String productIds, HttpSession session) {
        logger.info("Enter deleteCartProducts productIds: " + productIds);
        User user = this.iUserService.getCurrentUser(session);
        // Test Only
        user = new User();
        user.setId(21);
        if (user != null) {
            int count = this.iCartService.delete(productIds);
            if (count < 0) {
                logger.error("Exit deleteCartProducts --- Delete Cart Product(s) Failed");
                return ServerResponse.createServerResponseError("Delete Cart Product(s) Failed");
            } else {
                logger.info("Exit deleteCartProducts");
                return ServerResponse.createServerResponseSuccess(this.iCartService.list(user.getId()));
            }
        }
        logger.error("Exit deleteCartProducts --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<CartProductVo> selectCartProduct(Integer productId, HttpSession session) {
        logger.info("Enter selectCartProduct productId: " + productId);
        User user = this.iUserService.getCurrentUser(session);
        user = new User();
        user.setId(21);
        if (user != null) {
            int count = this.iCartService.select(productId, user.getId());
            if (count < 0) {
                logger.error("Exit selectCartProduct --- No Such Product ProductID: " + productId + " in Cart");
                return ServerResponse.createServerResponseError("No Such Product ProductID: " + productId + " in Cart");
            } else {
                logger.info("Exit selectCartProduct");
                return ServerResponse.createServerResponseSuccess(this.iCartService.list(user.getId()));
            }
        }
        logger.error("Exit selectCartProduct --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("selectAll.do")
    @ResponseBody
    public ServerResponse<CartProductVo> selectAllCartProducts(HttpSession session) {
        logger.info("Enter selectAllCartProducts");
        User user = this.iUserService.getCurrentUser(session);
        user = new User();
        user.setId(21);
        if (user != null) {
            this.iCartService.selectAll(user.getId());
            logger.info("Exit selectAllCartProducts");
            return ServerResponse.createServerResponseSuccess(this.iCartService.list(user.getId()));
        }
        logger.error("Exit selectAllCartProducts --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("unselect.do")
    @ResponseBody
    public ServerResponse<CartProductVo> unselectCartProduct(Integer productId, HttpSession session) {
        logger.info("Enter unselectCartProduct productId:" + productId);
        User user = this.iUserService.getCurrentUser(session);
        user = new User();
        user.setId(21);
        if (user != null) {
            int count = this.iCartService.unselect(productId, user.getId());
            if (count < 0) {
                logger.error("Exit unselectCartProduct --- No Such Product ProductID: " + productId + " in Cart");
                return ServerResponse.createServerResponseError("No Such Product ProductID: " + productId + " in Cart");
            } else {
                logger.info("Exit unselectCartProduct");
                return ServerResponse.createServerResponseSuccess(this.iCartService.list(user.getId()));
            }
        }
        logger.error("Exit unselectCartProduct --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("unselectAll.do")
    @ResponseBody
    public ServerResponse<CartProductVo> unselectAllCartProducts(HttpSession session) {
        logger.info("Enter unselectAllCartProducts");
        User user = this.iUserService.getCurrentUser(session);
        user = new User();
        user.setId(21);
        if (user != null) {
            this.iCartService.unselectAll(user.getId());
            logger.info("Exit unselectAllCartProducts");
            return ServerResponse.createServerResponseSuccess(this.iCartService.list(user.getId()));
        }
        logger.error("Exit unselectAllCartProducts --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("get_cart_total_items.do")
    @ResponseBody
    public ServerResponse getCartTotalItems(HttpSession session) {
        logger.info("Enter getCartTotalItems");
        User user = this.iUserService.getCurrentUser(session);
        user = new User();
        user.setId(21);
        if (user != null) {
            logger.info("Exit getCartTotalItems");
            return ServerResponse.createServerResponseSuccess(this.iCartService.getCartTotalItems(user.getId()));
        }
        logger.error("Exit getCartTotalItems --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }
}