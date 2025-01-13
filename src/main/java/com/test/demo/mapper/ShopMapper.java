package com.test.demo.mapper;

import com.test.demo.vo.ItemVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShopMapper {
    List<ItemVo> getShopItemList(String username);
}
