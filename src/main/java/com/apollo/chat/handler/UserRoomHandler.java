package com.apollo.chat.handler;

import com.apollo.chat.constant.RoutingConstant;
import com.apollo.chat.model.Room;
import com.apollo.chat.service.UserRoomService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserRoomHandler {

    private final UserRoomService userRoomService;

    public @NotNull Mono<ServerResponse> getUserRooms(ServerRequest request) {
        final String userId = request.pathVariable(RoutingConstant.USER_ID);
        final Flux<Room> userRooms = this.userRoomService.getUserRooms(userId);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userRooms , Room.class);
    }

}
