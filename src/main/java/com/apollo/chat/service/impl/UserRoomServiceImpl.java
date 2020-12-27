package com.apollo.chat.service.impl;

import com.apollo.chat.model.Room;
import com.apollo.chat.model.UserRoom;
import com.apollo.chat.service.UserRoomService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class UserRoomServiceImpl implements UserRoomService {

    private final InteractiveQueryService interactiveQueryService;
    @Value("${user.kafka.store}")
    private String userRoomStateStoreName;
    private ReadOnlyKeyValueStore<String, UserRoom> userRoomStateStore;

    private ReadOnlyKeyValueStore<String, UserRoom> getUserRoomStateStore() {
        if (this.userRoomStateStore == null)
            this.userRoomStateStore = this.interactiveQueryService.getQueryableStore(this.userRoomStateStoreName , QueryableStoreTypes.keyValueStore());
        return this.userRoomStateStore;
    }

    @Override
    public Flux<Room> getUserRooms(String userId) {
        return Flux.fromIterable(this.getUserRoomStateStore().get(userId).getUserRooms());
    }
}
