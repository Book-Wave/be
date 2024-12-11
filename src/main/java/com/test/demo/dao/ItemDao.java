package com.test.demo.dao;

import com.test.demo.vo.ItemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper // MyBatis 매퍼로 등록
public interface ItemDao {

    // 상품 목록 조회
    List<ItemVo> selectItems(Map<String, Object> params);

    // 특정 상품 상세 정보 조회
    ItemVo selectItemDetail(int itemId);

    // 상품 등록
    void insertItem(ItemVo item);

    // 상품 수정
    void updateItem(ItemVo item);
    // 상품 삭제
    void deleteItem(int itemId);

    // 조회수 증가
    void increaseView(int itemId);

    // 조회수 가져오기
    int getViewCount(int itemId);

    // 찜 추가
    void addZzim(@Param("itemId") int itemId, @Param("buyerId") int buyerId);

    // 상품 정가 가져오기
    int getOriPrice(int itemId);
}
