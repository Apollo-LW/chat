package com.apollo.chat.service;

import com.apollo.chat.model.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface ChatService {

    Flux<Message> getMessagesByRoomId(String roomId);

    Mono<Optional<Message>> sendMessage(Mono<Message> messageMono);

}
