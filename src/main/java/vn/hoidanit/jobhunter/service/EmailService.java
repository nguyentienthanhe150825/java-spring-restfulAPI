package vn.hoidanit.jobhunter.service;

  import org.springframework.mail.MailSender;
  import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
     
     private final MailSender mailSender;

     public EmailService(MailSender mailSender) {
     this.mailSender = mailSender;
     }

     public void sendSimpleEmail() {
          SimpleMailMessage msg = new SimpleMailMessage();
          msg.setTo("nguyenthanh2542001.work@gmail.com");
          msg.setSubject("Testing from Spring Boot");
          msg.setText("Hello World from Spring Boot Email");
          this.mailSender.send(msg);
     }

     // Test send Email: https://stackoverflow.com/questions/74694051/sending-email-via-spring-boot/74750259#74750259
}
