package com.bolsadeideas.springboot.backend.chat.controllers;

import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.bolsadeideas.springboot.backend.chat.models.documents.Mensaje;
import com.bolsadeideas.springboot.backend.chat.models.service.ChatService;

@Controller
public class ChatController {

	@Autowired
	private ChatService chatService;
	
	@Autowired
	private SimpMessagingTemplate notificacionUnitaria;
	
	private String[] colores= {"red","green","blue","magenta","purple","orange"};
	@MessageMapping("/mensaje")//destino en donde envian los mensajes del chat
	@SendTo("/chat/mensaje")//notificar a los que esten suscrito
	public Mensaje recibeMensaje(Mensaje mensaje) {
		//recive algo del cliente nada mas
		mensaje.setFecha(new Date().getTime());
		if(mensaje.getTipo().equals("NUEVO_USUARIO")) {
			mensaje.setTexto("Bienvenido");
			mensaje.setColor(colores[new Random().nextInt(colores.length)]);
		}else {
			chatService.guardar(mensaje);
		}
		return mensaje;
	}
	
	@MessageMapping("/escribiendo")
	@SendTo("/chat/escribiendo")
	public String estaEscribiendo(String username) {
		return username.concat(" está escribiendo ...");
	}
	
	@MessageMapping("/historial")
	//@SendTo("/chat/historial")
	public void historial(String clienteId) {
		//devolver este evento unicamente al usuario que se conecta mas no a todos los suscritos
		//hacerlo de esta manera te permite personalizar el evento de manera unitaria a diferencia de la anotacion SendTo
		notificacionUnitaria.convertAndSend("/chat/historial/"+clienteId,chatService.obtenerUtlimos10Mnesajes());
	}
}
