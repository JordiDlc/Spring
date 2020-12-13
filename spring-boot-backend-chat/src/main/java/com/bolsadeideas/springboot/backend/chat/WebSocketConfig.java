package com.bolsadeideas.springboot.backend.chat;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker//habilita el servidor web socket en spring 
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/chat-websocket")
		.setAllowedOrigins("http://localhost:4200")// el cors para angular
		.withSockJS();//por debajo stomp utilizara SockJS 		
					//sockJs permite usar el protocolo http
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/chat/");//es un prefijo. cuando el servidor emite un mensaje o notifica a los clientes debemos indicar el nombre del evento
		//servidor a cliente
		registry.setApplicationDestinationPrefixes("/app/");//es un prefijo para publicar un mensaje.
		//cliente a servidor
	}
	

}
