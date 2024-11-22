package com.test.demo.service;


import com.test.demo.dao.MemberDAO;
import com.test.demo.vo.MemberVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Member;
import java.util.Map;

@Service
public class MemberService {
    private final RestTemplate restTemplate;
    private final MemberDAO memberDAO;

    @Autowired
    public MemberService(RestTemplate restTemplate, MemberDAO memberDAO) {
        this.restTemplate = restTemplate;
        this.memberDAO = memberDAO;
    }

    @Value("${kakaoAPI.tokenUrl}") String token_url;
    @Value("${kakaoAPI.clientId}") String client_id;
    @Value("${kakaoAPI.redirectUrl}") String redirect_url;
    @Value("${kakaoAPI.logoutUrl}") String logout_url;


    public String get_kakao_token(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("grant_type", "authorization_code");
        param.add("client_id", client_id);
        param.add("redirect_uri", redirect_url);
        param.add("code", code);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(param, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(token_url, requestEntity, Map.class);
        Map<String, Object> body = response.getBody();
        return body != null ? (String) body.get("access_token") : null;
    }

    public Map<String, Object> get_kakao_info(String access_token) {
        String url = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(access_token);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

        Map<String, Object> kakao_info = response.getBody();
        Long idLong = (Long) kakao_info.get("id");
        String id = idLong.toString();
        Map<String, Object> properties = (Map<String, Object>) kakao_info.get("properties");
        String name = (String) properties.get("nickname");
        Map<String, Object> kakaoAccount = (Map<String, Object>) kakao_info.get("kakao_account");
        String email = (String) kakaoAccount.get("email");

        MemberVO memberVO = new MemberVO();
        memberVO.setKakao(id);
        memberVO.setName(name);
        memberVO.setEmail(email);
        save_member(memberVO);
        return response.getBody();
    }
    public void save_member(MemberVO memberVO) {
        memberDAO.save_member(memberVO);
    }
}
