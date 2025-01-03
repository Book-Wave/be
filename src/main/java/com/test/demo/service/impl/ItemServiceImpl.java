package com.test.demo.service.impl;

import com.test.demo.dao.ItemDao;
import com.test.demo.service.ItemService;
import com.test.demo.vo.ItemVo;
import com.test.demo.vo.PostVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.test.demo.mapper.CategoryMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * ItemServiceImpl: 서비스 계층 구현
 */
@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    @Autowired // ItemDao를 주입받아 데이터베이스 접근
    private ItemDao itemDao;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 상품 관련 메서드

    @Override
    public List<ItemVo> getItems(Map<String, Object> params) {
        // 새로운 수정 가능한 Map 생성
        Map<String, Object> mutableParams = new HashMap<>(params);

        // 기본값 설정
        int offset = mutableParams.get("offset") != null ? Integer.parseInt(mutableParams.get("offset").toString()) : 0;
        int limit = mutableParams.get("limit") != null ? Integer.parseInt(mutableParams.get("limit").toString()) : 10;
        System.out.println("mutableParams: " + mutableParams);

        // 수정 가능한 Map에 값 추가
        mutableParams.put("offset", offset);
        mutableParams.put("limit", limit);
        System.out.println("Params before DAO call: " + mutableParams);

        // DAO 호출
        return itemDao.selectItems(mutableParams);
    }

    @Override
    public ItemVo getItemDetail(int itemId) {
        log.info("service itemId : " + itemId);

        // 조회수 증가
        itemDao.increaseView(itemId);

        // 상품 상세 정보 가져오기
        ItemVo item = itemDao.selectItemDetail(itemId);

        // modDate 변환 (LocalDateTime -> String)
        if (item.getModDate() != null) {
            item.setModDate(formatDate(LocalDateTime.parse(item.getModDate(), formatter)));
        }

        return item;
    }



    @Override
    public void registerItem(ItemVo item) {
        // 날짜 설정
        LocalDateTime now = LocalDateTime.now();

        // 포맷터 적용
        String formattedDate = now.format(formatter);
        item.setRegDate(formattedDate);
        item.setModDate(formattedDate);

        // DB에 삽입
        itemDao.insertItem(item);
    }

    @Override
    public void updateItem(int itemId, ItemVo item) {
        // itemId 설정
        item.setItemId(itemId);

        // modDate를 현재 시간으로 설정 및 변환
        LocalDateTime now = LocalDateTime.now();
        item.setModDate(formatDate(now));

        // DB에 수정된 데이터 저장
        itemDao.updateItem(item);
    }

    @Override
    public void deleteItem(int itemId) {
        // 특정 상품을 삭제
        itemDao.deleteItem(itemId);
    }

    @Override
    public int increaseView(int itemId) {
        // 조회수를 1 증가시키고, 현재 조회수를 반환
        itemDao.increaseView(itemId);
        return itemDao.getViewCount(itemId);
    }

    @Override
    public void addZzim(int itemId, int buyerId) {
        // 특정 상품을 찜 목록에 추가
        itemDao.addZzim(itemId, buyerId);
    }

    // 판매 게시물 관련 메서드

    @Override
    public List<PostVo> getPosts(Map<String, Object> params) {
        // 조건에 맞는 판매 게시물 목록을 조회
        return itemDao.selectPosts(params);
    }

    @Override
    public PostVo getPostDetail(int postId) {
        // 특정 판매 게시물의 상세 정보를 조회
        return itemDao.selectPostDetail(postId);
    }

    @Override
    public void registerPost(PostVo post) {
        // 등록 날짜와 수정 날짜를 현재 날짜와 시간으로 설정
        post.setRegDate(LocalDateTime.now());
        post.setModDate(LocalDateTime.now());

        // 게시물 정보를 DB에 삽입
        itemDao.insertPost(post);
    }

    @Override
    public void updatePost(int postId, PostVo post) {
        // 게시물 수정 시 postId를 설정하고 수정 날짜를 현재 날짜로 갱신
        post.setPostId(postId);
        post.setModDate(LocalDateTime.now());
        // 수정된 판매 게시물 정보를 DB에 저장
        itemDao.updatePost(post);
    }

    @Override
    public void deletePost(int postId) {
        // 특정 판매 게시물을 삭제
        itemDao.deletePost(postId);
    }

    @Override
    public String getCategoryName(int categoryId) {
        // CategoryMapper 클래스의 static 메서드를 호출하여 카테고리 이름 반환
        return CategoryMapper.getCategoryName(categoryId);
    }

    // 페이지네이션 기능
    @Override
    public List<ItemVo> getItemsWithPagination(int page, int size) {
        int offset = (page - 1) * size; // 페이지 번호를 offset으로 변환 (0부터 시작)
        return itemDao.selectItemsWithPagination(offset, size);
    }

    @Override
    public int getTotalItemCount() {
        // ItemDao를 통해 전체 상품 개수를 가져옴
        return itemDao.selectTotalItemCount();
    }

    // LocalDateTime을 String으로 변환
    private String formatDate(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }
}
