package com.springudemy.cursomc.services;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.springudemy.cursomc.domain.Cidade;
import com.springudemy.cursomc.domain.Cliente;
import com.springudemy.cursomc.domain.Endereco;
import com.springudemy.cursomc.domain.enums.Perfil;
import com.springudemy.cursomc.domain.enums.TipoCliente;
import com.springudemy.cursomc.dto.ClienteDTO;
import com.springudemy.cursomc.dto.ClienteNewDTO;
import com.springudemy.cursomc.repositories.ClienteRepository;
import com.springudemy.cursomc.repositories.EnderecoRepository;
import com.springudemy.cursomc.security.UserSS;
import com.springudemy.cursomc.services.exceptions.AuthorizationException;
import com.springudemy.cursomc.services.exceptions.DataIntegrityException;
import com.springudemy.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository cliRepo;
	
	@Autowired
	private EnderecoRepository endRepo;
	
	@Autowired
	private BCryptPasswordEncoder bpe;
	
	@Autowired
	private S3Service s3Service;
	
	@Autowired
	private ImageService imgService;
	
	@Value("${img.prefix.client.profile}")
	private String prefix;
	
	@Value("${img.profile.size}")
	private Integer sizeImage;
	
	public Cliente find(Integer id) {
	
		UserSS user = UserService.authenticated();
		
		if(user==null || !user.hasRole(Perfil.ADMIN) && !id.equals(user.getId())) {
			throw new AuthorizationException("Acesso negado");
		}
		
		Optional<Cliente> obj = cliRepo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! Id: " + id + 
				", Tipo: " + Cliente.class.getName()));
	}
	
	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = cliRepo.save(obj);
		
		endRepo.saveAll(obj.getEnderecos());
		
		return obj;
	}
	
	public Cliente update(Cliente obj) {
		Cliente newObj = find(obj.getId());
		updateData(newObj, obj);
		return cliRepo.save(newObj);
	}
	
	public void delete(Integer id) {
		find(id);
		try {
			cliRepo.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir porque há pedidos relacionados!");
		}
	}
	
	public List<Cliente> findAll(){
		return cliRepo.findAll();
	}
	
	public Cliente findByEmail(String email) {
		UserSS user = UserService.authenticated();
		if(user==null || !user.hasRole(Perfil.ADMIN) && !email.equals(user.getUsername())) {
			throw new AuthorizationException("Acesso negado");
		}
		
		Cliente cli = cliRepo.findByEmail(email);
		if(cli==null) {
			throw new ObjectNotFoundException("Objeto não encontrado! Id: "+user.getId()
						+ ", Tipo: "+Cliente.class.getName());
		}
		return cli;
	}
	
	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction){
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return cliRepo.findAll(pageRequest);
	}
	
	public Cliente fromDTO(ClienteDTO objDTO) {
		return new Cliente(objDTO.getId(), objDTO.getNome(), objDTO.getEmail(), null, null, null);
	}
	
	public Cliente fromDTO(ClienteNewDTO objDTO) {
		Cliente cli = new Cliente(null, objDTO.getNome(), objDTO.getEmail(), objDTO.getCpfOuCnpj(), 
				TipoCliente.toEnum(objDTO.getTipo()), bpe.encode(objDTO.getSenha()));
		Cidade cid = new Cidade(objDTO.getCidadeId(), null, null);
		Endereco end = new Endereco(null, objDTO.getLogradouro(), objDTO.getNumero(), 
				objDTO.getComplemento(), objDTO.getBairro(), objDTO.getCep(), cli, cid);
		cli.getEnderecos().add(end);
		cli.getTelefones().add(objDTO.getTelefone1());
		if(objDTO.getTelefone2() != null) {
			cli.getTelefones().add(objDTO.getTelefone2());
		}
		if(objDTO.getTelefone3() != null) {
			cli.getTelefones().add(objDTO.getTelefone3());
		}
		return cli;
	}
	
	private void updateData(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}
	
	public URI uploadProfilePicture(MultipartFile file) {
		UserSS user = UserService.authenticated();
		if(user == null) {
			throw new AuthorizationException("Acesso negado");
		}
		
		BufferedImage jpgImage = imgService.getJpgImageFromFile(file);
		jpgImage = imgService.cropSquare(jpgImage);
		jpgImage = imgService.resize(jpgImage, sizeImage);
		
		String fileName = prefix + user.getId() + ".jpg";
		
		return s3Service.uploadFile(imgService.getInputStream(jpgImage, "jpg"), fileName, "image");
	}
	
}
