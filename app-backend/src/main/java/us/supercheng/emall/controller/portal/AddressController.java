package us.supercheng.emall.controller.portal;

import com.github.pagehelper.PageInfo;
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

    @Autowired
    private IUserService userService;

    @Autowired
    private IShippingService shippingService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<Shipping> add(Shipping shipping, HttpSession session) {
        User user = this.userService.getCurrentUser(session);
        user = new User();
        user.setId(1);
        if (user != null) {
            return this.shippingService.add(user.getId(), shipping);
        }
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("del.do")
    @ResponseBody
    public ServerResponse<String> del(Integer shippingId, HttpSession session) {
        User user = this.userService.getCurrentUser(session);
        user = new User();
        user.setId(1);
        if (user != null) {
            return this.shippingService.del(shippingId);
        }
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<String> update(Shipping shipping, HttpSession session) {
        User user = this.userService.getCurrentUser(session);
        user = new User();
        user.setId(1);
        if (shipping.getId() == null) {
            return ServerResponse.createServerResponseError(ResponseCode.ILLEGAL_ARGUMENT.getCode() + ": "
                    + ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        if (user != null) {
            return this.shippingService.update(user.getId(), shipping);
        }
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<Shipping> select(Integer shippingId, HttpSession session) {
        User user = this.userService.getCurrentUser(session);
        user = new User();
        user.setId(1);
        if (user != null) {
            return this.shippingService.select(shippingId);
        }
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpSession session,
                                         @RequestParam(value = "startPage", defaultValue = "1") Integer startPage,
                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        User user = this.userService.getCurrentUser(session);
        user = new User();
        user.setId(1);
        if (user != null) {
            return this.shippingService.list(startPage, pageSize, user.getId());
        }
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }
}