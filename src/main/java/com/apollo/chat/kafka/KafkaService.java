package com.apollo.chat.kafka;

import com.apollo.chat.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.util.Optional;

@Service
@CommonsLog
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaSender<String, Message> messageKafkaSender;
    @Value("${chat.kafka.topic}")
    private String chatTopicName;

    public Mono<Optional<Message>> sendMessageRecord(Mono<Message> messageMono) {
        return messageMono.flatMap(message -> this.messageKafkaSender
                .send(Mono.just(SenderRecord.create(new ProducerRecord<>(this.chatTopicName , message.getMessageRoomId() , message) , 1)))
                .next()
                .doOnNext(log::info)
                .doOnError(log::error)
                .map(integerSenderResult -> integerSenderResult.exception() == null ? Optional.of(message) : Optional.empty())
        );
    }

}
