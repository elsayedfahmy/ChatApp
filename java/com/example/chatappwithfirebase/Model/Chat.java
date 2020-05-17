package com.example.chatappwithfirebase.Model;

public class Chat {
    private String reciver;
    private String sender;
    private String message;
    private boolean if_seen;



    public Chat()
    {

    }
    public Chat(String reciver, String sender, String message,boolean if_seen) {
        this.reciver = reciver;
        this.sender = sender;
        this.message = message;
        this.if_seen = if_seen;

    }

    public boolean isIf_seen() {
        return if_seen;
    }

    public void setIf_seen(boolean if_seen) {
        this.if_seen = if_seen;
    }

    public String getReciver() {
        return reciver;
    }

    public void setReciver(String reciver) {
        this.reciver = reciver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
