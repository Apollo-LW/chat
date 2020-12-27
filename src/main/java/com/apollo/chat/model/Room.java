package com.apollo.chat.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@Data
public class Room {

    private String roomId = UUID.randomUUID().toString(), roomName;
    private HashSet<String> roomAdmins = new HashSet<>(), roomMembers = new HashSet<>();
    private HashSet<Message> roomMessages = new HashSet<>();
    private Boolean isActive = true;

    public Room addMessage(Message message) {
        this.roomMessages.add(message);
        return this;
    }

    public Room addMember(String memberId) {
        this.roomMembers.add(memberId);
        return this;
    }

    public Room addAdmin(String adminId) {
        this.roomAdmins.add(adminId);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(roomId , room.roomId);
    }
}
