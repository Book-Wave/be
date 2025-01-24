package com.test.demo.service;

import com.test.demo.dao.ShopDAO;
import com.test.demo.vo.ItemVo;
import com.test.demo.vo.ReviewVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ShopServiceImpl implements ShopService {


    @Autowired
    private final ShopDAO ShopDao;

    public ShopServiceImpl(ShopDAO shopDao) {
        ShopDao = shopDao;
    }


//    @Override
//    public ShopInfo getShopInfo(Long shopId) {
//        return null;
//    }

    @Override
    public List<ItemVo> getShopItems(String ShopId) {
        return ShopDao.getShopItems(ShopId);
    }

    @Override
    public List<ReviewVo> getShopReviews(String shopId) {
        return ShopDao.getShopReviews(shopId);

    }

//    @Override
//    public List<User> getFollowing(Long shopId) {
//        return null;
//    }
//
//    @Override
//    public List<User> getFollowers(Long shopId) {
//        return null;
//    }
//
    @Override
    public List<ItemVo> getShopZzims(String shopId) {
        return ShopDao.getShopZzims(shopId);
    }
}
