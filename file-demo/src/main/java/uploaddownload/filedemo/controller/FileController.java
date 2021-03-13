package uploaddownload.filedemo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import uploaddownload.filedemo.payloads.UploadFileResponse;
import uploaddownload.filedemo.service.FileStorageService;

@RestController
public class FileController {
	
	private Logger logger = LoggerFactory.getLogger(FileController.class);

	@Autowired
	private FileStorageService filestorageService;
	
	
	@PostMapping("/uploadFile")
	public UploadFileResponse uploadFile(@RequestParam("file")MultipartFile file) {
		
		String fileName = filestorageService.storeFile(file);
		
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/downloadFile/")
				.path(fileName)
				.toUriString();
		
		return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
	}
	
	
}
