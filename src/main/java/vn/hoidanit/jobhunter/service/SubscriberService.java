package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.domain.response.email.ResEmailJob;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.repository.SubscriberRepository;

@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final EmailService emailService;
    private final JobRepository jobRepository;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository, EmailService emailService, JobRepository jobRepository) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.emailService = emailService;
        this.jobRepository = jobRepository;
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

    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {

                        List<ResEmailJob> arr = listJobs.stream().map(job -> this.convertJobToSendEmail(job))
                                .collect(Collectors.toList());

                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                arr);
                    }
                }
            }
        }
    }

    public ResEmailJob convertJobToSendEmail (Job job) {
        ResEmailJob res = new ResEmailJob();
        res.setName(job.getName());
        res.setSalary(job.getSalary());

        ResEmailJob.CompanyEmail company = new ResEmailJob.CompanyEmail();
        company.setName(job.getCompany().getName());

        res.setCompany(company);

        List<ResEmailJob.SkillEmail> skill = new ArrayList<>();
        ResEmailJob.SkillEmail skillName = new ResEmailJob.SkillEmail();

        for (Skill sk : job.getSkills()) {
            skillName.setName(sk.getName());
            skill.add(skillName);
        }
        
        res.setSkills(skill);

        return res;
    }

}
