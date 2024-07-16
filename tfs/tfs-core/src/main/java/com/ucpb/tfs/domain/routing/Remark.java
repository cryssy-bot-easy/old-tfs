package com.ucpb.tfs.domain.routing;

import com.ipc.rbac.domain.User;

import java.util.Date;

/**
 */
public class Remark {

    private Long id;

    private String remarkId;

    private User user;

    private Date dateCreated = new Date();

    private String message;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        updateDate();
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRemarkId() {
        return remarkId;
    }

    public void setRemarkId(String remarkId) {
        this.remarkId = remarkId;
    }

    private void updateDate(){
        dateCreated = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Remark remark = (Remark) o;

        if (id != null ? !id.equals(remark.id) : remark.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Remark{" +
                "id=" + id +
                ", remarkId='" + remarkId + '\'' +
                ", user=" + user +
                ", dateCreated=" + dateCreated +
                ", message='" + message + '\'' +
                '}';
    }

    public Remark duplicateRemark(String tradeServiceId) {
        Remark remark = new Remark();


        remark.setRemarkId(tradeServiceId);
        remark.setUser(this.getUser());
        remark.setMessage(this.getMessage());

        return remark;
    }
}
