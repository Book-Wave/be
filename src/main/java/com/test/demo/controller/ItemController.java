package com.test.demo.controller;

// 서비스 계층과 연결
import com.test.demo.service.ItemService; // ItemService 인터페이스를 import
import com.test.demo.vo.ItemVo; // VO(Value Object) 클래스 import, 요청 및 응답 데이터 구조

// Spring Framework 어노테이션과 클래스
import org.springframework.beans.factory.annotation.Autowired; // 의존성 주입을 위한 어노테이션
import org.springframework.http.ResponseEntity; // HTTP 응답을 표현하는 클래스
import org.springframework.web.bind.annotation.*; // RESTful API 요청 처리에 필요한 어노테이션 세트

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

    @GetMapping // HTTP GET 요청 처리
    public ResponseEntity<List<ItemVo>> getItems(@RequestParam Map<String, Object> params) {
        // 서비스 계층에서 상품 목록 데이터 조회
        return ResponseEntity.ok(itemService.getItems(params));
    }

    @GetMapping("/{item_id}") // URL 경로에서 item_id를 전달받아 처리
    public ResponseEntity<ItemVo> getItemDetail(@PathVariable int item_id) {
        // 서비스 계층에서 상품 상세 정보 조회
        return ResponseEntity.ok(itemService.getItemDetail(item_id));
    }

    @PostMapping("/register") // HTTP POST 요청 처리
    public ResponseEntity<String> registerItem(@RequestBody ItemVo item) {
        // 서비스 계층에서 상품 데이터 등록
        itemService.registerItem(item);
        return ResponseEntity.ok(" 상품 등록 성공");
    }

    @PutMapping("/{item_id}/update") // HTTP PUT 요청 처리
    public ResponseEntity<String> updateItem(@PathVariable int item_id, @RequestBody ItemVo item) {
        // 서비스 계층에서 상품 데이터 수정
        itemService.updateItem(item_id, item);
        return ResponseEntity.ok(" 업데이트 완");
    }

    @DeleteMapping("/{item_id}/delete") // HTTP DELETE 요청 처리
    public ResponseEntity<String> deleteItem(@PathVariable int item_id) {
        // 서비스 계층에서 상품 데이터 삭제
        itemService.deleteItem(item_id);
        return ResponseEntity.ok("삭제 완");
    }
}
