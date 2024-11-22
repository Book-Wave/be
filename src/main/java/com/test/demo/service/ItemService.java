package com.test.demo.service;
import com.test.demo.vo.ItemVo;
import java.util.Map;
import java.util.List;
public interface ItemService {
    // 상품 목록 조회 (검색, 정렬, 페이지네이션)
    List<ItemVo> getItems(Map<String, Object> params);

    // 특정 상품 상세 정보 조회
    ItemVo getItemDetail(int itemId);

    // 상품 등록
    void registerItem(ItemVo item);

    // 상품 수정
    void updateItem(int itemId, ItemVo item);

    // 상품 삭제
    void deleteItem(int itemId);
}
