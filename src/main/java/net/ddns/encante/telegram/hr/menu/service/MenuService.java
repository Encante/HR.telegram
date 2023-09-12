package net.ddns.encante.telegram.hr.menu.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.ddns.encante.telegram.hr.Utils;
import net.ddns.encante.telegram.hr.hue.service.HueAuthorizationService;
import net.ddns.encante.telegram.hr.menu.entity.InlineMenuButton;
import net.ddns.encante.telegram.hr.menu.entity.Menu;
import net.ddns.encante.telegram.hr.menu.entity.MenuData;
import net.ddns.encante.telegram.hr.menu.entity.MenuPattern;
import net.ddns.encante.telegram.hr.menu.repository.MenuRepository;
import net.ddns.encante.telegram.hr.quiz.service.QuizService;
import net.ddns.encante.telegram.hr.telegram.api.methods.EditMessageText;
import net.ddns.encante.telegram.hr.telegram.api.methods.SendMessage;
import net.ddns.encante.telegram.hr.telegram.api.objects.ForceReply;
import net.ddns.encante.telegram.hr.telegram.api.objects.InlineKeyboardButton;
import net.ddns.encante.telegram.hr.telegram.api.objects.InlineKeyboardMarkup;
import net.ddns.encante.telegram.hr.telegram.api.objects.WebhookUpdate;
import net.ddns.encante.telegram.hr.telegram.entity.UserEntity;
import net.ddns.encante.telegram.hr.telegram.service.MessageManager;
import net.ddns.encante.telegram.hr.telegram.service.WebhookUpdateService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service("menuService")
public class MenuService {
    private QuizService quizService;
    private WebhookUpdateService webhookService;
    private MenuRepository menuRepo;
    private MessageManager msgMgr;
    private HueAuthorizationService hueAuthorizationService;
    //private Menu newMenu;
    private Menu menu;
    private MenuPattern newPattern;
    private MenuData data;

    public MenuService(QuizService quizService, MenuRepository menuRepository, MessageManager msgMgr, HueAuthorizationService hueAuthorizationService, WebhookUpdateService wus){
        this.quizService = quizService;
        this.menuRepo = menuRepository;
        this.msgMgr = msgMgr;
        this.hueAuthorizationService = hueAuthorizationService;
        this.webhookService = wus;
    }

