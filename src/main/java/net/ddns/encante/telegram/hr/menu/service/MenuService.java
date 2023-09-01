package net.ddns.encante.telegram.hr.menu.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.ddns.encante.telegram.hr.Utils;
import net.ddns.encante.telegram.hr.hue.service.HueAuthorizationService;
import net.ddns.encante.telegram.hr.menu.entity.InlineMenuButton;
import net.ddns.encante.telegram.hr.menu.entity.Menu;
import net.ddns.encante.telegram.hr.menu.entity.MenuPattern;
import net.ddns.encante.telegram.hr.menu.repository.MenuRepository;
import net.ddns.encante.telegram.hr.quiz.service.QuizService;
import net.ddns.encante.telegram.hr.telegram.api.methods.EditMessageText;
import net.ddns.encante.telegram.hr.telegram.api.methods.SendMessage;
import net.ddns.encante.telegram.hr.telegram.api.objects.ForceReply;
import net.ddns.encante.telegram.hr.telegram.api.objects.InlineKeyboardButton;
import net.ddns.encante.telegram.hr.telegram.api.objects.InlineKeyboardMarkup;
import net.ddns.encante.telegram.hr.telegram.api.objects.WebhookUpdate;
import net.ddns.encante.telegram.hr.telegram.service.MessageManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service("menuService")
public class MenuService {
    private QuizService quizService;
    private MenuRepository menuRepo;
    private MessageManager msgMgr;
    private HueAuthorizationService hueAuthorizationService;
    private Menu newMenu;
    private Menu oldMenu;
    private MenuPattern newPattern;
    private MenuPattern oldPattern;

    public MenuService(QuizService quizService, MenuRepository menuRepository, MessageManager msgMgr, HueAuthorizationService hueAuthorizationService){
        this.quizService = quizService;
        this.menuRepo = menuRepository;
        this.msgMgr = msgMgr;
        this.hueAuthorizationService = hueAuthorizationService;
    }

