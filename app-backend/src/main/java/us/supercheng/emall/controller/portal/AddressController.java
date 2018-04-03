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
import us.supercheng.emall.pojo.Shipping;
import us.supercheng.emall.pojo.User;
import us.supercheng.emall.service.IShippingService;
import us.supercheng.emall.service.IUserService;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/shipping/")
public class AddressController {
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private IShippingService shippingService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<Shipping> add(Shipping shipping, HttpSession session) {
        logger.info("Enter add shippingId: " + shipping.getId());
        User user = this.userService.getCurrentUser(session);
        user = new User();
        user.setId(1);
        if (user != null) {
            logger.info("Exit add");
            return this.shippingService.add(user.getId(), shipping);
        }
        logger.error("Exit add --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("del.do")
    @ResponseBody
    public ServerResponse<String> del(Integer shippingId, HttpSession session) {
        logger.info("Enter del shippingId: " + shippingId);
        User user = this.userService.getCurrentUser(session);
        user = new User();
        user.setId(1);
        if (user != null) {
            logger.info("Exit del");
            return this.shippingService.del(shippingId);
        }
        logger.error("Exit del --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<String> update(Shipping shipping, HttpSession session) {
        logger.info("Enter update shippingId: " + shipping.getId());
        User user = this.userService.getCurrentUser(session);
        user = new User();
        user.setId(1);
        if (shipping.getId() == null) {
            logger.error("Exit update --- " + ResponseCode.ILLEGAL_ARGUMENT.getDesc());
            return ServerResponse.createServerResponseError(ResponseCode.ILLEGAL_ARGUMENT.getCode() + ": "
                    + ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        if (user != null) {
            logger.info("Exit update");
            return this.shippingService.update(user.getId(), shipping);
        }
        logger.error("Exit update --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<Shipping> select(Integer shippingId, HttpSession session) {
        logger.info("Enter select shippingId: " + shippingId);
        User user = this.userService.getCurrentUser(session);
        user = new User();
        user.setId(1);
        if (user != null) {
            logger.info("Exit select");
            return this.shippingService.select(shippingId);
        }
        logger.error("Exit select --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpSession session,
                                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        logger.info("Enter list pageNum: " + pageNum + " pageSize: " + pageSize);
        User user = this.userService.getCurrentUser(session);
        user = new User();
        user.setId(1);
        if (user != null) {
            logger.info("Exit list");
            return this.shippingService.list(pageNum, pageSize, user.getId());
        }
        logger.error("Exit list --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }
}