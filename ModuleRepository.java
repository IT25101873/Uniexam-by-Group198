package com.example.uniexam.repository;

import com.example.uniexam.models.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findByFacultyId(Long facultyId);
    List<Module> findByCourseId(Long courseId);
    Optional<Module> findByLecturerId(Long lecturerId);
    Optional<Module> findByCode(String code);
    List<Module> findByLecturerIdIn(java.util.Collection<Long> lecturerIds);
}
