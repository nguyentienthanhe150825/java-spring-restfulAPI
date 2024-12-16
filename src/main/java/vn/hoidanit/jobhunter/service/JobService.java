package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResCreateJobDTO;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
    }

    public Optional<Job> fetchJobById(long id) {
        return this.jobRepository.findById(id);
    }

    public ResCreateJobDTO handleCreateJob(Job requestJob) {
        // check skill isExist before save Job to database (ManyToMany Relationship)
        if (requestJob.getSkills() != null) {
            List<Long> listIdSkill = requestJob.getSkills()
                    .stream().map(item -> item.getId())
                    .collect(Collectors.toList());

            List<Skill> listSkills = this.skillRepository.findByIdIn(listIdSkill);
            
            // RequestJob chỉ có thông tin của Id Skill và sau khi tìm
            // Set tất cả Attribute của Skill vào RequestJob
            requestJob.setSkills(listSkills);
        }

        // create Job
        Job currentJob = this.jobRepository.save(requestJob);

        // convert Job Object into ResponseDTO
        ResCreateJobDTO responseJobDTO = new ResCreateJobDTO();
        responseJobDTO.setId(currentJob.getId());
        responseJobDTO.setName(currentJob.getName());
        responseJobDTO.setSalary(currentJob.getSalary());
        responseJobDTO.setQuantity(currentJob.getQuantity());
        responseJobDTO.setLocation(currentJob.getLocation());
        responseJobDTO.setLevel(currentJob.getLevel());
        responseJobDTO.setStartDate(currentJob.getStartDate());
        responseJobDTO.setEndDate(currentJob.getEndDate());
        responseJobDTO.setActive(currentJob.isActive());
        responseJobDTO.setCreatedAt(currentJob.getCreatedAt());
        responseJobDTO.setCreatedBy(currentJob.getCreatedBy());

        if (currentJob.getSkills() != null) {
            List<String> ListNameSkills = currentJob.getSkills()
                    .stream().map(this::convertToSkillName)
                    .collect(Collectors.toList());
            responseJobDTO.setSkills(ListNameSkills);
        }

        return responseJobDTO;
    }

    public String convertToSkillName(Skill skill) {
        return skill.getName();
    }
}
