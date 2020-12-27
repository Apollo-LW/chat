package com.apollo.chat.controller;

import com.apollo.chat.model.ModifyRoom;
import com.apollo.chat.model.Room;
import com.apollo.chat.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/chat/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/{roomId}")
    public Mono<Room> getRoomById(@PathVariable("roomId") String roomId) {
        return this.roomService.getRoomById(roomId).flatMap(Mono::justOrEmpty);
    }

    @PostMapping("/")
    public Mono<Room> createRoom(@RequestBody Mono<Room> roomMono) {
        return this.roomService.createRoom(roomMono).flatMap(Mono::justOrEmpty);
    }

    @PutMapping("/")
    public Mono<Room> updateRoom(@RequestBody Mono<ModifyRoom> modifyRoomMono) {
        return this.roomService.updateRoom(modifyRoomMono).flatMap(Mono::justOrEmpty);
    }

    @DeleteMapping("/{roomId}")
    public Mono<Boolean> deleteRoom(@PathVariable("roomId") String roomId) {
        return this.roomService.deleteRoomById(roomId);
    }

}
