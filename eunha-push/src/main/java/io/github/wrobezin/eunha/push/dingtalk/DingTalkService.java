package io.github.wrobezin.eunha.push.dingtalk;

import io.github.wrobezin.eunha.crawler.HttpClient;
import io.github.wrobezin.eunha.data.entity.rule.PushContact;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/23 1:05
 */
@Slf4j
@Service
public class DingTalkService {
    private final HttpClient httpClient;
    private static final String URL_PREFIX = "https://oapi.dingtalk.com/robot/send";

    public DingTalkService(HttpClient httpClient) {
        this.httpClient = httpClient;
        Security.addProvider(new BouncyCastleProvider());
    }

    private String getSign(long timestamp, String secret) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
    }

    public void sendMarkdownMessage(PushContact contact, String message) {
        long timestamp = System.currentTimeMillis();
        String accessToken = contact.getValue();
        String secret = contact.getParams().getOrDefault("secret", "");
        String url = null;
        try {
            url = URL_PREFIX
                    + "?access_token=" + accessToken
                    + "&timestamp=" + timestamp
                    + "&sign=" + getSign(timestamp, secret);
        } catch (InvalidKeyException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
            log.error("生成签名出错", e);
        }
        try {
            Response response = httpClient.postJson(url, message);
            log.info("钉钉机器人推送结果{}", response.body().string());
        } catch (IOException e) {
            log.error("网络请求出错", e);
        }
    }
}
