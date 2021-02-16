package com.apollo.chat.handler;

import com.apollo.chat.constant.RoutingConstant;
import com.apollo.chat.model.Message;
import com.apollo.chat.service.ChatService;
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
public class ChatHandler {

    private final ChatService chatService;

    public @NotNull Mono<ServerResponse> getRoomMessages(final ServerRequest request) {
        final String roomId = request.pathVariable(RoutingConstant.ROOM_ID);
        final Flux<Message> messageFlux = this.chatService.getMessagesByRoomId(roomId);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(messageFlux , Message.class);
    }

    public @NotNull Mono<ServerResponse> sendMessage(final ServerRequest request) {
        final Mono<Message> messageMono = request.bodyToMono(Message.class);
        final Mono<Message> sentMessageMono = this.chatService.sendMessage(messageMono).flatMap(Mono::justOrEmpty);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(sentMessageMono , Message.class);
    }
}
