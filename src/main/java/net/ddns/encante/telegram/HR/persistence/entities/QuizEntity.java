package net.ddns.encante.telegram.HR.persistence.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "Quiz")
@FieldDefaults(level = AccessLevel.PRIVATE)

//class for persisting / retrieving data for quiz questions that bot can send according to harmonogram
public class QuizEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    Long keyId;

//for persisting text content of each sent question
    @Column (name = "question")
    String question;
//    for counting wrong answers
    @Column (name = "retries_count")
    Integer retriesCount = 0;
//    for building quizes with less answers to choose from
    @Column (name = "answers_left")
    Integer answersLeft = 4;
//    options to choose from
    @Column (name = "opt_a")
    String optA;
    @Column (name = "opt_b")
    String optB;
    @Column (name = "opt_c")
    String optC;
    @Column (name = "opt_d")
    String optD;
    @Column (name = "correct_answer")
    String correctAnswer;
    @Column (name = "last_answer")
    String lastAnswer;
    @Column (name = "success")
    Boolean success;
    @Column (name = "date_sent")
    Long dateSent;
    @Column (name = "date_answered")
    Long dateAnswered;
    @Column (name = "message_id")
    Long messageId;
}
