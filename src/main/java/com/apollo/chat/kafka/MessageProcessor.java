package com.apollo.chat.kafka;

import com.apollo.chat.model.Message;
import com.apollo.chat.model.Room;
import com.apollo.chat.model.UserRoom;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MessageProcessor {

    @Value("${chat.kafka.store}")
    private String chatStateStoreName;
    @Value("${user.kafka.store}")
    private String userRoomStateStoreName;

    @Bean
    public Function<KStream<String, Message>, KTable<String, Room>> messageStateProcessor() {
        return messageKStream -> {
            KStream<String, Room> roomKStream = messageKStream
                    .groupByKey(Grouped.with(Serdes.String() , CustomSerdes.messageSerde()))
                    .aggregate(Room::new , (roomId , message , room) -> room.addMessage(message) , Materialized.with(Serdes.String() , CustomSerdes.roomSerde())).toStream();

            roomKStream
                    .flatMap((roomId , room) -> room.getRoomMembers().stream().map(memberId -> new KeyValue<String, Room>(memberId , room)).collect(Collectors.toSet()))
                    .groupByKey()
                    .aggregate(UserRoom::new , (key , value , aggregate) -> {
                        aggregate.setUserId(key);
                        return aggregate.addRoom(value);
                    } , Materialized.with(Serdes.String() , CustomSerdes.userRoomSerde()))
                    .toStream()
                    .groupBy((key , value) -> value.getUserId() , Grouped.with(Serdes.String() , CustomSerdes.userRoomSerde()))
                    .reduce((userRoom , updatedUserRoom) -> updatedUserRoom , Materialized.as(this.userRoomStateStoreName));

            return roomKStream
                    .groupByKey()
                    .reduce((room , updatedRoom) -> updatedRoom , Materialized.as(this.chatStateStoreName));
        };
    }

}
