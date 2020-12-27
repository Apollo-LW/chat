package com.apollo.chat.service;

import com.apollo.chat.model.Room;
import reactor.core.publisher.Flux;

public interface UserRoomService {

    Flux<Room> getUserRooms(String userId);

}
