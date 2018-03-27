package us.supercheng.emall.service;

import com.github.pagehelper.PageInfo;
import org.aspectj.weaver.ast.Or;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.vo.OrderCartVo;
import us.supercheng.emall.vo.OrderVo;
import java.util.Map;

public interface IOrderService {
    ServerResponse<Map> pay(Long orderNo, Integer userId);
    ServerResponse alipayCallback(Map<String, String> params);
    ServerResponse queryOrderPayStatus(Long orderNo, Integer userId);
    ServerResponse<OrderVo> create(Integer userId, Integer shippingId);
    ServerResponse<Boolean> cancel(Long orderNo, Integer userId);
    ServerResponse<OrderCartVo> getOrderCart(Integer userId);
    ServerResponse<OrderVo> detail(Long orderNo, Integer userId);
    ServerResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize);

    ServerResponse<PageInfo> listAdmin(Integer pageNum, Integer pageSize);
    ServerResponse<OrderVo> detailAdmin(Long orderNo);
    ServerResponse<OrderVo> searchAdmin(Long orderNo, Integer pageNum, Integer pageSize);
    ServerResponse<String> shipGoods(Long orderNo);
}