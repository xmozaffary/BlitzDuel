package se.examenarbete.blitzduel.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.examenarbete.blitzduel.model.Question;
import se.examenarbete.blitzduel.model.Quiz;
import se.examenarbete.blitzduel.repository.QuestionRepository;
import se.examenarbete.blitzduel.repository.QuizRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuizService quizService;

    private Quiz testQuiz;
    private Question testQuestion1;
    private Question testQuestion2;
    private Long testQuizId;

    @BeforeEach
    void setUp() {
        testQuizId = 1L;

        testQuiz = new Quiz();
        testQuiz.setId(testQuizId);
        testQuiz.setTitle("Test Quiz");
        testQuiz.setDescription("Test Description");
        testQuiz.setCategory("Test Category");

        testQuestion1 = new Question();
        testQuestion1.setId(1L);
        testQuestion1.setQuestionText("Question 1");
        testQuestion1.setCorrectAnswerIndex(0);

        testQuestion2 = new Question();
        testQuestion2.setId(2L);
        testQuestion2.setQuestionText("Question 2");
        testQuestion2.setCorrectAnswerIndex(1);
    }

    @Test
    void getAllQuizzes_ShouldReturnAllQuizzes() {
        // Given
        List<Quiz> expectedQuizzes = Arrays.asList(testQuiz, new Quiz());
        when(quizRepository.findAllWithQuestions()).thenReturn(expectedQuizzes);

        // When
        List<Quiz> result = quizService.getAllQuizzes();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedQuizzes, result);
        verify(quizRepository).findAllWithQuestions();
    }

    @Test
    void getAllQuizzes_ShouldReturnEmptyList_WhenNoQuizzes() {
        // Given
        when(quizRepository.findAllWithQuestions()).thenReturn(List.of());

        // When
        List<Quiz> result = quizService.getAllQuizzes();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(quizRepository).findAllWithQuestions();
    }

    @Test
    void getQuizById_ShouldReturnQuiz_WhenExists() {
        // Given
        when(quizRepository.findByIdWithQuestions(testQuizId))
                .thenReturn(Optional.of(testQuiz));

        // When
        Optional<Quiz> result = quizService.getQuizById(testQuizId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testQuiz.getId(), result.get().getId());
        assertEquals(testQuiz.getTitle(), result.get().getTitle());
        verify(quizRepository).findByIdWithQuestions(testQuizId);
    }

    @Test
    void getQuizById_ShouldReturnEmpty_WhenDoesNotExist() {
        // Given
        when(quizRepository.findByIdWithQuestions(testQuizId))
                .thenReturn(Optional.empty());

        // When
        Optional<Quiz> result = quizService.getQuizById(testQuizId);

        // Then
        assertTrue(result.isEmpty());
        verify(quizRepository).findByIdWithQuestions(testQuizId);
    }

    @Test
    void getQuestionsByQuizId_ShouldReturnQuestions() {
        // Given
        List<Question> expectedQuestions = Arrays.asList(testQuestion1, testQuestion2);
        when(questionRepository.findByQuizId(testQuizId))
                .thenReturn(expectedQuestions);

        // When
        List<Question> result = quizService.getQuestionsByQuizId(testQuizId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedQuestions, result);
        verify(questionRepository).findByQuizId(testQuizId);
    }

    @Test
    void getQuestionsByQuizId_ShouldReturnEmptyList_WhenNoQuestions() {
        // Given
        when(questionRepository.findByQuizId(testQuizId))
                .thenReturn(List.of());

        // When
        List<Question> result = quizService.getQuestionsByQuizId(testQuizId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(questionRepository).findByQuizId(testQuizId);
    }

    @Test
    void createQuiz_ShouldSaveAndReturnQuiz() {
        // Given
        Quiz newQuiz = new Quiz();
        newQuiz.setTitle("New Quiz");

        when(quizRepository.save(newQuiz)).thenReturn(testQuiz);

        // When
        Quiz result = quizService.createQuiz(newQuiz);

        // Then
        assertNotNull(result);
        assertEquals(testQuiz.getId(), result.getId());
        assertEquals(testQuiz.getTitle(), result.getTitle());
        verify(quizRepository).save(newQuiz);
    }

    @Test
    void createQuestion_ShouldSaveAndReturnQuestion() {
        // Given
        Question newQuestion = new Question();
        newQuestion.setQuestionText("New Question");

        when(questionRepository.save(newQuestion)).thenReturn(testQuestion1);

        // When
        Question result = quizService.createQuestion(newQuestion);

        // Then
        assertNotNull(result);
        assertEquals(testQuestion1.getId(), result.getId());
        assertEquals(testQuestion1.getQuestionText(), result.getQuestionText());
        verify(questionRepository).save(newQuestion);
    }
}
