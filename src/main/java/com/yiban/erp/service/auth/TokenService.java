package com.yiban.erp.service.auth;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TokenService {
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private static final long EXPIRATIONTIME = 7 * 24 * 60 * 60000;     // 7天
    private static final String SECRET = "YiBaN#2018";            // JWT密码
    private static final String TOKEN_PREFIX = "Bearer";        // Token前缀
    private static final String HEADER_STRING = "Authorization";// 存放Token的Header Key

    static void addAuthentication(HttpServletResponse response, Authentication authentication) {

        List<GrantedAuthority> auths = (List<GrantedAuthority>) authentication.getAuthorities();
        StringBuilder sb = new StringBuilder();
        List<String> authPageList = new ArrayList<>();
        JSONObject result = new JSONObject();
        for (GrantedAuthority auth : auths) {
            sb.append(auth.getAuthority());
            authPageList.add(auth.getAuthority()); //定义为用户可以看见的路由名称
        }
        User user = (User) authentication.getPrincipal();

        // 生成JWT
        String JWT = Jwts.builder()
                // 保存权限（角色）
                .claim("authorities", sb.toString())
                // 用户名写入标题
//                .setSubject(authentication.getName())
                .claim("user", JSON.toJSONString(authentication.getPrincipal()))
//                .setSubject(authentication.getName())
                // 有效期设置
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                // 签名设置
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();

        // 生成登录日志


        // 将 结果 写入 body
        try {
            result.put("jwt", JWT);
            result.put("authPages", authPageList);
            result.put("userDetail", user);
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(result.toJSONString());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    // JWT验证方法
    static Authentication getAuthentication(HttpServletRequest request) {
        // 从Header中拿到token
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            try {
                // 解析 Token
                Claims claims = Jwts.parser()
                        // 验签
                        .setSigningKey(SECRET)
                        // 去掉 Bearer
                        .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                        .getBody();

                // 拿用户
                User user = null;
                if (claims.containsKey("user")) {
                    String userStr = (String) claims.get("user");
                    user = JSON.parseObject(userStr, User.class);
                }

                // 得到 权限（角色）
                List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList((String) claims.get("authorities"));

                // 返回验证令牌
                return user != null ?
                        new UsernamePasswordAuthenticationToken(user, null, authorities) :
                        null;

            }catch (JwtException e) {
                logger.warn("get an JWTException.{}", e.getMessage());
            }
        }
        return null;
    }

}
