package com.test.demo.controller;


import com.test.demo.service.ShopService;
import com.test.demo.vo.ItemVo;
import com.test.demo.vo.ReviewVo;
import com.test.demo.vo.ZzimVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/book/shop")
public class ShopController {

        @Autowired
        private final ShopService shopService;

//        @GetMapping("/{shopId}")
//        public ResponseEntity<ItemVo> getShopInfo(@PathVariable Long shopId) {
//            return ResponseEntity.ok(shopService.getShopInfo(shopId));
//        }

        @GetMapping("/{shopId}/items")
        public ResponseEntity<List<ItemVo>> getShopItems(@PathVariable String shopId) {
            log.info("내상점 items");
            return ResponseEntity.ok(shopService.getShopItems(shopId));
        }

        @GetMapping("/{shopId}/reviews")
        public ResponseEntity<List<ReviewVo>> getShopReviews(@PathVariable String shopId) {
            return ResponseEntity.ok(shopService.getShopReviews(shopId));
        }

//        @GetMapping("/{shopId}/following")
//        public ResponseEntity<List<User>> getFollowing(@PathVariable Long shopId) {
//            return ResponseEntity.ok(shopService.getFollowing(shopId));
//        }
//
//        @GetMapping("/{shopId}/followers")
//        public ResponseEntity<List<User>> getFollowers(@PathVariable Long shopId) {
//            return ResponseEntity.ok(shopService.getFollowers(shopId));
//        }
//
        @GetMapping("/{shopId}/zzim")
        public ResponseEntity<List<ItemVo>> getZzimlist(@PathVariable String shopId) {
            return ResponseEntity.ok(shopService.getShopZzims(shopId));
        }
}