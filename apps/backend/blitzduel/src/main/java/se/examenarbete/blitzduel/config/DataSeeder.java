package se.examenarbete.blitzduel.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import se.examenarbete.blitzduel.model.Question;
import se.examenarbete.blitzduel.model.Quiz;
import se.examenarbete.blitzduel.repository.QuizRepository;

import java.util.Arrays;

@Component
public class DataSeeder implements CommandLineRunner {

    private final QuizRepository quizRepository;


        public DataSeeder(QuizRepository quizRepository) {
            this.quizRepository = quizRepository;
        }

        @Override
         public void run(String... args) {
            if(quizRepository.count() > 0) {
                System.out.println("Database already seeded, skipping...");
                return;
            }
            System.out.println("Seeding database with quizzes...");

            seedGeografiQuiz();
            seedHistoriaQuiz();
            seedVetenskapQuiz();
            seedSportQuiz();
            seedAllmanbildningQuiz();
            seedPopKulturQuiz();

            System.out.println("Database seeded successfully with " + quizRepository.count() + " quizzes");
        }

        private void seedGeografiQuiz() {
            Quiz quiz = new Quiz("Geografi", "Testa dina kunskaper om världens geografi", "Geografi", "https://images.unsplash.com/photo-1594450281353-5a7a067358cc?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");

            quiz.addQuestion(new Question(
                    "Vad är huvudstaden i Afganistan",
                    Arrays.asList("Kabul", "Bamian", "Ghazni", "Jalalabad"),
                    0, 5
            ));

            quiz.addQuestion(new Question(
                    "Vad heter den längsta floden i världen?",
                    Arrays.asList("Nilen", "Amazonas", "Mississippi", "Yangtze"),
                    0, 5
            ));

            quiz.addQuestion(new Question(
                    "Vilket land har flest invånare?",
                    Arrays.asList("USA", "Indien", "Kina", "Indonesien"),
                    2, 5
            ));

            quiz.addQuestion(new Question(
                    "På vilken kontinent ligger Egypten?",
                    Arrays.asList("Asien", "Afrika", "Europa", "Sydamerika"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Vilket är världens minsta land?",
                    Arrays.asList("Monaco", "Vatikanstaten", "San Marino", "Liechtenstein"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Vilket hav ligger mellan Sverige och Finland?",
                    Arrays.asList("Östersjön", "Nordsjön", "Bottniska viken", "Öresund"),
                    2, 5
            ));

            quiz.addQuestion(new Question(
                    "Vad heter den högsta bergskedjan i världen?",
                    Arrays.asList("Alperna", "Anderna", "Himalaya", "Klippiga bergen"),
                    2, 5
            ));

            quiz.addQuestion(new Question(
                    "Vilket land är känt som 'det Land of the Rising Sun'?",
                    Arrays.asList("Kina", "Korea", "Japan", "Thailand"),
                    2, 5
            ));

            quiz.addQuestion(new Question(
                    "Hur många kontinenter finns det?",
                    Arrays.asList("5", "6", "7", "8"),
                    2, 5
            ));

            quiz.addQuestion(new Question(
                    "Vad heter öknen i norra Afrika?",
                    Arrays.asList("Gobi", "Sahara", "Atacama", "Kalahari"),
                    1, 5
            ));

            quizRepository.save(quiz);
        }

        private void seedHistoriaQuiz() {
            Quiz quiz = new Quiz("Historia", "Testa dina historiska kunskaper", "Historia", "https://plus.unsplash.com/premium_photo-1682125784386-d6571f1ac86a?q=80&w=908&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");

            quiz.addQuestion(new Question(
                    "Vilket år landade människan på månen?",
                    Arrays.asList("1965", "1969", "1972", "1975"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Vem var Sveriges första kvinnliga statsminister?",
                    Arrays.asList("Anna Lindh", "Mona Sahlin", "Margot Wallström", "Magdalena Andersson"),
                    3, 5
            ));

            quiz.addQuestion(new Question(
                    "Vilket år började andra världskriget?",
                    Arrays.asList("1937", "1939", "1941", "1943"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Vem skrev 'Hamlet'?",
                    Arrays.asList("Charles Dickens", "William Shakespeare", "Oscar Wilde", "Mark Twain"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Vilket år föll Berlinmuren?",
                    Arrays.asList("1987", "1989", "1991", "1993"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Vem var USA:s första president?",
                    Arrays.asList("Thomas Jefferson", "George Washington", "Abraham Lincoln", "John Adams"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Vilket århundrade levde Viking-eran?",
                    Arrays.asList("600-1100", "800-1100", "1000-1300", "700-1000"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Vem upptäckte Amerika år 1492?",
                    Arrays.asList("Vasco da Gama", "Ferdinand Magellan", "Christopher Columbus", "Amerigo Vespucci"),
                    2, 5
            ));

            quiz.addQuestion(new Question(
                    "Vilket år blev Sverige medlem i EU?",
                    Arrays.asList("1990", "1993", "1995", "1997"),
                    2, 5
            ));

            quiz.addQuestion(new Question(
                    "Vad hette det ryska imperiet före 1917?",
                    Arrays.asList("Sovjetunionen", "Tsarryssland", "Röda armén", "Bolsjevikerna"),
                    1, 5
            ));

            quizRepository.save(quiz);
        }

        private void seedVetenskapQuiz() {
            Quiz quiz = new Quiz("Vetenskap", "Utmana dig själv med vetenskapliga frågor", "Vetenskap", "https://images.unsplash.com/photo-1628595351029-c2bf17511435?q=80&w=1332&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");

            quiz.addQuestion(new Question(
                    "Hur många planeter finns det i solsystemet?",
                    Arrays.asList("7", "8", "9", "10"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Vad står DNA för?",
                    Arrays.asList("Deoxyribonucleic Acid", "Dynamic Nuclear Acid", "Digital Nucleic Acid", "Deoxygen Nuclear Acid"),
                    0, 5
            ));

            quiz.addQuestion(new Question(
                    "Vilket är det kemiska tecknet för guld?",
                    Arrays.asList("Go", "Gd", "Au", "Ag"),
                    2, 5
            ));

            quiz.addQuestion(new Question(
                    "Vad är ljusets hastighet?",
                    Arrays.asList("100 000 km/s", "200 000 km/s", "300 000 km/s", "400 000 km/s"),
                    2, 5
            ));

            quiz.addQuestion(new Question(
                    "Hur många ben har en vuxen människa?",
                    Arrays.asList("196", "206", "216", "226"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Vilket organ pumprar blod genom kroppen?",
                    Arrays.asList("Levern", "Hjärtat", "Lungorna", "Njurarna"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Vad kallas studien av liv?",
                    Arrays.asList("Geologi", "Fysik", "Biologi", "Kemi"),
                    2, 5
            ));

            quiz.addQuestion(new Question(
                    "Vilken planet är närmast solen?",
                    Arrays.asList("Venus", "Mars", "Merkurius", "Jorden"),
                    2, 5
            ));

            quiz.addQuestion(new Question(
                    "Vad är H2O?",
                    Arrays.asList("Syre", "Vatten", "Väte", "Kolsyra"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Hur många procent av jorden är täckt av vatten?",
                    Arrays.asList("50%", "60%", "70%", "80%"),
                    2, 5
            ));

            quizRepository.save(quiz);
        }

        private void seedSportQuiz() {
            Quiz quiz = new Quiz("Sport", "Testa dina kunskaper om sport", "Sport", "https://images.unsplash.com/photo-1511886929837-354d827aae26?q=80&w=764&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D\n");

            quiz.addQuestion(new Question(
                    "Vilket land vann fotbolls-VM 2018?",
                    Arrays.asList("Brasilien", "Tyskland", "Frankrike", "Argentina"),
                    2, 5
            ));

            quiz.addQuestion(new Question(
                    "Hur många spelare finns det i ett fotbollslag på planen?",
                    Arrays.asList("9", "10", "11", "12"),
                    2, 5
            ));

            quiz.addQuestion(new Question(
                    "Vilket år arrangerades de första moderna olympiska spelen?",
                    Arrays.asList("1892", "1896", "1900", "1904"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Hur lång är en maraton?",
                    Arrays.asList("40,195 km", "42,195 km", "44,195 km", "46,195 km"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Vilket land har vunnit flest OS-guld genom tiderna?",
                    Arrays.asList("Ryssland", "Kina", "Storbritannien", "USA"),
                    3, 5
            ));

            quiz.addQuestion(new Question(
                    "Vad heter Sveriges herrlandslag i ishockey kallat?",
                    Arrays.asList("Blågult", "Tre Kronor", "Viking Army", "Kronhjortarna"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Hur många Grand Slam-turneringar finns det i tennis?",
                    Arrays.asList("3", "4", "5", "6"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Vilket år vann Zlatan Ibrahimović sitt första stora pris?",
                    Arrays.asList("2002", "2004", "2006", "2008"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Vilken färg har den mellersta ringen i den olympiska logotypen?",
                    Arrays.asList("Röd", "Blå", "Svart", "Grön"),
                    2, 5
            ));

            quiz.addQuestion(new Question(
                    "Vilket år vann Sverige fotbolls-VM i guld för kvinnor?",
                    Arrays.asList("Aldrig", "1995", "2003", "2011"),
                    0, 5
            ));

            quizRepository.save(quiz);
        }

        private void seedAllmanbildningQuiz() {
            Quiz quiz = new Quiz("Allmänbildning", "Testa din allmänna kunskap", "Allmänbildning", "https://plus.unsplash.com/premium_photo-1675644727129-9e2fbc03c500?q=80&w=1344&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");

            quiz.addQuestion(new Question(
                    "Vilket programmeringsspråk använder Spring Boot?",
                    Arrays.asList("Python", "JavaScript", "Java", "C++"),
                    2, 5
            ));

            quiz.addQuestion(new Question(
                    "Hur många sekunder är det på en minut?",
                    Arrays.asList("30", "45", "60", "90"),
                    2, 5
            ));

            quiz.addQuestion(new Question(
                    "Vad står HTTP för?",
                    Arrays.asList("HyperText Transfer Protocol", "High Transfer Text Protocol", "HyperText Technical Protocol", "Home Text Transfer Protocol"),
                    0, 5
            ));

            quiz.addQuestion(new Question(
                    "Vilket djur är känt som 'djungelns kung'?",
                    Arrays.asList("Tiger", "Elefant", "Lejon", "Gorilla"),
                    2, 5
            ));

            quiz.addQuestion(new Question(
                    "Vilket år grundades Google?",
                    Arrays.asList("1996", "1998", "2000", "2002"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Vad heter världens största ö?",
                    Arrays.asList("Island", "Grönland", "Borneo", "Madagaskar"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Hur många tangenter har ett piano?",
                    Arrays.asList("76", "84", "88", "92"),
                    2, 5
            ));

            quiz.addQuestion(new Question(
                    "Vilket år lanserades första iPhone?",
                    Arrays.asList("2005", "2007", "2009", "2011"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Vad kallas rädslan för spindlar?",
                    Arrays.asList("Akrofobi", "Araknfobi", "Klaustrofobi", "Agorafobi"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Hur många färger finns det i regnbågen?",
                    Arrays.asList("5", "6", "7", "8"),
                    2, 5
            ));

            quizRepository.save(quiz);
        }

        private void seedPopKulturQuiz() {
            Quiz quiz = new Quiz("Pop-kultur", "Hur bra känner du till populärkulturen?", "Pop-kultur", "https://plus.unsplash.com/premium_photo-1682125724182-1eadf9d1360d?q=80&w=1028&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D\n");

            quiz.addQuestion(new Question(
                    "Vem spelade Iron Man i Marvel-filmerna?",
                    Arrays.asList("Chris Evans", "Robert Downey Jr.", "Chris Hemsworth", "Mark Ruffalo"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Vilket år släpptes första Harry Potter-filmen?",
                    Arrays.asList("1999", "2000", "2001", "2002"),
                    2, 5
            ));

            quiz.addQuestion(new Question(
                    "Vad heter huvudkaraktären i The Legend of Zelda?",
                    Arrays.asList("Zelda", "Link", "Ganon", "Navi"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Vilket år släpptes den första Star Wars-filmen?",
                    Arrays.asList("1975", "1977", "1979", "1981"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Vem sjunger 'Billie Jean'?",
                    Arrays.asList("Prince", "Michael Jackson", "Stevie Wonder", "James Brown"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Vilket band sjunger 'Bohemian Rhapsody'?",
                    Arrays.asList("The Beatles", "Led Zeppelin", "Queen", "Pink Floyd"),
                    2, 5
            ));

            quiz.addQuestion(new Question(
                    "Vad heter den gula familjen i den tecknade serien?",
                    Arrays.asList("The Flintstones", "The Jetsons", "The Simpsons", "Family Guy"),
                    2, 5
            ));

            quiz.addQuestion(new Question(
                    "Vilket år började TV-serien 'Friends'?",
                    Arrays.asList("1992", "1994", "1996", "1998"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Vem regisserade 'Titanic'?",
                    Arrays.asList("Steven Spielberg", "James Cameron", "Christopher Nolan", "Martin Scorsese"),
                    1, 5
            ));

            quiz.addQuestion(new Question(
                    "Vilket år lanserades Netflix?",
                    Arrays.asList("1995", "1999", "2001", "1997"),
                    3, 5
            ));

            quizRepository.save(quiz);
        }
    }



