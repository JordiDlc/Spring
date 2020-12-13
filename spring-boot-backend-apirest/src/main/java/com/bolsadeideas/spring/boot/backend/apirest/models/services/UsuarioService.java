package com.bolsadeideas.spring.boot.backend.apirest.models.services;



import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bolsadeideas.spring.boot.backend.apirest.models.dao.IUsuarioDao;
import com.bolsadeideas.spring.boot.backend.apirest.models.entity.Usuario;

@Service
public class UsuarioService implements IUsuarioService ,UserDetailsService{
	/*
	 * integrando la base de datos local de usuarios con los usuarios de spring
	 */
	private Logger logger=LoggerFactory.getLogger(UsuarioService.class);
	
	@Autowired
	private IUsuarioDao usuarioDao;
	
	@Override
	@Transactional(readOnly=true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario=usuarioDao.findByUsername(username);
		//obtengo mi usuario en base, mediante el nombre
		if(usuario==null) {
			logger.error("Error en el login: no existe el usuario: '"+username+"' en el sistema!");
			throw new UsernameNotFoundException("Error en el login: no existe el usuario: '"+username+"' en el sistema!");
		}
		List<GrantedAuthority>authorities=usuario.getRoles()
				.stream()
				.map(rol -> new SimpleGrantedAuthority(rol.getNombre()))
				.peek(autority ->logger.info("Rol: "+autority.getAuthority()))//mostrarlo en el logger
				.collect(Collectors.toList());
		//creo un usuario de spring con el contenido que se requiere
		return new User(usuario.getUsername(), usuario.getPassword(), usuario.getEnabled(), true, true, true, authorities);
	}
	
	@Override
	@Transactional(readOnly=true)
	public Usuario findByUsername(String username) {
		return usuarioDao.findByUsername(username);
	}

}
