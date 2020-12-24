package com.apollo.chat.model;

import lombok.Data;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Data
public class Message {

    private final String messageId = UUID.randomUUID().toString();
    private final Date messageSendAt = Calendar.getInstance().getTime();
    private String messageParent;
    private String messageText;
    private String messageSenderId, messageRoomId;

}
