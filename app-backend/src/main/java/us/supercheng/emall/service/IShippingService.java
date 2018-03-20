package us.supercheng.emall.service;

import com.github.pagehelper.PageInfo;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.pojo.Shipping;

public interface IShippingService {
    ServerResponse<Shipping> add(Integer userId, Shipping shipping);
    ServerResponse<String> del(Integer shippingId);
    ServerResponse<String> update(Integer userId, Shipping shipping);
    ServerResponse<Shipping> select(Integer shippingId);
    ServerResponse<PageInfo> list(Integer startPage, Integer pageSize, Integer userId);
}