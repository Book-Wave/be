package com.test.demo.controller;


import com.test.demo.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/book/shop")
public class ShopController {


        private final ShopService shopService;

        @GetMapping("/{shopId}")
        public ResponseEntity<ShopInfo> getShopInfo(@PathVariable Long shopId) {
            return ResponseEntity.ok(shopService.getShopInfo(shopId));
        }

        @GetMapping("/{shopId}/products")
        public ResponseEntity<List<Product>> getShopProducts(@PathVariable Long shopId) {
            return ResponseEntity.ok(shopService.getShopProducts(shopId));
        }

        @GetMapping("/{shopId}/reviews")
        public ResponseEntity<List<Review>> getShopReviews(@PathVariable Long shopId) {
            return ResponseEntity.ok(shopService.getShopReviews(shopId));
        }

        @GetMapping("/{shopId}/following")
        public ResponseEntity<List<User>> getFollowing(@PathVariable Long shopId) {
            return ResponseEntity.ok(shopService.getFollowing(shopId));
        }

        @GetMapping("/{shopId}/followers")
        public ResponseEntity<List<User>> getFollowers(@PathVariable Long shopId) {
            return ResponseEntity.ok(shopService.getFollowers(shopId));
        }

        @GetMapping("/{shopId}/wishlist")
        public ResponseEntity<List<Product>> getWishlist(@PathVariable Long shopId) {
            return ResponseEntity.ok(shopService.getWishlist(shopId));
        }
}