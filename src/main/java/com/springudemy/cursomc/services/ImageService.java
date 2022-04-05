package com.springudemy.cursomc.services;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.springudemy.cursomc.services.exceptions.FileException;

@Service
public class ImageService {

	public BufferedImage getJpgImageFromFile(MultipartFile file) {
		String ext = FilenameUtils.getExtension(file.getOriginalFilename());
		if(!ext.equals("png") && !ext.equals("jpg") && !ext.equals("jpeg")) {
			throw new FileException("Somente imagens PNG, JPG e JPEG são permitidas");
		}
		
		try {
			BufferedImage img = ImageIO.read(file.getInputStream());
			if(ext.equals("png") || ext.equals("jpeg")) {
				img = toJpg(img);
			}
			return img;
		} catch (IOException e) {
			throw new FileException("Erro ao ler arquivo");
		}
	}
	
	public BufferedImage toJpg(BufferedImage img) {
		BufferedImage jpgImage = new BufferedImage(img.getWidth(), img.getHeight(), 
					BufferedImage.TYPE_INT_RGB);
		jpgImage.createGraphics().drawImage(img, 0, 0, Color.WHITE, null);
		return jpgImage;
	}
	
	public InputStream getInputStream(BufferedImage img, String extension) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(img, extension, os);
			return new ByteArrayInputStream(os.toByteArray());
		} catch (IOException e) {
			throw new FileException("Erro ao ler arquivo");
		}
	}
	
}
