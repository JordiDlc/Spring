package com.bolsadeideas.spring.boot.backend.apirest.controllers;



import java.util.List;

import javax.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bolsadeideas.spring.boot.backend.apirest.models.entity.Cliente;
import com.bolsadeideas.spring.boot.backend.apirest.models.entity.Region;
import com.bolsadeideas.spring.boot.backend.apirest.models.services.IClienteService;

@CrossOrigin(origins= {"http://localhost:4200","*"})
@RestController
@RequestMapping("/api")
public class ClienteRestController {

	@Autowired
	//@Qualifier("clienteService1")
	private IClienteService clienteService;/*buscara una clase en concreta que implemente esta interfaz, 
					en este caso ClienteServiceImpl la implementa, sihay varias se usa un @Qualifier*/
	
	
	@GetMapping("/clientes")
	public List<Cliente>index(){
		return clienteService.findAll();
	}
	
	@GetMapping("/clientes/page/{page}")
	public Page<Cliente>index(@PathVariable Integer page){
		return clienteService.findByOrderByIdAsc(PageRequest.of(page, 5));
	}
	
	@Secured({"ROLE_ADMIN","ROLE_USER"})
	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id){
		return clienteService.findById(id);
	}
	
	@Secured("ROLE_ADMIN")
	@PostMapping("/clientes")
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result){//anotacion valid intercepta el request y valida		
		ResponseEntity<?> algo=clienteService.validacionBindingResult(result);
		if(algo.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
			return algo;
		}
		return clienteService.save(cliente);
	}
	
	@Secured("ROLE_ADMIN")
	@PutMapping("/clientes/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, BindingResult result, @PathVariable Long id) {//BindingResult objeto que contiene el mensaje de la anotacion valid 
		ResponseEntity<?> algo=clienteService.validacionBindingResult(result);
		if(algo.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
			return algo;
		}
		return clienteService.update(cliente, id);
	}
	
	@Secured("ROLE_ADMIN")
	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<?> delete(@Valid @PathVariable Long id) {
		return clienteService.delete(id);
	}
	
	@Secured({"ROLE_ADMIN","ROLE_USER"})
	@PostMapping("/clientes/upload")
	public ResponseEntity<?> upload(@RequestParam("archivo") MultipartFile archivo,@RequestParam("id") Long id) {
		return clienteService.upload(archivo,id);
	}
	
	@GetMapping("/uploads/img/{nombreFoto:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String nombreFoto){
		return clienteService.verFoto(nombreFoto);
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/clientes/regiones")
	public List<Region>listarRegiones(){
		return clienteService.findAllRegiones();
	}
	
	@PostMapping("/clientes/prueba")
	public ResponseEntity<?> upload(@RequestBody String Request) {
		return clienteService.prueba(Request);
	}
}
