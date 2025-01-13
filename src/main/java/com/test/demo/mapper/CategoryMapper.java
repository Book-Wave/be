package com.test.demo.mapper;

import java.util.HashMap;
import java.util.Map;

/**
 * CategoryMapper: 카테고리 이름과 ID를 매핑하는 유틸리티 클래스
 */
public class CategoryMapper {
    // 카테고리 이름 → 카테고리 ID 매핑
    private static final Map<String, Integer> categoryToIdMap = new HashMap<>();
    // 카테고리 ID → 카테고리 이름 매핑
    private static final Map<Integer, String> idToCategoryMap = new HashMap<>();

    // 정적 블록: 초기화 시 데이터 설정
    static {
        categoryToIdMap.put("총류", 1);
        categoryToIdMap.put("철학", 2);
        categoryToIdMap.put("종교", 3);
        categoryToIdMap.put("사회과학", 4);
        categoryToIdMap.put("자연과학", 5);
        categoryToIdMap.put("기술과학", 6);
        categoryToIdMap.put("예술", 7);
        categoryToIdMap.put("언어", 8);
        categoryToIdMap.put("문학", 9);
        categoryToIdMap.put("역사", 10);
        categoryToIdMap.put("기타", 11);

        // 역방향 매핑 생성 (ID → 이름)
        for (Map.Entry<String, Integer> entry : categoryToIdMap.entrySet()) {
            idToCategoryMap.put(entry.getValue(), entry.getKey());
        }
    }

    // 카테고리 이름을 받아서 ID 반환
    public static int getCategoryId(String categoryName) {
        return categoryToIdMap.getOrDefault(categoryName, 11); // 기본값 '기타'
    }

    // 카테고리 ID를 받아서 이름 반환
    public static String getCategoryName(int categoryId) {
        return idToCategoryMap.getOrDefault(categoryId, "기타");
    }
}
