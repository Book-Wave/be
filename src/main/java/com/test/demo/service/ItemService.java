package com.test.demo.service;

import com.test.demo.vo.ItemVo;
import com.test.demo.vo.PostVo;

import java.util.List;
import java.util.Map;

/**
 * ItemService: 서비스 계층 인터페이스
 */
public interface ItemService {

    // 기존 메서드들
    List<ItemVo> getItems(Map<String, Object> params);
    ItemVo getItemDetail(int itemId);
    void registerItem(ItemVo item);
    void updateItem(int itemId, ItemVo item);
    void deleteItem(int itemId);
    int increaseView(int itemId);
    void addZzim(int itemId, int buyerId);

    // 판매 게시물 관련 메서드
    List<PostVo> getPosts(Map<String, Object> params);
    PostVo getPostDetail(int postId);
    void registerPost(PostVo post);
    void updatePost(int postId, PostVo post);
    void deletePost(int postId);

    // 카테고리 조회
    String getCategoryName(int categoryId);

    // 페이지네이션 관련 메서드
    List<ItemVo> getItemsWithPagination(int page, int size);

    // 전체 상품 개수 반환 메서드 추가
    int getTotalItemCount();
    void updateItemStatus(int itemId, int status);
    void deleteItems(List<Integer> itemIds);
}
