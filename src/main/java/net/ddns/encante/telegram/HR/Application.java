package net.ddns.encante.telegram.HR;

import net.ddns.encante.telegram.HR.TelegramMethods.MessageManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = {"net.ddns.encante.telegram.HR.RemoteRequest", "net.ddns.encante.telegram.HR.TelegramObjects","net.ddns.encante.telegram.HR.TelegramMethods","net.ddns.encante.telegram.HR"})
public class Application {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(Application.class, args);
		MessageManager msgSender = ctx.getBean(MessageManager.class);
		msgSender.sendTelegramTextMessage("Bot odpalony T: " + Utils.getCurrentDateTime(),msgSender.getME());
		msgSender.sendTelegramTextMessage("Już wróciłem! :) Przepraszam za SPAM!", msgSender.getYASIA());
	}
}