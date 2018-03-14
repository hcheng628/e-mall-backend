package us.supercheng.emall.service;

import us.supercheng.emall.vo.CartProductVo;
import java.util.Map;

public interface ICartService {
    CartProductVo list(Integer userId);
    Map upsert(Integer productId, Integer count, Integer userId);
    int delete(String productIds);
}