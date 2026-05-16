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
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class LecturerController {

    private final LecturerRepository lecturerRepository;
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;
    private final StudentRepository studentRepository;
    private final ResultRepository resultRepository;
    private final FacultyRepository facultyRepository;
    private final AnnouncementRepository announcementRepository;
    private final PasswordEncoder passwordEncoder;

    public LecturerController(LecturerRepository lecturerRepository, UserRepository userRepository,
                               ModuleRepository moduleRepository, StudentRepository studentRepository,
                               ResultRepository resultRepository, FacultyRepository facultyRepository,
                               AnnouncementRepository announcementRepository,
                               PasswordEncoder passwordEncoder) {
        this.lecturerRepository = lecturerRepository;
        this.userRepository = userRepository;
        this.moduleRepository = moduleRepository;
        this.studentRepository = studentRepository;
        this.resultRepository = resultRepository;
        this.facultyRepository = facultyRepository;
        this.announcementRepository = announcementRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/lecturer/me")
    public ResponseEntity<ApiResponse<Lecturer>> getMe(Authentication auth) {
        User user = getUserFromAuth(auth);
        Optional<Lecturer> lec = lecturerRepository.findByUserId(user.getId());
        return lec.map(l -> ResponseEntity.ok(ApiResponse.ok(l, "OK")))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("Lecturer not found")));
    }

    @PutMapping("/lecturer/me")
    public ResponseEntity<ApiResponse<Lecturer>> updateMe(@RequestBody ProfileUpdateRequest req,
                                                           Authentication auth) {
        User user = getUserFromAuth(auth);
        Lecturer lec = lecturerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Not found"));
        if (req.getName() != null) lec.setName(req.getName());
        if (req.getEmail() != null) lec.setEmail(req.getEmail());
        if (req.getPhone() != null) lec.setPhone(req.getPhone());
        if (req.getNewPassword() != null && !req.getNewPassword().isBlank()) {
            if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPasswordHash()))
                return ResponseEntity.status(400).body(ApiResponse.error("Current password incorrect"));
            user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
            userRepository.save(user);
        }
        lecturerRepository.save(lec);
        return ResponseEntity.ok(ApiResponse.ok(lec, "Profile updated"));
    }

    @DeleteMapping("/lecturer/me")
    public ResponseEntity<ApiResponse<Void>> deactivateAccount(Authentication auth) {
        User user = getUserFromAuth(auth);
        user.setIsActive(false);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.ok(null, "Account deactivated"));
    }

    @GetMapping("/lecturer/students")
    public ResponseEntity<ApiResponse<List<Student>>> getStudents(Authentication auth) {
        User user = getUserFromAuth(auth);
        Lecturer lec = lecturerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Not found"));
        List<Student> students = studentRepository.findByFacultyId(lec.getFacultyId());
        return ResponseEntity.ok(ApiResponse.ok(students, "OK"));
    }

    @GetMapping("/lecturer/students/{studentId}/results")
    public ResponseEntity<ApiResponse<List<Result>>> getStudentResults(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok(resultRepository.findByStudentId(studentId), "OK"));
    }

    @PutMapping("/lecturer/results/{resultId}")
    public ResponseEntity<ApiResponse<Result>> adjustMark(@PathVariable Long resultId,
                                                           @RequestBody Map<String, Object> body) {
        Result result = resultRepository.findById(resultId)
                .orElseThrow(() -> new RuntimeException("Result not found"));
        double newScore = Double.parseDouble(String.valueOf(body.get("score")));
        result.setScore(newScore);
        double pct = (newScore / result.getTotalMarks()) * 100;
        result.setPercentage(pct);
        result.setGrade(calcGrade(pct));
        result.setPassed(pct >= 50);
        result.setPendingReview(false);
        resultRepository.save(result);
        return ResponseEntity.ok(ApiResponse.ok(result, "Mark updated"));
    }

    @DeleteMapping("/lecturer/results/{resultId}")
    public ResponseEntity<ApiResponse<Void>> deleteResult(@PathVariable Long resultId) {
        resultRepository.deleteById(resultId);
        return ResponseEntity.ok(ApiResponse.ok(null, "Result removed — student may reattempt"));
    }

    @GetMapping("/faculties")
    public ResponseEntity<ApiResponse<List<Faculty>>> getFaculties() {
        return ResponseEntity.ok(ApiResponse.ok(facultyRepository.findAll(), "OK"));
    }

    @GetMapping("/modules")
    public ResponseEntity<ApiResponse<List<Module>>> getModules() {
        return ResponseEntity.ok(ApiResponse.ok(moduleRepository.findAll(), "OK"));
    }

    @GetMapping("/modules/{id}")
    public ResponseEntity<ApiResponse<Module>> getModule(@PathVariable Long id) {
        return moduleRepository.findById(id)
                .map(m -> ResponseEntity.ok(ApiResponse.ok(m, "OK")))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("Module not found")));
    }

    @PutMapping("/modules/{id}")
    public ResponseEntity<ApiResponse<Module>> updateModule(@PathVariable Long id,
                                                             @RequestBody Map<String, Object> body) {
        Module m = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Module not found"));
        if (body.containsKey("description")) m.setDescription(String.valueOf(body.get("description")));
        if (body.containsKey("objectives")) m.setObjectives(String.valueOf(body.get("objectives")));
        moduleRepository.save(m);
        return ResponseEntity.ok(ApiResponse.ok(m, "Module updated"));
    }

    private User getUserFromAuth(Authentication auth) {
        String idCode = (String) auth.getPrincipal();
        return userRepository.findByIdCode(idCode)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private String calcGrade(double pct) {
        if (pct >= 70) return "A";
        if (pct >= 60) return "B";
        if (pct >= 50) return "C";
        return "F";
    }
}
