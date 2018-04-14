package com.yiban.erp.service.auth;

import com.yiban.erp.entities.User;

import java.util.List;

public interface TokenManager {

    static final String SECRET = "YiBaN#2018";
    static final int EXPIRATIONTIME = 7; //有效期，单位为天
    static final String TOKEN_PREFIX = "Bearer "; //token前缀 (注意多一个空格，因为在JWT验证中会存在有个空格)
    static final String HEADER_STRING = "Authorization";// 存放Token的Header Key

    /**
     * 根据用户创建一个TokenModel
     * @param user
     * @return
     */
    public String createToken(User user, List<String> accessApiList);


    /**
     * 根据请求头中的token字符串解析获取TokenModel
     * @param token
     * @return 如果返回null,证明解析失败，token无效
     */
    public TokenModel getTokenModel(String token);

    /**
     * 根据用户ID清除一个token
     * @param user
     */
    public void removeToken(User user);
}
