package us.supercheng.emall.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import us.supercheng.emall.common.Const;
import us.supercheng.emall.common.ResponseCode;
import us.supercheng.emall.common.ServerResponse;
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
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> user =  this.iUserService.login(username, password);
        if (user.getStatus() == ResponseCode.SUCCESS.getCode()) {
            session.setAttribute(Const.CURRENT_USER, user.getData());
            return user;
        }
        return user;
    }

    @RequestMapping(value = "check_valid.do", method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> check_valid(String inText, String acctType) {
        return this.iUserService.check_valid(inText, acctType);
    }

    @RequestMapping(value = "register.do", method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User newUser) {
        return this.iUserService.register(newUser);
    }

    @RequestMapping(value = "logout.do", method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session) {
        if (session.getAttribute(Const.CURRENT_USER) != null) {
            session.removeAttribute(Const.CURRENT_USER);
        }
        return ServerResponse.createServerResponseSuccess("Logout Success");
    }

    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createServerResponseError("No Login User Found");
        }
        return ServerResponse.createServerResponseSuccess(user);
    }

    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username) {
        return this.iUserService.getUserQuestion(username);
    }

    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
         return this.iUserService.checkQuestionAnswer(username, question, answer);
    }
}