package com.apollo.chat.service.impl;

import com.apollo.chat.kafka.KafkaService;
import com.apollo.chat.model.Message;
import com.apollo.chat.model.Room;
import com.apollo.chat.service.ChatService;
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
public class ChatServiceImpl implements ChatService {

    private final InteractiveQueryService interactiveQueryService;
    private final KafkaService kafkaService;
    @Value("${chat.kafka.store}")
    private String chatStateStoreName;
    private ReadOnlyKeyValueStore<String, Room> roomReadOnlyKeyValueStore;

    private ReadOnlyKeyValueStore<String, Room> getRoomReadOnlyKeyValueStore() {
        if (this.roomReadOnlyKeyValueStore == null)
            this.roomReadOnlyKeyValueStore = this.interactiveQueryService.getQueryableStore(this.chatStateStoreName , QueryableStoreTypes.keyValueStore());
        return this.roomReadOnlyKeyValueStore;
    }

    @Override
    public Mono<Optional<Message>> sendMessage(Mono<Message> messageMono) {
        return this.kafkaService.sendMessageRecord(messageMono);
    }

    @Override
    public Flux<Message> getMessagesByRoomId(String roomId) {
        return Flux.fromIterable(this.getRoomReadOnlyKeyValueStore().get(roomId).getRoomMessages());
    }
}
