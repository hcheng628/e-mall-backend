package us.supercheng.emall.service;

import us.supercheng.emall.common.ServerResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface IOrderService {
    ServerResponse<Map> create(Integer userId, Integer shippingId, HttpServletRequest request);
}