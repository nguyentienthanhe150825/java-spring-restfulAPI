package vn.hoidanit.jobhunter.domain.response.file;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResUploadFileDTO {
    private String fileName;
    private Instant uploadedAt;
}
