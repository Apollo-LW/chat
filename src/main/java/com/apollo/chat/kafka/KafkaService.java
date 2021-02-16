package com.apollo.chat.kafka;

import com.apollo.chat.model.Message;
import com.apollo.chat.model.Room;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KafkaService {

    @Value("${chat.kafka.topic}")
    private String chatTopicName;
    @Value("${room.kafka.topic}")
    private String roomTopicName;
    private final KafkaSender<String, Room> roomKafkaSender;
    private final KafkaSender<String, Message> messageKafkaSender;

    public Mono<Optional<Message>> sendMessageRecord(final Mono<Message> messageMono) {
        return messageMono.flatMap(message -> this.messageKafkaSender
                .send(Mono.just(SenderRecord.create(new ProducerRecord<>(this.chatTopicName , message.getMessageRoomId() , message) , message.getMessageRoomId())))
                .next()
                .map(senderResult -> senderResult.exception() == null ? Optional.of(message) : Optional.empty())
        );
    }

    public Mono<Optional<Room>> sendRoomRecord(final Mono<Room> roomMono) {
        return roomMono.flatMap(room -> this.roomKafkaSender
                .send(Mono.just(SenderRecord.create(new ProducerRecord<>(this.roomTopicName , room.getRoomId() , room) , room.getRoomId())))
                .next()
                .map(senderResult -> senderResult.exception() == null ? Optional.of(room) : Optional.empty())
        );
    }

}
