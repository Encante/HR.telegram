package net.ddns.encante.telegram.hr.events;

public enum EventType {
        QUIZ_GOOD_ANSWER("quizGoodAnswer"), QUIZ_BAD_ANSWER("quizBadAnswer"),QUIZ_FR_GOOD_ANSWER("quizFrGoodAnswer"),QUIZ_FR_BAD_ANSWER("quizFrBadAnswer");
        private String code;

        EventType(String code){
                this.code = code;
        }
        public String getCode(){
                return code;
        }
}
