package us.supercheng.emall.controller.backend;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import us.supercheng.emall.common.Const;
import us.supercheng.emall.common.ResponseCode;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.pojo.User;
import us.supercheng.emall.service.IOrderService;
import us.supercheng.emall.service.IUserService;
import us.supercheng.emall.vo.OrderVo;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/order/")
public class ManageOrderController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;


    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                         HttpSession session) {
        User currentUser = this.iUserService.getCurrentUser(session);
        currentUser = new User();
        currentUser.setId(1);
        currentUser.setRole(Const.ROLE_ADMIN);
        if (currentUser != null) {
            if (currentUser.getRole() == Const.ROLE_ADMIN) {
                return this.iOrderService.listAdmin(pageNum, pageSize);
            }
            return ServerResponse.createServerResponse(ResponseCode.ERROR.getCode(), "Only Admin Can Access");
        }
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }


    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> search(Long orderNo,
                                          @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                          @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                          HttpSession session) {
        User currentUser = this.iUserService.getCurrentUser(session);
        currentUser = new User();
        currentUser.setId(1);
        currentUser.setRole(Const.ROLE_ADMIN);
        if (currentUser != null) {
            if (currentUser.getRole() == Const.ROLE_ADMIN) {
                return this.iOrderService.searchAdmin(orderNo, pageNum, pageSize);
            }
            return ServerResponse.createServerResponse(ResponseCode.ERROR.getCode(), "Only Admin Can Access");
        }
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> detail(Long orderNo, HttpSession session) {
        User currentUser = this.iUserService.getCurrentUser(session);
        currentUser = new User();
        currentUser.setId(1);
        currentUser.setRole(Const.ROLE_ADMIN);
        if (currentUser != null) {
            if (currentUser.getRole() == Const.ROLE_ADMIN) {
                return this.iOrderService.detailAdmin(orderNo);
            }
            return ServerResponse.createServerResponse(ResponseCode.ERROR.getCode(), "Only Admin Can Access");
        }
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("ship_goods.do")
    @ResponseBody
    public ServerResponse<String> shipGoods(Long orderNo, HttpSession session) {
        User currentUser = this.iUserService.getCurrentUser(session);
        currentUser = new User();
        currentUser.setId(1);
        currentUser.setRole(Const.ROLE_ADMIN);
        if (currentUser != null) {
            if (currentUser.getRole() == Const.ROLE_ADMIN) {
                return this.iOrderService.shipGoods(orderNo);
            }
            return ServerResponse.createServerResponse(ResponseCode.ERROR.getCode(), "Only Admin Can Access");
        }
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }
}
