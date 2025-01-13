package com.test.demo.dao;

import com.test.demo.mapper.ShopMapper;
import com.test.demo.vo.ItemVo;

import java.util.List;

public class ShopDao {

    private final ShopMapper shopMapper;

    public List<ItemVo> getShopItemList(String username);
}
