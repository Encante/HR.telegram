package net.ddns.encante.telegram.HR.HR.telegram;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestHandler {

    ObjectMapper objectMapper = new ObjectMapper();
    @PostMapping("/test")
    public String postHandler (@RequestBody String content){
        System.out.println(content);
        return "ok";
    }

}
