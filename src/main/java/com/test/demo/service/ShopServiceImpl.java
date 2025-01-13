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
    public List<ItemVo> getShopItemList(String username) {

    }

    @Override
    public List<ReviewVo> getShopReview(String username) {

    }
}
