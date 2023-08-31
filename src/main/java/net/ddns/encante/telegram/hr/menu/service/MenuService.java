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
            newMenu = new Menu(chatId,Utils.getCurrentUnixTime(),menuRepo.getPatternByName("MainMenu"));
            newMenu.setMessageId(msgMgr.sendTelegramObjAsMessage(createMenuMessage(newMenu)).getResult().getMessage_id());
            saveMenu();
        }else {
            log.debug("Menu existing in DB");
            oldMenu = menuRepo.findByChatId(chatId);
            if (oldMenu.getMessageId() == null) {
                msgMgr.sendAndLogErrorMsg("MS.cMM001","Can't delete old menu message. Menu object doesn't have a messageId assigned");
            }else{
                //            delete old menu message
                msgMgr.deleteTelegramMessage(msgMgr.getOriginalSender().getId(), oldMenu.getMessageId());
            }
//            set up new menu
            newMenu = new Menu(chatId,Utils.getCurrentUnixTime(),menuRepo.getPatternByName("MainMenu"));
            newMenu.setMessageId(msgMgr.sendTelegramObjAsMessage(createMenuMessage(newMenu)).getResult().getMessage_id());
            saveMenu();
        }
    }
    public void handleMenuCallback (@NotNull WebhookUpdate update){
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
                        sendNextMenuPatternByEditingOldAndSave("TestMenu");
                    }
                    case "/getInput" ->{
                        newMenu=oldMenu;
                        sendDataInputMenuMessageAndSave("Wpisz tu:");
                    }
                    case "/back" ->{
                        if (oldMenu.getCurrentPattern().getUpperPatternName() == null) {
                            msgMgr.sendAndLogErrorMsg("MS.hMC003", "No upper pattern in MenuPattern: "+ newMenu.getCurrentPattern().getName()+". Object invalid");
                        }else {
                            newMenu=oldMenu;
                            newMenu.setLastPattern(oldMenu.getCurrentPattern());
                            sendNextMenuPatternByEditingOldAndSave(newMenu.getLastPattern().getUpperPatternName());
                        }
                    }
                    case "/quiz" -> {
                        sendNextMenuPatternByEditingOldAndSave("QuizMainChoice");
                    }
                    case "/hue" -> {
                        sendNextMenuPatternByEditingOldAndSave("HueMainChoice");
                    }
                    case "/hueCheckTokens" -> {
                        sendInfoMenuWithBackButton(hueAuthorizationService.checkAndRefreshToken());
                    }
                    case "/hmql" -> {
//                    send response
                        sendInfoMenuWithBackButton("Zostało " + quizService.countRemainingQuizToSend() + " pytań.");
                    }
                    case "/testWeekend" -> {
                        sendInfoMenuWithBackButton(quizService.testQuizForWeekend());
                    }
                }
            }
        }
    }
    public void handleMenuReply (@NotNull WebhookUpdate update){
        oldMenu = getMenuByCredentials(update.getMessage().getFrom().getId(), update.getMessage().getReply_to_message().getMessage_id());
        String menuReply = update.getMessage().getText();
//        if we don't get text in reply send warning and send menu with forceReply again
        if (oldMenu == null || oldMenu.getCurrentPattern()==null) {
            msgMgr.sendAndLogErrorMsg("MS.hMR001", "oldMenu field is null, or oldMenu.currentPattern is null.");
            throw new RuntimeException("MS.hMR001");
        }else {
            // TODO: 31.08.2023 Te trzy ify na dole zacząłem przepisywać. dokończyć i skasować
        }
        if (newMenu != null){
            if (newMenu.getCurrentPattern()!=null){
                if (menuReply == null) {
                    log.warn("Reply to menu does not contain text. Called by: MenuService.handleMenuReply");
            //            delete old menu message
                    msgMgr.deleteTelegramMessage(newMenu.getChatId(), newMenu.getMessageId());

//                    send again current menu pattern
                    sendNextMenuPatternByDeletingOldAndSave(newMenu, newMenu.getCurrentPattern());
                }else {
                    String menu = this.newMenu.getCurrentPattern().getName();
                    switch (menu) {
                        case "TestMenu" -> {
//                        Please note, that it is currently only possible to edit messages without reply_markup or with inline keyboards.< thats why we have to delete old menu message
                            MenuPattern mp = menuRepo.getPatternByName("infoWithBackButton");
                            mp.setText("Wpisałeś: " + menuReply);
                            mp.setUpperPatternName("TestMenu");
                            sendNextMenuPatternByDeletingOldAndSave(this.newMenu, mp);
                        }
                    }
                }
            }else {
                log.warn("Corruped menu object in db. Called by MenuService.handleMenuReply");
                throw new RuntimeException("Corruped menu object in db. Called by MenuService.handleMenuReply");
            }
        } else {
            log.warn("No menu with such credentials in db. ChatId: "+update.getMessage().getFrom().getId()+" MessageId: "+update.getMessage().getReply_to_message().getMessage_id()+" Called by MenuService.handleMenuReply");
            throw new RuntimeException("No menu with such credentials in db. Called by MenuService.handleMenuReply");
        }
    }
    public Menu getMenuByCredentials(@NonNull Long chatId, @NonNull Long messageId){
        if (menuRepo.findByCredentials(chatId,messageId)!=null) return menuRepo.findByCredentials(chatId, messageId);
        else log.warn("No Menu entries in db with such credentials. MessageId: "+messageId+"ChatId: "+chatId+"  getMenuByCredentials");
        return null;
    }

