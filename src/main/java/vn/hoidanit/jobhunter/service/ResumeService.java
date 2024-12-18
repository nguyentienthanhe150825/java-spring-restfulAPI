package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.Meta;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.ResumeRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public ResumeService(ResumeRepository resumeRepository, UserRepository userRepository, JobRepository jobRepository) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public Optional<Resume> fetchResumeById(long id) {
        return this.resumeRepository.findById(id);
    }

    public boolean checkResumeExistByUserAndJob(Resume requestResume) {
        // check user by id
        if (requestResume.getUser() == null) {
            return false;
        }
        Optional<User> userOptional = this.userRepository.findById(requestResume.getUser().getId());
        if (!userOptional.isPresent()) {
            return false;
        }

        // check job by id
        if (requestResume.getJob() == null) {
            return false;
        }
        Optional<Job> jobOptional = this.jobRepository.findById(requestResume.getJob().getId());
        if (!jobOptional.isPresent()) {
            return false;
        }

        return true;
    }

    public ResCreateResumeDTO handleCreateResume(Resume requestResume) {
        Resume newResume = this.resumeRepository.save(requestResume);
        
        ResCreateResumeDTO dto = new ResCreateResumeDTO();
        dto.setId(newResume.getId());
        dto.setCreatedAt(newResume.getCreatedAt());
        dto.setCreatedBy(newResume.getCreatedBy());

        return dto;
    }

    public ResUpdateResumeDTO updateStatusResume(Resume currentResume, Resume requestResume) {
        // set status current resume
        currentResume.setStatus(requestResume.getStatus());
        
        // update database
        this.resumeRepository.save(currentResume);
        
        // convert Resume Object into DTO
        ResUpdateResumeDTO dto = new ResUpdateResumeDTO();
        dto.setUpdatedAt(currentResume.getUpdatedAt());
        dto.setUpdatedBy(currentResume.getUpdatedBy());

        return dto;
    }

    public void deleteResume(long id) {
        this.resumeRepository.deleteById(id);
    }

    public ResFetchResumeDTO convertToResFetchResumeDTO(Resume resume) {
        ResFetchResumeDTO dto = new ResFetchResumeDTO();
        dto.setId(resume.getId());
        dto.setEmail(resume.getEmail());
        dto.setUrl(resume.getUrl());
        dto.setStatus(resume.getStatus());
        dto.setCreatedAt(resume.getCreatedAt());
        dto.setCreatedBy(resume.getCreatedBy());
        dto.setUpdatedAt(resume.getUpdatedAt());
        dto.setUpdatedBy(resume.getUpdatedBy());

        if (resume.getJob() != null) {
            dto.setCompanyName(resume.getJob().getCompany().getName());
        }

        ResFetchResumeDTO.UserResume user = new ResFetchResumeDTO.UserResume(resume.getUser().getId(), resume.getUser().getName());
        dto.setUser(user);

        ResFetchResumeDTO.JobResume job = new ResFetchResumeDTO.JobResume(resume.getJob().getId(), resume.getJob().getName());
        dto.setJob(job);
    
        return dto;
    }

    public ResultPaginationDTO fetchAllResumes(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);
        
        ResultPaginationDTO result = new ResultPaginationDTO();
        Meta meta = new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageResume.getTotalPages());
        meta.setTotal(pageResume.getTotalElements());

        result.setMeta(meta);
        
        // convert Resume Objct into DTO
        List<ResFetchResumeDTO> listResume = pageResume.getContent()
                .stream().map(this::convertToResFetchResumeDTO)
                .collect(Collectors.toList());
        
        result.setResult(listResume);

        return result;
    }

}
