package com.apollo.chat.model;

import lombok.Data;

@Data
public class ModifyRoom {

    private String roomId, roomAdminId, roomName;
}
