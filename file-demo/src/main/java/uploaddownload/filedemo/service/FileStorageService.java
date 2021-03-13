package uploaddownload.filedemo.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.tomcat.websocket.server.UriTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import uploaddownload.filedemo.exceptions.FileStorageException;
import uploaddownload.filedemo.exceptions.MyFileNotFoundException;
import uploaddownload.filedemo.property.FileStorageProperty;

@Service
public class FileStorageService {
	
	private Path fileStorageLocation;
	
	@Autowired
	public FileStorageService(FileStorageProperty fileStorageProperty) {
		this.fileStorageLocation = Paths.get(fileStorageProperty.getUploadDir())
				.toAbsolutePath().normalize();
		try {
			Files.createDirectory(fileStorageLocation);
		}catch(Exception ex) {
			throw new FileStorageException("Could not create directory: "+fileStorageProperty);
		}
		
	}
	
	public String storeFile(MultipartFile file) {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		
		try {
			if(fileName.contains("..")) {
				throw new FileStorageException("File has invalid name with .. "+fileName);
			}
			
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return fileName;
		}catch(Exception ex) {
			throw new FileStorageException("Could not store file: "+fileName+" please try again");
		}
	}
	
	public Resource loadFileAsResource(String fileName) {
		
		try {
			Path fileTarget = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(fileTarget.toUri());
			
			if(resource.exists()) {
				return resource;
			}
			
			else {
				throw new MyFileNotFoundException("File not found "+fileName);
			}
			
		}catch(MalformedURLException ex) {
			throw new MyFileNotFoundException("File not found: ",ex);
		}
	}
}
