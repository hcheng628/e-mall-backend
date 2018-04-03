package us.supercheng.emall.controller.portal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        logger.info("Enter login username: " + username + " password: " + password);
        ServerResponse<User> user = this.iUserService.login(username, password);
        if (user.getStatus() == ResponseCode.SUCCESS.getCode()) {
            session.setAttribute(Const.CURRENT_USER, user.getData());
            logger.info("Exit login");
            return user;
        }
        logger.error("Exit login --- Fail");
        return user;
    }

    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> check_valid(String inText, String acctType) {
        logger.info("Enter check_valid inText: " + inText + " acctType: " + acctType);
        logger.info("Exit check_valid");
        return this.iUserService.check_valid(inText, acctType);
    }

    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User newUser) {
        logger.info("Enter register newUsername: " + newUser.getUsername());
        logger.info("Exit register");
        return this.iUserService.register(newUser);
    }

    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session) {
        logger.info("Enter logout");
        if (session.getAttribute(Const.CURRENT_USER) != null) {
            session.removeAttribute(Const.CURRENT_USER);
        }
        logger.info("Exit logout");
        return ServerResponse.createServerResponseSuccess("Logout Success");
    }

    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session) {
        logger.info("Enter getUserInfo");
        User user = this.iUserService.getCurrentUser(session);
        if (user == null) {
            logger.error("Exit getUserInfo --- " + ResponseCode.LOGIN_REQUIRED.getDesc());
            return ServerResponse.createServerResponse(ResponseCode.LOGIN_REQUIRED.getCode(), ResponseCode.LOGIN_REQUIRED.getDesc());
        }
        logger.info("Exit getUserInfo");
        return ServerResponse.createServerResponseSuccess(user);
    }

    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username) {
        logger.info("Enter forgetGetQuestion username: " + username);
        logger.info("Exit forgetGetQuestion");
        return this.iUserService.getUserQuestion(username);
    }

    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        logger.info("Enter forgetCheckAnswer username: " + username + " question: " + question + " answer: " + answer);
        logger.info("Exit forgetCheckAnswer");
        return this.iUserService.checkQuestionAnswer(username, question, answer);
    }

    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username, String newPassword, String token) {
        logger.info("Enter forgetResetPassword username: " + username + " newPassword: " + newPassword + " token: " + token);
        logger.info("Exit forgetResetPassword");
        return this.iUserService.resetPassword(username, newPassword, token);
    }

    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(String newPassword, HttpSession session) {
        logger.info("Enter resetPassword newPassword: " + newPassword);
        User user = this.iUserService.getCurrentUser(session);
        if (user != null) {
            logger.info("Exit resetPassword");
            return this.iUserService.resetPassword(user.getId(), newPassword);
        }
        logger.error("Exit resetPassword --- Login Required");
        return ServerResponse.createServerResponseError("Login Required");
    }

    @RequestMapping(value = "update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInfo(String email, String phone, String question, String answer, HttpSession session) {
        logger.info("Enter updateInfo email: " + email + " phone: " + phone + " question: " + question + " answer: " + answer);
        User user = iUserService.getCurrentUser(session);
        if (user != null) {
            logger.info("Exit updateInfo");
            return this.iUserService.updateInfo(user.getId(), email, phone, question, answer);
        }
        logger.error("Exit updateInfo --- Login Required");
        return ServerResponse.createServerResponseError("Login Required");
    }
}