package us.supercheng.emall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.supercheng.emall.common.ServerResponse;
import us.supercheng.emall.dao.ShippingMapper;
import us.supercheng.emall.pojo.Shipping;
import us.supercheng.emall.service.IShippingService;

import java.util.Date;
import java.util.List;

@Service("iAddressService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse<Shipping> add(Integer userId, Shipping shipping) {
        shipping.setId(null);
        shipping.setUserId(userId);
        shipping.setCreateTime(new Date()); // To trigger Mybstis using now()
        int count = this.shippingMapper.insertSelective(shipping);
        if (count <0 ) {
            return ServerResponse.createServerResponseError("Add Shipping Address Failed");
        }
        return ServerResponse.createServerResponseSuccess(shipping);
    }

    public ServerResponse<String> del(Integer shippingId) {
        int count = this.shippingMapper.deleteByPrimaryKey(shippingId);
        if (count <0 ) {
            return ServerResponse.createServerResponseError("Delete Shipping Address AddressID: " + shippingId + " Failed");
        }
        return ServerResponse.createServerResponseError("Delete Shipping Address AddressID: " + shippingId + " Success");
    }

    public ServerResponse<String> update(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        shipping.setUpdateTime(new Date()); // To trigger Mybstis using now()
        int count = this.shippingMapper.updateByPrimaryKeyAndUserIdSelective(shipping);
        if (count > 0 ) {
            return ServerResponse.createServerResponseError("Update Shipping Address AddressID: " + shipping.getId() + " Success");
        }
        return ServerResponse.createServerResponseError("Update Shipping Address AddressID: " + shipping.getId() + " Failed");
    }

    public ServerResponse<Shipping> select(Integer shippingId) {
        Shipping shipping = this.shippingMapper.selectByPrimaryKey(shippingId);
        if (shipping != null) {
            return ServerResponse.createServerResponseSuccess(shipping);
        }
        return ServerResponse.createServerResponseError("No Such Shipping Address AddressID: " + shippingId);
    }

    public ServerResponse<PageInfo> list(Integer startPage, Integer pageSize, Integer userId) {
        PageHelper.startPage(startPage, pageSize);
        List<Shipping> shippingList = this.shippingMapper.selectShippingsByUserId(userId, null);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createServerResponseSuccess(pageInfo);
    }
}