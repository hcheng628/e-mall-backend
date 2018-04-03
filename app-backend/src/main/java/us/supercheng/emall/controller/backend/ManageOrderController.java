package us.supercheng.emall.controller.backend;

import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(ManageOrderController.class);

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;


    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                         HttpSession session) {
        logger.info("Enter list pageNum: " + pageNum + " pageSize: " + pageSize);
        User currentUser = this.iUserService.getCurrentUser(session);
        currentUser = new User();
        currentUser.setId(1);
        currentUser.setRole(Const.ROLE_ADMIN);
        if (currentUser != null) {
            if (currentUser.getRole() == Const.ROLE_ADMIN) {
                logger.info("Exit list");
                return this.iOrderService.listAdmin(pageNum, pageSize);
            }
            logger.error("Exit list --- Not Admin User");
            return ServerResponse.createServerResponse(ResponseCode.ERROR.getCode(), "Only Admin Can Access");
        }
        logger.error("Exit list --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }


    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> search(Long orderNo,
                                          @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                          @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                          HttpSession session) {
        logger.info("Enter search orderNo: " + orderNo + " pageNum: " + pageNum + " pageSize: " + pageSize);
        User currentUser = this.iUserService.getCurrentUser(session);
        currentUser = new User();
        currentUser.setId(1);
        currentUser.setRole(Const.ROLE_ADMIN);
        if (currentUser != null) {
            if (currentUser.getRole() == Const.ROLE_ADMIN) {
                logger.info("Exit search");
                return this.iOrderService.searchAdmin(orderNo, pageNum, pageSize);
            }
            logger.error("Exit search --- Only Admin Can Access");
            return ServerResponse.createServerResponse(ResponseCode.ERROR.getCode(), "Only Admin Can Access");
        }
        logger.error("Exit search --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> detail(Long orderNo, HttpSession session) {
        logger.info("Enter detail orderNo: " + orderNo);
        User currentUser = this.iUserService.getCurrentUser(session);
        currentUser = new User();
        currentUser.setId(1);
        currentUser.setRole(Const.ROLE_ADMIN);
        if (currentUser != null) {
            if (currentUser.getRole() == Const.ROLE_ADMIN) {
                logger.info("Exit detail");
                return this.iOrderService.detailAdmin(orderNo);
            }
            logger.error("Exit detail --- Only Admin Can Access");
            return ServerResponse.createServerResponse(ResponseCode.ERROR.getCode(), "Only Admin Can Access");
        }
        logger.error("Exit detail --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }

    @RequestMapping("ship_goods.do")
    @ResponseBody
    public ServerResponse<String> shipGoods(Long orderNo, HttpSession session) {
        logger.info("Enter shipGoods orderNo: " + orderNo);
        User currentUser = this.iUserService.getCurrentUser(session);
        currentUser = new User();
        currentUser.setId(1);
        currentUser.setRole(Const.ROLE_ADMIN);
        if (currentUser != null) {
            if (currentUser.getRole() == Const.ROLE_ADMIN) {
                logger.info("Exit shipGoods");
                return this.iOrderService.shipGoods(orderNo);
            }
            logger.error("Exit shipGoods --- Only Admin Can Access");
            return ServerResponse.createServerResponse(ResponseCode.ERROR.getCode(), "Only Admin Can Access");
        }
        logger.error("Exit shipGoods --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
        return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
    }
}
