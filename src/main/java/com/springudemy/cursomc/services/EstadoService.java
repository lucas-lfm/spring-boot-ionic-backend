package com.springudemy.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springudemy.cursomc.domain.Estado;
import com.springudemy.cursomc.repositories.EstadoRepository;
import com.springudemy.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class EstadoService {

	@Autowired
	private EstadoRepository estRepo;
	
	public List<Estado> findAllOrderByNome(){
		return estRepo.findAllByOrderByNome();
	}
	
	public Estado find(Integer id) {
		Optional<Estado> obj = estRepo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! Id: " + id + 
				", Tipo: " + Estado.class.getName()));
	}
	
}
