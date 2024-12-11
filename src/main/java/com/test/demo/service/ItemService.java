package com.test.demo.service;

import com.test.demo.vo.ItemVo; // VO 클래스 import
import java.util.Map; // 요청 데이터 키-값 구조를 사용하기 위해 import
import java.util.List; // 결과 데이터를 리스트 형태로 반환하기 위해 import

/**
 * ItemService 인터페이스
 * 서비스 계층에서 사용되는 메서드를 정의
 */
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

    // 조회수 증가
    int increaseView(int itemId);

    // 찜 추가
    void addZzim(int itemId, int buyerId);

    // 상품 정가 비교
    Map<String, Object> comparePrice(int itemId);
}
