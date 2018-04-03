package us.supercheng.emall.controller.portal;

import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/order/")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;


    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(Integer shippingId, HttpSession session) {
        logger.info("Enter create shippingId: " + shippingId);
        User currentUser = this.iUserService.getCurrentUser(session);
        currentUser = new User();
        currentUser.setId(1);
        if (currentUser != null) {
            logger.info("Exit create");
            return this.iOrderService.create(currentUser.getId(), shippingId);
        }
        logger.error("Exit create --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse cancel(Long orderNo, HttpSession session) {
        logger.info("Enter cancel orderNo: " + orderNo);
        User currentUser = this.iUserService.getCurrentUser(session);
        currentUser = new User();
        currentUser.setId(1);
        if (currentUser != null) {
            logger.info("Exit cancel");
            return this.iOrderService.cancel(orderNo, currentUser.getId());
        }
        logger.error("Exit cancel --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }


    @RequestMapping("get_order_cart.do")
    @ResponseBody
    public ServerResponse<OrderCartVo> getOrderCart(HttpSession session) {
        logger.info("Enter getOrderCart");
        User currentUser = this.iUserService.getCurrentUser(session);
        currentUser = new User();
        currentUser.setId(1);
        if (currentUser != null) {
            logger.info("Exit getOrderCart");
            return this.iOrderService.getOrderCart(currentUser.getId());
        }
        logger.error("Exit getOrderCart --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "pageSize", defaultValue = "1") Integer pageSize,
                                         @RequestParam(value = "pageNum", defaultValue = "10") Integer pageNum,
                                         HttpSession session) {
        logger.info("Enter list pageNum: " + pageNum + " pageSize: " + pageSize);
        User currentUser = this.iUserService.getCurrentUser(session);
        currentUser = new User();
        currentUser.setId(1);
        if (currentUser != null) {
            logger.info("Exit list");
            return this.iOrderService.list(currentUser.getId(), pageNum, pageSize);
        }
        logger.error("Exit list --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(Long orderNo, HttpSession session) {
        logger.info("Enter detail orderNo: " + orderNo);
        User currentUser = this.iUserService.getCurrentUser(session);
        currentUser = new User();
        currentUser.setId(1);
        if (currentUser != null) {
            logger.info("Exit detail");
            return this.iOrderService.detail(orderNo, currentUser.getId());
        }
        logger.error("Exit detail --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());    }

    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse<Map> pay(Long orderNo, HttpSession session) {
        logger.info("Enter pay orderNo: " + orderNo);
        User currentUser = this.iUserService.getCurrentUser(session);
        currentUser = new User();
        currentUser.setId(1);
        if (currentUser != null) {
            logger.info("Exit pay");
            return this.iOrderService.pay(orderNo, currentUser.getId());
        }
        logger.error("Exit pay --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Map> queryOrderPayStatus(Long orderNo, HttpSession session) {
        logger.info("Enter queryOrderPayStatus");
        User currentUser = this.iUserService.getCurrentUser(session);
        currentUser = new User();
        currentUser.setId(1);
        if (currentUser != null) {
            logger.info("Exit queryOrderPayStatus");
            return this.iOrderService.queryOrderPayStatus(orderNo, currentUser.getId());
        }
        logger.error("Exit queryOrderPayStatus --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public String alipayCallback(HttpServletRequest request) {
        logger.info("Enter alipayCallback");
        // Enumeration<String> paraNames = request.getHeaderNames();
        Map<String, String> map = new HashMap<>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            map.put(name, valueStr);
        }
        this.iOrderService.alipayCallback(map);
        logger.info("Exit alipayCallback");
        return "success";
    }
}