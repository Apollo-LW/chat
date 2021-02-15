package com.apollo.chat.config;

import com.apollo.chat.constant.RoutingConstant;
import com.apollo.chat.handler.ChatHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ChatRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> chatRoute(ChatHandler chatHandler) {
        return RouterFunctions
                .route()
                .path(RoutingConstant.CHAT_PATH , routeFunctionBuilder ->
                        routeFunctionBuilder.nest(RequestPredicates.accept(MediaType.APPLICATION_JSON) , builder -> builder
                                .GET(RoutingConstant.ROOM_ID_PATH , chatHandler::getRoomMessages)
                                .POST(chatHandler::sendMessage)))
                .build();
    }

}
