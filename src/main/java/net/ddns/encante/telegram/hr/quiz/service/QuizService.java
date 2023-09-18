package net.ddns.encante.telegram.hr.quiz.service;

import lombok.extern.slf4j.Slf4j;
import net.ddns.encante.telegram.hr.Utils;
import net.ddns.encante.telegram.hr.events.EventType;
import net.ddns.encante.telegram.hr.events.entity.Event;
import net.ddns.encante.telegram.hr.events.repository.EventRepository;
import net.ddns.encante.telegram.hr.exceptions.UnexpectedStateException;
import net.ddns.encante.telegram.hr.quiz.entity.Quiz;
import net.ddns.encante.telegram.hr.quiz.repository.QuizRepository;
import net.ddns.encante.telegram.hr.telegram.api.methods.AnswerCallbackQuery;
import net.ddns.encante.telegram.hr.telegram.api.methods.EditMessageText;
import net.ddns.encante.telegram.hr.telegram.api.methods.SendMessage;
import net.ddns.encante.telegram.hr.telegram.api.objects.ForceReply;
import net.ddns.encante.telegram.hr.telegram.api.objects.InlineKeyboardMarkup;
import net.ddns.encante.telegram.hr.telegram.api.objects.SentMessage;
import net.ddns.encante.telegram.hr.telegram.api.objects.WebhookUpdate;
import net.ddns.encante.telegram.hr.telegram.service.MessageManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service("quizService")
@Slf4j

public class QuizService {
    private Quiz quiz;
    private QuizRepository quizRepository;
    private MessageManager msgMgr;
    private EventRepository eventsRepo;

public QuizService(QuizRepository repository, MessageManager msgMgr, EventRepository eventsRepo){
    this.quizRepository=repository;
    this.msgMgr=msgMgr;
    this.eventsRepo=eventsRepo;
}

    /**
     * Checked for nulls
     * @return
     */
    private Quiz saveQuiz() {
        if (this.quiz == null) {
            throw new UnexpectedStateException("QS.sQ", "Quiz field in QuizService is null",msgMgr);
        }else {
            //        no checking for existing records @ db right now
            return quizRepository.save(quiz);
        }
    }
    
    public boolean deleteQuizById(final Long quizId){
        quizRepository.deleteById(quizId);
        return true;
    }
    
    public Quiz getFirstQuizFromDb(){
        if(quizRepository.count()>0){
            return quizRepository.findFirstByOrderByKeyIdAsc();
        }
        else {
            log.warn("No entries in db. getFirstQuizFromDb");
            throw new RuntimeException("No entries in db.");
        }
    }

    /**
     * Checked for nulls
     * @param chatId
     * @param messageId
     * @return
     */
    public Quiz getQuizByCredentials(Long chatId, Long messageId){
        this.quiz=quizRepository.findByCredentials(chatId, messageId);
            if (quiz == null) throw new UnexpectedStateException("QS.gQBC","No quiz in DB with such credentials. ChatId: "+chatId+" messageId: "+messageId,msgMgr);
            else return quiz;
    }
    public int countRemainingQuizToSend(){
        return quizRepository.findAllQuizEntitiesToSend().size();
    }
//    gets next quiz from db to send, checks if quiz is not already sent but unanswered
    
    private Quiz getNextQuizToSendFromDb(){
        if(countRemainingQuizToSend()>0)
            return quizRepository.findAllQuizEntitiesToSend().get(0);
        else {
            log.debug("No entries in db. MenuData: getNextQuizToSendFromDb()");
            return null;
        }
    }
    
