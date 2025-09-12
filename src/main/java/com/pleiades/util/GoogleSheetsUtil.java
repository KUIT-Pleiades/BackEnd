package com.pleiades.util;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.pleiades.exception.CustomException;
import com.pleiades.exception.ErrorCode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class GoogleSheetsUtil {

    private static final String APPLICATION_NAME = "PleiadesItemSheetSync";

    public static Sheets getSheetsService(){
        try {
            String raw = System.getenv("GOOGLE_APPLICATION_CREDENTIALS_JSON");
            String b64 = System.getenv("GOOGLE_APPLICATION_CREDENTIALS_JSON_BASE64");
            String json;
            if (b64 != null && !b64.isBlank()) {
                json = new String(java.util.Base64.getDecoder().decode(b64));
            } else {
                json = raw;
            }

            if (json == null || json.isBlank()) {
                throw new CustomException(ErrorCode.ENV_NOT_SET);
            }

            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(new ByteArrayInputStream(json.getBytes()))
                    .createScoped(List.of("https://www.googleapis.com/auth/spreadsheets.readonly"));

            return new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (IOException | GeneralSecurityException e) {
            throw new CustomException(ErrorCode.GOOGLE_SHEET_CONNECTION_ERROR);
        }
    }
}
