package com.example.uniexam.repository;

import com.example.uniexam.models.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, Long> {
    Optional<Lecturer> findByUserId(Long userId);
    Optional<Lecturer> findByEmployeeId(String employeeId);
    List<Lecturer> findByFacultyId(Long facultyId);
}
