package us.supercheng.emall.dao;

import org.apache.ibatis.annotations.Param;
import us.supercheng.emall.pojo.Shipping;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    List<Shipping> selectShippingsByUserId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    int updateByPrimaryKeyAndUserIdSelective(Shipping shipping);
}