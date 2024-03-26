package com.example.springSecurity.controller;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Controller
@RequestMapping("/file")
public class FileController {
	@Value("${spring.servlet.multipart.location}") private String uploadDir;
	
	@GetMapping("/download/{dir}/{filename}")
	public ResponseEntity<Resource> profile(@PathVariable String dir, @PathVariable String filename) {
		Path path = Paths.get(uploadDir + dir + "/" + filename);
		try {
			String contentType = Files.probeContentType(path);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentDisposition(
					ContentDisposition.builder("attachment")
					 				  .filename(filename, StandardCharsets.UTF_8)
					 				  .build()
					);
			headers.add(HttpHeaders.CONTENT_TYPE, contentType);
			Resource resource = new InputStreamResource(Files.newInputStream(path));
			return new ResponseEntity<>(resource, headers, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@ResponseBody
	@PostMapping("/imageUpload")
	public String imageUpload(MultipartHttpServletRequest req) {
		String callback = req.getParameter("CKEditorFuncNum"); 		// 1
		String error = "";
		String url = null;
		Map<String, MultipartFile> map = req.getFileMap();
		for (Map.Entry<String, MultipartFile> pair: map.entrySet()) {
			MultipartFile file = pair.getValue();
			String filename = file.getOriginalFilename();
			int idx = filename.lastIndexOf(".");
			String format = filename.substring(idx);
			if (format.equals(".jfif"))
				format = ".jpg";
			filename = System.currentTimeMillis() + format;
			String uploadPath = uploadDir + "image/" + filename;
			try {
				file.transferTo(new File(uploadPath));
			} catch (Exception e) {
				e.printStackTrace();
			}
			url = "/sbbs/file/download/image/" + filename;
			System.out.println(url);
		}
		
		String ajaxResponse = "<script>"
				+ "	window.parent.CKEDITOR.tools.callFunction("
				+ 		callback + ", '" + url + "', '" + error + "'"
				+ "	);"
				+ "</script>";
		return ajaxResponse;
	}
	
}