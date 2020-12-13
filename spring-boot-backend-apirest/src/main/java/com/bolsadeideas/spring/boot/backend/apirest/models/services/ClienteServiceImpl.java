package com.bolsadeideas.spring.boot.backend.apirest.models.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.bolsadeideas.spring.boot.backend.apirest.models.dao.IClienteDao;
import com.bolsadeideas.spring.boot.backend.apirest.models.dao.IFacturaDao;
import com.bolsadeideas.spring.boot.backend.apirest.models.dao.IProductoDao;
import com.bolsadeideas.spring.boot.backend.apirest.models.entity.Cliente;
import com.bolsadeideas.spring.boot.backend.apirest.models.entity.Factura;
import com.bolsadeideas.spring.boot.backend.apirest.models.entity.Producto;
import com.bolsadeideas.spring.boot.backend.apirest.models.entity.Region;

@Service
//@Qualifier("clienteService1")
public class ClienteServiceImpl implements IClienteService {

	//inyeccion de dependencias
	@Autowired
	private IClienteDao clienteDao;
	private final Logger log=LoggerFactory.getLogger(ClienteServiceImpl.class);
	@Autowired
	private IFacturaDao facturaDao;
	@Autowired
	private IProductoDao productoDao;
	

	@Override
	@Transactional(readOnly=true)//manejar las transacciones
	public List<Cliente> findAll() {
		return (List<Cliente>) clienteDao.findAll();
	}

