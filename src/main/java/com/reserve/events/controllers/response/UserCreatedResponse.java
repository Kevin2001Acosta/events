package com.reserve.events.controllers.response;

import com.reserve.events.controllers.domain.model.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedResponse {

    private String id;
    private String fullName;
    private String email;
    private String phone;
    private String city;
    private UserType type;
}
