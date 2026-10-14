package com.library_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library_management.Entity.students;

@Repository
public interface StudentRepository extends JpaRepository<students, Long> {
}