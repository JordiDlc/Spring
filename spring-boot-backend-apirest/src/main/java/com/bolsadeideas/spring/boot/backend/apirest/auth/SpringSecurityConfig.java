package com.bolsadeideas.spring.boot.backend.apirest.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled=true)//habilitar las anotaciones para usar en el controlador
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService usuarioService;/*
												 * busca una clase que implemente UserDetailsService que seria la clase
												 * UsuarioService.java
												 */

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		/*
		 * encryptar las claves - HS256
		 */
		return new BCryptPasswordEncoder();
	}

	@Override
	@Autowired
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		/*
		 * como voy a authenticar, el mecanismo para authenticar
		 */
		auth.userDetailsService(this.usuarioService).passwordEncoder(passwordEncoder());
		//agregas para la authenticacion tu propio servicio de usuarios ("this.usuarioService")
	}
	
	/**Lado de spring**/
	@Override
	public void configure(HttpSecurity http) throws Exception {
		/*
		 * se configura la seguridad
		 * recursos a traves de sus uri's
		 */
		http.authorizeRequests()
		.anyRequest().authenticated()/*cualquier peticion requiere de authenticacion**/
		.and()
		.csrf().disable()//para que un formulario no sea falsificado, se lo deshabilita ya que es un api
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);//creo una politica de no sesiones ya que manejaremos token
	}
	
	

	@Override
	@Bean("authenticationManager")
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}
	

}
