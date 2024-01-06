package com.kosuri.stores.handler;

import com.kosuri.stores.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsHandler {

    private final AppProperties appProperties;

    public boolean sendSMSMessage(String mobileNumber, String otp) {
        try {
            String message = String.format(appProperties.getSmsMessage(), otp);
            String apiKey = "apikey=" + URLEncoder.encode(appProperties.getApiKey(), StandardCharsets.UTF_8);
            String encodedMessage = "&message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
            String sender = "&sender=" + URLEncoder.encode(appProperties.getSender(), StandardCharsets.UTF_8);
            String numbers = "&numbers=91" + URLEncoder.encode(mobileNumber, StandardCharsets.UTF_8);

            String data = appProperties.getUrl()+apiKey+numbers+encodedMessage+sender;
            HttpURLConnection conn = (HttpURLConnection) new URL(data).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder smsResponse= new StringBuilder();
            while ((line = rd.readLine()) != null) {
                smsResponse.append(line).append(" ");
            }
            rd.close();
            log.info("Response From SMS Service {}", smsResponse);
            return true;
        }catch(Exception exception){
            log.error("Exception while Sending SMS to Mobile Number {} with error {}",mobileNumber,exception.getMessage());
            return false;
        }
    }
}
