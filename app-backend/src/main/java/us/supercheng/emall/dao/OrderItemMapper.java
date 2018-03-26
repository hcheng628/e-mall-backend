package us.supercheng.emall.dao;

import org.apache.ibatis.annotations.Param;
import us.supercheng.emall.pojo.OrderItem;
import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);
    int insert(OrderItem record);
    int insertSelective(OrderItem record);
    OrderItem selectByPrimaryKey(Integer id);
    int updateByPrimaryKeySelective(OrderItem record);
    int updateByPrimaryKey(OrderItem record);
    List<OrderItem> selectByOrderNoAndUserId(@Param("orderNo") Long orderNo, @Param("userId") Integer userId);
    List<OrderItem> selectByOrderNo(@Param("orderNo") Long orderNo);
    int insertBatch(@Param("orderItems") List<OrderItem> orderItems);
}