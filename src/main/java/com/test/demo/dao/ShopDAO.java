package com.test.demo.dao;

import com.test.demo.mapper.ShopMapper;
import com.test.demo.vo.ItemVo;
import com.test.demo.vo.ReviewVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ShopDAO {

    @Autowired
    private final ShopMapper shopMapper;


    public ShopDAO(ShopMapper shopMapper) {
        this.shopMapper = shopMapper;
    }


    public List<ItemVo> getShopItems(String ShopId) {
        return shopMapper.getShopItems(ShopId);
    };

    public List<ReviewVo> getShopReviews(String ShopId) {
        return shopMapper.getShopReviews(ShopId);
    }

    public List<ItemVo> getShopZzims(String shopId) {
        return shopMapper.getShopZzims(shopId);
    }
}
