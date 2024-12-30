package vn.hoidanit.jobhunter.controller;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.service.EmailService;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class EmailController {

    private final EmailService emailService;
    private final SubscriberService subscriberService;

    public EmailController(EmailService emailService, SubscriberService subscriberService) {
        this.emailService = emailService;
        this.subscriberService = subscriberService;
    }

    @GetMapping("/email")
    @ApiMessage("Send email")
    // @Scheduled(cron = "*/30 * * * * *")
    // @Transactional
    public String sendSimpleEmail() {
        this.subscriberService.sendSubscribersEmailJobs();
        return "Send Email Success";
    }
}

// send email use Async
// https://www.danvega.dev/blog/sending-async-emails-in-spring

// Cron job guider: https://spring.io/guides/gs/scheduling-tasks
// Scheduling-cron-expression:
// https://docs.spring.io/spring-framework/reference/integration/scheduling.html#scheduling-cron-expression
