package com.springudemy.cursomc.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springudemy.cursomc.domain.Cidade;
import com.springudemy.cursomc.repositories.CidadeRepository;

@Service
public class CidadeService {

	@Autowired
	private CidadeRepository cidRepo;
	
	public List<Cidade> findByEstado(Integer estadoId){
		return cidRepo.findCidades(estadoId);
	}
	
}
