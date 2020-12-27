package com.apollo.chat.kafka;

import com.apollo.chat.model.Message;
import com.apollo.chat.model.Room;
import com.apollo.chat.model.UserRoom;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.jetbrains.annotations.Contract;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

public final class CustomSerdes {

    @Contract(" -> new")
    public static Serde<Message> messageSerde() {
        return new CustomSerdes.MessageSerde();
    }

    @Contract(" -> new")
    public static Serde<Room> roomSerde() {
        return new CustomSerdes.RoomSerde();
    }

    @Contract(" -> new")
    public static Serde<UserRoom> userRoomSerde() {
        return new CustomSerdes.UserRoomSerde();
    }

    static public final class MessageSerde extends Serdes.WrapperSerde<Message> {
        public MessageSerde() {
            super(new JsonSerializer<>() , new JsonDeserializer<>(Message.class));
        }
    }

    static public final class RoomSerde extends Serdes.WrapperSerde<Room> {
        public RoomSerde() {
            super(new JsonSerializer<>() , new JsonDeserializer<>(Room.class));
        }
    }

    static public final class UserRoomSerde extends Serdes.WrapperSerde<UserRoom> {
        public UserRoomSerde() {
            super(new JsonSerializer<>() , new JsonDeserializer<>(UserRoom.class));
        }
    }

}
