package com.bolsadeideas.spring.boot.backend.apirest.models.dao;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bolsadeideas.spring.boot.backend.apirest.models.entity.Cliente;
import com.bolsadeideas.spring.boot.backend.apirest.models.entity.Region;

public interface IClienteDao extends JpaRepository<Cliente,Long>{/*Jpa Repository es lo necesario para el ordenamiento*/
	Page<Cliente> findByOrderByIdAsc(Pageable pageable);
	
	@Query("from Region")//clase entity  java, no de la tabla
	public List<Region> findAllRegiones();/*se define asi ya que las regiones 
										se encuentran en tablas diferentes a Cliente*/
	
	
}
