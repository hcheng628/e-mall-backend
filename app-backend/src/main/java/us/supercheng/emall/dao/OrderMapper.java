package us.supercheng.emall.dao;

import org.apache.ibatis.annotations.Param;
import us.supercheng.emall.pojo.Order;
import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);
    int insert(Order record);
    int insertSelective(Order record);
    Order selectByPrimaryKey(Integer id);
    int updateByPrimaryKeySelective(Order record);
    int updateByPrimaryKey(Order record);
    Order selectByOrderNoAndUserId(@Param("orderNo") Long orderNo, @Param("userId") Integer userId);
    List<Order> selectOrdersByUserId(Integer userId);
}