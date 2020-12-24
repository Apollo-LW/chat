package com.apollo.chat.model;

import lombok.Data;

import java.util.HashSet;
import java.util.UUID;

@Data
public class Room {

    private final String roomId = UUID.randomUUID().toString();
    private HashSet<String> roomAdmins = new HashSet<>(), roomMembers = new HashSet<>();
    private HashSet<Message> roomMessages = new HashSet<>();


    public void addMessage(Message message) {
        this.roomMessages.add(message);
    }

}
