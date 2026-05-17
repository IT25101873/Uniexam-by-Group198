package com.example.uniexam.controllers;

import com.example.uniexam.dto.ApiResponse;
import com.example.uniexam.dto.ProfileUpdateRequest;
import com.example.uniexam.models.*;
import com.example.uniexam.models.Module;
import com.example.uniexam.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/student")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;
    private final ResultRepository resultRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentController(StudentRepository studentRepository, UserRepository userRepository,
                              ModuleRepository moduleRepository, ResultRepository resultRepository,
                              PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.moduleRepository = moduleRepository;
        this.resultRepository = resultRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Student>> getMe(Authentication auth) {
        User user = getUser(auth);
        Optional<Student> stu = studentRepository.findByUserId(user.getId());
        return stu.map(s -> ResponseEntity.ok(ApiResponse.ok(s, "OK")))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("Student not found")));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<Student>> updateMe(@RequestBody ProfileUpdateRequest req,
                                                          Authentication auth) {
        User user = getUser(auth);
        Student stu = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Not found"));
        if (req.getName() != null) stu.setName(req.getName());
        if (req.getEmail() != null) stu.setEmail(req.getEmail());
        if (req.getPhone() != null) stu.setPhone(req.getPhone());
        if (req.getNewPassword() != null && !req.getNewPassword().isBlank()) {
            if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPasswordHash()))
                return ResponseEntity.status(400).body(ApiResponse.error("Current password incorrect"));
            user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
            userRepository.save(user);
        }
        studentRepository.save(stu);
        return ResponseEntity.ok(ApiResponse.ok(stu, "Profile updated"));
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deactivate(Authentication auth) {
        User user = getUser(auth);
        user.setIsActive(false);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.ok(null, "Account deactivated"));
    }

    @GetMapping("/me/results")
    public ResponseEntity<ApiResponse<List<Result>>> getMyResults(Authentication auth) {
        User user = getUser(auth);
        Student stu = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Not found"));
        return ResponseEntity.ok(ApiResponse.ok(resultRepository.findByStudentId(stu.getId()), "OK"));
    }

    @PostMapping("/me/enroll/{moduleId}")
    public ResponseEntity<ApiResponse<Student>> enrollElective(@PathVariable Long moduleId,
                                                                Authentication auth) {
        User user = getUser(auth);
        Student stu = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Not found"));
        String existing = stu.getEnrolledModuleIds() != null ? stu.getEnrolledModuleIds() : "";
        if (!existing.contains(moduleId.toString())) {
            stu.setEnrolledModuleIds(existing.isBlank() ? moduleId.toString() : existing + "," + moduleId);
            studentRepository.save(stu);
        }
        Module m = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));
        return ResponseEntity.ok(ApiResponse.ok(stu, "Enrolled in " + m.getName()));
    }

    @DeleteMapping("/me/enroll/{moduleId}")
    public ResponseEntity<ApiResponse<Student>> dropElective(@PathVariable Long moduleId,
                                                              Authentication auth) {
        User user = getUser(auth);
        Student stu = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Not found"));
        String existing = stu.getEnrolledModuleIds() != null ? stu.getEnrolledModuleIds() : "";
        String updated = java.util.Arrays.stream(existing.split(","))
                .filter(id -> !id.equals(moduleId.toString()))
                .reduce((a, b) -> a + "," + b).orElse("");
        stu.setEnrolledModuleIds(updated);
        studentRepository.save(stu);
        return ResponseEntity.ok(ApiResponse.ok(stu, "Module dropped"));
    }

    private User getUser(Authentication auth) {
        String idCode = (String) auth.getPrincipal();
        return userRepository.findByIdCode(idCode)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
