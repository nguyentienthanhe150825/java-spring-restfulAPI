package vn.hoidanit.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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


    @PutMapping("/subscribers")
    @ApiMessage("Update a subscriber")
    public ResponseEntity<Subscriber> updateSubscriber (@RequestBody Subscriber requestSubscriber) throws IdInvalidException {
        // check subscriber by id
        Subscriber currentSubscriber = this.subscriberService.fetchSubscriberById(requestSubscriber.getId());
        if (currentSubscriber == null) {
            throw new IdInvalidException("Id " + requestSubscriber.getId() + " không tồn tại");
        }
        Subscriber updateSubscriber = this.subscriberService.updateSubscriber(currentSubscriber, requestSubscriber);
        return ResponseEntity.status(HttpStatus.OK).body(updateSubscriber);
    }

}
