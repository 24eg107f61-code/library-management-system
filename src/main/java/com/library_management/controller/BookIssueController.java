package com.library_management.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.library_management.Entity.Book;
import com.library_management.Entity.BookIssue;
import com.library_management.service.BookIssueService;
import com.library_management.service.BookService;
import com.library_management.service.StudentService;

@Controller
public class BookIssueController {

    private final BookIssueService bookIssueService;
    private final BookService bookService;
    private final StudentService studentService;

    public BookIssueController(
            BookIssueService bookIssueService,
            BookService bookService,
            StudentService studentService) {

        this.bookIssueService = bookIssueService;
        this.bookService = bookService;
        this.studentService = studentService;
    }

    @GetMapping("/issues")
    public String listIssues(Model model) {
        model.addAttribute("issues", bookIssueService.getAllIssues());
        return "issues";
    }

    @GetMapping("/issues/add")
    public String showIssueForm(Model model) {
        model.addAttribute("issue", new BookIssue());
        model.addAttribute("books", bookService.getAllBooks());
        model.addAttribute("students", studentService.getAllStudents());
        return "add-issue";
    }

    @PostMapping("/issues/save")
    public String saveIssue(
            @ModelAttribute("issue") BookIssue issue,
            RedirectAttributes redirectAttributes) {

        if (issue.getId() != null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Invalid issue request.");
            return "redirect:/issues";
        }

        if (issue.getBookId() == null || issue.getStudentId() == null
                || issue.getReturnDate() == null) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Please select a book, student, and return date.");
            return "redirect:/issues";
        }

        if (issue.getReturnDate().isBefore(LocalDate.now())) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "The return date cannot be in the past.");
            return "redirect:/issues";
        }

        Book book = bookService.getBookById(issue.getBookId())
                .orElse(null);

        if (book == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "The selected book was not found.");
            return "redirect:/issues";
        }

        boolean studentExists = studentService
                .getStudentById(issue.getStudentId()).isPresent();

        if (!studentExists) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "The selected student was not found.");
            return "redirect:/issues";
        }

        if (book.getQuantity() <= 0) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "This book is out of stock!");
            return "redirect:/issues/add";
        }

        issue.setIssueDate(LocalDate.now());
        issue.setActualReturnDate(null);

        book.setQuantity(book.getQuantity() - 1);
        bookService.saveBook(book);
        bookIssueService.saveIssue(issue);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Book issued successfully!");

        return "redirect:/issues";
    }

    @PostMapping("/issues/return/{id}")
    public String returnBook(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        BookIssue issue = bookIssueService.getIssueById(id)
                .orElse(null);

        if (issue == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Issue record not found.");
            return "redirect:/issues";
        }

        if (issue.getActualReturnDate() != null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "This book has already been returned.");
            return "redirect:/issues";
        }

        Book book = bookService.getBookById(issue.getBookId())
                .orElse(null);

        if (book == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "The book record was not found.");
            return "redirect:/issues";
        }

        issue.setActualReturnDate(LocalDate.now());
        book.setQuantity(book.getQuantity() + 1);

        bookService.saveBook(book);
        bookIssueService.saveIssue(issue);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Book returned successfully!");

        return "redirect:/issues";
    }
}