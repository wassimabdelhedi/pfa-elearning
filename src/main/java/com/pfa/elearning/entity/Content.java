package com.pfa.elearning.entity;

import com.pfa.elearning.enums.ContentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Content entity representing learning materials within a chapter.
 * 
 * Supports multiple content types: VIDEO, PDF, TEXT
 * Each content has a specific URL/data and duration information.
 */
@Entity
@Table(name = "contents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType contentType;

    @Column(columnDefinition = "TEXT")
    private String contentUrl;  // URL for VIDEO/PDF, or content for TEXT

    @Column(columnDefinition = "TEXT")
    private String textContent;  // For TEXT type content

    @Column
    private Integer durationMinutes;  // Estimated duration in minutes

    @Column(nullable = false)
    private Integer orderIndex;  // Order within the chapter

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL)
    private List<TimeSpent> timeSpentRecords;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
