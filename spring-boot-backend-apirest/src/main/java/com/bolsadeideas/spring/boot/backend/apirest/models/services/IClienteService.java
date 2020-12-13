package com.bolsadeideas.spring.boot.backend.apirest.models.services;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import com.bolsadeideas.spring.boot.backend.apirest.models.entity.Cliente;
import com.bolsadeideas.spring.boot.backend.apirest.models.entity.Factura;
import com.bolsadeideas.spring.boot.backend.apirest.models.entity.Producto;
import com.bolsadeideas.spring.boot.backend.apirest.models.entity.Region;

public interface IClienteService {

	public List<Cliente>findAll();

	public Page<Cliente>findByOrderByIdAsc(Pageable pageable);
	
	public ResponseEntity<?> findById(Long id);
	
	public ResponseEntity<?> save(Cliente cliente);
	
	public ResponseEntity<?> delete(Long id);
	
	public ResponseEntity<?> update(Cliente cliente,Long id);
	
	public ResponseEntity<?> validacionBindingResult(BindingResult result);
	
	public ResponseEntity<?> upload(MultipartFile archivo, Long id);
	
	public ResponseEntity<Resource> verFoto(@PathVariable String nombreFoto);
	
	public List<Region> findAllRegiones();
	
	public Factura findFacturaById(Long id);
	
	public Factura saveFactura(Factura factura);
	
	public void deleteFacturaById(Long id);
	
	public List<Producto> findProductoByNombre(String term);
	
	public ResponseEntity<?> prueba(String request);
}
