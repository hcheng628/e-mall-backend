package us.supercheng.emall.service;

import us.supercheng.emall.vo.CartProductVo;
import java.util.Map;

public interface ICartService {
    CartProductVo list(Integer userId);
    Map add(Integer productId, Integer count, Integer userId);
}