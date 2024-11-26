package com.test.demo.service.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.demo.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoOAuthService {
    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}") String kakao_token_uri;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}") String kakao_client_id;
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}") String kakao_user_info_uri;
    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}") String kakao_authorization_grant_type;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}") String kakao_redirect_uri;
    @Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}") String kakao_authorization_uri;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public String kakao_login() {
        return kakao_authorization_uri + "?client_id=" + kakao_client_id + "&redirect_uri=" + kakao_redirect_uri + "&response_type=code";
    }

    public MemberVO get_kakao_user_info(String code) {
        try {
            if (code == null || code.isEmpty()) {
                throw new IllegalArgumentException("Authorization code is missing or invalid");
            }

            String token_response = request_access_token(code);
            JsonNode tokenJsonNode = objectMapper.readTree(token_response);
            String access_token = tokenJsonNode.get("access_token").asText();

            String user_info_response = request_user_info(access_token);
            JsonNode user_info_json_node = objectMapper.readTree(user_info_response);

            String oauth_id = user_info_json_node.get("id").asText();
            String name = user_info_json_node.path("properties").path("nickname").asText();
            String email = user_info_json_node.path("kakao_account").path("email").asText();

            MemberVO memberVO = new MemberVO();
            memberVO.setOauth_provider("kakao");
            memberVO.setOauth_id(oauth_id);
            memberVO.setName(name);
            memberVO.setEmail(email);
            memberVO.setHertz(0);
            return memberVO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String request_access_token(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakao_client_id);
        body.add("redirect_uri", kakao_redirect_uri);
        body.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            return restTemplate.exchange(kakao_token_uri, HttpMethod.POST, request, String.class).getBody();
        } catch (Exception e) {
            log.error("Error requesting access token from Kakao", e);
            throw new RuntimeException("Error requesting access token", e);
        }
    }

    private String request_user_info(String access_token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + access_token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            return restTemplate.exchange(kakao_user_info_uri, HttpMethod.GET, entity, String.class).getBody();
        } catch (Exception e) {
            log.error("Error requesting user info from Kakao", e);
            throw new RuntimeException("Error requesting user info", e);
        }
    }
}
