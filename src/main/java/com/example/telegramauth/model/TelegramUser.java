package com.example.telegramauth.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TelegramUser {

    private Long id;

    private String firstName;
    private String lastName;
    private String username;
    private String photoUrl;
    private Long authDate;
    private String hash;

}
