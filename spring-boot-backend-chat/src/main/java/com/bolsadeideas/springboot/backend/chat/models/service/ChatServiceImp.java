package com.bolsadeideas.springboot.backend.chat.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolsadeideas.springboot.backend.chat.models.dao.ChatRepository;
import com.bolsadeideas.springboot.backend.chat.models.documents.Mensaje;

@Service
public class ChatServiceImp implements ChatService{

	@Autowired
	private ChatRepository chatdao;
	
	@Override
	public List<Mensaje> obtenerUtlimos10Mnesajes() {
		// TODO Auto-generated method stub
		return chatdao.findFirst10ByOrderByFechaAsc();
	}

	@Override
	public Mensaje guardar(Mensaje mensaje) {
		// TODO Auto-generated method stub
		return chatdao.save(mensaje);
	}

}
