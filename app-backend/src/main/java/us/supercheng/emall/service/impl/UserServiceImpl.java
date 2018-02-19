package us.supercheng.emall.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.supercheng.emall.common.ResponseCode;
import us.supercheng.emall.common.ServerRsponse;
import us.supercheng.emall.dao.UserMapper;
import us.supercheng.emall.pojo.User;
import us.supercheng.emall.service.IUserService;

@Service("iUserService")
public class UserServiceImpl implements IUserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerRsponse<User> login(String username, String password) {
        int resultCount = this.userMapper.checkUsername(username);
        if (resultCount > 0) {
            User user = this.userMapper.selectLogin(username, password);
            if (user == null) {
                return ServerRsponse.createServerResponse(ResponseCode.ERROR.getCode(), "Password Not Match");
            } else {
                user.setPassword(StringUtils.EMPTY);
                return ServerRsponse.createServerResponseSuccess(user);
            }
        }
        return ServerRsponse.createServerResponse(ResponseCode.ERROR.getCode(), "No Such Username");
    }
}