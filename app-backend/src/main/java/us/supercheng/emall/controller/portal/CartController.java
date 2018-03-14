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

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<String> add(Integer productId, Integer count, HttpSession session) {
        User user = this.iUserService.getCurrentUser(session);
        if (user != null) {
            Map map = this.iCartService.add(productId , count, user.getId());

        }
        return null;
    }

}
