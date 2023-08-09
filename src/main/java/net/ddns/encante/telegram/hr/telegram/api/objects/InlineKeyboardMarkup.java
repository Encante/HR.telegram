package net.ddns.encante.telegram.hr.telegram.api.objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Collections;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InlineKeyboardMarkup {
    ArrayList<ArrayList<InlineKeyboardButton>> inline_keyboard;
    InlineKeyboardMarkup(KeyboardBuilder builder){
        this.inline_keyboard = builder.keyboardLayout;
    }
    public InlineKeyboardMarkup(ArrayList<ArrayList<InlineKeyboardButton>> inline_keyboard){
        this.inline_keyboard=inline_keyboard;
    }
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class KeyboardBuilder {
        @org.springframework.lang.NonNull
        int rows;
        @org.springframework.lang.NonNull
        int cols;
        @NonNull
        ArrayList<String> names;
        ArrayList<InlineKeyboardButton> rowx;
        ArrayList<ArrayList<InlineKeyboardButton>> keyboardLayout;

        public KeyboardBuilder(int rows, int cols, String ...namesStream){
            this.rows=rows;
            this.cols=cols;
            this.names=new ArrayList<>();
            Collections.addAll(this.names, namesStream);
        }
        public InlineKeyboardMarkup build(){
            this.rowx=new ArrayList<>();
            this.keyboardLayout =new ArrayList<>();
//            check if there is suitable names for all buttons
            if(rows*cols <= names.size()) {
                for (int i = 0; i < cols; i++) {
                    for (int j = 0; j < rows; j++) {
                        this.rowx.add(new InlineKeyboardButton(this.names.get(0)));
                        this.names.remove(0);
                    }
                    this.keyboardLayout.add(this.rowx);
                    this.rowx = new ArrayList<>();
                }
                return new InlineKeyboardMarkup(this);
            }
            else{
                System.out.println("Not enough names for all buttons.");
            }
            throw new RuntimeException("couldn't build a keyboard");
        }
    }
}