package uploaddownload.filedemo.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	@PostMapping("/uploadMultipleFiles")
	public List<UploadFileResponse> uploadFiles(@RequestParam("files")MultipartFile[] files) {
		return Arrays.asList(files)
				.stream()
				.map(file->uploadFile(file))
				.collect(Collectors.toList());
	}
	
	@GetMapping("/download/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName,HttpServletRequest request) {
		
		Resource resource = filestorageService.loadFileAsResource(fileName);
		
		String contentType = null;
		
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		}catch(IOException ex) {
			logger.info("Could not determine file type");
		}
		
		if(contentType==null) {
			contentType = "application/octet-stream";
		}
		
		return  ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
	}
	
}
