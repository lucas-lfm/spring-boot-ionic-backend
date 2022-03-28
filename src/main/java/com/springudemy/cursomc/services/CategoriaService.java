package com.springudemy.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.springudemy.cursomc.domain.Categoria;
import com.springudemy.cursomc.dto.CategoriaDTO;
import com.springudemy.cursomc.repositories.CategoriaRepository;
import com.springudemy.cursomc.services.exceptions.DataIntegrityException;
import com.springudemy.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository catRepo;
	
	public Categoria find(Integer id) {
		Optional<Categoria> obj = catRepo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! Id: " + id + 
				", Tipo: " + Categoria.class.getName()));
	}
	
	public Categoria insert(Categoria obj) {
		obj.setId(null);
		return catRepo.save(obj);
	}
	
	public Categoria update(Categoria obj) {
		find(obj.getId());
		return catRepo.save(obj);
	}
	
	public void delete(Integer id) {
		find(id);
		try {
			catRepo.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir uma categoria que possui produtos!");
		}
	}
	
	public List<Categoria> findAll(){
		return catRepo.findAll();
	}
	
	public Page<Categoria> findPage(Integer page, Integer linesPerPage, String orderBy, String direction){
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return catRepo.findAll(pageRequest);
	}
	
	public Categoria fromDTO(CategoriaDTO objDTO) {
		return new Categoria(objDTO.getId(), objDTO.getNome());
	}
	
}
