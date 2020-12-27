package com.apollo.chat.service.impl;

import com.apollo.chat.kafka.KafkaService;
import com.apollo.chat.model.ModifyRoom;
import com.apollo.chat.model.Room;
import com.apollo.chat.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final KafkaService kafkaService;
    private final InteractiveQueryService interactiveQueryService;
    @Value("${room.kafka.store}")
    private String roomStateStoreName;
    private ReadOnlyKeyValueStore<String, Room> roomStateStore;

    private ReadOnlyKeyValueStore<String, Room> getRoomStateStore() {
        if (this.roomStateStore == null)
            this.roomStateStore = this.interactiveQueryService.getQueryableStore(this.roomStateStoreName , QueryableStoreTypes.keyValueStore());
        return this.roomStateStore;
    }

    private boolean isNotValid(Optional<Room> room , String adminId) {
        return room.isEmpty() || !room.get().getRoomAdmins().contains(adminId);
    }

    @Override
    public Mono<Optional<Room>> createRoom(Mono<Room> roomMono) {
        return this.kafkaService.sendRoomRecord(roomMono);
    }

    @Override
    public Mono<Optional<Room>> updateRoom(Mono<ModifyRoom> modifyRoomMono) {
        return modifyRoomMono.flatMap(modifyRoom -> {
            Optional<Room> optionalRoom = Optional.ofNullable(this.getRoomStateStore().get(modifyRoom.getRoomId()));
            if (optionalRoom.isEmpty()) return Mono.empty();
            Room room = this.getRoomStateStore().get(modifyRoom.getRoomId());
            room.setRoomName(modifyRoom.getRoomName());
            return this.kafkaService.sendRoomRecord(Mono.just(room));
        });
    }

    @Override
    public Mono<Optional<Room>> getRoomById(String roomId) {
        return Mono.just(Optional.ofNullable(this.getRoomStateStore().get(roomId)));
    }

    @Override
    public Mono<Boolean> deleteRoomById(String roomId) {
        Optional<Room> optionalRoom = Optional.ofNullable(this.getRoomStateStore().get(roomId));
        if (optionalRoom.isEmpty()) return Mono.just(false);
        Room room = optionalRoom.get();
        room.setIsActive(false);
        return this.kafkaService.sendRoomRecord(Mono.just(room)).map(Optional::isPresent);
    }

    @Override
    public Mono<Boolean> addMember(Flux<String> membersIds , String roomId , String adminId) {
        Optional<Room> optionalRoom = Optional.ofNullable(this.getRoomStateStore().get(roomId));
        if (this.isNotValid(optionalRoom , adminId)) return Mono.just(false);
        Room room = optionalRoom.get();
        return membersIds.flatMap(memberId -> Mono.just(room.addMember(memberId) != null))
                .all(aBoolean -> aBoolean)
                .flatMap(result -> this.kafkaService.sendRoomRecord(Mono.just(room)).map(Optional::isPresent));
    }

    @Override
    public Mono<Boolean> addOwners(Flux<String> adminsIds , String roomId , String adminId) {
        Optional<Room> optionalRoom = Optional.ofNullable(this.roomStateStore.get(roomId));
        if (this.isNotValid(optionalRoom , adminId)) return Mono.just(false);
        Room room = optionalRoom.get();
        return adminsIds.flatMap(newAdminId -> Mono.just(room.addAdmin(newAdminId) != null)).all(aBoolean -> aBoolean);
    }
}
