package com.bolsadeideas.spring.boot.backend.apirest.models.dao;
 

import org.springframework.data.repository.CrudRepository;

import com.bolsadeideas.spring.boot.backend.apirest.models.entity.Factura;

public interface IFacturaDao extends CrudRepository<Factura,Long> {

}
