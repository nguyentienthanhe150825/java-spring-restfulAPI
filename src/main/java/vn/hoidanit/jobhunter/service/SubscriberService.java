package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.repository.SubscriberRepository;

@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
    }

    public boolean isExistEmail (String email) {
        return this.subscriberRepository.existsByEmail(email);
    }

    public Subscriber fetchSubscriberById (long id) {
        Optional<Subscriber> subscriberOptional = this.subscriberRepository.findById(id);
        if (subscriberOptional.isPresent()) {
            return subscriberOptional.get();
        }
        return null;
    }

    public Subscriber handleCreateSubscriber(Subscriber requestSubscriber) {
        // check Skill id exist
        if (requestSubscriber.getSkills() != null) {
            
            List<Long> listIdSkill = new ArrayList<>();
            // get List Skill id
            for (Skill skill : requestSubscriber.getSkills()) {
                listIdSkill.add(skill.getId());
            }
            // get List Skill by List Skill id
            List<Skill> listSkills = this.skillRepository.findByIdIn(listIdSkill);
            requestSubscriber.setSkills(listSkills);
        }
        return this.subscriberRepository.save(requestSubscriber);
    }

    public Subscriber updateSubscriber(Subscriber currentSubscriber, Subscriber requestSubscriber) {
        // check skill id exist
        if (requestSubscriber.getSkills() != null) {
            // get List Skill id
            List<Long> listIdSkill = requestSubscriber.getSkills()
                    .stream().map(skill -> skill.getId())
                    .collect(Collectors.toList());
            
            // get List Skill by List Skill id
            List<Skill> listSkills = this.skillRepository.findByIdIn(listIdSkill);
            // set currentSubscriber
            currentSubscriber.setSkills(listSkills);
        }
        currentSubscriber = this.subscriberRepository.save(currentSubscriber);
        return currentSubscriber;
    }
}
