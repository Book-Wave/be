package com.test.demo.service;

import com.test.demo.vo.ItemVo;
import com.test.demo.vo.ReviewVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ShopService {
//    ShopInfo getShopInfo(Long shopId);
    List<ItemVo> getShopItems(String shopId);
    List<ReviewVo> getShopReviews(String shopId);
//    List<User> getFollowing(Long shopId);
//    List<User> getFollowers(Long shopId);
    List<ItemVo> getShopZzims(String shopId);

}
