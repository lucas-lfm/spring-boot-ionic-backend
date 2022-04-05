package com.springudemy.cursomc.resources;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.springudemy.cursomc.domain.Cidade;
import com.springudemy.cursomc.domain.Estado;
import com.springudemy.cursomc.dto.CidadeDTO;
import com.springudemy.cursomc.dto.EstadoDTO;
import com.springudemy.cursomc.services.CidadeService;
import com.springudemy.cursomc.services.EstadoService;

@RestController
@RequestMapping(value="/estados")
public class EstadoResource {

	@Autowired
	private EstadoService estServ;
	
	@Autowired
	private CidadeService cidServ;
	
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<List<EstadoDTO>> findAllOrderByName(){
		List<Estado> estados = estServ.findAllOrderByNome();
		List<EstadoDTO> listDTO = estados.stream().map(obj -> 
									new EstadoDTO(obj)).collect(Collectors.toList());
		return ResponseEntity.ok().body(listDTO);
	}
	
	@RequestMapping(value="/{estadoId}/cidades", method=RequestMethod.GET)
	public ResponseEntity<List<CidadeDTO>> findCidadesByEstado(@PathVariable Integer estadoId){
		List<Cidade> cidades = cidServ.findByEstado(estadoId);
		List<CidadeDTO> listDTO = cidades.stream().map(obj -> 
									new CidadeDTO(obj)).collect(Collectors.toList());
		return ResponseEntity.ok().body(listDTO);
	}
	
}
