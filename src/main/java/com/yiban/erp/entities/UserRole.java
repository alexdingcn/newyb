package com.yiban.erp.entities;

import java.util.Objects;

public class UserRole {
    private Long id;

    private Long userId;

    private String name;

    private Boolean canWrite;

    private Boolean canUpdate;

    private Boolean canAudit;

    private Boolean canDelete;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Boolean getCanWrite() {
        return canWrite;
    }

    public void setCanWrite(Boolean canWrite) {
        this.canWrite = canWrite;
    }

    public Boolean getCanUpdate() {
        return canUpdate;
    }

    public void setCanUpdate(Boolean canUpdate) {
        this.canUpdate = canUpdate;
    }

    public Boolean getCanAudit() {
        return canAudit;
    }

    public void setCanAudit(Boolean canAudit) {
        this.canAudit = canAudit;
    }

    public Boolean getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(Boolean canDelete) {
        this.canDelete = canDelete;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof UserRole)) {
            return false;
        }
        UserRole userRole = (UserRole) o;
        return Objects.equals(userId, userRole.getUserId())
                && Objects.equals(name, userRole.getName())
                && Objects.equals(canAudit, userRole.getCanAudit())
                && Objects.equals(canDelete, userRole.getCanDelete())
                && Objects.equals(canUpdate, userRole.getCanUpdate())
                && Objects.equals(canWrite, userRole.getCanWrite());

    }
}