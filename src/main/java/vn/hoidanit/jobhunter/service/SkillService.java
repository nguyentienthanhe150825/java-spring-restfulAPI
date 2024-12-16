package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.Meta;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public boolean isNameExist(String name) {
        return this.skillRepository.existsByName(name);
    } 

    public Skill fetchSkillById(long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        if(skillOptional.isPresent()) {
            return skillOptional.get();
        }
        return null;
    }

    public Skill handleCreateSkill(Skill reqSkill) {
        return this.skillRepository.save(reqSkill);
    }

    public Skill updateSkill(Skill currentSkill, Skill requestSkill) {
        currentSkill.setName(requestSkill.getName());
        return this.skillRepository.save(currentSkill);
    }

    public ResultPaginationDTO fetchAllSkills(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> pageSkill = this.skillRepository.findAll(spec, pageable);
        
        ResultPaginationDTO result = new ResultPaginationDTO();
        Meta meta = new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageSkill.getTotalPages());
        meta.setTotal(pageSkill.getTotalElements());

        result.setMeta(meta);
        result.setResult(pageSkill.getContent());

        return result;
    }

    public void deleteSkill(long id) {
        // delete job (inside job_skill table)
       Skill currentSkill = this.fetchSkillById(id);
       currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));

       // delete skill
       this.skillRepository.delete(currentSkill);
    }

}
