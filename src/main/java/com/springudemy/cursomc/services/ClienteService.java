package com.springudemy.cursomc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springudemy.cursomc.domain.Cliente;
import com.springudemy.cursomc.repositories.ClienteRepository;
import com.springudemy.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository catRepo;
	
	public Cliente find(Integer id) {
		Optional<Cliente> obj = catRepo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! Id: " + id + 
				", Tipo: " + Cliente.class.getName()));
	}
	
}
