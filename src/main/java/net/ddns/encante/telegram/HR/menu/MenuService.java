package net.ddns.encante.telegram.HR.menu;

import lombok.extern.slf4j.Slf4j;
import net.ddns.encante.telegram.HR.TelegramMethods.EditMessageText;
import net.ddns.encante.telegram.HR.TelegramMethods.MessageManager;
import net.ddns.encante.telegram.HR.TelegramMethods.SendMessage;
import net.ddns.encante.telegram.HR.TelegramObjects.ForceReply;
import net.ddns.encante.telegram.HR.TelegramObjects.InlineKeyboardButton;
import net.ddns.encante.telegram.HR.TelegramObjects.InlineKeyboardMarkup;
import net.ddns.encante.telegram.HR.TelegramObjects.WebhookUpdate;
import net.ddns.encante.telegram.HR.Utils;
import net.ddns.encante.telegram.HR.persistence.repository.QuizRepository;
import net.ddns.encante.telegram.HR.persistence.service.HueAuthorizationService;
import net.ddns.encante.telegram.HR.persistence.service.QuizService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;

@Slf4j
@Service("menuService")
public class MenuService {
    @Resource(name = "quizService")
    private QuizService quizService;
    @Autowired
    private MenuRepository menuRepo;
    @Autowired
    MessageManager msgMgr;
    @Autowired
    QuizRepository quizRepo;
    @Resource(name = "hueAuthorizationService")
    private HueAuthorizationService hueAuthorizationService;
    private Menu currentMenu;

