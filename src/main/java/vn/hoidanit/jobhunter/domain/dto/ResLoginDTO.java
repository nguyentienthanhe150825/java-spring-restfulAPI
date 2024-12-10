package vn.hoidanit.jobhunter.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ResLoginDTO {
    private String accessToken;
    private UserLogin user;

    // Inner Class
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class UserLogin {
        private long id;
        private String email;
        private String name;
    }

}
