package com.apollo.chat.controller;

import com.apollo.chat.model.Message;
import com.apollo.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/{roomId}")
    public Flux<Message> getRoomMessages(@PathVariable("roomId") String roomId) {
        return this.chatService.getMessagesByRoomId(roomId);
    }

    @PostMapping("/")
    public Mono<Boolean> sendMessage(@RequestBody Mono<Message> messageMono) {
        return this.chatService.sendMessage(messageMono).map(Optional::isPresent);
    }

}
