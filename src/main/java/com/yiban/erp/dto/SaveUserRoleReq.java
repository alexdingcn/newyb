package com.yiban.erp.dto;

import com.yiban.erp.entities.UserRole;

import java.util.List;

public class SaveUserRoleReq {

    private List<String> roleTypes;
    private List<UserRole> roles;

    public List<String> getRoleTypes() {
        return roleTypes;
    }

    public void setRoleTypes(List<String> roleTypes) {
        this.roleTypes = roleTypes;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }
}
