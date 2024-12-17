package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.Meta;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResUpdateJobDTO;
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

    public ResUpdateJobDTO updateJob(Job requestJob, Job jobUpdate) {
        // check skill isExist before save Job to database (ManyToMany Relationship)
        if (requestJob.getSkills() != null) {
            List<Long> listIdSkill = requestJob.getSkills()
                    .stream().map(item -> item.getId())
                    .collect(Collectors.toList());

            List<Skill> listSkills = this.skillRepository.findByIdIn(listIdSkill);
            jobUpdate.setSkills(listSkills);
        }

        // update correct info
        jobUpdate.setName(requestJob.getName());
        jobUpdate.setSalary(requestJob.getSalary());
        jobUpdate.setQuantity(requestJob.getQuantity());
        jobUpdate.setLocation(requestJob.getLocation());
        jobUpdate.setLevel(requestJob.getLevel());
        jobUpdate.setStartDate(requestJob.getStartDate());
        jobUpdate.setEndDate(requestJob.getEndDate());
        jobUpdate.setActive(requestJob.isActive());
        jobUpdate.setDescription(requestJob.getDescription());

        // update job
        Job currentJob = this.jobRepository.save(jobUpdate);

        // convert Job Object into ResponseDTO
        ResUpdateJobDTO dto = new ResUpdateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLocation(currentJob.getLocation());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.isActive());
        dto.setUpdatedAt(currentJob.getUpdatedAt());
        dto.setUpdatedBy(currentJob.getUpdatedBy());

        if (currentJob.getSkills() != null) {
            List<String> ListNameSkills = currentJob.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            dto.setSkills(ListNameSkills);
        }

        return dto;
    }

    public void deleteJob(long id) {
        this.jobRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchAllSkills(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageJob = this.jobRepository.findAll(spec, pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();
        Meta meta = new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageJob.getTotalPages());
        meta.setTotal(pageJob.getTotalElements());

        result.setMeta(meta);
        result.setResult(pageJob.getContent());

        return result;
    }

}
