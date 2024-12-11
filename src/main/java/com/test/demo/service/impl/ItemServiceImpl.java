package com.test.demo.service.impl;

import com.test.demo.dao.ItemDao; // ItemDao 인터페이스 import
import com.test.demo.service.ItemService; // ItemService 인터페이스 import
import com.test.demo.vo.ItemVo; // ItemVo 클래스 import
import org.springframework.beans.factory.annotation.Autowired; // 의존성 주입을 위한 어노테이션
import org.springframework.stereotype.Service; // 서비스 계층으로 등록하기 위한 어노테이션

import java.util.List; // 리스트 데이터 구조를 사용하기 위한 import
import java.util.Map; // 키-값 데이터 구조를 사용하기 위한 import
import java.util.HashMap; // 정가 비교 결과를 반환하기 위한 데이터 구조

/**
 * ItemServiceImpl 클래스
 * 서비스 계층에서 비즈니스 로직 구현
 */
@Service // 스프링 서비스 계층으로 등록
public class ItemServiceImpl implements ItemService {

    @Autowired // 의존성 주입
    private ItemDao itemDao;

    @Override
    public List<ItemVo> getItems(Map<String, Object> params) {
        // 상품 목록 조회
        return itemDao.selectItems(params);
    }

    @Override
    public ItemVo getItemDetail(int itemId) {
        // 특정 상품 상세 정보 조회
        return itemDao.selectItemDetail(itemId);
    }

    @Override
    public void registerItem(ItemVo item) {
        // 상품 등록
        itemDao.insertItem(item);
    }

    @Override
    public void updateItem(int itemId, ItemVo item) {
        // 상품 ID를 설정한 후 업데이트 수행
        item.setItemId(itemId);
        itemDao.updateItem(item);
    }

    @Override
    public void deleteItem(int itemId) {
        // 상품 삭제
        itemDao.deleteItem(itemId);
    }

    @Override
    public int increaseView(int itemId) {
        // 조회수를 증가시키고 새로운 조회수를 반환
        itemDao.increaseView(itemId);
        return itemDao.getViewCount(itemId);
    }

    @Override
    public void addZzim(int itemId, int buyerId) {
        // 찜하기 데이터 추가
        itemDao.addZzim(itemId, buyerId);
    }

    @Override
    public Map<String, Object> comparePrice(int itemId) {
        // 상품의 정가와 현재 가격 비교
        ItemVo item = itemDao.selectItemDetail(itemId);
        Map<String, Object> result = new HashMap<>();
        result.put("originalPrice", item.getOriPrice());
        result.put("currentPrice", item.getPrice());
        result.put("difference", item.getOriPrice() - item.getPrice());
        return result;
    }
}
