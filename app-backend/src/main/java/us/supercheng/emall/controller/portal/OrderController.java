package us.supercheng.emall.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.service.IOrderService;
import us.supercheng.emall.service.IUserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/order/")
public class OrderController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;


    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(Integer shippingId, HttpServletRequest request) {
        return iOrderService.create(21, 30, request);
    }

    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpSession session) {
        return null;
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
}