//    CAUTION: NEED MENU OBJECT WITH CORRECT CURRENT AND LAST MENU PATTERN
    private EditMessageText createEditedMenuMessage(@NotNull Menu menu){
    if(menu.getCurrentPattern().getText()!=null
            && menu.getCurrentPattern().getRows()*menu.getCurrentPattern().getCols()==menu.getCurrentPattern().getButtons().size()) {
        ArrayList<InlineKeyboardButton> inlineButtons = new ArrayList<>();
        ArrayList<InlineKeyboardButton> rowx = new ArrayList<>();
        ArrayList<ArrayList<InlineKeyboardButton>> inlineLayout = new ArrayList<>();
        for (InlineMenuButton menuButton :
                menu.getCurrentPattern().getButtons()) {
            inlineButtons.add(menuButton.transformToInlineKeyboardButton());
        }
        for (int i = 0; i < menu.getCurrentPattern().getRows(); i++) {
            for (int j = 0; j < menu.getCurrentPattern().getCols(); j++) {
                rowx.add(inlineButtons.get(0));
                inlineButtons.remove(0);
            }
            inlineLayout.add(rowx);
            rowx = new ArrayList<>();
        }
        return EditMessageText.builder()
                .chat_id(menu.getChatId())
                .message_id(menu.getMessageId())
                .text(menu.getCurrentPattern().getText())
                .reply_markup(new InlineKeyboardMarkup(inlineLayout))
                .build();
    }else {
        log.warn("ERROR. Pattern is bad. createMenuMessage");
        log.debug("Text: "+menu.getCurrentPattern().getText());
        log.debug("Cols: "+menu.getCurrentPattern().getCols());
        log.debug("Rows: "+menu.getCurrentPattern().getRows());
        log.debug("Size of Buttons Array: "+menu.getCurrentPattern().getButtons().size());
        throw new RuntimeException("Pattern is bad. Edit menu.message");
    }
}
    private SendMessage createDataInputMenuMessage (String inputFieldPlaceholder){
        if (newMenu == null) {
            msgMgr.sendAndLogErrorMsg("MS.cDIMM001","NewMenu field is null.");
            throw new RuntimeException("MS.cDIMM001");
        }else {
            return new SendMessage().setText(newMenu.getCurrentPattern().getText())
                    .setChat_id(newMenu.getChatId())
                    .setReply_markup(new ForceReply()
                            .setForce_reply(true)
                            .setInput_field_placeholder(inputFieldPlaceholder));
        }
    }
    private void sendDataInputMenuMessageAndSave(String inputFieldPlaceholder){
        if (newMenu == null) {
            msgMgr.sendAndLogErrorMsg("MS.sDIMM001","NewMenu field is null.");
            throw new RuntimeException("MS.sDIMM001");
        }else{
            deleteOldMenuMessage();
            SendMessage msg = new SendMessage().setText(newMenu.getCurrentPattern().getText())
                    .setChat_id(newMenu.getChatId())
                    .setReply_markup(new ForceReply()
                            .setForce_reply(true)
                            .setInput_field_placeholder(inputFieldPlaceholder))
            newMenu.setMessageId(msgMgr.sendTelegramObjAsMessage(msg).getResult().getMessage_id());
            saveMenu();
        }
    }
    private void sendInfoMenuWithBackButton(String info){
        if (oldMenu == null) {
            msgMgr.sendAndLogErrorMsg("MS.sIMWBB","OldMenu field is null.");
            throw new RuntimeException("MS.sIMWBB");
        }else {
            newPattern = menuRepo.getPatternByName("InfoWithBackButton");
            newPattern.setText(info);
            newPattern.setUpperPatternName(oldMenu.getCurrentPattern().getName());
            sendNextMenuPatternByEditingOldAndSave(newPattern);
        }
    }
    private void deleteOldMenuMessage(){
        if (oldMenu == null || oldMenu.getMessageId() ==null) {
            msgMgr.sendAndLogErrorMsg("MS.dOMM","OldMenu field is null.");
            throw new RuntimeException("MS.dOMM");
        }else {
            msgMgr.deleteTelegramMessage(oldMenu.getChatId(), oldMenu.getMessageId());
        }
    }
    private SendMessage createMenuMessage(@NotNull Menu menu){
//        null check
    if(menu.getCurrentPattern().getText()!=null
            && menu.getCurrentPattern().getRows()*menu.getCurrentPattern().getCols()==menu.getCurrentPattern().getButtons().size()){
        ArrayList<InlineKeyboardButton> inlineButtons = new ArrayList<>();
        ArrayList<InlineKeyboardButton> rowx = new ArrayList<>();
        ArrayList<ArrayList<InlineKeyboardButton>> inlineLayout = new ArrayList<>();
        for (InlineMenuButton menuButton :
                menu.getCurrentPattern().getButtons()) {
            inlineButtons.add(menuButton.transformToInlineKeyboardButton());
        }
        for (int i = 0; i < menu.getCurrentPattern().getRows(); i++) {
            for (int j = 0; j < menu.getCurrentPattern().getCols(); j++) {
                rowx.add(inlineButtons.get(0));
                inlineButtons.remove(0);
            }
            inlineLayout.add(rowx);
            rowx=new ArrayList<>();
        }
        return new SendMessage()
                .setChat_id(menu.getChatId())
                .setText(menu.getCurrentPattern().getText())
                .setReply_markup(new InlineKeyboardMarkup(inlineLayout));
    }else {
        log.warn("ERROR. Pattern is bad. createMenuMessage");
        log.debug("Text: "+menu.getCurrentPattern().getText());
        log.debug("Cols: "+menu.getCurrentPattern().getCols());
        log.debug("Rows: "+menu.getCurrentPattern().getRows());
        log.debug("Size of Buttons Array: "+menu.getCurrentPattern().getButtons().size());
        throw new RuntimeException("Pattern is bad.");
    }
}
    private Menu sendNextMenuPatternByEditingOldAndSave(@NonNull String nextPatternName){
        if (oldMenu == null) {
            msgMgr.sendAndLogErrorMsg("MS.sNMPBEOAS001","OldMenu field is null.");
            throw new RuntimeException("MS.sNMPBEOAS001");
        }else {
            newMenu = oldMenu;
            newMenu.setLastSentDate(Utils.getCurrentUnixTime());
            newMenu.setLastPattern(oldMenu.getCurrentPattern());
            newMenu.setCurrentPattern(menuRepo.getPatternByName(nextPatternName));
            newMenu.setMessageId(msgMgr.editTelegramTextMessage(createEditedMenuMessage(newMenu)).getResult().getMessage_id());
            return saveMenu();
        }
    }
    private Menu sendNextMenuPatternByEditingOldAndSave(@NonNull MenuPattern nextPattern){
        if (oldMenu == null) {
            msgMgr.sendAndLogErrorMsg("MS.sNMPBEOAS002","OldMenu field is null.");
            throw new RuntimeException("MS.sNMPBEOAS002")
        }else {
            newMenu=oldMenu;
            newMenu.setLastSentDate(Utils.getCurrentUnixTime());
            newMenu.setLastPattern(oldMenu.getCurrentPattern());
            newMenu.setCurrentPattern(nextPattern);
            newMenu.setMessageId(msgMgr.editTelegramTextMessage(createEditedMenuMessage(newMenu)).getResult().getMessage_id());
            return saveMenu();
        }
    }
    private Menu sendNextMenuPatternByDeletingOldAndSave(@NonNull MenuPattern nextPattern){
        if (oldMenu == null) {
            msgMgr.sendAndLogErrorMsg("MS.sNMPBDOAS001","OldMenu field is null.");
            throw new RuntimeException("MS.sNMPBDOAS001");
        }else {
            deleteOldMenuMessage();
            newMenu=oldMenu;
            newMenu.setLastSentDate(Utils.getCurrentUnixTime());
            newMenu.setLastPattern(oldMenu.getCurrentPattern());
            newMenu.setCurrentPattern(nextPattern);
            newMenu.setMessageId(msgMgr.sendTelegramObjAsMessage(createMenuMessage(newMenu)).getResult().getMessage_id());
            return saveMenu();
        }
    }
    private Menu sendNextMenuPatternByDeletingOldAndSave(@NonNull String nextPatternName){
        if (oldMenu == null) {
            msgMgr.sendAndLogErrorMsg("MS.sNMPBDOAS002","OldMenu field is null.");
            throw new RuntimeException("MS.sNMPBDOAS002");
        }else {
            deleteOldMenuMessage();
            newMenu=oldMenu;
            newMenu.setLastSentDate(Utils.getCurrentUnixTime());
            newMenu.setLastPattern(oldMenu.getCurrentPattern());
            newMenu.setCurrentPattern(menuRepo.getPatternByName(nextPatternName));
            newMenu.setMessageId(msgMgr.sendTelegramObjAsMessage(createMenuMessage(newMenu)).getResult().getMessage_id());
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
