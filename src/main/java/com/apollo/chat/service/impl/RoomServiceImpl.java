package com.apollo.chat.service.impl;

import com.apollo.chat.kafka.KafkaService;
import com.apollo.chat.model.ModifyRoom;
import com.apollo.chat.model.Room;
import com.apollo.chat.model.ShareRoom;
import com.apollo.chat.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;
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

    private boolean isNotValid(final Optional<Room> room , final String adminId) {
        return room.isEmpty() || !room.get().getIsActive() || room.get().doesNotHaveAdmin(adminId);
    }

    @Override
    public Mono<Optional<Room>> createRoom(final Mono<Room> roomMono) {
        return this.kafkaService.sendRoomRecord(roomMono);
    }

    @Override
    public Mono<Boolean> updateRoom(final Mono<ModifyRoom> modifyRoomMono) {
        return modifyRoomMono.flatMap(modifyRoom -> this.getRoomById(modifyRoom.getRoomId()).flatMap(roomOptional -> {
            if (this.isNotValid(roomOptional , modifyRoom.getRoomAdminId())) return Mono.just(false);
            final Room room = roomOptional.get();
            room.setRoomName(modifyRoom.getRoomName());
            return this.kafkaService.sendRoomRecord(Mono.just(room)).map(Optional::isPresent);
        }));
    }

    @Override
    public Mono<Optional<Room>> getRoomById(final String roomId) {
        return Mono.just(Optional.ofNullable(this.getRoomStateStore().get(roomId)));
    }

    @Override
    public Mono<Boolean> deleteRoomById(final String roomId , final String adminId) {
        return this.getRoomById(roomId).flatMap(roomOptional -> {
            if (this.isNotValid(roomOptional , adminId)) return Mono.just(false);
            final Room room = roomOptional.get();
            room.setIsActive(false);
            return this.kafkaService.sendRoomRecord(Mono.just(room)).map(Optional::isPresent);
        });
    }

    @Override
    public Mono<Boolean> addMember(final Mono<ShareRoom> shareRoomMono) {
        return shareRoomMono.flatMap(shareRoom -> this.getRoomById(shareRoom.getRoomId()).flatMap(roomOptional -> {
            if (this.isNotValid(roomOptional , shareRoom.getAdminId())) return Mono.just(false);
            final Room room = roomOptional.get();
            Boolean isAdded = room.addMembers(shareRoom.getUserIds());
            return this.kafkaService.sendRoomRecord(Mono.just(room)).map(updatedRoom -> updatedRoom.isPresent() && isAdded);
        }));
    }

    @Override
    public Mono<Boolean> addAdmins(final Mono<ShareRoom> shareRoomMono) {
        return shareRoomMono.flatMap(shareRoom -> this.getRoomById(shareRoom.getRoomId()).flatMap(roomOptional -> {
            if (this.isNotValid(roomOptional , shareRoom.getAdminId())) return Mono.just(false);
            final Room room = roomOptional.get();
            Boolean isAdded = room.addAdmins(shareRoom.getUserIds());
            return this.kafkaService.sendRoomRecord(Mono.just(room)).map(updatedRoom -> updatedRoom.isPresent() && isAdded);
        }));
    }
}
