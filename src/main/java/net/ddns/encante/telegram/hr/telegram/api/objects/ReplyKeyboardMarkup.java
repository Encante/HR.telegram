package net.ddns.encante.telegram.hr.telegram.api.objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Collections;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReplyKeyboardMarkup {
    @NonNull
    ArrayList<ArrayList<KeyboardButton>> keyboard;
    boolean resize_keyboard;
    boolean one_time_keyboard;
    String input_field_placeholder;
    boolean selective;
    ReplyKeyboardMarkup(KeyboardBuilder builder){
        this.keyboard = builder.keyboardLayout;
        this.resize_keyboard = builder.resize_keyboard;
        this.one_time_keyboard = builder.one_time_keyboard;
        this.input_field_placeholder = builder.input_field_placeholder;
        this.selective = builder.selective;
    }
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class KeyboardBuilder {
        @org.springframework.lang.NonNull
        int rows;
        @org.springframework.lang.NonNull
        int cols;
        @NonNull
        ArrayList<String> names;
        ArrayList<KeyboardButton> rowx;
        ArrayList<ArrayList<KeyboardButton>> keyboardLayout;
        //        optional
        boolean resize_keyboard;
        boolean one_time_keyboard;
        String input_field_placeholder;
        boolean selective;

        public KeyboardBuilder(int rows, int cols, String ...namesStream){
            this.rows=rows;
            this.cols=cols;
            this.names=new ArrayList<>();
            Collections.addAll(this.names, namesStream);
        }
        public KeyboardBuilder setResizeKeyboard(boolean resizeKeyboard){
            this.resize_keyboard = resizeKeyboard;
            return this;
        }
        public KeyboardBuilder setOneTimeKeyboard(boolean oneTimeKeyboard){
            this.one_time_keyboard = oneTimeKeyboard;
            return this;
        }
        public KeyboardBuilder setInputFieldPlaceholder(String inputFieldPlaceholder){
            this.input_field_placeholder = inputFieldPlaceholder;
            return this;
        }
        public KeyboardBuilder setSelective(boolean selective){
            this.selective = selective;
            return this;
        }
        public ReplyKeyboardMarkup build(){
            this.rowx=new ArrayList<>();
            this.keyboardLayout =new ArrayList<>();
//            check if there is suitable names for all buttons
            if(rows*cols == names.size()) {
                for (int i = 0; i < cols; i++) {
                    for (int j = 0; j < rows; j++) {
                        this.rowx.add(new KeyboardButton(this.names.get(0)));
                        this.names.remove(0);
                    }
                    this.keyboardLayout.add(this.rowx);
                    this.rowx = new ArrayList<>();
                }
                return new ReplyKeyboardMarkup(this);
            }
            else{
                System.out.println("Not enough names for all buttons.");
            }
            return new ReplyKeyboardMarkup(this);
        }
    }
}