package vn.hoidanit.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("skills")
    @ApiMessage("Create a skill")
    public ResponseEntity<Skill> createNewSkill(@Valid @RequestBody Skill reqSkill) throws IdInvalidException {
        // check name is exist in database
        boolean isExist = this.skillService.isNameExist(reqSkill.getName());
        if (isExist == true) {
            throw new IdInvalidException("Skill name = " + reqSkill.getName() + " đã tồn tại");
        }

        Skill skillCreate = this.skillService.handleCreateSkill(reqSkill);
        return ResponseEntity.status(HttpStatus.CREATED).body(skillCreate);
    }

    @PutMapping("/skills")
    @ApiMessage("Update a skill")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill requestSkill) throws IdInvalidException {
        // Find Skill by id request
        Skill currentSkill = this.skillService.fetchSkillById(requestSkill.getId());
        if (currentSkill == null) {
            throw new IdInvalidException("Skill id = " + requestSkill.getId() + " không tồn tại");
        }

        // check name request is exist in database
        boolean isExist = this.skillService.isNameExist(requestSkill.getName());
        if (isExist) {
            throw new IdInvalidException("Skill name = " + requestSkill.getName() + " đã tồn tại");
        }

        Skill skillUpdate = this.skillService.updateSkill(currentSkill, requestSkill);

        return ResponseEntity.status(HttpStatus.OK).body(skillUpdate);
    }
}
