package com.apollo.chat.model;

import lombok.Data;

import java.util.HashSet;

@Data
public class UserRoom {

    private String userId;
    private HashSet<Room> userRooms = new HashSet<>();

    public UserRoom addRoom(Room room) {
        userRooms.add(room);
        return this;
    }

}
