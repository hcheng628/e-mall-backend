package us.supercheng.emall.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import us.supercheng.emall.common.ResponseCode;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.pojo.User;
import us.supercheng.emall.service.IOrderService;
import us.supercheng.emall.service.IUserService;
import us.supercheng.emall.vo.OrderCartVo;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/order/")
public class OrderController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;


    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(Integer shippingId, HttpSession session) {
        User currentUser = this.iUserService.getCurrentUser(session);
        currentUser = new User();
        currentUser.setId(1);
        if (currentUser != null) {
            return this.iOrderService.create(currentUser.getId(), shippingId);
        }
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServerResponse<OrderCartVo> getOrderCartProduct(HttpSession session) {
        User currentUser = this.iUserService.getCurrentUser(session);
        currentUser = new User();
        currentUser.setId(1);
        if (currentUser != null) {
            return this.iOrderService.getOrderCartProduct(currentUser.getId());
        }
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(@RequestParam(value = "pageSize", defaultValue = "1") Integer pageSize,
                                              @RequestParam(value = "pageNum", defaultValue = "10") Integer pageNum,
                                              HttpSession session) {
        return null;
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(Integer orderNum, HttpSession session) {
        return null;
    }

    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse<Map> pay(Long orderNo, HttpSession session) {
        User currentUser = this.iUserService.getCurrentUser(session);
        currentUser = new User();
        currentUser.setId(1);
        if (currentUser != null) {
            return this.iOrderService.pay(orderNo, currentUser.getId());
        }
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }
}