package com.reciclaview.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Component
public class TokenService {

    private static final Duration VALIDADE = Duration.ofHours(12);

    private final byte[] segredo;

    public TokenService(@Value("${app.auth.secret}") String segredo) {
        if (segredo == null || segredo.length() < 16) {
            throw new IllegalStateException("app.auth.secret precisa ter pelo menos 16 caracteres.");
        }
        this.segredo = segredo.getBytes(StandardCharsets.UTF_8);
    }

    public String gerar(Integer usuarioId) {
        long expira = Instant.now().plus(VALIDADE).getEpochSecond();
        String carga = Base64.getUrlEncoder().withoutPadding()
                .encodeToString((usuarioId + ":" + expira).getBytes(StandardCharsets.UTF_8));
        return carga + "." + assinar(carga);
    }

    public Optional<Integer> validar(String token) {
        try {
            if (token == null) {
                return Optional.empty();
            }
            int ponto = token.indexOf('.');
            if (ponto <= 0) {
                return Optional.empty();
            }
            String carga = token.substring(0, ponto);
            String assinatura = token.substring(ponto + 1);

            byte[] esperado = assinar(carga).getBytes(StandardCharsets.UTF_8);
            if (!MessageDigest.isEqual(esperado, assinatura.getBytes(StandardCharsets.UTF_8))) {
                return Optional.empty();
            }

            String[] partes = new String(Base64.getUrlDecoder().decode(carga), StandardCharsets.UTF_8).split(":");
            if (partes.length != 2) {
                return Optional.empty();
            }
            if (Instant.now().getEpochSecond() > Long.parseLong(partes[1])) {
                return Optional.empty();
            }
            return Optional.of(Integer.valueOf(partes[0]));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private String assinar(String dados) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(segredo, "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(dados.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }
}
