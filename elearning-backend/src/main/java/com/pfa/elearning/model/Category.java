package com.pfa.elearning.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    private String icon;

    @org.hibernate.annotations.Formula("(SELECT COUNT(*) FROM courses c WHERE c.category_id = id)")
    private Integer courseCount;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    private List<Course> courses = new ArrayList<>();
}
