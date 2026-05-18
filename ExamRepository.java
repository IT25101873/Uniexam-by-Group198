package com.example.uniexam.repository;

import com.example.uniexam.models.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByModuleId(Long moduleId);
    List<Exam> findByLecturerId(Long lecturerId);
    List<Exam> findByStatus(String status);
    List<Exam> findByModuleIdIn(java.util.Collection<Long> moduleIds);
}