    public SendMessage createQuizMessage(Quiz quiz) {
        if (quiz.getOptA() != null && quiz.getOptB() != null && quiz.getOptC() != null && quiz.getOptD() != null && quiz.getQuestion() != null && quiz.getChatId()!= null) {
//            there are two types of quiz messages actually possible
//            if optA in DB is "forceReply" we know it will be a force-reply type
//            if not - it will be 4 options to choose as keys
            if (quiz.getOptA().equalsIgnoreCase("forceReply")){
//                small work-around so that force-reply type of quiz get valid 4 chances
                if(quiz.getMessageId()==null){
                    quiz.setAnswersLeft(5);
                    saveQuiz(quiz);
                }
//                do force-reply type of quiz
                return createForceReplyQuizMessageWithButton(quiz);
            }else return createAbcdQuizMessage(quiz);
        }else {
            log.warn("quiz object incomplete. Can't create quiz message. MenuData: QuizServiceImpl.createMessage");
            throw new RuntimeException("quiz object incomplete. Can't create quiz message. MenuData: QuizServiceImpl.createMessage");
        }
    }
    
    public void resolveQuizAnswer (WebhookUpdate update){ //method is void cause effect of its work is saved to db
//        we have to differentiate now is it answer for abcd type quiz or force-reply type of quiz, because it determines how we gonna handle it
//        if update doesn't contain callback query but it contains reply to message- it's gonna be a force-reply type of quiz
        if (update.getCallback_query()!=null){
            if(update.getCallback_query().getData().equalsIgnoreCase("відповісти")){
                sendForceReplyQuizWithForceReply(update);
            }else resolveAbcdQuiz(update);
        }else if (update.getMessage() == null) {
            msgMgr.sendBackTelegramTextMessage("Erorr code: QS.rQA001");
            log.warn("Error code: QS.rQA001. Update doesn't contain Message nor Callback Querry. Can't be resolved by QuizService.resolveQuizAnswer");
            throw new RuntimeException("Error code: QS.rQA001. Update doesn't contain Message nor Callback Querry. Can't be resolved by QuizService.resolveQuizAnswer");
        } else if (update.getMessage().getReply_to_message()!= null) {
            resolveForceReplyQuiz(update);
        }else {
            msgMgr.sendBackTelegramTextMessage("Erorr code: QS.rQA002");
            log.warn("Erorr code: QS.rQA002. Update not containing crucial objects.");
            throw new RuntimeException("Erorr code: QS.rQA002. Update not containing crucial objects.");
        }
    }
    
