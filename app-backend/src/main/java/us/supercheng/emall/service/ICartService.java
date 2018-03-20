package us.supercheng.emall.service;

import us.supercheng.emall.vo.CartProductVo;
import java.util.Map;

public interface ICartService {
    CartProductVo list(Integer userId);
    Map upsert(Integer productId, Integer count, Integer userId);
    int delete(String productIds);
    int select(Integer productId, Integer userId);
    int unselect(Integer productId, Integer userId);
    void selectAll(Integer userId);
    void unselectAll(Integer userId);
    int getCartTotalItems(Integer userId);
}