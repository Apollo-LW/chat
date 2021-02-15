package com.apollo.chat.kafka.processor;

import com.apollo.chat.kafka.CustomSerdes;
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
public class RoomProcessor {

    @Value("${chat.kafka.store}")
    private String chatStateStoreName;
    @Value("${user.kafka.store}")
    private String userRoomStateStoreName;
    @Value("${room.kafka.store}")
    private String roomStateStoreName;

    @Bean
    public Function<KStream<String, Message>, KTable<String, Room>> roomMessagesProcessorState() {
        return messageKStream -> messageKStream
                .groupByKey(Grouped.with(Serdes.String() , CustomSerdes.messageSerde()))
                .aggregate(Room::new ,
                        (roomId , message , room) -> room.addMember(message.getMessageSenderId()).addMessage(message) ,
                        Materialized.with(Serdes.String() , CustomSerdes.roomSerde()))
                .toStream()
                .groupByKey(Grouped.with(Serdes.String() , CustomSerdes.roomSerde()))
                .reduce((room , updatedRoom) -> updatedRoom , Materialized.as(this.chatStateStoreName));
    }

    @Bean
    public Function<KStream<String, Room>, KTable<String, UserRoom>> userRoomProcessor() {
        return roomKStream -> roomKStream
                .flatMap((roomId , room) -> room.getRoomMembers().stream().map(memberId -> new KeyValue<String, Room>(memberId , room)).collect(Collectors.toSet()))
                .groupByKey(Grouped.with(Serdes.String() , CustomSerdes.roomSerde()))
                .aggregate(UserRoom::new ,
                        (memberId , room , userRoom) -> userRoom.addRoom(room) ,
                        Materialized.with(Serdes.String() , CustomSerdes.userRoomSerde()))
                .toStream()
                .groupByKey(Grouped.with(Serdes.String() , CustomSerdes.userRoomSerde()))
                .reduce((userRoom , updatedUserRoom) -> updatedUserRoom , Materialized.as(this.userRoomStateStoreName));
    }

    @Bean
    public Function<KStream<String, Room>, KTable<String, Room>> roomProcessorState() {
        return roomKStream -> roomKStream
                .groupByKey(Grouped.with(Serdes.String() , CustomSerdes.roomSerde()))
                .reduce((room , updatedRoom) -> updatedRoom , Materialized.as(this.roomStateStoreName));
    }

}
