package com.test.demo.mapper;

import com.test.demo.vo.ItemVo;
import com.test.demo.vo.ReviewVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShopMapper {
    List<ItemVo> getShopItems(String shopId);
    List<ReviewVo> getShopReviews(String shopId);
    List<ItemVo> getShopZzims(String shopId);
}
