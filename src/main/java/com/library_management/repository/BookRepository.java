package com.library_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library_management.Entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}