package net.ddns.encante.telegram.HR.events;

public enum EventType {
        QUIZ_GOOD_ANSWER("quizGoodAnswer"), QUIZ_BAD_ANSWER("quizBadAnswer");
        private String code;

        private EventType(String code){
                this.code = code;
        }
        public String getCode(){
                return code;
        }
}
