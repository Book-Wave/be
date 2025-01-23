package com.test.demo.service;

import com.test.demo.vo.ItemVo;
import com.test.demo.vo.ReviewVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ShopServiceImpl implements ShopService {


    @Override
    public ShopInfo getShopInfo(Long shopId) {
        return null;
    }

    @Override
    public List<Product> getShopProducts(Long shopId) {
        return null;
    }

    @Override
    public List<Review> getShopReviews(Long shopId) {
        return null;
    }

    @Override
    public List<User> getFollowing(Long shopId) {
        return null;
    }

    @Override
    public List<User> getFollowers(Long shopId) {
        return null;
    }

    @Override
    public List<Product> getWishlist(Long shopId) {
        return null;
    }
}
