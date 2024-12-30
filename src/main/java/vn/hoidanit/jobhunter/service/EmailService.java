package vn.hoidanit.jobhunter.service;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
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

     @Async
     public void sendEmailFromTemplateSync(String to, String subject, String templateName, String username, Object value) {
          Context context = new Context();

          context.setVariable("name", username);
          context.setVariable("jobs", value);

          String content = this.templateEngine.process(templateName, context);
          this.sendEmailSync(to, subject, content, false, true);
     }

     // https://github.com/hoidanit-be-java-spring-rest/02-java-jhipster-with-filter/blob/master/src/main/java/com/mycompany/myapp/service/MailService.java?ref_type=heads
     // https://stackoverflow.com/questions/74694051/sending-email-via-spring-boot/74750259#74750259
}    
     // send email use Async
     // https://www.danvega.dev/blog/sending-async-emails-in-spring

     // Async: Khi có Annotation Async thì đoạn code trong controller sendSimpleEmail
     // Sẽ trả về "Send Email Success" ngay lập tức mà không mất thời gian chờ xử lý code [sendSubscribersEmailJobs]
     // Bởi vì trong method: sendSubscribersEmailJobs
     // khi gọi tới this.emailService.sendEmailFromTemplateSync thì đã có Annotation
     
     // => sendEmailFromTemplateSync sẽ ĐƯỢC XỬ LÝ BÊN TRONG SEVER
     // => sendSubscribersEmailJobs cũng được xử lý trong sever
     // => do đó ở controller sendSimpleEmail

     // sẽ ngay lập tức trả về kết quả cho người dùng còn method nào có Annotation Async thì SEVER SẼ XỬ LÝ ĐỘC LẬP
     // tránh người dùng mất thời gian chờ PHẢN HỒI khi method đang được xử lý nhiều dữ liệu

     // LỢI THẾ:
     // Trả về kết quả cho người dùng tránh chờ thời gian SEVER xử lý dữ liệu lớn

     // BẤT LỢI:
     // Annotation Async là đa luồng (Hibernate Session)
     // Ở trong file html nếu phải gọi tới DATABASE để lấy dữ liệu nhiều lần sẽ tạo ra lỗi bất đồng bộ
     
     // => cách khắc phục: Ko dc bắt front-end gọi tới DATABASE để lấy dữ liệu

     // => những Param khi gửi từ back-end
     // sẽ phải gọi tới database lấy dữ liệu trước khi gủi lên Front-end
     

     // => ở trong (SubscriberService) trước khi gọi tới method gửi email (sendEmailFromTemplateSync)
     // thì sẽ convert dữ liệu từ DATABASE sang ĐỐI TƯỢNG ResEmailJob

     // CÁCH KHÁC: ở trong (sendEmailFromTemplateSync) sẽ sử dụng
     // context.setVariable để set tất cả các Param trước khi gửi lên front-end