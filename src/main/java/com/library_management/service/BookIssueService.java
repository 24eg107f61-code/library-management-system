package com.library_management.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.library_management.Entity.BookIssue;
import com.library_management.repository.BookIssueRepository;

@Service
public class BookIssueService {

    private final BookIssueRepository bookIssueRepository;

    public BookIssueService(BookIssueRepository bookIssueRepository) {
        this.bookIssueRepository = bookIssueRepository;
    }

    public List<BookIssue> getAllIssues() {
        return bookIssueRepository.findAll();
    }

    public Optional<BookIssue> getIssueById(Long id) {
        return bookIssueRepository.findById(id);
    }

    public BookIssue saveIssue(BookIssue bookIssue) {
        return bookIssueRepository.save(bookIssue);
    }
}