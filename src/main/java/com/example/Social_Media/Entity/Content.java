package com.example.Social_Media.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "content")
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User creator;

    private String title;
    private String description;
    private String videoUrl;
    private String thumbnailUrl;
    private Integer views = 0;

    @Column(name = "likes")
    private Integer likes = 0;
}
