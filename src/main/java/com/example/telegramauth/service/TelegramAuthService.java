package com.example.telegramauth.service;

import com.example.telegramauth.model.TelegramUser;

public interface TelegramAuthService {

    boolean checkTelegramAuthorization(TelegramUser authRequestDto);

}
