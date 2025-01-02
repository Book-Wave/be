package com.test.demo.controller;

import com.test.demo.service.ItemService;
import com.test.demo.vo.ItemVo;
import com.test.demo.vo.PostVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * ItemController: REST API 요청을 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/book/item") // 기본 URL 경로 설정
@Slf4j
public class ItemController {

    @Autowired // ItemService를 주입받아 서비스 계층에 접근
    private ItemService itemService;

    // 상품 관련 메서드

    // 상품 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<ItemVo>> getItems(@RequestParam Map<String, Object> params) {
        return ResponseEntity.ok(itemService.getItems(params));
    }

    // 페이지네이션된 상품 목록 조회
    @GetMapping("/list/page")
    public ResponseEntity<Map<String, Object>> getPaginatedItems(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "1 0") int size
    ) {
        // 수정 가능한 Map 생성
        Map<String, Object> params = new HashMap<>();
        params.put("offset", (page - 1) * size);
        params.put("limit", size);

        // 서비스 호출
        List<ItemVo> items = itemService.getItems(params);
        int totalCount = itemService.getTotalItemCount();

        // 응답 데이터 생성
        Map<String, Object> response = new HashMap<>();
        response.put("items", items);
        response.put("currentPage", page);
        response.put("pageSize", size);
        response.put("totalItems", totalCount);
        response.put("totalPages", (int) Math.ceil((double) totalCount / size));

        return ResponseEntity.ok(response);
    }


    @GetMapping("/categories")
    public ResponseEntity<List<Map<String, Object>>> getCategories() {
        List<Map<String, Object>> categories = Arrays.asList(
                Map.of("category_id", 1, "category_name", "총류"),
                Map.of("category_id", 2, "category_name", "철학"),
                Map.of("category_id", 3, "category_name", "종교"),
                Map.of("category_id", 4, "category_name", "사회과학"),
                Map.of("category_id", 5, "category_name", "자연과학"),
                Map.of("category_id", 6, "category_name", "기술과학"),
                Map.of("category_id", 7, "category_name", "예술"),
                Map.of("category_id", 8, "category_name", "언어"),
                Map.of("category_id", 9, "category_name", "문학"),
                Map.of("category_id", 10, "category_name", "역사"),
                Map.of("category_id", 11, "category_name", "기타")
        );
        return ResponseEntity.ok(categories);
    }

    // 특정 상품 상세 조회
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemVo> getItemDetail(@PathVariable int itemId) {
        log.info("Itemid : {}",itemId);
        itemService.increaseView(itemId); // 조회수 증가
        return ResponseEntity.ok(itemService.getItemDetail(itemId));
    }

    // 상품 등록
    @PostMapping("/register")
    public ResponseEntity<String> registerItem(@RequestBody ItemVo item) {
        itemService.registerItem(item);
        return ResponseEntity.ok("상품 등록 성공");
    }

    // 상품 수정
    @PostMapping("/{itemId}/update")
    public ResponseEntity<String> updateItem(@PathVariable int itemId, @RequestBody ItemVo item) {
        itemService.updateItem(itemId, item);
        return ResponseEntity.ok("상품 수정 성공");
    }

    // 상품 삭제
    @DeleteMapping("/{itemId}/delete")
    public ResponseEntity<String> deleteItem(@PathVariable int itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.ok("상품 삭제 성공");
    }

    // 찜 추가
    @PostMapping("/{itemId}/zzim")
    public ResponseEntity<String> addZzim(@PathVariable int itemId, @RequestParam int buyerId) {
        itemService.addZzim(itemId, buyerId);
        return ResponseEntity.ok("상품 찜 성공");
    }

    // 판매 게시물 관련 메서드

    // 판매 게시물 목록 조회
    @GetMapping("/post/list")
    public ResponseEntity<List<PostVo>> getPosts(@RequestParam Map<String, Object> params) {
        return ResponseEntity.ok(itemService.getPosts(params));
    }

    // 특정 판매 게시물 상세 조회
    @GetMapping("/post/{postId}")
    public ResponseEntity<PostVo> getPostDetail(@PathVariable int postId) {
        return ResponseEntity.ok(itemService.getPostDetail(postId));
    }

    // 판매 게시물 등록
    @PostMapping("/post/register")
    public ResponseEntity<String> registerPost(@RequestBody PostVo post) {
        itemService.registerPost(post);
        return ResponseEntity.ok("판매 게시물 등록 성공");
    }

    // 판매 게시물 수정
    @PutMapping("/post/{postId}/update")
    public ResponseEntity<String> updatePost(@PathVariable int postId, @RequestBody PostVo post) {
        itemService.updatePost(postId, post);
        return ResponseEntity.ok("판매 게시물 수정 성공");
    }

    // 판매 게시물 삭제
    @DeleteMapping("/post/{postId}/delete")
    public ResponseEntity<String> deletePost(@PathVariable int postId) {
        itemService.deletePost(postId);
        return ResponseEntity.ok("판매 게시물 삭제 성공");
    }

    // 네이버 API 키
    private final String CLIENT_ID = "1OVKwoLQ_G3UrjXzFI6Y";
    private final String CLIENT_SECRET = "UONgp6mBRe";

    // 책 검색 API 호출
    @GetMapping("/search")
    public ResponseEntity<Object> searchBooks(@RequestParam String query) {
        try {
            // API URL
            String apiUrl = "https://openapi.naver.com/v1/search/book.json?query=" + query + "&display=5";

            // 로그 출력
            System.out.println("API 호출 URL: " + apiUrl);

            // RestTemplate 객체 생성
            RestTemplate restTemplate = new RestTemplate();

            // 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Naver-Client-Id", CLIENT_ID);
            headers.add("X-Naver-Client-Secret", CLIENT_SECRET);

            // 요청 엔티티 생성
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            // 네이버 API 호출
            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl, org.springframework.http.HttpMethod.GET, requestEntity, Map.class
            );

            // API 응답 데이터 로깅
            System.out.println("네이버 API 응답: " + response.getBody());

            // 응답 데이터 파싱
            List<Map<String, String>> books = new ArrayList<>();
            List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");

            for (Map<String, Object> item : items) {
                Map<String, String> book = new HashMap<>();
                book.put("title", item.get("title").toString());
                book.put("link", item.get("link").toString());
                book.put("image", item.get("image").toString());
                book.put("author", item.get("author").toString());
                book.put("publisher", item.get("publisher").toString());
                book.put("description", item.get("description").toString());
                books.add(book);
            }

            // 파싱 결과 반환
            return ResponseEntity.ok(books);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

//    @GetMapping("/search2")
//    public ResponseEntity<Object> searchBooks2(@RequestParam String query) {
//        try {
//            // URL 인코딩된 검색어 포함된 API URL
//
//            String apiUrl = "https://openapi.naver.com/v1/search/book.json?query=" + query + "&display=5";
//
//            // RestTemplate 초기화
//            RestTemplate restTemplate = new RestTemplate();
//
//            // 요청 헤더 설정
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("X-Naver-Client-Id", CLIENT_ID);
//            headers.add("X-Naver-Client-Secret", CLIENT_SECRET);
//
//            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
//
//            // API 호출
//            ResponseEntity<Map> response = restTemplate.exchange(
//                    apiUrl, org.springframework.http.HttpMethod.GET, requestEntity, Map.class
//            );
//
//            // API 요청 및 응답 확인
//            System.out.println("Encoded API 호출 URL: " + apiUrl);
//            System.out.println("Headers: " + headers);
//            System.out.println("API Response: " + response.getBody());
//
//            // 결과 반환
//            return ResponseEntity.ok(response.getBody());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Error: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/search/test")
//    public ResponseEntity<Object> searchBooksTest(@RequestParam String query) {
//        try {
//            // 동적 URL 생성
//            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
//            String apiUrl = "https://openapi.naver.com/v1/search/book.json?query=" + encodedQuery + "&display=5";
//
//            // 하드코딩된 URL
//            String hardcodedUrl = "https://openapi.naver.com/v1/search/book.json?query=소나기&display=5";
//
//            // RestTemplate 생성
//            RestTemplate restTemplate = new RestTemplate();
//
//            // 요청 헤더 설정
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("X-Naver-Client-Id", CLIENT_ID);
//            headers.add("X-Naver-Client-Secret", CLIENT_SECRET);
//            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
//
//            // 동적 URL 호출
//            ResponseEntity<Map> dynamicResponse = restTemplate.exchange(apiUrl, org.springframework.http.HttpMethod.GET, requestEntity, Map.class);
//
//            // 하드코딩된 URL 호출
//            ResponseEntity<Map> hardcodedResponse = restTemplate.exchange(hardcodedUrl, org.springframework.http.HttpMethod.GET, requestEntity, Map.class);
//
//            // 응답 비교
//            Map<String, Object> comparison = new HashMap<>();
//            comparison.put("dynamicUrl", apiUrl);
//            comparison.put("hardcodedUrl", hardcodedUrl);
//            comparison.put("dynamicResponse", dynamicResponse.getBody());
//            comparison.put("hardcodedResponse", hardcodedResponse.getBody());
//            System.out.println("Dynamic URL Response: " + dynamicResponse);
//            System.out.println("API Request Headers: " + headers.toString());
//            System.out.println("API Request URL: " + apiUrl);
//
//
//            return ResponseEntity.ok(comparison);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Error: " + e.getMessage());
//        }
//    }
//
//
//

}
