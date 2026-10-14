package com.library_management.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.library_management.Entity.students;
import com.library_management.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<students> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<students> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public students saveStudent(students student) {
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}