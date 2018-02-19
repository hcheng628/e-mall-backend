package us.supercheng.emall.service;

import us.supercheng.emall.common.ServerRsponse;
import us.supercheng.emall.pojo.User;

public interface IUserService {
    public ServerRsponse<User> login(String username, String password);
}