package com.elsofthost.syncapp.service;

import java.io.IOException;

import java.util.stream.Stream;

import org.springframework.web.multipart.MultipartFile;

import com.elsofthost.syncapp.entity.FileEntity;
 
public interface FileService {
    Stream<FileEntity> getAllFiles();
    
    FileEntity getFile(String id);
    
    FileEntity store(MultipartFile file) throws IOException;
}