package net.ddns.encante.telegram.HR.persistence.service;

import net.ddns.encante.telegram.HR.Quiz.Quiz;
import net.ddns.encante.telegram.HR.RequestHandler;
import net.ddns.encante.telegram.HR.TelegramMethods.AnswerCallbackQuery;
import net.ddns.encante.telegram.HR.TelegramMethods.MessageManager;
import net.ddns.encante.telegram.HR.TelegramMethods.SendMessage;
import net.ddns.encante.telegram.HR.TelegramObjects.InlineKeyboardMarkup;
import net.ddns.encante.telegram.HR.TelegramObjects.WebhookUpdate;
import net.ddns.encante.telegram.HR.Utils;
import net.ddns.encante.telegram.HR.persistence.entities.QuizEntity;
import net.ddns.encante.telegram.HR.persistence.repository.QuizRepository;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service("quizService")

public class QuizServiceImpl implements QuizService {
    private static final Logger log = LoggerFactory.getLogger(QuizServiceImpl.class);
    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private MessageManager msgMgr;
    private static final Logger log = LoggerFactory.getLogger(QuizServiceImpl.class);

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
    public SendMessage createQuizMessage(Long chatId, Quiz quiz) {
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
    @Override
    public void resolveQuizAnswer (WebhookUpdate update){ //method is void cause effect of its work is saved to db
//        prerequisite check for possible null-pointers
        if (getQuizByMessageId(update.getCallback_query().getMessage().getMessage_id()) != null
                && update.getCallback_query().getData() != null
                && update.getCallback_query().getMessage().getChat().getId() != null
                && update.getCallback_query().getMessage().getText() != null
        ){
//        getting quiz entity from db by message id and saving answer data to it
        Quiz quiz = getQuizByMessageId(update.getCallback_query().getMessage().getMessage_id());
//        check for null-pointers on quiz object
            if (quiz.getOptA() != null
            && quiz.getOptB() != null
            && quiz.getOptC() != null
            && quiz.getOptD() != null
            && quiz.getCorrectAnswer() != null
            && quiz.getAnswersLeft() != null
            && quiz.getRetriesCount() != null
            ){
        quiz.setDateAnswered(Utils.getCurrentUnixTime());
        quiz.setLastAnswer(update.getCallback_query().getData());
        //              edit sent quiz message: add answer. We would do it anyway so we'll do it at start
        msgMgr.editTelegramMessage(update.getCallback_query().getMessage().getChat().getId(),update.getCallback_query().getMessage().getMessage_id(), update.getCallback_query().getMessage().getText() + " Twoja odpowiedź: "+ quiz.getLastAnswer());
//        actual check
//        if answer is good-
        if (quiz.getLastAnswer().equals(quiz.getCorrectAnswer())){
//            write result to quiz object
            quiz.setSuccess(true);
//            set up reaction for answer:
//            by callback
            quiz.setReactionForAnswerCallback(new AnswerCallbackQuery(update.getCallback_query().getId(),"Bardzo dobrze!",true));
//            ...and by message
            quiz.setReactionForAnswerMessage(new SendMessage()
                    .setText("Dobra odpowiedź! ;)")
                    .setReply_to_message_id(update.getCallback_query().getMessage().getMessage_id())
                    .setChat_id(update.getCallback_query().getMessage().getChat().getId()));
        }
//        if answer is bad-
        else {
//            write answer details to quiz object
            quiz.setSuccess(false);
            quiz.setAnswersLeft(quiz.getAnswersLeft()-1);
            quiz.setRetriesCount(quiz.getRetriesCount()+1);
//            react for answer:
//            callback will be the same either it was last answer or not so well set it now
            quiz.setReactionForAnswerCallback(new AnswerCallbackQuery(update.getCallback_query().getId(),"Zła odpowiedź!",true);
//            check if it isn't last answer
            if (quiz.getAnswersLeft()>1) {
//                randomize answers and set used on last places
                List<String> answers = new ArrayList<>(List.of(quiz.getOptA(), quiz.getOptB(), quiz.getOptC(), quiz.getOptD()));
                List<String> usedAnswers = new ArrayList<>();
                switch (quiz.getAnswersLeft()) {
                    case 3 -> {
                        usedAnswers.add(quiz.getLastAnswer());
                        answers.remove(quiz.getLastAnswer());
                        Collections.shuffle(answers);
                        answers.add(quiz.getLastAnswer());
                    }
                    case 2 -> {
                        usedAnswers.add(quiz.getOptD());
                        usedAnswers.add(quiz.getLastAnswer());
                        answers.removeAll(usedAnswers);
                        Collections.shuffle(answers);
                        answers.add(usedAnswers.get(0));
                        answers.add(usedAnswers.get(1));
                    }
                }
                quiz.setOptA(answers.get(0));
                quiz.setOptB(answers.get(1));
                quiz.setOptC(answers.get(2));
                quiz.setOptD(answers.get(3));

//                react for answer by message now
                quiz.setReactionForAnswerMessage(new SendMessage()
                        .setText("Niestety zła odpowiedź :(")
                        .setReply_to_message_id(update.getCallback_query().getMessage().getMessage_id())
                        .setChat_id(update.getCallback_query().getMessage().getChat().getId()));
            }
//            if it was last try prepare correct answer to be sent in response
            else {
                quiz.setReactionForAnswerMessage(new SendMessage()
                        .setText("Niestety zła odpowiedź :( Prawidłowa odpowiedź to: "+ quiz.getCorrectAnswer())
                        .setReply_to_message_id(update.getCallback_query().getMessage().getMessage_id())
                        .setChat_id(update.getCallback_query().getMessage().getChat().getId()))
            }
        }
//        either way it was good or bad answer now is the time to send reaction
//        by callback (popup message with response)...
        msgMgr.answerCallbackQuery(quiz.getReactionForAnswerCallback());
//        and by message (reply to original message)
        msgMgr.sendTelegramObjAsMessage(quiz.getReactionForAnswerMessage());
//        send me an info about quiz answer:
        msgMgr.sendQuizResultInfo(quiz,msgMgr.getME());
//        after modifications save quiz to and end method
        saveQuiz(quiz);
//        if quiz object is incompatibile (unexpected nulls)
            }else {
                log.warn("ERROR. Unexpected null on Quiz object. Invoker: QuizServiceImpl.resolveQuizAnswer.");
                throw new RuntimeException("ERROR. Unexpected null on Quiz object. Invoker: QuizServiceImpl.resolveQuizAnswer.")
            }
//        if prerequisite checks failed:
        }else {
            log.warn("ERROR. Prerequisite check failed for QuizServiceImpl.resolveQuizAnswer.");
            throw new RuntimeException("ERROR. Prerequisite check failed for QuizServiceImpl.resolve answer.");
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