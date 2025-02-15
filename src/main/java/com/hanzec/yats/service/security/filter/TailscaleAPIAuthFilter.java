package com.hanzec.yats.service.security.filter;

import com.hanzec.yats.service.security.token.APIAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TailscaleAPIAuthFilter extends OncePerRequestFilter {

    /**
     * Check if request have Authentication header, if header contains Bearer token that has prefix of tskey-api-****,
     * then it will be authenticated by TailscaleAPIAuthFilter. Otherwise, it will be continued to the next filter.
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer tskey-api-")) {
            // construct TailscaleAPIToken object and set it to SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(new APIAuthenticationToken(;

        }
    }
