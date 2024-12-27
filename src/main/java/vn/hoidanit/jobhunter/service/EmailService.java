package vn.hoidanit.jobhunter.service;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.repository.JobRepository;

@Service
public class EmailService {

     private final MailSender mailSender;
     private final JavaMailSender javaMailSender;
     private final SpringTemplateEngine templateEngine;
     private final JobRepository jobRepository;

     public EmailService(MailSender mailSender, JavaMailSender javaMailSender, SpringTemplateEngine templateEngine, JobRepository jobRepository) {
          this.mailSender = mailSender;
          this.javaMailSender = javaMailSender;
          this.templateEngine = templateEngine;
          this.jobRepository = jobRepository;
     }

     public void sendSimpleEmail() {
          SimpleMailMessage msg = new SimpleMailMessage();
          msg.setTo("nguyenthanh2542001.work@gmail.com");
          msg.setSubject("Testing from Spring Boot");
          msg.setText("Hello World from Spring Boot Email");
          this.mailSender.send(msg);
     }

     public void sendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
          // Prepare message using a Spring helper
          MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
          try {
               MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart,
                         StandardCharsets.UTF_8.name());
               message.setTo(to);
               message.setSubject(subject);
               message.setText(content, isHtml);
               this.javaMailSender.send(mimeMessage);
          } catch (MailException | MessagingException e) {
               System.out.println("ERROR SEND EMAIL: " + e);
          }
     }

     public void sendEmailFromTemplateSync(String to, String subject, String templateName) {
          Context context = new Context();

          List<Job> arrJob = this.jobRepository.findAll();
          context.setVariable("jobs", arrJob);

          String content = this.templateEngine.process(templateName, context);
          this.sendEmailSync(to, subject, content, false, true);
     }

     // https://github.com/hoidanit-be-java-spring-rest/02-java-jhipster-with-filter/blob/master/src/main/java/com/mycompany/myapp/service/MailService.java?ref_type=heads
     // https://stackoverflow.com/questions/74694051/sending-email-via-spring-boot/74750259#74750259
}
