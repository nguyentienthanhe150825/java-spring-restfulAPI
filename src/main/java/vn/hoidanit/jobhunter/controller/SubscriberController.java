package vn.hoidanit.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    
    private final SubscriberService subscriberService;

    public SubscriberController (SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("Create a subscriber")
    public ResponseEntity<Subscriber> createNewSubscriber (@Valid @RequestBody Subscriber requestSubscriber) throws IdInvalidException {
        // check email exist
        boolean isExistEmail = this.subscriberService.isExistEmail(requestSubscriber.getEmail());
        if (isExistEmail) {
            throw new IdInvalidException("Email " + requestSubscriber.getEmail() + " đã tồn tại");
        }
        Subscriber newSubscriber = this.subscriberService.handleCreateSubscriber(requestSubscriber);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSubscriber);
    }

}
