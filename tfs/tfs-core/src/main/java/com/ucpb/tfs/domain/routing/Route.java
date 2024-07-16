package com.ucpb.tfs.domain.routing;

import com.ipc.rbac.domain.User;

import java.io.Serializable;
import java.util.Date;

/**
 */
public class Route implements Serializable {

    private Long id;

    private User sender;

    private User receiver;

    private String status;

    private Date dateSent = new Date();

    public Route(){}

    public Route(User sender, User receiver, String status){
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
        this.dateSent = new Date();
    }

    public Route(User sender,User receiver, String status, Date dateSent){
        this(sender, receiver,status);
        this.dateSent = dateSent;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", status='" + status + '\'' +
                ", dateSent=" + dateSent +
                '}';
    }
}
