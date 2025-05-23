package com.example.telegramauth.service;

import com.example.telegramauth.model.TelegramUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

@Service
public class TelegramAuthServiceImpl implements TelegramAuthService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Override
    public boolean checkTelegramAuthorization(TelegramUser user) {
        try {
            Map<String, String> userData = parseTelegramUserData(user);
            String dataCheckString = generateDataCheckString(userData);

            byte[] secretKey = MessageDigest.getInstance("SHA-256")
                    .digest(botToken.getBytes(StandardCharsets.UTF_8));

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey, "HmacSHA256"));
            byte[] computedHash = mac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : computedHash) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString().equals(user.getHash());

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private Map<String, String> parseTelegramUserData(TelegramUser telegramUser) {
        Map<String, String> telegramUserData = new TreeMap<>();

        if (telegramUser.getId() != null) telegramUserData.put("id", telegramUser.getId().toString());
        if (telegramUser.getFirstName() != null) telegramUserData.put("first_name", telegramUser.getFirstName());
        if (telegramUser.getLastName() != null) telegramUserData.put("last_name", telegramUser.getLastName());
        if (telegramUser.getUsername() != null) telegramUserData.put("username", telegramUser.getUsername());
        if (telegramUser.getPhotoUrl() != null) telegramUserData.put("photo_url", telegramUser.getPhotoUrl());
        if (telegramUser.getAuthDate() != null)
            telegramUserData.put("auth_date", telegramUser.getAuthDate().toString());

        return telegramUserData;
    }

    private String generateDataCheckString(Map<String, String> telegramUserData) {
        StringBuilder checkStringBuilder = new StringBuilder();

        for (Map.Entry<String, String> entry : telegramUserData.entrySet()) {
            checkStringBuilder
                    .append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append("\n");
        }

        if (!checkStringBuilder.isEmpty()) {
            checkStringBuilder.deleteCharAt(checkStringBuilder.length() - 1);
        }

        return checkStringBuilder.toString();
    }
}
