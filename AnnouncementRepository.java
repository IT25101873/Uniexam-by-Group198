package com.example.uniexam.repository;

import com.example.uniexam.models.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByAuthorId(Long authorId);
    List<Announcement> findByTargetRole(String targetRole);
    List<Announcement> findByOrderByPinnedDescCreatedAtDesc();
}
