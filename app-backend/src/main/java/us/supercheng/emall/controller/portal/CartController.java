package us.supercheng.emall.controller.portal;

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

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICartService iCartService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartProductVo> list(HttpSession session) {
        User user = this.iUserService.getCurrentUser(session);
        if (user == null) {
           return ServerResponse.createServerResponseSuccess(this.iCartService.list(21));
        }
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("upsert.do")
    @ResponseBody
    public ServerResponse<String> upsert(Integer productId, Integer count, HttpSession session) {
        User user = this.iUserService.getCurrentUser(session);
        // Test Only
        user = new User();
        user.setId(21);
        if (user != null) {
            Map map = this.iCartService.upsert(productId , count, user.getId());
            if (map.get("count").toString().equalsIgnoreCase("0")) {
                return ServerResponse.createServerResponseError("No Such Product ProductID: " + productId);
            }
            if (map.get("countDb").toString().equalsIgnoreCase("1")) {
                return ServerResponse.createServerResponseSuccess("Add Product to Cart Success");
            }
        }
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse<CartProductVo> deleteCartProducts(String productIds, HttpSession session) {
        User user = this.iUserService.getCurrentUser(session);
        // Test Only
        user = new User();
        user.setId(21);
        if (user != null) {
            int count = this.iCartService.delete(productIds);
            if (count < 0) {
                return ServerResponse.createServerResponseError("Delete Cart Product(s) Failed");
            } else {
                return ServerResponse.createServerResponseSuccess(this.iCartService.list(user.getId()));
            }
        }
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<CartProductVo> selectCartProduct(Integer productId, HttpSession session) {
        User user = this.iUserService.getCurrentUser(session);
        user = new User();
        user.setId(21);
        if (user != null) {
            int count = this.iCartService.select(productId,user.getId());
            if (count < 0) {
                return ServerResponse.createServerResponseError("No Such Product ProductID: " + productId + " in Cart");
            } else {
                return ServerResponse.createServerResponseSuccess(this.iCartService.list(user.getId()));
            }
        }
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("selectAll.do")
    @ResponseBody
    public ServerResponse<CartProductVo> selectAllCartProducts(HttpSession session) {
        User user = this.iUserService.getCurrentUser(session);
        user = new User();
        user.setId(21);
        if (user != null) {
            this.iCartService.selectAll(user.getId());
            return ServerResponse.createServerResponseSuccess(this.iCartService.list(user.getId()));
        }
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("unselect.do")
    @ResponseBody
    public ServerResponse<CartProductVo> unselectCartProduct(Integer productId, HttpSession session) {
        User user = this.iUserService.getCurrentUser(session);
        user = new User();
        user.setId(21);
        if (user != null) {
            int count = this.iCartService.unselect(productId,user.getId());
            if (count < 0) {
                return ServerResponse.createServerResponseError("No Such Product ProductID: " + productId + " in Cart");
            } else {
                return ServerResponse.createServerResponseSuccess(this.iCartService.list(user.getId()));
            }
        }
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("unselectAll.do")
    @ResponseBody
    public ServerResponse<CartProductVo> unselectAllCartProducts(HttpSession session) {
        User user = this.iUserService.getCurrentUser(session);
        user = new User();
        user.setId(21);
        if (user != null) {
            this.iCartService.unselectAll(user.getId());
            return ServerResponse.createServerResponseSuccess(this.iCartService.list(user.getId()));
        }
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

}