    public Quiz sendNextQuizToId(Long chatId){
        if (getNextQuizToSendFromDb() == null) {
            List<Quiz> allQuizes = quizRepository.findAllByOrderByKeyIdAsc();
            Collections.shuffle(allQuizes);
            allQuizes.subList(1,allQuizes.size()).clear();
            resetQuizList(allQuizes);
        }
//                            get next not sent quiz from db
        Quiz quiz = getNextQuizToSendFromDb();
        quiz.setChatId(chatId);
//                            send quiz message
        SentMessage sentQuizMsg = msgMgr.sendTelegramMessage(createQuizMessage(quiz));
//                            update quiz obj
        quiz.setMessageId(sentQuizMsg.getResult().getMessage_id());
        quiz.setChatId(sentQuizMsg.getResult().getChat().getId());
        quiz.setDateSent(sentQuizMsg.getResult().getDate());
        quiz.setLastAnswer(null);
//        send raport
        msgMgr.sendTelegramMessage("quiz "+ quiz.getQuestion() +" wysłany", msgMgr.getME());
        log.debug("quiz "+quiz.getKeyId()+ " wyslany <<<<<<<<<");
//                            save updated quiz to db
        return saveQuiz(quiz);
    }
    @Async
    @Scheduled(cron = "0 55 7 ? * SAT")
    public void prepareQuizForWeekend(){
        List<Quiz> retriesFromLastWeek = quizRepository.findAllRetriesFromLastWeek(Utils.getCurrentUnixTime());
        List<Quiz> notSentYet = quizRepository.findAllQuizEntitiesToSend();
        int lastWeekEvents = eventsRepo.findAllEventsFromLastWeek(Utils.getCurrentUnixTime()).size();
        int goodAnswerCount = eventsRepo.findGoodAnswerEventsFromLastWeek(Utils.getCurrentUnixTime()).size();
        int rate =  Math.round(((float)goodAnswerCount/lastWeekEvents)*100);
        String summary = "W zeszłym tygodniu miałaś "+goodAnswerCount+" dobrych odpowiedzi na "+ lastWeekEvents+". Miałaś "+rate+"% odpowiedzi poprawnych.";
        msgMgr.sendTelegramMessage(summary,msgMgr.getME());
        msgMgr.sendTelegramMessage(summary, msgMgr.getYASIA());
        if (retriesFromLastWeek.size()+notSentYet.size()<24){
            List<Quiz>allOtherRetries = quizRepository.findAllRetries();
            allOtherRetries.removeAll(retriesFromLastWeek);
            Collections.shuffle(allOtherRetries);
            for (int i = retriesFromLastWeek.size()+notSentYet.size(); i < 24; i++) {
                retriesFromLastWeek.add(allOtherRetries.get(0));
                allOtherRetries.remove(0);
            }
            resetQuizList(retriesFromLastWeek);
        }else if (retriesFromLastWeek.size()>24){
            Collections.shuffle(retriesFromLastWeek);
            retriesFromLastWeek.subList(24,retriesFromLastWeek.size()).clear();
            resetQuizList(retriesFromLastWeek);
        }
        else resetQuizList(retriesFromLastWeek);
        log.info("Lista weekendowych Quizow przygotowana.");
        msgMgr.sendTelegramMessage("Lista weekendowych Quizow przygotowana.",msgMgr.getME());
    }
    public String testQuizForWeekend(){
        int lastWeekEvents = eventsRepo.findAllEventsFromLastWeek(Utils.getCurrentUnixTime()).size();
        int goodAnswerCount = eventsRepo.findGoodAnswerEventsFromLastWeek(Utils.getCurrentUnixTime()).size();
        int rate =  Math.round(((float)goodAnswerCount/lastWeekEvents)*100);
        return "W zeszłym tygodniu miałaś "+goodAnswerCount+" dobrych odpowiedzi na "+ lastWeekEvents+" i miałaś "+rate+"% odpowiedzi poprawnych.";
    }


    @Async
    @Scheduled(cron = "0 0 8-18 ? * *")
    public void sendNextQuizToYasia(){
    sendNextQuizToId(msgMgr.getYASIA());
    }
    private SendMessage createAbcdQuizMessage (){
        if (quiz.getOptA() == null
        || quiz.getOptB() == null
        || quiz.getOptC() == null
        || quiz.getOptD() == null
        || quiz.getQuestion() == null
        || quiz.getChatId() == null
        || quiz.getAnswersLeft() == null
        ) throw new UnexpectedStateException("QS.cAQM","Quiz field does not contain required attributes.",msgMgr);
        String[] keys = {quiz.getOptA(), quiz.getOptB(), quiz.getOptC(), quiz.getOptD()};
        SendMessage msg = new SendMessage().setText(quiz.getQuestion()).setChat_id(quiz.getChatId());
        switch (quiz.getAnswersLeft()) {
            case 4 -> msg
                    .setReply_markup(new InlineKeyboardMarkup
                            .KeyboardBuilder(1, 4, keys).build());
            case 3 -> msg
                    .setReply_markup(new InlineKeyboardMarkup
                            .KeyboardBuilder(1, 3, keys).build());
            case 2 -> msg
                    .setReply_markup(new InlineKeyboardMarkup
                            .KeyboardBuilder(1, 2, keys).build());
            default -> throw new UnexpectedStateException("QS.cAQM01", "Unexpected answersLeft number cant create quiz message", msgMgr);
        }
        return msg;
    }
    private SendMessage createForceReplyQuizMessageWithButton(){
        if (quiz.getQuestion() == null
        || quiz.getChatId() == null) throw new UnexpectedStateException("QS.cFRQMWB","Quiz field does not contain required attributes.",msgMgr);
        String[] key = {"відповісти"};
        return new SendMessage().setText(quiz.getQuestion())
                .setChat_id(quiz.getChatId())
                .setReply_markup(new InlineKeyboardMarkup.KeyboardBuilder(1,1,key).build());
    }

