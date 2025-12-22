package se.examenarbete.blitzduel.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import se.examenarbete.blitzduel.model.Quiz;
import se.examenarbete.blitzduel.service.QuizService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class QuizControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizService quizService;

    @Test
    void getAllQuizzes_ShouldReturnListOfQuizzes() throws Exception {
        // Given
        Quiz quiz1 = new Quiz();
        quiz1.setId(1L);
        quiz1.setTitle("Quiz 1");
        quiz1.setDescription("Description 1");
        quiz1.setCategory("Category 1");

        Quiz quiz2 = new Quiz();
        quiz2.setId(2L);
        quiz2.setTitle("Quiz 2");
        quiz2.setDescription("Description 2");
        quiz2.setCategory("Category 2");

        List<Quiz> quizzes = Arrays.asList(quiz1, quiz2);
        when(quizService.getAllQuizzes()).thenReturn(quizzes);

        // When & Then
        mockMvc.perform(get("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Quiz 1"))
                .andExpect(jsonPath("$[0].description").value("Description 1"))
                .andExpect(jsonPath("$[0].category").value("Category 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Quiz 2"));
    }

    @Test
    void getAllQuizzes_ShouldReturnEmptyList_WhenNoQuizzes() throws Exception {
        // Given
        when(quizService.getAllQuizzes()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getQuizById_ShouldReturnQuiz_WhenExists() throws Exception {
        // Given
        Long quizId = 1L;
        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        quiz.setTitle("Test Quiz");
        quiz.setDescription("Test Description");
        quiz.setCategory("Test Category");

        when(quizService.getQuizById(quizId)).thenReturn(Optional.of(quiz));

        // When & Then
        mockMvc.perform(get("/api/quizzes/{id}", quizId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(quizId))
                .andExpect(jsonPath("$.title").value("Test Quiz"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.category").value("Test Category"));
    }

    @Test
    void getQuizById_ShouldReturn404_WhenQuizDoesNotExist() throws Exception {
        // Given
        Long quizId = 999L;
        when(quizService.getQuizById(quizId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/quizzes/{id}", quizId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getQuizById_ShouldHandleInvalidId() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/quizzes/{id}", "invalid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
