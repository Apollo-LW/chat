package com.apollo.chat.handler;

import com.apollo.chat.constant.RoutingConstant;
import com.apollo.chat.model.ModifyRoom;
import com.apollo.chat.model.Room;
import com.apollo.chat.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RoomHandler {

    private final RoomService roomService;

    public @NotNull Mono<ServerResponse> getRoomById(ServerRequest request) {
        final String roomId = request.pathVariable(RoutingConstant.ROOM_ID);
        final Mono<Room> roomMono = this.roomService.getRoomById(roomId).flatMap(Mono::justOrEmpty);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(roomMono , Room.class);
    }

    public @NotNull Mono<ServerResponse> createRoom(ServerRequest request) {
        final Mono<Room> roomMono = request.bodyToMono(Room.class);
        final Mono<Room> createdRoomMono = this.roomService.createRoom(roomMono).flatMap(Mono::justOrEmpty);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createdRoomMono , Room.class);
    }

    public @NotNull Mono<ServerResponse> updateRoom(ServerRequest request) {
        final Mono<ModifyRoom> modifyRoomMono = request.bodyToMono(ModifyRoom.class);
        final Mono<Boolean> isRoomUpdated = this.roomService.updateRoom(modifyRoomMono);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(isRoomUpdated , Boolean.class);
    }

    public @NotNull Mono<ServerResponse> deleteRoom(ServerRequest request) {
        final String roomId = request.pathVariable(RoutingConstant.ROOM_ID);
        final Mono<Boolean> isRoomDeleted = this.roomService.deleteRoomById(roomId);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(isRoomDeleted , Boolean.class);
    }

}