    /**
     * Checked for nulls
     * @param chatId
     * @return
     */
    private SendMessage createForceReplyQuizMessageWithForceReply(Long chatId){
        if (quiz == null
        || quiz.getQuestion() == null) throw new UnexpectedStateException("QS.cFRQMWFR", "Quiz field is null or question is missing.",msgMgr);
        else {
            return new SendMessage().setText(quiz.getQuestion())
                    .setChat_id(chatId)
                    .setReply_markup(new ForceReply()
                            .setForce_reply(true)
                            .setInput_field_placeholder("Введіть свою відповідь тут."));
        }
    }
    private void sendForceReplyQuizWithForceReply(WebhookUpdate update){
        this.quiz = getQuizByCredentials(update.getCallback_query().getFrom().getId(), update.getCallback_query().getMessage().getMessage_id());
//        firstly we delete original message because it cant be edited to force reply
        msgMgr.deleteTelegramMessage(quiz.getChatId(),quiz.getMessageId());
//        now send message with force reply to chat and update quiz object with new message id...
        quiz.setMessageId(msgMgr.sendTelegramMessage(createForceReplyQuizMessageWithForceReply(update.getCallback_query().getFrom().getId())).getResult().getMessage_id());
//        and save new message id to quiz in db
        saveQuiz();
    }

    /**
     * Checked for nulls
     */
    private void sendForceReplyQuizWithForceReply(){
        if (quiz == null
        || quiz.getChatId() == null
        || quiz.getMessageId() == null) throw new UnexpectedStateException("QS.sFRQWFR","Quiz field is null or missing chatId or messageId",msgMgr);
        else {
//        firstly we delete original message because it cant be edited to force reply
            msgMgr.deleteTelegramMessage(quiz.getChatId(),quiz.getMessageId());
//        now send message with force reply to chat and update quiz object with new message id...
            quiz.setMessageId(msgMgr.sendTelegramMessage(createForceReplyQuizMessageWithForceReply(quiz.getChatId())).getResult().getMessage_id());
//        and save new message id to quiz in db
            saveQuiz();
        }
    }
    private void resolveAbcdQuiz (WebhookUpdate update){
        if (update == null
        || update.getCallback_query() == null
        || update.getCallback_query().getMessage() == null
        || update.getCallback_query().getMessage().getChat() == null
        || update.getCallback_query().getMessage().getChat().getId() == null
        || update.getCallback_query().getMessage().getMessage_id() == null
        || update.getCallback_query().getData() == null) throw new UnexpectedStateException("QS.rAQ00", "Webhook update obj invalid.", msgMgr);
//        null check on update obj
        else {
            this.quiz = getQuizByCredentials(update.getCallback_query().getMessage().getChat().getId(), update.getCallback_query().getMessage().getMessage_id());
            if (quiz == null
                    || quiz.getOptA() == null
                    || quiz.getOptB() == null
                    || quiz.getOptC() == null
                    || quiz.getOptD() == null
                    || quiz.getCorrectAnswer() == null
                    || quiz.getAnswersLeft() == null
                    || quiz.getRetriesCount() == null) throw new UnexpectedStateException("QS.rAQ01","Quiz field incomplete. Check quiz object in db.",msgMgr);
//        null checks on quiz
            else {
                quiz.setDateAnswered(Utils.getCurrentUnixTime());
                quiz.setLastAnswer(update.getCallback_query().getData());
//              edit sent quiz message: add answer. We would do it anyway so we'll do it at start
                msgMgr.editTelegramMessage(EditMessageText.builder()
                        .chat_id(quiz.getChatId())
                        .message_id(quiz.getMessageId())
                        .text(update.getCallback_query().getMessage().getText()+"\nTwoja odpowiedź: "+ update.getCallback_query().getData())
                        .build()
                );
                /// TODO: 18.09.2023  
            }
        }

//        actual check
//        if answer is good-
                if (quiz.getLastAnswer().equals(quiz.getCorrectAnswer())){
//            write result to quiz object
                    quiz.setSuccess(true);
//                    we add event for statistics
                    eventsRepo.save(new Event().type(EventType.QUIZ_GOOD_ANSWER).date(Utils.getCurrentUnixTime()));
//            reset available answers. Will need it for future reuse of quiz.
                    quiz.setAnswersLeft(4);
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
//                    we add event for stats
                    eventsRepo.save(new Event().type(EventType.QUIZ_BAD_ANSWER).date(Utils.getCurrentUnixTime()));
//            react for answer:
//            callback will be the same either it was last answer or not so well set it now
                    quiz.setReactionForAnswerCallback(new AnswerCallbackQuery(update.getCallback_query().getId(),"Zła odpowiedź!",true));
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

//                set up reaction for answer by message
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
                                .setChat_id(update.getCallback_query().getMessage().getChat().getId()));
                        quiz.setAnswersDepleted(true);
                        quiz.setAnswersLeft(4);
                    }
                }