    public void sendMainMenu(@NotNull Long chatId){
        menu = menuRepo.findByChatId(chatId);
        if (menu == null){
            log.debug("New menu.");
            newMenu = new Menu(chatId,Utils.getCurrentUnixTime(),menuRepo.getPatternByName("MainMenu"),"new");
            newMenu.setMessageId(msgMgr.sendTelegramMessage(createMenuMessageWithButtons()).getResult().getMessage_id());
            saveMenu();
        }else {
            log.debug("Menu existing in DB");
            if (menu.getMessageId() == null) {
                msgMgr.sendAndLogErrorMsg("MS.cMM001","Can't delete old menu message. Menu object doesn't have a messageId assigned");
            }else{
                //            set up new menu
                sendNextMenuPatternByDeletingOldAndSave("MainMenu", "created");
            }
        }
    }
    public void handleMenuButton(@NotNull WebhookUpdate update){
        menu = menuRepo.findByCredentials(msgMgr.getOriginalSender().getId(),update.getCallback_query().getMessage().getMessage_id());
        if(menu == null){
            msgMgr.sendAndLogErrorMsg("MS.hMC001","Can't find menu in DB");
        }else{
            String buttonAction = update.getCallback_query().getData();
            if (buttonAction == null) {
                msgMgr.sendAndLogErrorMsg("MS.hMC002","Button Action is null.");
            }else {
                log.debug("ButtonAction received: "+buttonAction);
                switch (buttonAction){
                    case "/testMenu" -> {
                        sendNextMenuPatternByEditingOldAndSave("TestMenu",buttonAction);
                    }
                    case "/getTestInput" ->{
                        sendNextInfoTextInputMenuAndSave("Wpisz tu: ","Wpisz tu:",buttonAction);
                    }
                    case "/back" ->{
                        if (menu == null || menu.getCurrentPattern() == null || menu.getCurrentPattern().getUpperPatternName() == null) {
                            msgMgr.sendAndLogErrorMsg("MS.hMC003", "No upper pattern in MenuPattern: "+ newMenu.getCurrentPattern().getName()+". Object invalid");
                        }else {
                            newMenu= menu;
                            newMenu.setLastSentDate(Utils.getCurrentUnixTime());
                            newMenu.setLastPattern(menu.getCurrentPattern());
                            sendNextMenuPatternByEditingOldAndSave(newMenu.getLastPattern().getUpperPatternName(),buttonAction);
                        }
                    }
                    case "/quiz" -> {
                        sendNextMenuPatternByEditingOldAndSave("QuizMainChoice",buttonAction);
                    }
                    case "/hue" -> {
                        sendNextMenuPatternByEditingOldAndSave("HueMainChoice",buttonAction);
                    }
                    case "/hueCheckTokens" -> {
                        sendNextInfoMenuWithBackButton(hueAuthorizationService.checkAndRefreshToken(),buttonAction);
                    }
                    case "/hmql" -> {
//                    send response
                        sendNextInfoMenuWithBackButton("Zostało " + quizService.countRemainingQuizToSend() + " pytań.",buttonAction);
                    }
                    case "/testWeekend" -> {
                        sendNextInfoMenuWithBackButton(quizService.testQuizForWeekend(),buttonAction);
                    }
                    case "/messageManager" ->{
                        sendNextMenuPatternByEditingOldAndSave("MessageManager",buttonAction);
                    }
                    case "/mmSendMessage" ->{
                        sendNextMenuPatternByEditingOldAndSave("MessageSender",buttonAction);
                    }
                    case "/mmsmMe" ->{
                        sendNextInfoTextInputMenuAndSave("Wiadomość do Michała: ","Wpisz tutaj: ",buttonAction);
                    }
                    case "/mmsmYana" ->{
                        sendNextInfoTextInputMenuAndSave("Wiadomość do Яни: ","Wpisz tutaj: ",buttonAction);
                    }
                    case "/mmsmChomik" ->{
                        sendNextInfoTextInputMenuAndSave("Wiadomość do Chomika: ","Wpisz tutaj: ",buttonAction);
                    }
                    case "/mmsmId", "/snqId" -> {
                        sendNextInfoTextInputMenuAndSave("Wpisz ID użytkownika", "Wpisz tu", buttonAction);
                    }
                    case "/sendNextQuiz" ->{
                        sendNextMenuPatternByEditingOldAndSave("QuizSendChoice", buttonAction);
                    }
                    case "/snqMe" -> {
                        sendNextInfoMenuWithBackButton("Quiz "+quizService.sendNextQuizToId(msgMgr.getME()).getQuestion() + " wysłany do Michała.",buttonAction);
                    }
                    case "/snqYas" -> {
                        sendNextInfoMenuWithBackButton("Quiz "+quizService.sendNextQuizToId(msgMgr.getYASIA()).getQuestion() + " wysłany do Яни.",buttonAction);
                    }
                    case "/snqChom" -> {
                        sendNextInfoMenuWithBackButton("Quiz "+quizService.sendNextQuizToId(msgMgr.getCHOMIK()).getQuestion() + " wysłany do Chomika.",buttonAction);
                    }
                }
            }
        }
    }
    public void handleMenuInput(@NotNull WebhookUpdate update){
        menu = getMenuByCredentials(update.getMessage().getFrom().getId(), update.getMessage().getReply_to_message().getMessage_id());
        String menuReply = update.getMessage().getText();
//        if we don't get text in reply send warning and send menu with forceReply again
        if (menu == null || menu.getCurrentPattern()==null || menu.getData() == null) {
            msgMgr.sendAndLogErrorMsg("MS.hMR001", "oldMenu field is invalid");
            throw new RuntimeException("MS.hMR001");
        }else {
            if (menuReply == null) {
                log.debug("Reply to menu does not contain text. Sending menu again.");
                sendNextInfoTextInputMenuAndSave("Nie wpisałeś tekstu. Wpisz tekst: ", "Wpisz tu: ",newMenu.getInvoker());
            }else {
//                    This is name of the menu that requested input.
                String menu = this.menu.getCurrentPattern().getName();
                switch (menu){
                    case "TestMenu" -> {
                        sendNextInfoMenuWithBackButton("Wpisałeś: " + menuReply,menu);
                    }
                    case "MessageSender" -> {
                        String invoker = this.menu.getData();
                        switch (invoker){
                            case "/mmsmMe" -> {
                                sendNextInfoMenuWithBackButton("Wiadomość: '"+msgMgr.sendTelegramMessage(menuReply, msgMgr.getME()).getResult().getText()+"' wysłana do Michała.","MessageSender");
                            }
                            case "/mmsmYana" -> {
                                sendNextInfoMenuWithBackButton("Wiadomość: '"+msgMgr.sendTelegramMessage(menuReply, msgMgr.getYASIA()).getResult().getText()+"' wysłana do Яни.",menu);
                            }
                            case "/mmsmChomik" -> {
                                sendNextInfoMenuWithBackButton("Wiadomość: '"+msgMgr.sendTelegramMessage(menuReply, msgMgr.getCHOMIK()).getResult().getText()+"' wysłana do Chomika.",menu);
                            }
                            case "/mmsmId" -> {
                                UserEntity user = webhookService.getUserEntityByUserId(Long.decode(menuReply.replaceAll("[^\\d-]", "0")));//we only want numbers in our ID
                                if (user == null) {
                                    sendNextInfoMenuWithBackButton("Nie ma użytkownika z takim ID w bazie.",menu);
                                }else sendNextInfoTextInputMenuAndSave("Jaka ma być wiadomość?","Wpisz wiadomość",">mmsmIdInput");
                            }
                            case ">mmsmIdInput" ->{
                                sendNextInfoMenuWithBackButton("Wiadomość: '"+msgMgr.sendTelegramMessage(menuReply,user.getUserId()).getResult().getText()+"' wysłana do użytkownika "+user.getFirstName(),menu);
                            }
                        }
                    }
                    case "QuizSendChoice" -> {
                        String invoker = this.menu.getData();
                        if (invoker == null) {
                            msgMgr.sendAndLogErrorMsg("MS.hMI001","MenuData is null.");
                        } else if (invoker.equals("/snqId")) {
                            UserEntity user = webhookService.getUserEntityByUserId(Long.decode(menuReply.replaceAll("[^\\d-]", "0")));//we only want numbers in our ID
                            if (user == null) {
                                sendNextInfoMenuWithBackButton("Nie ma użytkownika z takim ID w bazie.",menu);
                            }else sendNextInfoMenuWithBackButton("Quiz "+quizService.sendNextQuizToId(user.getUserId()).getQuestion() + " wysłany do użytkownika "+user.getFirstName()+".",menu);
                        }
                    }
                }
            }
        }
    }

