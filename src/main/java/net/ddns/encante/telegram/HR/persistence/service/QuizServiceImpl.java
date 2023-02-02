package net.ddns.encante.telegram.HR.persistence.service;

import net.ddns.encante.telegram.HR.Quiz.Quiz;
import net.ddns.encante.telegram.HR.persistence.entities.QuizEntity;
import net.ddns.encante.telegram.HR.persistence.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("quizService")

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
    @Override
    public Quiz getFirstQuizFromDb(){
        if(quizRepository.count()<1){
            throw new RuntimeException("No entries in db");
        }
        else {
            return convertQuizEntityToObj(quizRepository.findFirstByOrderByKeyIdAsc());
        }
    }
    @Override
    public Quiz getQuizByMessageId(final Long messageId){
        if (quizRepository.findByMessageId(messageId)!= null)
        return convertQuizEntityToObj(quizRepository.findByMessageId(messageId));
        else {
            return null;
        }
    }
//    gets next quiz from db to send, checks if quiz is not already sent but unanswered
    @Override
    public Quiz getNextQuizToSendFromDb(){
        if(quizRepository.findAllQuizEntitiesToSend().size()>0)
            return convertQuizEntityToObj(quizRepository.findAllQuizEntitiesToSend().get(0));
        else {
            throw new RuntimeException("No entries in db");
        }
    }
//
//      CONVERT ENTITIES TO OBJECTS
//
    private Quiz convertQuizEntityToObj(QuizEntity entity) {
        Quiz obj = new Quiz(entity.getQuestion(),entity.getOptA(),entity.getOptB(),entity.getOptC(),entity.getOptD(),entity.getCorrectAnswer());
        if(entity.getKeyId() != null)
            obj.setQuizId(entity.getKeyId());
        obj.setQuestion(entity.getQuestion());
        if (entity.getWord() != null)
            obj.setWord(entity.getWord());
        if(entity.getAnswer() != null)
            obj.setAnswer(entity.getAnswer());
        obj.setSuccess(entity.getSuccess());
        if(entity.getDateSent()!=null)
            obj.setDateSent(entity.getDateSent());
        if(entity.getDateAnswered()!=null)
            obj.setDateAnswered(entity.getDateAnswered());
        if(entity.getMessageId()!=null)
            obj.setMessageId(entity.getMessageId());
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
        entity.setSuccess(obj.getSuccess());
        if (obj.getDateSent()!=null)
            entity.setDateSent(obj.getDateSent());
        if(obj.getDateAnswered()!=null)
            entity.setDateAnswered(obj.getDateAnswered());
        if(obj.getMessageId()!=null)
            entity.setMessageId(obj.getMessageId());
        return entity;
    }

}