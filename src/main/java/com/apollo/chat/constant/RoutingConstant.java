package com.apollo.chat.constant;

public abstract class RoutingConstant {

    public static final String CHAT_PATH = "/chat";
    public static final String ROOM_PATH = CHAT_PATH + "/room";
    public static final String USER_PATH = CHAT_PATH + "/user";
    public static final String ROOM_ID = "roomId";
    public static final String USER_ID = "userId";
    public static final String ADMIN_ID = "adminId";
    public static final String ROOM_ID_PATH = "/{" + ROOM_ID + "}";
    public static final String USER_ID_PATH = "/{" + USER_ID + "}";
    public static final String ADMIN_ID_PATH = "/{" + ADMIN_ID + "}";
    public static final String DELETE_PATH = ROOM_ID_PATH + ADMIN_ID_PATH;
}
