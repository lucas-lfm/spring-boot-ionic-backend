package com.springudemy.cursomc.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springudemy.cursomc.domain.ItemPedido;
import com.springudemy.cursomc.domain.PagamentoComBoleto;
import com.springudemy.cursomc.domain.Pedido;
import com.springudemy.cursomc.domain.enums.EstadoPagamento;
import com.springudemy.cursomc.repositories.ItemPedidoRepository;
import com.springudemy.cursomc.repositories.PagamentoRepository;
import com.springudemy.cursomc.repositories.PedidoRepository;
import com.springudemy.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {

	@Autowired
	private BoletoService bolServ;
	
	@Autowired 
	private ProdutoService proServ;
	
	@Autowired
	private ClienteService cliServ;
	
	@Autowired
	private PedidoRepository pedRepo;
	
	@Autowired
	private PagamentoRepository pagRepo;
	
	@Autowired 
	private ItemPedidoRepository ipRepo;
	
	@Autowired
	private EmailService emailServ;
	
	public Pedido find(Integer id) {
		Optional<Pedido> obj = pedRepo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! Id: " + id + 
				", Tipo: " + Pedido.class.getName()));
	}
	
	@Transactional
	public Pedido insert(Pedido obj) {
		obj.setId(null);
		obj.setInstante(new Date());
		obj.setCliente(cliServ.find(obj.getCliente().getId()));
		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);
		
		if (obj.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
			bolServ.preencherPagamentoComBoleto(pagto, obj.getInstante());
		}
		
		obj = pedRepo.save(obj);
		pagRepo.save(obj.getPagamento());
		
		for(ItemPedido ip: obj.getItens()) {
			ip.setDesconto(0.0);
			ip.setProduto(proServ.find(ip.getProduto().getId()));
			ip.setPreco(ip.getProduto().getPreco());
			ip.setPedido(obj);
		}
		
		ipRepo.saveAll(obj.getItens());
		
		//emailServ.sendOrderConfirmationEmail(obj);
		emailServ.sendOrderConfirmationHtmlEmail(obj);
		
		return obj;
	}
	
}
