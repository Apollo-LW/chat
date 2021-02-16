package com.apollo.chat.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
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

    public Boolean doesNotHaveAdmin(String adminId) {
        return !this.roomAdmins.contains(adminId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(roomId , room.roomId);
    }

    public Boolean addMembers(Set<String> membersIds) {
        return this.roomMembers.addAll(membersIds);
    }

    public Boolean addAdmins(Set<String> adminIds) {
        return this.roomAdmins.addAll(adminIds);
    }

    public Room addMember(String memberId) {
        this.roomMembers.add(memberId);
        return this;
    }
}
