package com.apollo.chat.model;

import lombok.Data;

import java.util.Set;

@Data
public class ShareRoom {

    private String roomId, adminId;
    private Set<String> userIds;

}