    /**
     * Search DB for Menu by chatId and messageId, sets oldMenu field to it and returns it
     * @param chatId
     * @param messageId
     * @return
     */
    public Menu getMenuByCredentials(@NonNull Long chatId, @NonNull Long messageId){
        menu = menuRepo.findByCredentials(chatId,messageId);
        if (menu == null) {
            msgMgr.sendAndLogErrorMsg("MS.gMBC001", "No Menu entries in db with such credentials. MessageId: "+messageId+"ChatId: "+chatId);
            return null;
        }else return menu;
    }
    /**
     * Deletes *menu* message and sends new menu using *menu* field data but changing reply markup to Force Reply.
     * "Please note, that it is currently only possible to edit messages without reply_markup or with inline keyboards."< That's why we have to delete old menu message.
     * @param inputFieldPlaceholder message that will appear in input text window.
     * @return saved Menu obj.
     */
    private Menu sendNextTextInputMenuAndSave(String inputFieldPlaceholder){
        if (menu == null) {
            msgMgr.sendAndLogErrorMsg("MS.sDIMM001","menu field is null.");
            throw new RuntimeException("MS.sDIMM001");
        }else{
            deleteMenuMessage();
            menu.setData(data);
            menu.setLastSentDate(Utils.getCurrentUnixTime());
            menu.setMessageId(msgMgr.sendTelegramMessage(createMenuMessageWithTextInput(inputFieldPlaceholder)).getResult().getMessage_id());
            return saveMenu();
        }
    }

