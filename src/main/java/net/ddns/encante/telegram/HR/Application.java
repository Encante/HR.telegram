package net.ddns.encante.telegram.HR;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class Application {

//	@Autowired
//	RemoteRequest request;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

//		SendMessage botWelcomeMessage =
				SendMessage.builder().chat_id(5580797031L).text("Bot odpalony T: "+ Utils.getCurrentDateTime()).build().send();
//		sendMessage.setChat_id(5580797031L);
//		sendMessage.setText("Bot odpalony T: "+ Utils.getCurrentDateTime());
//		sendMessage.sendMessageToChatIdByJson(ReplyKeyboardType.NO);
		SendMessage.builder().chat_id(5580797031L)
				.text("So")
				.build()
				.send();
	}
}
