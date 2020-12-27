package com.apollo.chat.kafka;

import com.apollo.chat.model.Room;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class RoomProcessor {

    @Value("${room.kafka.store}")
    private String roomStateStoreName;

    @Bean
    public Function<KStream<String, Room>, KTable<String, Room>> roomStateProcessor() {
        return roomKStream -> roomKStream.groupByKey().reduce((room , updatedRoom) -> updatedRoom , Materialized.as(this.roomStateStoreName));
    }

}
