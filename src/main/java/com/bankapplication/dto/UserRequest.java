package com.bankapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private Long id;
    private String firstname;
    private String lastname;
    private String gender;
    private String address;
    private String state;
    private String country;
    private String accountNumber;
    private String email;
    private String phoneNumber;
    private String alternativePhoneNumber;
    private String status;
}
