package us.supercheng.emall.service;

import com.github.pagehelper.PageInfo;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.vo.OrderCartVo;
import us.supercheng.emall.vo.OrderVo;
import java.util.Map;

public interface IOrderService {
    ServerResponse<OrderVo> create(Integer userId, Integer shippingId);
    ServerResponse<OrderCartVo> getOrderCart(Integer userId);
    ServerResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize);
    ServerResponse<PageInfo> listAdmin(Integer pageNum, Integer pageSize);
    ServerResponse<OrderVo> detail(Long orderNo, Integer userId);
    ServerResponse<OrderVo> detailAdmin(Long orderNo);
    ServerResponse<String> shipGoods(Long orderNo);
    ServerResponse<Map> pay(Long orderNo, Integer userId);
}