package net.ddns.encante.telegram.HR;

import net.ddns.encante.telegram.HR.RemoteRequest.RemoteRequest;
import net.ddns.encante.telegram.HR.TelegramMethods.SendMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = {"net.ddns.encante.telegram.HR.RemoteRequest", "net.ddns.encante.telegram.HR.TelegramObjects","net.ddns.encante.telegram.HR.TelegramMethods","net.ddns.encante.telegram.HR"})
public class Application {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(Application.class, args);
		RemoteRequest request = ctx.getBean(RemoteRequest.class);
		request.sendTelegramMessage(new SendMessage()
				.setText("Bot odpalony T: " + Utils.getCurrentDateTime())
				.toMe());
	}
}