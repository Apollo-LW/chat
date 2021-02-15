package com.apollo.chat.config;

import com.apollo.chat.constant.RoutingConstant;
import com.apollo.chat.handler.UserRoomHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class UserRoomRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> userRoomRoute(UserRoomHandler userRoomHandler) {
        return RouterFunctions
                .route()
                .path(RoutingConstant.USER_PATH , routeBuilderFunction ->
                        routeBuilderFunction.nest(RequestPredicates.accept(MediaType.APPLICATION_JSON) , builder -> builder
                                .GET(RoutingConstant.USER_ID_PATH , userRoomHandler::getUserRooms)))
                .build();
    }

}
