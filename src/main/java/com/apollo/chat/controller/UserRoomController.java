package com.apollo.chat.controller;

import com.apollo.chat.model.Room;
import com.apollo.chat.service.UserRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class UserRoomController {

    private final UserRoomService userRoomService;

    @GetMapping("/{userId}")
    public Flux<Room> getUserRooms(@PathVariable("userId") String userId) {
        return this.userRoomService.getUserRooms(userId);
    }

}
