package com.springudemy.cursomc.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.springudemy.cursomc.domain.Cliente;
import com.springudemy.cursomc.dto.ClienteDTO;
import com.springudemy.cursomc.repositories.ClienteRepository;
import com.springudemy.cursomc.resources.exceptions.FieldMessage;

public class ClienteUpdateValidator implements ConstraintValidator<ClienteUpdate, ClienteDTO>{

	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private ClienteRepository cliRepo;
	
	@Override
	public void initialize(ClienteUpdate ann) {}
	
	@Override
	public boolean isValid(ClienteDTO objDTO, ConstraintValidatorContext context) {
		
		@SuppressWarnings("unchecked")
		Map<String, String> map = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		Integer uriId = Integer.parseInt(map.get("id"));
		
		List<FieldMessage> list = new ArrayList<>();
		
		Cliente aux = cliRepo.findByEmail(objDTO.getEmail());
		
		if( aux != null && !aux.getId().equals(uriId) ) {
			list.add(new FieldMessage("email", "Esse email já existe"));
		}
		
		for (FieldMessage e: list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage())
				.addPropertyNode(e.getFieldName()).addConstraintViolation();
		}
		return list.isEmpty();
	}
	
}
