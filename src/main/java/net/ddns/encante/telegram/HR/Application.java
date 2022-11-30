package net.ddns.encante.telegram.HR;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class Application {

//	@Autowired
//	RemoteRequest request;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

		SendMessage.builder()
				.chat_id(5580797031L)
				.text("Bot odpalony T: "+ Utils.getCurrentDateTime()).build().send();
		SendMessage.builder().chat_id(5580797031L)
				.text("So")
				.build()
				.sendMessageWithKeyboard(ReplyKeyboardType.INLINE);
	}
}
