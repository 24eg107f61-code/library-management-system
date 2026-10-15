package com.library_management.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.library_management.Entity.Book;
import com.library_management.Entity.BookIssue;
import com.library_management.service.BookIssueService;
import com.library_management.service.BookService;
import com.library_management.service.StudentService;

@RestController
@RequestMapping("/api/issues")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "https://rococo-daifuku-f950ea.netlify.app"
})
public class BookIssueRestController {

    private final BookIssueService bookIssueService;
    private final BookService bookService;
    private final StudentService studentService;

    public BookIssueRestController(
            BookIssueService bookIssueService,
            BookService bookService,
            StudentService studentService) {
        this.bookIssueService = bookIssueService;
        this.bookService = bookService;
        this.studentService = studentService;
    }

    @GetMapping
    public List<BookIssue> getAllIssues() {
        return bookIssueService.getAllIssues();
    }

    @PostMapping
    public BookIssue issueBook(@RequestBody BookIssue issue) {

        if (issue.getId() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid issue request");
        }

        if (issue.getBookId() == null
                || issue.getStudentId() == null
                || issue.getReturnDate() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Book, student and return date are required");
        }

        if (issue.getReturnDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Return date cannot be in the past");
        }

        Book book = bookService.getBookById(issue.getBookId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Book not found"));

        studentService.getStudentById(issue.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Student not found"));

        if (book.getQuantity() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Book is out of stock");
        }

        issue.setIssueDate(LocalDate.now());
        issue.setActualReturnDate(null);

        book.setQuantity(book.getQuantity() - 1);
        bookService.saveBook(book);

        return bookIssueService.saveIssue(issue);
    }

    @PostMapping("/return/{id}")
    public BookIssue returnBook(@PathVariable Long id) {

        BookIssue issue = bookIssueService.getIssueById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Issue record not found"));

        if (issue.getActualReturnDate() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Book has already been returned");
        }

        Book book = bookService.getBookById(issue.getBookId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Book not found"));

        issue.setActualReturnDate(LocalDate.now());
        book.setQuantity(book.getQuantity() + 1);

        bookService.saveBook(book);

        return bookIssueService.saveIssue(issue);
    }
}