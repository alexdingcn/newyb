package com.yiban.erp.controller.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.UserRoleMapper;
import com.yiban.erp.dto.SaveUserRoleReq;
import com.yiban.erp.entities.User;
import com.yiban.erp.entities.UserRole;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/userrole")
public class UserRoleController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRoleMapper userRoleMapper;

    /**
     * 获取用户+权限的列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> listUsersIdsWithRole(@AuthenticationPrincipal User user,
                                                       @RequestParam(required = false) String roleQuery) {
        String[] roles = null;
        if (roleQuery != null) {
            roles = roleQuery.toUpperCase().split(";");
        }

        List<UserRole> userList = userRoleMapper.findByRoles(Arrays.asList(roles), user.getCompanyId());
        return ResponseEntity.ok().body(JSON.toJSONString(userList));
    }


    /**
     * 获取用户+权限的列表
     *
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveUserRoles(@AuthenticationPrincipal User user,
                                                @RequestBody SaveUserRoleReq roleReq) throws Exception {

        if (roleReq == null || roleReq.getRoles() == null || roleReq.getRoleTypes() == null
                || roleReq.getRoleTypes().isEmpty()) {
            logger.warn("update user roles params is error. params:{}", JSON.toJSONString(roleReq));
            throw new BizException(ErrorCode.USER_ROLE_SAVE_PARAMS);
        }
        List<UserRole> roles = roleReq.getRoles();
        logger.info("update user roles");
        List<UserRole> userRoleList = userRoleMapper.findByRoles(roleReq.getRoleTypes(), user.getCompanyId());
        Map<String, UserRole> roleMap = new HashMap<>();
        Map<Long, UserRole> idRoleMap = new HashMap<>();

        for (UserRole role : userRoleList) {
            roleMap.put(role.getUserId() + role.getName(), role);
            idRoleMap.put(role.getId(), role);
        }

        for (UserRole role : roles) {
            if (role.getId() == null) {
                role.setCanWrite(true);
                role.setCanDelete(true);
                role.setCanUpdate(true);
                role.setCanAudit(true);
            } else {
                idRoleMap.remove(role.getId());
                // compare existing role
                UserRole existingRole = roleMap.get(role.getUserId() + role.getName());
                if (existingRole != null) {
                    if (existingRole.equals(role)) {
                        continue;
                    }
                }
            }
            userRoleMapper.replaceInto(role);
        }

        // delete
        for (UserRole role : idRoleMap.values()) {
            userRoleMapper.deleteByPrimaryKey(role.getId());
        }

        return ResponseEntity.ok().body(JSON.toJSONString(roles));
    }
}
