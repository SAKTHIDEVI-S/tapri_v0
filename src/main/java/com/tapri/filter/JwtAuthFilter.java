package com.tapri.filter;

import com.tapri.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        System.out.println("JWT Filter - Request URL: " + request.getRequestURL());
        System.out.println("JWT Filter - Auth Header: " + authHeader);
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("JWT Filter - Token: " + token);
            System.out.println("JWT Filter - Token valid: " + jwtUtil.validateToken(token));
            System.out.println("JWT Filter - Is temp token: " + jwtUtil.isTempToken(token));
            
            if (jwtUtil.validateToken(token) && !jwtUtil.isTempToken(token)) {
                try {
                    String phone = jwtUtil.getPhoneFromToken(token);
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    System.out.println("JWT Filter - Phone: " + phone + ", UserId: " + userId);
                    
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            phone, null, Collections.emptyList());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    request.setAttribute("userId", userId);
                    request.setAttribute("phone", phone);
                    
                    System.out.println("JWT Filter - Authentication set successfully");
                } catch (Exception e) {
                    System.out.println("JWT Filter - Error extracting user info: " + e.getMessage());
                }
            } else {
                System.out.println("JWT Filter - Token validation failed");
            }
        } else {
            System.out.println("JWT Filter - No valid auth header found");
        }
        filterChain.doFilter(request, response);
    }
} 