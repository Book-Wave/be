package com.test.demo.service;

import com.test.demo.vo.ItemVo;
import com.test.demo.vo.ReviewVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ShopService {
    List<ItemVo> getShopItemList(String username);
    List<ReviewVo> getShopReview(String username);

}
