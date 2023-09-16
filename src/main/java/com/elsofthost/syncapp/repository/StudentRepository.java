package com.elsofthost.syncapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elsofthost.syncapp.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long>{

}

