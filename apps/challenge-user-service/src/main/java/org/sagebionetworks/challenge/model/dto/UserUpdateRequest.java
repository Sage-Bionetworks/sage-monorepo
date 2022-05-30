package org.sagebionetworks.challenge.model.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private UserStatus status; // TODO: UserStatus?
}