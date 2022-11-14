package net.ddns.encante.telegram.HR;

import lombok.Getter;

public class WebhookUpdate {

        @Getter
        private Long update_id;
        Message message;
    }

    class Message {
        @Getter
        private Long message_id;
        From from;
        Chat chat;
        @Getter
        private Long date;
        @Getter
        private String text;
    }

    class Chat {
        @Getter
        private Long id;
        @Getter
        private String first_name;
        @Getter
        private String last_name;
        @Getter
        private String type;
    }

    class From {
        @Getter
        private Long id;
        @Getter
        private boolean is_bot;
        @Getter
        private String first_name;
        @Getter
        private String last_name;
        @Getter
        private String language_code;
    }
