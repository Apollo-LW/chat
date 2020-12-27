package com.apollo.chat.service;

import com.apollo.chat.model.ModifyRoom;
import com.apollo.chat.model.Room;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface RoomService {

    Mono<Optional<Room>> createRoom(Mono<Room> roomMono);

    Mono<Optional<Room>> updateRoom(Mono<ModifyRoom> modifyRoomMono);

    Mono<Optional<Room>> getRoomById(String roomId);

    Mono<Boolean> addMember(Flux<String> membersIds , String roomId , String adminId);

    Mono<Boolean> addOwners(Flux<String> ownersIds , String roomId , String adminId);

}
