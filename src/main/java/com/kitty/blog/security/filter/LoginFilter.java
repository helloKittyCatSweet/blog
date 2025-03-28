//package com.kitty.blog.security.filter;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationServiceException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//public class LoginFilter extends UsernamePasswordAuthenticationFilter {
//
//    private final AuthenticationManager authenticationManager;
//
//    @Autowired
//    public LoginFilter(AuthenticationManager authenticationManager) {
//        this.authenticationManager = authenticationManager;
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//        if (!request.getMethod().equals("POST")) {
//            throw new AuthenticationServiceException(
//                    "Authentication method not supported: " + request.getMethod());
//        }
//        if (request.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)
//                || request.getContentType().contains(MediaType.APPLICATION_JSON_UTF8_VALUE)) {
//            Map<String, String> loginData = new HashMap<>();
//            try {
//                loginData = new ObjectMapper().readValue(request.getInputStream(), Map.class);
//            } catch (IOException e) {
//                throw new RuntimeException("Failed to parse JSON", e);
//            }
//            String username = loginData.get("username");
//            String password = loginData.get("password");
//            if (username == null) {
//                username = "";
//            }
//            if (password == null) {
//                password = "";
//            }
//            username = username.trim();
//            UsernamePasswordAuthenticationToken authenticationToken =
//                    new UsernamePasswordAuthenticationToken(username, password);
//            return authenticationManager.authenticate(authenticationToken);
//        } else {
//            return super.attemptAuthentication(request, response);
//        }
//    }
//}
