package us.supercheng.emall.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import us.supercheng.emall.common.Const;
import us.supercheng.emall.common.ResponseCode;
import us.supercheng.emall.common.ServerRsponse;
import us.supercheng.emall.pojo.User;
import us.supercheng.emall.service.IUserService;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do", method=RequestMethod.POST)
    @ResponseBody
    public ServerRsponse<User> login(String username, String password, HttpSession session) {
        ServerRsponse<User> user =  iUserService.login(username, password);
        if (user.getStatus() == ResponseCode.SUCCESS.getCode()) {
            session.setAttribute(Const.CURRENT_USER, user.getData());
            return user;
        }
        return user;
    }
}