package net.ddns.encante.telegram.HR.menu;

import lombok.extern.slf4j.Slf4j;
import net.ddns.encante.telegram.HR.TelegramMethods.EditMessageText;
import net.ddns.encante.telegram.HR.TelegramMethods.MessageManager;
import net.ddns.encante.telegram.HR.TelegramMethods.SendMessage;
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

    public void createMainMenu(@NotNull Long chatId){
        if (menuRepo.findByChatId(chatId)!= null){
            log.debug("Menu istniejące w bazie.");
            Menu menu = menuRepo.findByChatId(chatId);
//            delete old menu message
            msgMgr.deleteTelegramMessage(msgMgr.getOriginalSender().getId(), menu.getMessageId());
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
        Menu currentMenu = menuRepo.findByCredentials(msgMgr.getOriginalSender().getId(),update.getCallback_query().getMessage().getMessage_id());
        if(currentMenu != null){
            String buttonAction = update.getCallback_query().getData();
            log.info("ButtonAction received: "+buttonAction);
            switch (buttonAction){
                case "/back" ->{
                    if (currentMenu.getLastPattern()!= null) {
                        if (currentMenu.getLastPattern() != currentMenu.getCurrentPattern()) {
                            saveMenu(sendNextMenuPatternByName(currentMenu, currentMenu.getLastPattern().getName()));
                        } else {
                            log.info("Can't go back in menu.");
                        }
                    }else {
                        log.warn("No last pattern in Menu object. Object invalid.");
                    }
                }
                case "/quiz" -> {
                    saveMenu(sendNextMenuPatternByName(currentMenu, "quizMainChoice"));
                }
                case "/hue" -> {
                    saveMenu(sendNextMenuPatternByName(currentMenu, "hueMainChoice"));
                }
                case "/hueCheckTokens" -> {
                    MenuPattern editedPattern = menuRepo.getPatternByName("hueTokenInfo");
                    editedPattern.setText(hueAuthorizationService.checkAndRefreshToken(hueAuthorizationService.getFirstAuthorization()));
                    saveMenu(sendNextMenuPatternByPattern(currentMenu,editedPattern));
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
    public Menu getMenuByCredentials(@NotNull Long chatId, @NotNull Long messageId){
        if (menuRepo.findByCredentials(chatId,messageId)!=null) return menuRepo.findByCredentials(chatId, messageId);
        else log.warn("No Menu entries in db with such credentials. MessageId: "+messageId+"ChatId: "+chatId+"  getMenuByCredentials");
        return null;
    }
//    CAUTION: NEED MENU OBJECT WITH CORRECT CURRENT AND LAST MENU PATTERN
    public EditMessageText createEditedMenuMessage(@NotNull Menu menu){
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
//    PRIVATE METHODS
//
//
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
    private Menu sendNextMenuPatternByName(Menu currentMenu, String nextPatternName){
        currentMenu.setLastSentDate(Utils.getCurrentUnixTime());
        currentMenu.setLastPattern(currentMenu.getCurrentPattern());
        currentMenu.setCurrentPattern(menuRepo.getPatternByName(nextPatternName));
        currentMenu.setMessageId(msgMgr.editTelegramTextMessage(createEditedMenuMessage(currentMenu)).getResult().getMessage_id());
        return currentMenu;
    }
    private Menu sendNextMenuPatternByPattern(Menu currentMenu, MenuPattern nextPattern){
        currentMenu.setLastSentDate(Utils.getCurrentUnixTime());
        currentMenu.setLastPattern(currentMenu.getCurrentPattern());
        currentMenu.setCurrentPattern(nextPattern);
        currentMenu.setMessageId(msgMgr.editTelegramTextMessage(createEditedMenuMessage(currentMenu)).getResult().getMessage_id());
        return currentMenu;
    }
    private Menu saveMenu(Menu menu){
        return menuRepo.save(menu);
    }
}
