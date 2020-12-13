package com.bolsadeideas.springboot.backend.chat.models.service;

import java.util.List;

import com.bolsadeideas.springboot.backend.chat.models.documents.Mensaje;

public interface ChatService {
	
	public List<Mensaje> obtenerUtlimos10Mnesajes();
	public Mensaje guardar(Mensaje mensaje);
}
