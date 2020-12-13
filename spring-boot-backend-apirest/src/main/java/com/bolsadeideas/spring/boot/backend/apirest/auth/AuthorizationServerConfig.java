package com.bolsadeideas.spring.boot.backend.apirest.auth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private InfoAdicinalToken infoAdicinalToken;

	@Autowired
	@Qualifier("authenticationManager")
	private AuthenticationManager authenticationManager;

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		//permisos de las rutas endpoint para obtener el token
		security.tokenKeyAccess("permitAll()")//permisos a cualquier usuario, se generara el token
		.checkTokenAccess("isAuthenticated()");//valida el token
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		//registrar la aplicacion de angular con sus credenciales
		clients.inMemory().withClient("angularApp")
		.secret(passwordEncoder.encode("12345"))
		.scopes("read","write")
		.authorizedGrantTypes("password","refresh_token")
		.accessTokenValiditySeconds(3600)//segundos - 1 hora
		.refreshTokenValiditySeconds(3600);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		/*
		 * se encarga de la autenticacion y validar el token
		 */
		TokenEnhancerChain tokenEnhancerChain=new TokenEnhancerChain(); 
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(infoAdicinalToken,accessTokenConverter()));
		
		endpoints.authenticationManager(authenticationManager)//se encarga de la autenticacion 
		.tokenStore(tokenStore())
		.accessTokenConverter(accessTokenConverter())//se encarga de traducir el token
		.tokenEnhancer(tokenEnhancerChain);
	}

	@Bean
	public JwtTokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter jwtAccessTokenConverter=new JwtAccessTokenConverter();
		//asignando la llave MAC para firmar el token
		//esa firma es unicamente en el servidor se usa cuando se genera el token 
		//y cuando lo recibe debe de decodificar con el mismo codigo
		jwtAccessTokenConverter.setSigningKey(JwtConfig.LLAVE_SECRETA);
		return jwtAccessTokenConverter;
	}

}
