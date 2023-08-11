package net.ddns.encante.telegram.hr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ddns.encante.telegram.hr.telegram.service.MessageManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
//		(scanBasePackages = "net.ddns.encante.telegram.hr, net.ddns.encante.telegram.hr.events, net.ddns.encante.telegram.hr.hue.api, net.ddns.encante.telegram.hr.hue.entity, net.ddns.encante.telegram.hr.hue.repository, net.ddns.encante.telegram.hr.hue.service, net.ddns.encante.telegram.hr.menu, net.ddns.encante.telegram.hr.menu.entity, net.ddns.encante.telegram.hr.menu.repository, net.ddns.encante.telegram.hr.menu.service, net.ddns.encante.telegram.hr.quiz, net.ddns.encante.telegram.hr.quiz.entity, net.ddns.encante.telegram.hr.quiz.repository, net.ddns.encante.telegram.hr.quiz.service, net.ddns.encante.telegram.hr.request, net.ddns.encante.telegram.hr.telegram, net.ddns.encante.telegram.hr.telegram.api, net.ddns.encante.telegram.hr.telegram.api.methods, net.ddns.encante.telegram.hr.telegram.api.objects, net.ddns.encante.telegram.hr.telegram.entity, net.ddns.encante.telegram.hr.telegram.repository, net.ddns.encante.telegram.hr.telegram.service")
public class Application {
	@Bean
	public Gson gson(){
		return new GsonBuilder().setPrettyPrinting().create();
	}
	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(Application.class, args);
		MessageManager msgSender = ctx.getBean(MessageManager.class);
		msgSender.sendTelegramTextMessage("Bot odpalony \n T: " + Utils.getCurrentDateTime(),msgSender.getME());
		msgSender.sendTelegramTextMessage("Już wróciłem! :) Przepraszam za SPAM! \nBot jest włączony.", msgSender.getYASIA());
		}
}