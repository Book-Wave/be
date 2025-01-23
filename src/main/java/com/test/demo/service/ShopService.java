package com.test.demo.service;

import com.test.demo.vo.ItemVo;
import com.test.demo.vo.ReviewVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ShopService {
    ShopInfo getShopInfo(Long shopId);
    List<Product> getShopProducts(Long shopId);
    List<Review> getShopReviews(Long shopId);
    List<User> getFollowing(Long shopId);
    List<User> getFollowers(Long shopId);
    List<Product> getWishlist(Long shopId);

}
