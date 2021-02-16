package com.apollo.chat.service;

import com.apollo.chat.model.ModifyRoom;
import com.apollo.chat.model.Room;
import com.apollo.chat.model.ShareRoom;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface RoomService {

    Mono<Optional<Room>> createRoom(final Mono<Room> roomMono);

    Mono<Boolean> updateRoom(final Mono<ModifyRoom> modifyRoomMono);

    Mono<Optional<Room>> getRoomById(final String roomId);

    Mono<Boolean> deleteRoomById(final String roomId , final String adminId);

    Mono<Boolean> addMember(final Mono<ShareRoom> shareRoomMono);

    Mono<Boolean> addAdmins(final Mono<ShareRoom> shareRoomMono);

}
