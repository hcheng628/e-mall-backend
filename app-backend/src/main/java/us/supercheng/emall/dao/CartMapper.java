package us.supercheng.emall.dao;

import org.apache.ibatis.annotations.Param;
import us.supercheng.emall.pojo.Cart;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);
    int insert(Cart record);
    int insertSelective(Cart record);
    Cart selectByPrimaryKey(Integer id);
    int updateByPrimaryKeySelective(Cart record);
    int updateByPrimaryKey(Cart record);
    List<Cart> selectCartsByUserId(Integer userId);
    Cart selectByProductIdAndUserId(@Param("productId") Integer productId, @Param("userId") Integer userId);
    int deleteCartsByProductIds(@Param("productIds") String productIds);
    int selectAllCartProducts(Integer userId);
}