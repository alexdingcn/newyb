package com.yiban.erpcustomer.service.auth;

import com.yiban.erpcustomer.entities.User;
import com.yiban.erpcustomer.exception.BizRuntimeException;
import com.yiban.erpcustomer.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AuthenticationFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    private TokenManager tokenManager;

    public AuthenticationFilter(TokenManager tokenManager) {
        super();
        this.tokenManager = tokenManager;
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {

        Authentication authentication = null;
        try {
            authentication = getAuthenticationByRequest((HttpServletRequest) request);
        }catch (BizRuntimeException e) {
            logger.warn("get authentication by token fail.");
            HttpServletResponse res = (HttpServletResponse) response;
            res.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //401
            res.getWriter().println(e.getErrorCode().toString());
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private Authentication getAuthenticationByRequest(HttpServletRequest request) {
        String token = request.getHeader(TokenManager.HEADER_STRING);
        logger.info("token:{}", token);
        TokenModel tokenModel = tokenManager.getTokenModel(token);
        if (tokenModel == null) {
            throw new BizRuntimeException(ErrorCode.LOGIN_STATUS_MISS);
        }
        User user = tokenModel.getUser();
        if (user == null) {
            throw new BizRuntimeException(ErrorCode.LOGIN_STATUS_MISS);
        }
        List<String> accessList = tokenModel.getAccessApiList();
        List<GrantedAuthority> authorities = new ArrayList<>(accessList != null ? accessList.size() : 0);
        if (accessList != null) {
            for (String access : accessList) {
                authorities.add(new SimpleGrantedAuthority(access));
            }
        }
        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }
}
