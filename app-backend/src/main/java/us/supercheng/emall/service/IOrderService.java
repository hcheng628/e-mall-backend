package us.supercheng.emall.service;

import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.vo.OrderCartVo;
import us.supercheng.emall.vo.OrderVo;
import java.util.Map;

public interface IOrderService {
    ServerResponse<OrderVo> create(Integer userId, Integer shippingId);
    ServerResponse<OrderCartVo> getOrderCartProduct(Integer userId);
    ServerResponse<Map> pay(Long orderNo, Integer userId);
}