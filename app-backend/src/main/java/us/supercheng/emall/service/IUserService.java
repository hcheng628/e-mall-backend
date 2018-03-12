package us.supercheng.emall.service;

import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.pojo.User;

import javax.servlet.http.HttpSession;

public interface IUserService {
    ServerResponse<User> login(String username, String password);

    ServerResponse<String> check_valid(String inText, String acctType);

    ServerResponse<String> register(User inUser);

    ServerResponse<String> getUserQuestion(String username);

    ServerResponse<String> checkQuestionAnswer(String username, String question, String answer);

    ServerResponse<String> resetPassword(String username, String passwordNew, String forgetToken);

    ServerResponse<String> resetPassword(Integer id, String newPassword);

    ServerResponse<User> updateInfo(Integer id, String email, String phone, String question,String answer);

    User getCurrentUser(HttpSession session);

    ServerResponse checkAdminUser(User user);
}