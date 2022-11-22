package net.ddns.encante.telegram.HR;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class Application {

//	@Autowired
//	RemoteRequest request;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		RemoteRequest request = new RemoteRequest();
		request.sendMessageToChatId("Bot odpalony T: "
				+ Utils.getCurrentDateTime()
				, 5580797031L);
		request.sendMessageWithKeyboardToChatId(5580797031L);
}
}
