package com.springudemy.cursomc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springudemy.cursomc.domain.Pedido;
import com.springudemy.cursomc.repositories.PedidoRepository;
import com.springudemy.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository pedRepo;
	
	public Pedido find(Integer id) {
		Optional<Pedido> obj = pedRepo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! Id: " + id + 
				", Tipo: " + Pedido.class.getName()));
	}
	
}
