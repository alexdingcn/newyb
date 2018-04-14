package com.yiban.erp.service.auth;

import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.ErrorCode;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginFilter extends AbstractAuthenticationProcessingFilter {

    private TokenManager tokenManager;

    public LoginFilter(String url, AuthenticationManager authenticationManager, TokenManager tokenManager) {
        super(url);
        setAuthenticationManager(authenticationManager);
        this.tokenManager = tokenManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse resp) throws AuthenticationException, IOException, ServletException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        // 返回一个验证令牌
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    @Override
    protected void successfulAuthentication (
            HttpServletRequest req,
            HttpServletResponse res, FilterChain chain,
            Authentication auth) throws IOException, ServletException {
        //用户名和密码验证成功成功后，生成对应的token信息返回前端
        List<GrantedAuthority> auths = (List<GrantedAuthority>) auth.getAuthorities();
        List<String> authPageList = new ArrayList<>();
        for (GrantedAuthority authority : auths) {
            authPageList.add(authority.getAuthority()); //定义为用户可以看见的路由名称
        }
        User user = (User) auth.getPrincipal();
        String token = tokenManager.createToken(user, authPageList);

        // 将 结果 写入 body
        try {
            JSONObject result = new JSONObject();
            result.put("jwt", token);
            result.put("authPages", authPageList);
            result.put("userDetail", user);
            res.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            res.setStatus(HttpServletResponse.SC_OK);
            res.getWriter().println(result.toJSONString());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(ErrorCode.LOGIN_PASSWORD_INVALID.toString());
    }

}
