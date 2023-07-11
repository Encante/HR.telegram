package net.ddns.encante.telegram.HR.menu;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.ddns.encante.telegram.HR.TelegramObjects.CallbackGame;
import net.ddns.encante.telegram.HR.TelegramObjects.InlineKeyboardButton;
import net.ddns.encante.telegram.HR.TelegramObjects.LoginUrl;
import net.ddns.encante.telegram.HR.TelegramObjects.WebAppInfo;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "inline_menu_button")
public class InlineMenuButton {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    Long keyId;

    String text;
    String url;
    String callback_data;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "web_app_key", referencedColumnName = "key_id")
    WebAppInfo web_app;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "login_url_key", referencedColumnName = "key_id")
    LoginUrl login_url;
    String switch_inline_query;
    String switch_inline_query_current_chat;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "callback_game_key", referencedColumnName = "key_id")
    CallbackGame callback_game;
    boolean pay;
    @ManyToOne
    @JoinColumn(name = "pattern_id", nullable=false)
    private MenuPattern pattern;

    public InlineMenuButton(String keyText) {
        this.text = keyText;
        this.callback_data=text;
    }
    public InlineMenuButton(String keyText, String callbackData){
        this.text=keyText;
        this.callback_data=callbackData;
    }
    public InlineKeyboardButton transformToInlineKeyboardButton (){
        return new InlineKeyboardButton(this.text, this.url, this.callback_data, this.web_app, this.login_url, this.switch_inline_query, this.switch_inline_query_current_chat, this.callback_game, this.pay);
    }
}
