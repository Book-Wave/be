package com.test.demo.controller;

// 서비스 계층과 연결
import com.test.demo.service.ItemService; // ItemService 인터페이스를 import
import com.test.demo.vo.ItemVo; // VO(Value Object) 클래스 import, 요청 및 응답 데이터 구조

// Spring Framework 어노테이션과 클래스
import org.springframework.beans.factory.annotation.Autowired; // 의존성 주입을 위한 어노테이션
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity; // HTTP 응답을 표현하는 클래스
import org.springframework.web.bind.annotation.*; // RESTful API 요청 처리에 필요한 어노테이션 세트
import org.springframework.web.client.RestTemplate;
// 네이버 api utf-8 인코딩
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

// Java 기본 라이브러리
import java.util.List; // 리스트 데이터 구조를 사용하기 위한 import
import java.util.Map; // 키-값 쌍 데이터 구조를 사용하기 위한 import



/**
 * REST API 요청을 처리하는 ItemController 클래스
 */
@RestController // REST API 요청을 처리하는 컨트롤러 클래스
@RequestMapping("/book/item") // 기본 URL 경로 설정
public class ItemController {

    @Autowired // 서비스 계층의 의존성을 자동으로 주입
    private ItemService itemService;

    // 네이버 API 클라이언트 정보
    private final String CLIENT_ID = "1OVKwoLQ_G3UrjXzFI6Y"; // 네이버 API 클라이언트 ID
    private final String CLIENT_SECRET = "jIwxdPxoNm"; // 네이버 API 클라이언트 Secret

    @GetMapping("/list") // HTTP GET 요청 처리
    public ResponseEntity<List<ItemVo>> getItems(@RequestParam Map<String, Object> params) {
        // 서비스 계층에서 상품 목록 데이터 조회
        return ResponseEntity.ok(itemService.getItems(params));
    }

    @GetMapping("/{item_id}") // URL 경로에서 item_id를 전달받아 처리
    public ResponseEntity<ItemVo> getItemDetail(@PathVariable int item_id) {
        // 서비스 계층에서 상품 상세 정보 조회
        return ResponseEntity.ok(itemService.getItemDetail(item_id));
    }

    @GetMapping("/register/{item_id}") // HTTP GET 요청 처리 (정가 비교)
    public ResponseEntity<Map<String, Object>> comparePrice(@PathVariable int item_id) {
        // 서비스 계층에서 정가 비교 및 추가 정보 조회
        Map<String, Object> response = itemService.comparePrice(item_id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register") // HTTP POST 요청 처리
    public ResponseEntity<String> registerItem(@RequestBody ItemVo item) {
        // 서비스 계층에서 상품 데이터 등록
        itemService.registerItem(item);
        return ResponseEntity.ok("상품 등록 성공");
    }

    @PutMapping("/{item_id}/update") // HTTP PUT 요청 처리
    public ResponseEntity<String> updateItem(@PathVariable int item_id, @RequestBody ItemVo item) {
        // 서비스 계층에서 상품 데이터 수정
        itemService.updateItem(item_id, item);
        return ResponseEntity.ok("상품 수정 성공");
    }

    @DeleteMapping("/{item_id}/delete") // HTTP DELETE 요청 처리
    public ResponseEntity<String> deleteItem(@PathVariable int item_id) {
        // 서비스 계층에서 상품 데이터 삭제
        itemService.deleteItem(item_id);
        return ResponseEntity.ok("상품 삭제 성공");
    }

    @PutMapping("/{item_id}/views") // HTTP PUT 요청 처리 (조회수 증가)
    public ResponseEntity<Map<String, Object>> increaseView(@PathVariable int item_id) {
        // 서비스 계층에서 조회수 증가
        int updatedViewCount = itemService.increaseView(item_id);
        Map<String, Object> response = Map.of("message", "조회수 증가 성공", "viewCount", updatedViewCount);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{item_id}/zzim") // HTTP POST 요청 처리 (찜하기)
    public ResponseEntity<String> addZzim(@PathVariable int item_id, @RequestParam int buyer_id) {
        // 서비스 계층에서 찜하기 추가
        itemService.addZzim(item_id, buyer_id);
        return ResponseEntity.ok("상품 찜 성공");
    }



    @GetMapping("/search") // HTTP GET 요청 처리 (네이버 책 검색 API)
    public ResponseEntity<String> searchBooks(
            @RequestParam String query, // 필수: 검색어
            @RequestParam(required = false, defaultValue = "10") int display, // 선택: 결과 개수 (기본값 10)
            @RequestParam(required = false, defaultValue = "1") int start,    // 선택: 시작 위치 (기본값 1)
            @RequestParam(required = false, defaultValue = "sim") String sort // 선택: 정렬 방식 (기본값 sim)
    ) {
        try {
            // query 값을 UTF-8로 URL 인코딩
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

            // 네이버 API URL 설정
            String apiUrl = "https://openapi.naver.com/v1/search/book.json";
            String url = String.format("%s?query=%s&display=%d&start=%d&sort=%s",
                    apiUrl, encodedQuery, display, start, sort);

            // RestTemplate 생성
            RestTemplate restTemplate = new RestTemplate();

            // 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Naver-Client-Id", CLIENT_ID); // 네이버 클라이언트 ID
            headers.add("X-Naver-Client-Secret", CLIENT_SECRET); // 네이버 클라이언트 Secret

            HttpEntity<Void> request = new HttpEntity<>(headers);

            // 네이버 API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                    url, org.springframework.http.HttpMethod.GET, request, String.class);

            // 성공 시 응답 반환
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            // 오류 발생 시 처리
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
