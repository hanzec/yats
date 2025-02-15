package com.hanzec.yats.service.security.filter;

import com.hanzec.yats.service.security.token.APIAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;

public class TailscaleAPIAuthFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_FILTER_PROCESSES_URL = "/api/v2/**";

    protected TailscaleAPIAuthFilter() {
        super(DEFAULT_FILTER_PROCESSES_URL);
    }

    /**
     * Check if request have Authentication header, if header contains Bearer token that has prefix of tskey-api-****,
     * then it will be authenticated by TailscaleAPIAuthFilter. Otherwise, it will be continued to the next filter.
     *
     * @param request request
     * @param response response
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer tskey-api-")) {
            try {
                return getAuthenticationManager()
                        .authenticate(
                                new APIAuthenticationToken(
                                        header.substring(header.indexOf("Bearer tskey-api-"))));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
