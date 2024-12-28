package com.test.demo.service.member;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.demo.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverOAuthService {
    @Value("${spring.security.oauth2.client.provider.naver.token-uri}") String naver_token_uri;
    @Value("${spring.security.oauth2.client.registration.naver.client-id}") String naver_client_id;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}") String naver_client_secret;
    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}") String naver_user_info_uri;
    @Value("${spring.security.oauth2.client.registration.naver.authorization-grant-type}") String naver_authorization_grant_type;
    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}") String naver_redirect_uri;
    @Value("${spring.security.oauth2.client.provider.naver.authorization-uri}") String naver_authorization_uri;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public String naver_login() {
        return naver_authorization_uri + "?response_type=code&client_id=" + naver_client_id + "&redirect_uri=" + naver_redirect_uri + "&state=" + generate_state();
    }

    public MemberVO get_naver_user_info(String code, String state) {
        try {
            if (code == null || code.isEmpty()) {
                throw new IllegalArgumentException("Authorization code is missing or invalid");
            }

            String access_token = request_access_token(code, state);
            String user_info_response = request_user_info(access_token);
            JsonNode user_info_json_node = objectMapper.readTree(user_info_response).get("response");

            String oauth_id = user_info_json_node.get("id").asText();
            String name = user_info_json_node.get("name").asText();
            String email = user_info_json_node.get("email").asText();

            MemberVO memberVO = new MemberVO();
            memberVO.setOauth_provider("naver");
            memberVO.setOauth_id(oauth_id);
            memberVO.setName(name);
            memberVO.setEmail(email);
            memberVO.setHertz(0);

            return memberVO;
        } catch (Exception e) {
            return null;
        }
    }

    private String request_access_token(String code, String state) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", naver_client_id);
        body.add("client_secret", naver_client_secret);
        body.add("code", code);
        body.add("state", state);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(naver_token_uri, HttpMethod.POST, request, String.class);
            String responseBody = response.getBody();
            if (response.getStatusCode() == HttpStatus.OK) {
                // JSON 응답에서 access_token 추출
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                return jsonNode.get("access_token").asText();
            } else {
                throw new RuntimeException("Failed to obtain access token: " + responseBody);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error requesting access token", e);
        }
    }

    private String request_user_info(String access_token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + access_token);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(naver_user_info_uri, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to fetch user info: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error requesting user info", e);
        }
    }

    private String generate_state() {
        return Long.toHexString(System.currentTimeMillis());  // 현재 시간으로 랜덤 상태값 생성
    }

}