    public void createMainMenu(@NotNull Long chatId){
        if (menuRepo.findByChatId(chatId)== null){
            log.debug("New menu.");
            newMenu = new Menu(chatId,Utils.getCurrentUnixTime(),menuRepo.getPatternByName("MainMenu"),"new");
            newMenu.setMessageId(msgMgr.sendTelegramObjAsMessage(createMenuMessage()).getResult().getMessage_id());
            saveMenu();
        }else {
            log.debug("Menu existing in DB");
            oldMenu = menuRepo.findByChatId(chatId);
            if (oldMenu.getMessageId() == null) {
                msgMgr.sendAndLogErrorMsg("MS.cMM001","Can't delete old menu message. Menu object doesn't have a messageId assigned");
            }else{
                //            delete old menu message
                deleteOldMenuMessage();
            }
//            set up new menu
            newMenu = new Menu(chatId,Utils.getCurrentUnixTime(),menuRepo.getPatternByName("MainMenu"),"created");
            newMenu.setMessageId(msgMgr.sendTelegramObjAsMessage(createMenuMessage()).getResult().getMessage_id());
            saveMenu();
        }
    }
    public void handleMenuButton(@NotNull WebhookUpdate update){
        oldMenu = menuRepo.findByCredentials(msgMgr.getOriginalSender().getId(),update.getCallback_query().getMessage().getMessage_id());
        if(oldMenu == null){
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
                        sendTextInputMenuMessageAndSave("Wpisz tu:",buttonAction);
                    }
                    case "/back" ->{
                        if (oldMenu == null || oldMenu.getCurrentPattern() == null || oldMenu.getCurrentPattern().getUpperPatternName() == null) {
                            msgMgr.sendAndLogErrorMsg("MS.hMC003", "No upper pattern in MenuPattern: "+ newMenu.getCurrentPattern().getName()+". Object invalid");
                        }else {
                            newMenu=oldMenu;
                            newMenu.setLastSentDate(Utils.getCurrentUnixTime());
                            newMenu.setLastPattern(oldMenu.getCurrentPattern());
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
                        sendInfoMenuWithBackButton(hueAuthorizationService.checkAndRefreshToken(),buttonAction);
                    }
                    case "/hmql" -> {
//                    send response
                        sendInfoMenuWithBackButton("Zostało " + quizService.countRemainingQuizToSend() + " pytań.",buttonAction);
                    }
                    case "/testWeekend" -> {
                        sendInfoMenuWithBackButton(quizService.testQuizForWeekend(),buttonAction);
                    }
                    case "/messageManager" ->{
                        sendNextMenuPatternByEditingOldAndSave("MessageManager",buttonAction);
                    }
                    case "/mmSendMessage" ->{
                        sendNextMenuPatternByEditingOldAndSave("MessageSender",buttonAction);
                    }
                    case "/mmsmMe" ->{
                        sendTextInputMenuMessageAndSave("Wiadomość do Michała",buttonAction);
                    }
                    case "/mmsmYana" ->{
                        sendTextInputMenuMessageAndSave("Wiadomość do Yany",buttonAction);
                    }
                }
            }
        }
    }
    public void handleMenuInput(@NotNull WebhookUpdate update){
        oldMenu = getMenuByCredentials(update.getMessage().getFrom().getId(), update.getMessage().getReply_to_message().getMessage_id());
        String menuReply = update.getMessage().getText();
//        if we don't get text in reply send warning and send menu with forceReply again
        if (oldMenu == null || oldMenu.getCurrentPattern()==null) {
            msgMgr.sendAndLogErrorMsg("MS.hMR001", "oldMenu field is null, or oldMenu.currentPattern is null.");
            throw new RuntimeException("MS.hMR001");
        }else {
            if (menuReply == null) {
                log.debug("Reply to menu does not contain text. Sending menu again.");
                newMenu=oldMenu;
                sendTextInputMenuMessageAndSave("Wpisz tu: ",newMenu.getInvoker());
            }else {
//                    This is name of the menu that requested input.
                String menu = oldMenu.getCurrentPattern().getName();
                switch (menu){
                    case "TestMenu" -> {
                        sendInfoMenuWithBackButton("Wpisałeś: " + menuReply,menu);
                    }
                    case "MessageSender" -> {
                        String whoTo = oldMenu.getInvoker();
                        switch (whoTo){
                            case "/mmsmMe" -> {
                                msgMgr.sendTelegramTextMessage(menuReply, msgMgr.getME());
                                sendInfoMenuWithBackButton("Wiadomość: "+menuReply+" wysłana do Michała.","MessageSender");
                            }
                            case "/mmsmYana" -> {
                                msgMgr.sendTelegramTextMessage(menuReply, msgMgr.getYASIA());
                                sendInfoMenuWithBackButton("Wiadomość: "+menuReply+" wysłana do Яни.","MessageSender");
                            }
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
        oldMenu = menuRepo.findByCredentials(chatId,messageId);
        if (oldMenu == null) {
            msgMgr.sendAndLogErrorMsg("MS.gMBC001", "No Menu entries in db with such credentials. MessageId: "+messageId+"ChatId: "+chatId);
            return null;
        }else return oldMenu;
    }
    /**
     * Deletes oldMenu message and sends new menu using oldMenu field data but inserting input into it.
     * "Please note, that it is currently only possible to edit messages without reply_markup or with inline keyboards."< That's why we have to delete old menu message
     * @param inputFieldPlaceholder
     */
    private void sendTextInputMenuMessageAndSave(String inputFieldPlaceholder,@NonNull String invoker){
        if (oldMenu == null) {
            msgMgr.sendAndLogErrorMsg("MS.sDIMM001","OldMenu field is null.");
            throw new RuntimeException("MS.sDIMM001");
        }else{
            newMenu=oldMenu;
            deleteOldMenuMessage();
            SendMessage msg = new SendMessage().setText(newMenu.getCurrentPattern().getText())
                    .setChat_id(newMenu.getChatId())
                    .setReply_markup(new ForceReply()
                            .setForce_reply(true)
                            .setInput_field_placeholder(inputFieldPlaceholder));
            newMenu.setLastSentDate(Utils.getCurrentUnixTime());
            newMenu.setInvoker(invoker);
            newMenu.setMessageId(msgMgr.sendTelegramObjAsMessage(msg).getResult().getMessage_id());
            saveMenu();
        }
    }

    /**
     * Deletes oldMenu message and sends new one (without using newMenu field) with info and back button and saves
     * @param info
     */
    private void sendInfoMenuWithBackButton(String info, String invoker){
        newPattern = menuRepo.getPatternByName("InfoWithBackButton");
        newPattern.setText(info);
        newPattern.setUpperPatternName(oldMenu.getCurrentPattern().getName());
        sendNextMenuPatternByDeletingOldAndSave(newPattern,invoker);
    }
    private void deleteOldMenuMessage(){
        if (oldMenu == null || oldMenu.getMessageId() ==null) {
            msgMgr.sendAndLogErrorMsg("MS.dOMM001","OldMenu field is null.");
            throw new RuntimeException("MS.dOMM001");
        }else {
            msgMgr.deleteTelegramMessage(oldMenu.getChatId(), oldMenu.getMessageId());
        }
    }

    /**
     * Creates SendMessage object with menu based on newMenu field
     * @return
     */
    private SendMessage createMenuMessage(){
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
    private Menu sendNextMenuPatternByEditingOldAndSave(@NonNull String nextPatternName, @NonNull String invoker){
        if (oldMenu == null || oldMenu.getCurrentPattern() == null) {
            msgMgr.sendAndLogErrorMsg("MS.sNMPBEOAS001","OldMenu field is null.");
            throw new RuntimeException("MS.sNMPBEOAS001");
        }else {
            newMenu = oldMenu;
            newMenu.setLastSentDate(Utils.getCurrentUnixTime());
            newMenu.setInvoker(invoker);
            newMenu.setLastPattern(oldMenu.getCurrentPattern());
            newMenu.setCurrentPattern(menuRepo.getPatternByName(nextPatternName));
            newMenu.setMessageId(msgMgr.editTelegramTextMessage(createEditedMenuMessage()).getResult().getMessage_id());
            return saveMenu();
        }
    }
    private Menu sendNextMenuPatternByEditingOldAndSave(@NonNull MenuPattern nextPattern, @NonNull String invoker){
        if (oldMenu == null || oldMenu.getCurrentPattern() == null) {
            msgMgr.sendAndLogErrorMsg("MS.sNMPBEOAS002","OldMenu field is null.");
            throw new RuntimeException("MS.sNMPBEOAS002");
        }else {
            newMenu=oldMenu;
            newMenu.setLastSentDate(Utils.getCurrentUnixTime());
            newMenu.setInvoker(invoker);
            newMenu.setLastPattern(oldMenu.getCurrentPattern());
            newMenu.setCurrentPattern(nextPattern);
            newMenu.setMessageId(msgMgr.editTelegramTextMessage(createEditedMenuMessage()).getResult().getMessage_id());
            return saveMenu();
        }
    }
    private Menu sendNextMenuPatternByDeletingOldAndSave(@NonNull MenuPattern nextPattern, @NonNull String invoker){
        if (oldMenu == null || oldMenu.getCurrentPattern() == null) {
            msgMgr.sendAndLogErrorMsg("MS.sNMPBDOAS001","OldMenu field is null.");
            throw new RuntimeException("MS.sNMPBDOAS001");
        }else {
            deleteOldMenuMessage();
            newMenu=oldMenu;
            newMenu.setLastSentDate(Utils.getCurrentUnixTime());
            newMenu.setInvoker(invoker);
            newMenu.setLastPattern(oldMenu.getCurrentPattern());
            newMenu.setCurrentPattern(nextPattern);
            newMenu.setMessageId(msgMgr.sendTelegramObjAsMessage(createMenuMessage()).getResult().getMessage_id());
            return saveMenu();
        }
    }
    private Menu sendNextMenuPatternByDeletingOldAndSave(@NonNull String nextPatternName, @NonNull String invoker){
        if (oldMenu == null  || oldMenu.getCurrentPattern() == null) {
            msgMgr.sendAndLogErrorMsg("MS.sNMPBDOAS002","OldMenu field is null.");
            throw new RuntimeException("MS.sNMPBDOAS002");
        }else {
            deleteOldMenuMessage();
            newMenu=oldMenu;
            newMenu.setLastSentDate(Utils.getCurrentUnixTime());
            newMenu.setInvoker(invoker);
            newMenu.setLastPattern(oldMenu.getCurrentPattern());
            newMenu.setCurrentPattern(menuRepo.getPatternByName(nextPatternName));
            newMenu.setMessageId(msgMgr.sendTelegramObjAsMessage(createMenuMessage()).getResult().getMessage_id());
            return saveMenu();
        }
    }
    private Menu saveMenu(){
        if (newMenu == null) {
            msgMgr.sendAndLogErrorMsg("MS.sM001","NewMenu field is null.");
            throw new RuntimeException("MS.sM001");
        }else {
            return menuRepo.save(newMenu);
        }
    }
}
