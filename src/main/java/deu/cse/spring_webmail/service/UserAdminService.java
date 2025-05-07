/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.spring_webmail.service;

import java.util.Arrays;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
/**
 *
 * @author jaejin
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserAdminService {

    @Value("${james.webadmin.base-url}")
    private String baseUrl;

    @Value("${james.webadmin.auth.id}")
    private String username;

    @Value("${james.webadmin.auth.password}")
    private String password;

    private final RestTemplate restTemplate = new RestTemplate();

    private HttpHeaders createHeaders() {
        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.setContentType(MediaType.TEXT_PLAIN);
        return headers;
    }

    public boolean addUser(String userId, String userPassword) {
        String url = baseUrl + "/users/" + userId;
        HttpEntity<String> request = new HttpEntity<>(userPassword, createHeaders());

        try {
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, request, Void.class);
            return response.getStatusCode() == HttpStatus.NO_CONTENT;
        } catch (Exception e) {
            log.error("addUser() failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean deleteUsers(String[] userList) {
        boolean allSuccess = true;

        for (String userId : userList) {
            String url = baseUrl + "/users/" + userId;
            HttpEntity<Void> request = new HttpEntity<>(createHeaders());

            try {
                ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class);
                if (response.getStatusCode() != HttpStatus.NO_CONTENT) {
                    allSuccess = false;
                }
            } catch (Exception e) {
                log.error("deleteUser({}) failed: {}", userId, e.getMessage());
                allSuccess = false;
            }
        }

        return allSuccess;
    }

    public List<String> getUserList() {
        String url = baseUrl + "/users";
        HttpEntity<Void> request = new HttpEntity<>(createHeaders());

        try {
            ResponseEntity<String[]> response = restTemplate.exchange(url, HttpMethod.GET, request, String[].class);
            List<String> users = Arrays.asList(response.getBody());
            users.sort(String::compareTo);
            return users;
        } catch (Exception e) {
            log.error("getUserList() failed: {}", e.getMessage());
            return List.of();
        }
    }
}
