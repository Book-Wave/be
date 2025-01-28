package com.test.demo.dao;

import com.test.demo.vo.ItemVo;
import com.test.demo.vo.PostVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ItemDao {

    // 상품 관련 메서드
    List<ItemVo> selectItems(Map<String, Object> params); // 상품 목록 조회
    ItemVo selectItemDetail(int itemId); // 상품 상세 조회
    void insertItem(ItemVo item); // 상품 등록
    void updateItem(ItemVo item); // 상품 수정
    void deleteItem(int itemId); // 상품 삭제
    void increaseView(int itemId); // 조회수 증가
    int getViewCount(int itemId); // 조회수 가져오기
    void addZzim(@Param("itemId") int itemId, @Param("buyerId") int buyerId); // 찜 추가

    // 판매 게시물 관련 메서드
    List<PostVo> selectPosts(Map<String, Object> params); // 게시물 목록 조회
    PostVo selectPostDetail(int postId); // 게시물 상세 조회
    void insertPost(PostVo post); // 게시물 등록
    void updatePost(PostVo post); // 게시물 수정
    void deletePost(int postId); //

    // 카테고리 이름 조회
    String getCategoryName(int categoryId);

    // 카테고리 ID 조회
    Integer getCategoryId(String categoryName);

    // 전체 상품 개수 조회
    int selectTotalItemCount();

    // 페이지네이션 메서드 추가
    List<ItemVo> selectItemsWithPagination(@Param("offset") int offset, @Param("limit") int limit);

    void updateItemStatus(ItemVo item);
}

