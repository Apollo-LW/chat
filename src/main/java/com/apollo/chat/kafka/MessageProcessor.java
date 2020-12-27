package com.apollo.chat.kafka;

import com.apollo.chat.model.Message;
import com.apollo.chat.model.Room;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class MessageProcessor {

    @Value("${chat.kafka.store}")
    private String chatStateStoreName;

    @Bean
    public Function<KStream<String, Message>, KTable<String, Room>> messageStateProcessor() {
        return messageKStream -> messageKStream
                .groupByKey(Grouped.with(Serdes.String() , CustomSerdes.messageSerde()))
                .aggregate(Room::new , (roomId , message , room) -> room.addMessage(message) , Materialized.with(Serdes.String() , CustomSerdes.roomSerde()))
                .toStream()
                .groupByKey()
                .reduce((room , updatedRoom) -> updatedRoom , Materialized.as(this.chatStateStoreName));
    }

}