    public void createMainMenu(@NotNull Long chatId){
        if (menuRepo.findByChatId(chatId)!= null){
            log.debug("Menu istniejące w bazie.");
            Menu menu = menuRepo.findByChatId(chatId);
//            delete old menu message
            msgMgr.deleteTelegramMessage(msgMgr.getOriginalSender().getId(), menu.getMessageId());
//            set up new menu
            menu.setChatId(chatId);
            menu.setLastSentDate(Utils.getCurrentUnixTime());
            menu.setCurrentPattern(menuRepo.getPatternByName("mainMenu"));
            menu.setMessageId(msgMgr.sendTelegramObjAsMessage(createMenuMessage(menu)).getResult().getMessage_id());
            saveMenu(menu);
        }else {
            log.debug("Nowe menu.");
            Menu menu = new Menu();
            menu.setChatId(chatId);
            menu.setLastSentDate(Utils.getCurrentUnixTime());
            menu.setCurrentPattern(menuRepo.getPatternByName("mainMenu"));
            menu.setMessageId(msgMgr.sendTelegramObjAsMessage(createMenuMessage(menu)).getResult().getMessage_id());
            saveMenu(menu);
        }
    }
    public void handleMenuCallback (@NotNull WebhookUpdate update){
        currentMenu = menuRepo.findByCredentials(msgMgr.getOriginalSender().getId(),update.getCallback_query().getMessage().getMessage_id());
        if(currentMenu != null){
            String buttonAction = update.getCallback_query().getData();
            log.info("ButtonAction received: "+buttonAction);
            switch (buttonAction){
                case "/testMenu" -> {
                    saveMenu(sendNextMenuPatternByEditingOld(currentMenu, "testMenu"));
                }
                case "/getInput" ->{
                    currentMenu = getMenuByCredentials(update.getCallback_query().getFrom().getId(), update.getCallback_query().getMessage().getMessage_id());
//        firstly we delete original message because it cant be edited to force reply
                    msgMgr.deleteTelegramMessage(update.getCallback_query().getFrom().getId(),update.getCallback_query().getMessage().getMessage_id());
//        now send message with force reply to chat and update menu object with new message id...
                    currentMenu.setMessageId(msgMgr.sendTelegramObjAsMessage(createDataInputMenuMessage(update.getCallback_query().getFrom().getId(), currentMenu)).getResult().getMessage_id());
//                    and save menu obj
                    saveMenu(currentMenu);
                }
                case "/back" ->{
                    if (currentMenu.getLastPattern()!= null) {
                        if (currentMenu.getLastPattern() != currentMenu.getCurrentPattern()) {
                            saveMenu(sendNextMenuPatternByEditingOld(currentMenu, currentMenu.getLastPattern().getName()));
                        } else {
                            log.info("Can't go back in menu.");
                        }
                    }else {
                        log.warn("No last pattern in Menu object. Object invalid.");
                    }
                }
                case "/quiz" -> {
                    saveMenu(sendNextMenuPatternByEditingOld(currentMenu, "quizMainChoice"));
                }
                case "/hue" -> {
                    saveMenu(sendNextMenuPatternByEditingOld(currentMenu, "hueMainChoice"));
                }
                case "/hueCheckTokens" -> {
                    MenuPattern currentPattern = menuRepo.getPatternByName("infoWithBackButton");
                    currentPattern.setText(hueAuthorizationService.checkAndRefreshToken(hueAuthorizationService.getFirstAuthorization()));
                    saveMenu(sendNextMenuPatternByEditingOld(currentMenu,currentPattern));
                }
                case "/hmql" -> {
//                    firstly delete menu message
                    msgMgr.deleteTelegramMessage(msgMgr.getOriginalSender().getId(), currentMenu.getMessageId());
//                    send response
                    msgMgr.sendBackTelegramTextMessage("Zostało " + quizRepo.findAllQuizEntitiesToSend().size() + " pytań.");
                }
                case "/testWeekend" -> {
                    msgMgr.deleteTelegramMessage(msgMgr.getOriginalSender().getId(), currentMenu.getMessageId());
                    quizService.testQuizForWeekend();
                }
            }
        }
    }
    public void handleMenuReply (@NotNull WebhookUpdate update){
        currentMenu = getMenuByCredentials(update.getMessage().getFrom().getId(), update.getMessage().getReply_to_message().getMessage_id());
        String menuReply = update.getMessage().getText();
//        if we don't get text in reply send warning and send menu with forceReply again
        if (currentMenu!= null){
            if (currentMenu.getCurrentPattern()!=null){
                if (menuReply == null) {
                    log.warn("Reply to menu does not contain text. Called by: MenuService.handleMenuReply");
            //            delete old menu message
                    msgMgr.deleteTelegramMessage(currentMenu.getChatId(), currentMenu.getMessageId());
                    saveMenu(sendNextMenuPatternByDeletingOld(currentMenu, currentMenu.getCurrentPattern()));
                }
                String menu = currentMenu.getCurrentPattern().getName();
                switch (menu){
                    case "testMenu" -> {
//                        Please note, that it is currently only possible to edit messages without reply_markup or with inline keyboards.< thats why we have to delete old menu message
                        MenuPattern mp = menuRepo.getPatternByName("infoWithBackButton");
                        mp.setText("Wpisałeś: "+ menuReply);
                        saveMenu(sendNextMenuPatternByDeletingOld(currentMenu, mp));
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
    public Menu getMenuByCredentials(@NotNull Long chatId, @NotNull Long messageId){
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
    private SendMessage createDataInputMenuMessage (@NotNull Long chatId, @NotNull Menu menu){
        return new SendMessage().setText(menu.getCurrentPattern().getText())
                .setChat_id(chatId)
                .setReply_markup(new ForceReply()
                        .setForce_reply(true)
                        .setInput_field_placeholder("Wpisz tu:"));
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
    private Menu sendNextMenuPatternByEditingOld(Menu menu, String nextPatternName){
        menu.setLastSentDate(Utils.getCurrentUnixTime());
        menu.setLastPattern(menu.getCurrentPattern());
        menu.setCurrentPattern(menuRepo.getPatternByName(nextPatternName));
        menu.setMessageId(msgMgr.editTelegramTextMessage(createEditedMenuMessage(menu)).getResult().getMessage_id());
        return saveMenu(menu);
    }
    private Menu sendNextMenuPatternByEditingOld(Menu menu, MenuPattern nextPattern){
        menu.setLastSentDate(Utils.getCurrentUnixTime());
        menu.setLastPattern(menu.getCurrentPattern());
        menu.setCurrentPattern(nextPattern);
        menu.setMessageId(msgMgr.editTelegramTextMessage(createEditedMenuMessage(menu)).getResult().getMessage_id());
        return saveMenu(menu);
    }
    private Menu sendNextMenuPatternByDeletingOld (Menu menu, MenuPattern nextPattern){
        menu.setLastSentDate(Utils.getCurrentUnixTime());
        menu.setLastPattern(menu.getCurrentPattern());
        menu.setCurrentPattern(nextPattern);
        msgMgr.deleteTelegramMessage(menu.getChatId(), menu.getMessageId());
        menu.setMessageId(msgMgr.sendTelegramObjAsMessage(createMenuMessage(menu)).getResult().getMessage_id());
        return saveMenu(menu);
    }
    private Menu sendNextMenuPatternByDeletingOld (Menu menu, String nextPatternName){
        menu.setLastSentDate(Utils.getCurrentUnixTime());
        menu.setLastPattern(menu.getCurrentPattern());
        menu.setCurrentPattern(menuRepo.getPatternByName(nextPatternName));
        msgMgr.deleteTelegramMessage(menu.getChatId(), menu.getMessageId());
        menu.setMessageId(msgMgr.sendTelegramObjAsMessage(createMenuMessage(menu)).getResult().getMessage_id());
        return saveMenu(menu);
    }
    private Menu saveMenu(Menu menu){
        return menuRepo.save(menu);
    }
}
