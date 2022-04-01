package com.springudemy.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import com.springudemy.cursomc.domain.Pedido;

public interface EmailService {

	void sendOrderConfirmationEmail(Pedido pedido);
	
	void sendEmail(SimpleMailMessage msg);
	
}
