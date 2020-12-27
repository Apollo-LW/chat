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
            KTable<String, Room> roomKTable = messageKStream
                    .groupByKey(Grouped.with(Serdes.String() , CustomSerdes.messageSerde()))
                    .aggregate(Room::new , (roomId , message , room) -> {
                        room.setRoomId(roomId);
                        return room.addMember(message.getMessageSenderId()).addMessage(message);
                    } , Materialized.with(Serdes.String() , CustomSerdes.roomSerde())).toStream()
                    .groupByKey(Grouped.with(Serdes.String() , CustomSerdes.roomSerde()))
                    .reduce((room , updatedRoom) -> updatedRoom , Materialized.as(this.chatStateStoreName));

            roomKTable
                    .toStream()
                    .flatMap((roomId , room) -> room.getRoomMembers().stream().map(memberId -> new KeyValue<String, Room>(memberId , room)).collect(Collectors.toSet()))
                    .groupByKey(Grouped.with(Serdes.String() , CustomSerdes.roomSerde()))
                    .aggregate(UserRoom::new , (memberId , room , userRoom) -> {
                        userRoom.setUserId(memberId);
                        return userRoom.addRoom(room);
                    } , Materialized.with(Serdes.String() , CustomSerdes.userRoomSerde()))
                    .toStream()
                    .groupByKey(Grouped.with(Serdes.String() , CustomSerdes.userRoomSerde()))
                    .reduce((userRoom , updatedUserRoom) -> updatedUserRoom , Materialized.as(this.userRoomStateStoreName));

            return roomKTable;
        };
    }

}