    /**
     * Deletes oldMenu message and sends menu with info and Force Reply based on oldMenu field, and saves it. This does not change text in MenuPattern, so it's safe to use with Menu schemas that You don't want to edit.
     * @param info
     * @param inputFieldPlaceholder
     * @param invoker
     * @return
     */
    private Menu sendNextInfoTextInputMenuAndSave(String info, String inputFieldPlaceholder, @NonNull String invoker){
        if (menu == null || menu.getCurrentPattern() == null) {
            msgMgr.sendAndLogErrorMsg("MS.sNITIM001","OldMenu field is null.");
            throw new RuntimeException("MS.sNITIM001");
        }else {
            deleteMenuMessage();
            newMenu= menu;
            String originalPatternText = newMenu.getCurrentPattern().getText();
            newMenu.getCurrentPattern().setText(info);
            newMenu.setInvoker(invoker);
            newMenu.setLastSentDate(Utils.getCurrentUnixTime());
            newMenu.setMessageId(msgMgr.sendTelegramMessage(createMenuMessageWithTextInput(inputFieldPlaceholder)).getResult().getMessage_id());
            newMenu.getCurrentPattern().setText(originalPatternText);
            return saveMenu();
        }
    }

    /**
     * Deletes oldMenu message and sends new one (without using newMenu field) with info and back button and saves
     * @param info
     */
    private void sendNextInfoMenuWithBackButton(String info, String invoker){
        newPattern = menuRepo.getPatternByName("InfoWithBackButton");
        newPattern.setText(info);
        newPattern.setUpperPatternName(menu.getCurrentPattern().getName());
        sendNextMenuPatternByDeletingOldAndSave(newPattern,invoker);
    }

    /**
     * Deletes menu message based on menu field.
     */
    private void deleteMenuMessage(){
        if (menu == null || menu.getMessageId() ==null) {
            msgMgr.sendAndLogErrorMsg("MS.dOMM001","menu field is null.");
            throw new RuntimeException("MS.dOMM001");
        }else {
            msgMgr.deleteTelegramMessage(menu.getChatId(), menu.getMessageId());
        }
    }

    /**
     * Creates SendMessage object with menu based on newMenu field
     * @return
     */
    private SendMessage createMenuMessageWithButtons(){
        if (newMenu == null
                || newMenu.getCurrentPattern() == null
                || newMenu.getCurrentPattern().getText() == null
                || newMenu.getCurrentPattern().getCols() == 0
                || newMenu.getCurrentPattern().getRows() == 0
                || newMenu.getCurrentPattern().getButtons() == null) {
            msgMgr.sendAndLogErrorMsg("MS.cMM001","newMenu field object null or missing fields.");
            throw new RuntimeException("MS.cMM001");
        } else if (newMenu.getCurrentPattern().getRows()*newMenu.getCurrentPattern().getCols() != newMenu.getCurrentPattern().getButtons().size()) {
            msgMgr.sendAndLogErrorMsg("MS.cEMM002","Number of cols and rows assigned to menu is invalid or there is not enough menu buttons in DB " +
                    "\nText: "+newMenu.getCurrentPattern().getText()+
                    "\nCols: "+newMenu.getCurrentPattern().getCols()+
                    "\nRows: "+newMenu.getCurrentPattern().getRows()+
                    "\nSize of Buttons Array: "+newMenu.getCurrentPattern().getButtons().size());
            throw new RuntimeException("MS.cEMM002");
        }else {
            ArrayList<InlineKeyboardButton> inlineButtons = new ArrayList<>();
            ArrayList<InlineKeyboardButton> rowx = new ArrayList<>();
            ArrayList<ArrayList<InlineKeyboardButton>> inlineLayout = new ArrayList<>();
            for (InlineMenuButton menuButton :
                    newMenu.getCurrentPattern().getButtons()) {
                inlineButtons.add(menuButton.transformToInlineKeyboardButton());
            }
            for (int i = 0; i < newMenu.getCurrentPattern().getRows(); i++) {
                for (int j = 0; j < newMenu.getCurrentPattern().getCols(); j++) {
                    rowx.add(inlineButtons.get(0));
                    inlineButtons.remove(0);
                }
                inlineLayout.add(rowx);
                rowx=new ArrayList<>();
            }
            return new SendMessage()
                    .setChat_id(newMenu.getChatId())
                    .setText(newMenu.getCurrentPattern().getText())
                    .setReply_markup(new InlineKeyboardMarkup(inlineLayout));
        }


}