//        either way it was good or bad answer now is the time to send reaction
//        by callback (popup message with response)...
                msgMgr.answerCallbackQuery(quiz.getReactionForAnswerCallback());
//        and by message (reply to original message)
                msgMgr.sendTelegramMessage(quiz.getReactionForAnswerMessage());
//        send me an info about quiz answer:
                msgMgr.sendQuizResultInfo(quiz,msgMgr.getME());
//        after modifications save quiz to and end method
                saveQuiz(quiz);
//        if quiz object is incompatibile (unexpected nulls)
            }
    private void resolveForceReplyQuiz (WebhookUpdate update){
        if (update == null
        || update.getMessage() == null
        || update.getMessage().getFrom() == null
        || update.getMessage().getFrom().getId() == null
        || update.getMessage().getReply_to_message() == null
        || update.getMessage().getReply_to_message().getMessage_id() == null) throw new UnexpectedStateException("QS.rFRQ", "Webhook update object invalid.",msgMgr);
//        check for nulls on update obj
        else {
//        getting quiz entity from db by credentials
            this.quiz = getQuizByCredentials(update.getMessage().getFrom().getId(), update.getMessage().getReply_to_message().getMessage_id());
//        check for null-pointers on quiz object
            if (quiz == null
                    || quiz.getOptA() == null
                    || quiz.getOptB() == null
                    || quiz.getOptC() == null
                    || quiz.getOptD() == null
                    || quiz.getCorrectAnswer() == null
                    || quiz.getAnswersLeft() == null
                    || quiz.getRetriesCount() == null) throw new UnexpectedStateException("QS.rFRQ01","Quiz field incomplete. Check quiz object in db.",msgMgr);
            else if (update.getMessage().getText() == null){
//                        send again quiz question asking to reply correctly
                msgMgr.sendBackTelegramTextMessage("Nie wpisano odpowiedzi. Spróbuj jeszcze raz.");
                sendForceReplyQuizWithForceReply();
            }
//        null checks passed
            else {
                quiz.setDateAnswered(Utils.getCurrentUnixTime());
                quiz.setLastAnswer(update.getMessage().getText());
//              edit sent quiz message: add user answer. We would do it anyway so we'll do it at start
//                we actually can't edit it, because of telegram restrictions, so we are deleting it and sending again and saving new messageId to quiz object
                msgMgr.deleteTelegramMessage(quiz.getChatId(), quiz.getMessageId());
                quiz.setMessageId(msgMgr.sendTelegramMessage(quiz.getQuestion() + "\nTwoja odpowiedź: " + quiz.getLastAnswer(), quiz.getChatId()).getResult().getMessage_id());
//        actual check
//        if answer is good-
                if (quiz.getLastAnswer().equalsIgnoreCase(quiz.getCorrectAnswer()) || quiz.getLastAnswer().equalsIgnoreCase(quiz.getCorrectAnswer() + " ")) {
//            write result to quiz object
                    quiz.setSuccess(true);
//                        if it's Yana answer save an event for statistics
                    if (quiz.getChatId().equals(msgMgr.getYASIA())) eventsRepo.save(new Event().date(Utils.getCurrentUnixTime()).type(EventType.QUIZ_FR_GOOD_ANSWER));
//            reset available answers. Will need it for future reuse of quiz.
                    quiz.setAnswersLeft(5);
//                    set up message reaction for answer
                    quiz.setReactionForAnswerMessage(new SendMessage()
                            .setText("Dobra odpowiedź! :)")
                            .setReply_to_message_id(update.getMessage().getMessage_id())
                            .setChat_id(quiz.getChatId()));
//                if answer is bad:
                }else {
//            write answer details to quiz object
                    quiz.setSuccess(false);
                    quiz.setAnswersLeft(quiz.getAnswersLeft() - 1);
                    quiz.setRetriesCount(quiz.getRetriesCount() + 1);
//                        save an event to the table if it's Yana answer
                    if (quiz.getChatId().equals(msgMgr.getYASIA())) eventsRepo.save(new Event().date(Utils.getCurrentUnixTime()).type(EventType.QUIZ_FR_BAD_ANSWER));
//                    react for answer:
//                    check if it isn't last answer
                    if (quiz.getAnswersLeft() > 1) {
//                set up reaction for answer by message
                        int answersLeft = quiz.getAnswersLeft() - 1;
                        quiz.setReactionForAnswerMessage(new SendMessage()
                                .setText("Niestety zła odpowiedź :( Pozostało " + answersLeft + " prób.")
                                .setReply_to_message_id(update.getMessage().getMessage_id())
                                .setChat_id(update.getMessage().getFrom().getId()));
//            if it was last try prepare correct answer to be sent in response
                    }else {
                        quiz.setReactionForAnswerMessage(new SendMessage()
                                .setText("Niestety zła odpowiedź :( Prawidłowa odpowiedź to: " + quiz.getCorrectAnswer())
                                .setReply_to_message_id(update.getMessage().getMessage_id())
                                .setChat_id(update.getMessage().getFrom().getId()));
//                    again "reset" quiz object but this time set answers depleted flag for quiz to know that it's not marked to send
                        quiz.setAnswersLeft(5);
                        quiz.setAnswersDepleted(true);
                    }
                }
//        either way it was good or bad answer now is the time to send reaction
//                by message (reply to original message)
                msgMgr.sendTelegramMessage(quiz.getReactionForAnswerMessage());
//        send me an info about quiz answer:
                sendQuizResultInfo(msgMgr.getME());
//        after modifications save quiz to and end method
                saveQuiz();
            }
        }
    }
    private void resetQuizList(List<Quiz> listToReset){
        for (Quiz q :
                listToReset) {
            q.setSuccess(false);
            q.setAnswersDepleted(false);
            quizRepository.save(q);
        }
        log.debug("List of Quizes reseted.");
    }
    private void sendQuizResultInfo (Long whoTo){
    if (quiz == null ||
    quiz.getSuccess() == null ||
    quiz.getQuestion() == null ||
    quiz.getLastAnswer() == null) throw new UnexpectedStateException("QS.sQRI","Quiz is null or question or lastAnswer missing.",msgMgr);
        else {
            if (quiz.getSuccess()) msgMgr.sendTelegramMessage("Dobra odpowiedź na pytanie: " + quiz.getQuestion(), whoTo);
            else msgMgr.sendTelegramMessage("Zła odpowiedź na pytanie: " + quiz.getQuestion() + " Odpowiedź: " + quiz.getLastAnswer(), whoTo);
        }
    }
}