package com.elsofthost.syncapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elsofthost.syncapp.entity.FileEntity;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, String> {
 
}