	@Override
	@Transactional(readOnly=true)
	public Page<Cliente> findByOrderByIdAsc(Pageable pageable) {
		return clienteDao.findByOrderByIdAsc(pageable);	}
	@Override
	@Transactional(readOnly=true)
	public ResponseEntity<?> findById(Long id) {
		Map<String,Object> response=new HashMap<>();
		Cliente cliente=null;
		try {
			cliente=clienteDao.findById(id).orElse(null);
		}catch(DataAccessException e) {
			response.put("mensaje","Error en la consulta del cliente de la base de datos!");
			response.put("error", e.getMessage()+": "+e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(cliente == null) {
			response.put("mensaje","El cliente ID: "+id.toString()+" no existe en la base de datos!");
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Cliente>(cliente,HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> save(Cliente cliente) {
		Map<String,Object> response=new HashMap<>();
		Cliente clienteNuevo=null;
		try {
			clienteNuevo=clienteDao.save(cliente);
		}catch(DataAccessException e) {
			response.put("mensaje","Error al guardar el cliente de la base de datos!");
			response.put("error", e.getMessage()+": "+e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje","El cliente ha sido creado con éxito!");
		response.put("cliente", clienteNuevo);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<?> delete(Long id) {
		Map<String,Object> response=new HashMap<>();		
		try {
			Cliente cliente=clienteDao.findById(id).orElse(null);
			String nombreFotoAnterior=cliente.getFoto();
			if(nombreFotoAnterior !=null && nombreFotoAnterior.length()>0){
				Path rutaFotoAnterior=Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
				File archivoFotoAnterior=new File(rutaFotoAnterior.toString());
				if(archivoFotoAnterior.exists()&&archivoFotoAnterior.canRead()) {
					archivoFotoAnterior.delete();
				}
			}
			clienteDao.deleteById(id);
		}catch(DataAccessException e) {
			response.put("mensaje","Error al eliminar el cliente de la base de datos!");
			response.put("error", e.getMessage()+": "+e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje","El cliente ha sido eliminado con éxito!");		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	
	}
	
	@Override
	public ResponseEntity<?> update(Cliente cliente,Long id) {
		Cliente clienteActualizado=null;
		Map<String,Object> response=new HashMap<>();
		Cliente clienteActual=clienteDao.findById(id).orElse(null);

		if(clienteActual == null) {
			response.put("mensaje","El cliente ID: "+id.toString()+" no existe en la base de datos!");
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		clienteActual.setApellido(cliente.getApellido());
		clienteActual.setNombre(cliente.getNombre());
		clienteActual.setEmail(cliente.getEmail());
		clienteActual.setCreateAt(cliente.getCreateAt());
		clienteActual.setRegion(cliente.getRegion());
		
		try {
			clienteActualizado=clienteDao.save(clienteActual);
		}catch(DataAccessException e) {
			response.put("mensaje","Error al Actualizar los datos del cliente de la base de datos!");
			response.put("error", e.getMessage()+": "+e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje","El cliente ha sido actualizado con éxito!");
		response.put("cliente", clienteActualizado);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	
	}

	@Override
	public ResponseEntity<?> validacionBindingResult(BindingResult result) {
		if(result.hasErrors()) {
			Map<String,Object> response=new HashMap<>();
			List<String>errors= result.getFieldErrors()
					.stream()//flujo
					.map(err ->"El campo '"+err.getField()+"' "+err.getDefaultMessage())//cada uno de las iteraciones las convertiremos en string
					.collect(Collectors.toList());//conversion del string a lista
			response.put("errors", errors);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}
	return new ResponseEntity<Object>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> upload(MultipartFile archivo, Long id) {
		Cliente cliente=clienteDao.findById(id).orElse(null);
		Map<String,Object> response=new HashMap<>();
		
		if(cliente == null) {
			response.put("mensaje","El cliente ID: "+id.toString()+" no existe en la base de datos!");
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}else if(!archivo.isEmpty()) {
			String nombreArchivo=UUID.randomUUID().toString()+"_"+archivo.getOriginalFilename().replace(" ", "");
			Path rutaArchivo=Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();//c://Temp//uploads
			log.info(rutaArchivo.toString());
			try {
				Files.copy(archivo.getInputStream(), rutaArchivo);//copia el archivo a la ruta especificada
			} catch (IOException e) {				
				response.put("mensaje","Error al subir la imagen del cliente "+nombreArchivo);
				response.put("error", e.getMessage()+": "+e.getCause().getMessage());
				System.out.println("1");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
			System.out.println("2");
			String nombreFotoAnterior=cliente.getFoto();
			if(nombreFotoAnterior !=null && nombreFotoAnterior.length()>0){
				Path rutaFotoAnterior=Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
				File archivoFotoAnterior=new File(rutaFotoAnterior.toString());
				if(archivoFotoAnterior.exists()&&archivoFotoAnterior.canRead()) {
					archivoFotoAnterior.delete();
				}
			}
			cliente.setFoto(nombreArchivo);
			clienteDao.save(cliente);
			response.put("cliente", cliente); 
			response.put("mensaje","Has subido correctamente la imagen: "+nombreArchivo);
		}else {
			response.put("mensaje","Error desconocido, asegurese de estar subiendo la imagen!");
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
	
	return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	@Override
	public ResponseEntity<Resource> verFoto(String nombreFoto) {
		Path rutaArchivo=Paths.get("uploads").resolve(nombreFoto).toAbsolutePath();
		log.info(rutaArchivo.toString());
		Resource recurso=null;
		try {
			recurso=new UrlResource(rutaArchivo.toUri());
		} catch ( IOException e) {
			e.printStackTrace();
		}
		
		if(!recurso.exists()&&!recurso.isReadable()) {
			rutaArchivo=Paths.get("src/main/resources/static/images").resolve("image_default.png").toAbsolutePath();
			try {
				recurso=new UrlResource(rutaArchivo.toUri());
			} catch ( IOException e) {				
				e.printStackTrace();
			}
			log.error("No se pudo cargar la imagen: "+nombreFoto);
			//throw new RuntimeException("No se pudo cargar la imagen: "+nombreFoto); 
		}
		HttpHeaders cabecera=new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+recurso.getFilename()+"\"");//Atachtment es para forzar el descargado
		return new ResponseEntity<Resource>(recurso,cabecera,HttpStatus.OK);
	}
	@Override
	@Transactional(readOnly=true)
	public List<Region> findAllRegiones() {		
		return clienteDao.findAllRegiones();
	}

	@Override
	@Transactional(readOnly=true)
	public Factura findFacturaById(Long id) {
		return facturaDao.findById(id).orElse(null);
	}

	@Override
	public Factura saveFactura(Factura factura) {
		return facturaDao.save(factura);
	}

	@Override
	public void deleteFacturaById(Long id) {
		facturaDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly=true)
	public List<Producto> findProductoByNombre(String term) {
		return productoDao.findByNombreContainingIgnoreCase(term);
	}

	@Override
	public ResponseEntity<?> prueba(String request) {
		Map<String,Object> response=new HashMap<>();
		response.put("request", request);
		response.put("mensaje", "chupalo");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
	}

}
