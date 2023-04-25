package net.ddns.encante.telegram.HR.persistence.service;

import net.ddns.encante.telegram.HR.Quiz.Quiz;
import net.ddns.encante.telegram.HR.TelegramMethods.SendMessage;
import net.ddns.encante.telegram.HR.TelegramObjects.InlineKeyboardMarkup;
import net.ddns.encante.telegram.HR.TelegramObjects.WebhookUpdate;
import net.ddns.encante.telegram.HR.persistence.entities.QuizEntity;
import net.ddns.encante.telegram.HR.persistence.repository.QuizRepository;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("quizService")

public class QuizServiceImpl implements QuizService {
    private static final Logger log = LoggerFactory.getLogger(QuizServiceImpl.class);
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
            log.warn("No entries in db. Invoker: QuizServiceImpl.getNextQuizToSendFromDb()");
            throw new RuntimeException("No entries in db. Invoker: QuizServiceImpl.getNextQuizToSendFromDb()");
        }
    }
    @Override
    public SendMessage createQuizMessageFromCommand (WebhookUpdate update){
        String[] commands = update.getMessage().getText().split(" ");
        String[] keys = {commands[2],commands[3],commands[4],commands[5]};
        return new SendMessage()
                .setText(commands[1])
                .setReply_markup(new InlineKeyboardMarkup
                        .KeyboardBuilder(1,4,keys).build())
                .setChat_id(update.getMessage().getChat().getId());
    }
    @Override
    public SendMessage createMessage (Long chatId, Quiz quiz) {
        if (quiz.getOptA() != null && quiz.getOptB() != null && quiz.getOptC() != null && quiz.getOptD() != null && quiz.getQuestion() != null) {
            String[] keys = {quiz.getOptA(), quiz.getOptB(), quiz.getOptC(), quiz.getOptD()};
            SendMessage msg = new SendMessage().setText(quiz.getQuestion());
            switch (quiz.getAnswersLeft()) {
                case 4 -> msg
//                    .setText(quiz.getQuestion())
                        .setReply_markup(new InlineKeyboardMarkup
                                .KeyboardBuilder(1, 4, keys).build())
                        .setChat_id(chatId);
                case 3 -> msg
//                    .setText(quiz.getQuestion())
                        .setReply_markup(new InlineKeyboardMarkup
                                .KeyboardBuilder(1, 3, keys).build())
                        .setChat_id(chatId);
                case 2 -> msg
//                    .setText(this.question)
                        .setReply_markup(new InlineKeyboardMarkup
                                .KeyboardBuilder(1, 2, keys).build())
                        .setChat_id(chatId);
                default -> {
                    log.warn("Bad answers left number cant create quiz message");
                    throw new RuntimeException("Bad answers left number cant create quiz message");
                }
            }
            return msg;
        }else {
            log.warn("Quiz object incomplete. Can't create Quiz message. Invoker: QuizServiceImpl.createMessage");
            throw new RuntimeException("Quiz object incomplete. Can't create Quiz message. Invoker: QuizServiceImpl.createMessage");
        }
    }
//    public SendMessage createNextQuizMessage (Long chatId){
//        Quiz quiz = getNextQuizToSendFromDb();
//        String[] keys = {quiz.getOptA(),quiz.getOptB(),quiz.getOptC(),quiz.getOptD()};
//        SendMessage msg = new SendMessage().setText(quiz.getQuestion());
//        switch (quiz.getAnswersLeft()){
//            case 4 -> msg
////                    .setText(quiz.getQuestion())
//                    .setReply_markup(new InlineKeyboardMarkup
//                            .KeyboardBuilder(1,4,keys).build())
//                    .setChat_id(chatId);
//            case 3 -> msg
////                    .setText(quiz.getQuestion())
//                    .setReply_markup(new InlineKeyboardMarkup
//                            .KeyboardBuilder(1,3,keys).build())
//                    .setChat_id(chatId);
//            case 2 -> msg
////                    .setText(this.question)
//                    .setReply_markup(new InlineKeyboardMarkup
//                            .KeyboardBuilder(1,2,keys).build())
//                    .setChat_id(chatId);
//            default -> {
//                log.warn("Bad answers left number cant create quiz message");
//                throw new RuntimeException("Bad answers left number cant create quiz message");
//            }
//        }
//        return msg;
//    }


//
//      CONVERT ENTITIES TO OBJECTS
//
    private Quiz convertQuizEntityToObj(QuizEntity entity) {
        Quiz obj = new Quiz(StringEscapeUtils.escapeJava(entity.getQuestion()),entity.getOptA(),entity.getOptB(),entity.getOptC(),entity.getOptD(),entity.getCorrectAnswer());
        if(entity.getKeyId() != null)
            obj.setQuizId(entity.getKeyId());
        obj.setQuestion(entity.getQuestion());
        obj.setRetriesCount(entity.getRetriesCount());
        obj.setAnswersLeft(entity.getAnswersLeft());
        if(entity.getLastAnswer() != null)
            obj.setLastAnswer(entity.getLastAnswer());
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
        entity.setQuestion(StringEscapeUtils.unescapeJava(obj.getQuestion()));
        entity.setRetriesCount(obj.getRetriesCount());
        entity.setAnswersLeft(obj.getAnswersLeft());
        entity.setOptA(obj.getOptA());
        entity.setOptB(obj.getOptB());
        entity.setOptC(obj.getOptC());
        entity.setOptD(obj.getOptD());
        entity.setCorrectAnswer(obj.getCorrectAnswer());
        if(obj.getLastAnswer()!=null)
            entity.setLastAnswer(obj.getLastAnswer());
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