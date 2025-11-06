package se.examenarbete.blitzduel.model;

import java.time.LocalDateTime;

public class Quiz {
    private Long id;
    private String title;
    private String description;
    private String category;
    private int totalQuestions;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public Quiz(Long id, String title, String description, String category, int totalQuestions) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.createdAt = LocalDateTime.now();
        this.totalQuestions = totalQuestions;
    }
}
