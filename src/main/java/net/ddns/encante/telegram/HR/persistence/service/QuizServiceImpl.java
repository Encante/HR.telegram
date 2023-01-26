package net.ddns.encante.telegram.HR.persistence.service;

import net.ddns.encante.telegram.HR.Quiz.Quiz;
import net.ddns.encante.telegram.HR.persistence.entities.QuizEntity;
import net.ddns.encante.telegram.HR.persistence.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class QuizServiceImpl implements QuizService {
    @Autowired
    private QuizRepository quizRepository;

    @Override
    public Quiz saveQuiz(Quiz quiz) {
        QuizEntity quizEntity = convertQuizObjToEntity(quiz);
//        no checking for existing records @ db right now
        return convertQuizEntityToObj(quizRepository.save(quizEntity));
    }
    @Override
    public boolean deleteQuizById(final Long quizId){
        quizRepository.deleteById(quizId);
        return true;
    }
    //
//      CONVERT ENTITIES TO OBJECTS
//
    private Quiz convertQuizEntityToObj(QuizEntity entity) {
        Quiz obj = new Quiz();
        if(entity.getKeyId() != null)
            obj.setQuizId(entity.getKeyId());
        obj.setQuestion(entity.getQuestion());
        if (entity.getWord() != null)
            obj.setWord(entity.getWord());
        obj.setOptA(entity.getOptA());
        obj.setOptB(entity.getOptB());
        obj.setOptC(entity.getOptC());
        obj.setOptD(entity.getOptD());
        obj.setCorrectAnswer(entity.getCorrectAnswer());
        if(entity.getAnswer() != null)
            obj.setAnswer(entity.getAnswer());
        obj.setSuccess(entity.isSuccess());
        if(entity.getDateSent()!=null)
            obj.setDateSent(entity.getDateSent());
        if(entity.getDateAnswered()!=null)
            obj.setDateAnswered(entity.getDateAnswered());
        return obj;
    }
//
//      CONVERT OBJECTS TO ENTITIES
//
    private QuizEntity convertQuizObjToEntity(Quiz obj){
        QuizEntity entity = new QuizEntity();
        if(obj.getQuizId()!=null)
        entity.setKeyId(obj.getQuizId());
        entity.setQuestion(obj.getQuestion());
        if (obj.getWord() != null)
            entity.setWord(obj.getWord());
        entity.setOptA(obj.getOptA());
        entity.setOptB(obj.getOptB());
        entity.setOptC(obj.getOptC());
        entity.setOptD(obj.getOptD());
        entity.setCorrectAnswer(obj.getCorrectAnswer());
        if(obj.getAnswer()!=null)
            entity.setAnswer(obj.getAnswer());
        entity.setSuccess(obj.isSuccess());
        if (obj.getDateSent()!=null)
            entity.setDateSent(obj.getDateSent());
        if(obj.getDateAnswered()!=null)
            entity.setDateAnswered(obj.getDateAnswered());
        return entity;
    }

}