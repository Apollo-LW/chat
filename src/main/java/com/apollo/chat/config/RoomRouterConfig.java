package com.apollo.chat.config;

import com.apollo.chat.constant.RoutingConstant;
import com.apollo.chat.handler.RoomHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RoomRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> roomRoute(RoomHandler roomHandler) {
        return RouterFunctions
                .route()
                .path(RoutingConstant.ROOM_PATH , routeBuilderFunction ->
                        routeBuilderFunction.nest(RequestPredicates.accept(MediaType.APPLICATION_JSON) , builder -> builder
                                .GET(RoutingConstant.ROOM_ID_PATH , roomHandler::getRoomById)
                                .POST(roomHandler::createRoom)
                                .PUT(roomHandler::updateRoom)
                                .DELETE(RoutingConstant.ROOM_ID_PATH , roomHandler::deleteRoom)))
                .build();
    }

}
