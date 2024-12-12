package vn.hoidanit.jobhunter.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ResLoginDTO {
    @JsonProperty("access_token")
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

    // Inner Class
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class UserGetAccount {
        private UserLogin user;
    }

}
