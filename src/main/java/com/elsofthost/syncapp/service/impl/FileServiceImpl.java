package com.elsofthost.syncapp.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.elsofthost.syncapp.entity.FileEntity;
import com.elsofthost.syncapp.repository.FileRepository;
import com.elsofthost.syncapp.service.FileService;

@Service
public class FileServiceImpl implements FileService {
 
	  @Autowired
	  private FileRepository fileRepository;
	  public FileEntity store(MultipartFile file) throws IOException {
	    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
	    FileEntity FileDB = new FileEntity(fileName, file.getContentType(), file.getBytes());
	    return fileRepository.save(FileDB);
	  }
	  public FileEntity getFile(String id) {
	    return fileRepository.findById(id).get();
	  }
	  
	  public Stream<FileEntity> getAllFiles() {
	    return fileRepository.findAll().stream();
	  }
}