    /**
     * Creates SendMessage object with menu with FOrce Reply based on newMenu field data
     * @return
     */
    private SendMessage createMenuMessageWithTextInput(String inputFieldPlaceholder){
        if (newMenu == null
        || newMenu.getCurrentPattern() == null
        || newMenu.getCurrentPattern().getText() == null
        || newMenu.getChatId() == null) {
            msgMgr.sendAndLogErrorMsg("MS.cMMWTI001", "New Menu field is corrupted.");
            throw new RuntimeException("MS.cMMWTI001");
        }else {
            return new SendMessage().setText(newMenu.getCurrentPattern().getText())
                    .setChat_id(newMenu.getChatId())
                    .setReply_markup(new ForceReply()
                            .setForce_reply(true)
                            .setInput_field_placeholder(inputFieldPlaceholder));
        }
    }
    /**
     * Creates edited menu message object based on newMenu field
     * @return
     */
    private EditMessageText createEditedMenuMessage(){
        if (newMenu == null
                || newMenu.getCurrentPattern() == null
                || newMenu.getCurrentPattern().getText() == null
                || newMenu.getCurrentPattern().getCols() == 0
                || newMenu.getCurrentPattern().getRows() == 0
                || newMenu.getCurrentPattern().getButtons() == null) {
            msgMgr.sendAndLogErrorMsg("MS.cEMM001","newMenu field object null or missing fields.");
            throw new RuntimeException("MS.cEMM001");
        } else if (newMenu.getCurrentPattern().getRows()*newMenu.getCurrentPattern().getCols() != newMenu.getCurrentPattern().getButtons().size()) {
            msgMgr.sendAndLogErrorMsg("MS.cEMM002","Number of cols and rows assigned to menu is invalid or there is not enough menu buttons in DB " +
                    "\nText: "+newMenu.getCurrentPattern().getText()+
                    "\nCols: "+newMenu.getCurrentPattern().getCols()+
                    "\nRows: "+newMenu.getCurrentPattern().getRows()+
                    "\nSize of Buttons Array: "+newMenu.getCurrentPattern().getButtons().size());
            throw new RuntimeException("MS.cEMM002");
        }else {
            ArrayList<InlineKeyboardButton> inlineButtons = new ArrayList<>();
            ArrayList<InlineKeyboardButton> rowx = new ArrayList<>();
            ArrayList<ArrayList<InlineKeyboardButton>> inlineLayout = new ArrayList<>();
            for (InlineMenuButton menuButton :
                    newMenu.getCurrentPattern().getButtons()) {
                inlineButtons.add(menuButton.transformToInlineKeyboardButton());
            }
            for (int i = 0; i < newMenu.getCurrentPattern().getRows(); i++) {
                for (int j = 0; j < newMenu.getCurrentPattern().getCols(); j++) {
                    rowx.add(inlineButtons.get(0));
                    inlineButtons.remove(0);
                }
                inlineLayout.add(rowx);
                rowx = new ArrayList<>();
            }
            return EditMessageText.builder()
                    .chat_id(newMenu.getChatId())
                    .message_id(newMenu.getMessageId())
                    .text(newMenu.getCurrentPattern().getText())
                    .reply_markup(new InlineKeyboardMarkup(inlineLayout))
                    .build();
        }
    }
    private Menu sendNextMenuPatternByEditingOldAndSave(@NonNull String nextPatternName){
        if (menu == null || menu.getCurrentPattern() == null || this.invoker == null) {
            msgMgr.sendAndLogErrorMsg("MS.sNMPBEOAS001","OldMenu field is invalid or invoker is null.");
            throw new RuntimeException("MS.sNMPBEOAS001");
        }else {

            newMenu = menu;
            newMenu.setLastSentDate(Utils.getCurrentUnixTime());
            newMenu.setInvoker(invoker);
            newMenu.setLastPattern(menu.getCurrentPattern());
            newMenu.setCurrentPattern(menuRepo.getPatternByName(nextPatternName));
            newMenu.setMessageId(msgMgr.editTelegramMessage(createEditedMenuMessage()).getResult().getMessage_id());
            return saveMenu();
        }
    }
    private Menu sendNextMenuPatternByEditingOldAndSave(@NonNull MenuPattern nextPattern, @NonNull String invoker){
        if (menu == null || menu.getCurrentPattern() == null) {
            msgMgr.sendAndLogErrorMsg("MS.sNMPBEOAS002","OldMenu field is null.");
            throw new RuntimeException("MS.sNMPBEOAS002");
        }else {
            newMenu= menu;
            newMenu.setLastSentDate(Utils.getCurrentUnixTime());
            newMenu.setInvoker(invoker);
            newMenu.setLastPattern(menu.getCurrentPattern());
            newMenu.setCurrentPattern(nextPattern);
            newMenu.setMessageId(msgMgr.editTelegramMessage(createEditedMenuMessage()).getResult().getMessage_id());
            return saveMenu();
        }
    }
    private Menu sendNextMenuPatternByDeletingOldAndSave(@NonNull MenuPattern nextPattern, @NonNull String invoker){
        if (menu == null || menu.getCurrentPattern() == null) {
            msgMgr.sendAndLogErrorMsg("MS.sNMPBDOAS001","OldMenu field is null.");
            throw new RuntimeException("MS.sNMPBDOAS001");
        }else {
            deleteMenuMessage();
            newMenu= menu;
            newMenu.setLastSentDate(Utils.getCurrentUnixTime());
            newMenu.setInvoker(invoker);
            newMenu.setLastPattern(menu.getCurrentPattern());
            newMenu.setCurrentPattern(nextPattern);
            newMenu.setMessageId(msgMgr.sendTelegramMessage(createMenuMessageWithButtons()).getResult().getMessage_id());
            return saveMenu();
        }
    }
    private Menu sendNextMenuPatternByDeletingOldAndSave(@NonNull String nextPatternName){
        if (menu == null  || menu.getCurrentPattern() == null || invoker == null) {
            msgMgr.sendAndLogErrorMsg("MS.sNMPBDOAS002","menu field or invoker is null.");
            throw new RuntimeException("MS.sNMPBDOAS002");
        }else {
            deleteMenuMessage();
            newMenu= menu;
            newMenu.setLastSentDate(Utils.getCurrentUnixTime());
            newMenu.setInvoker(invoker);
            newMenu.setLastPattern(menu.getCurrentPattern());
            newMenu.setCurrentPattern(menuRepo.getPatternByName(nextPatternName));
            newMenu.setMessageId(msgMgr.sendTelegramMessage(createMenuMessageWithButtons()).getResult().getMessage_id());
            return saveMenu();
        }
    }

    /**
     * Saves menu field to DB
     * @return saved Menu obj.
     */
    private Menu saveMenu(){
        if (menu == null) {
            msgMgr.sendAndLogErrorMsg("MS.sM001","NewMenu field is null.");
            throw new RuntimeException("MS.sM001");
        }else {
            return menuRepo.save(menu);
        }
    }
}
