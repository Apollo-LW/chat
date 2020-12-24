package com.apollo.chat.model;

import lombok.Data;

import java.util.HashSet;

@Data
public class User {

    private String userId;
    private HashSet<Message> userMessages = new HashSet<>();

    public void addMessage(Message message) {
        this.userMessages.add(message);
    }

}
