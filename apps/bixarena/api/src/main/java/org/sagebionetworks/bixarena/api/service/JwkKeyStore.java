package org.sagebionetworks.bixarena.api.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Component;

@Component
public class JwkKeyStore {

  private final AtomicReference<List<RSAKey>> keys = new AtomicReference<>(List.of(generate()));

  private static RSAKey generate() {
    try {
      return new RSAKeyGenerator(2048)
        .keyID(UUID.randomUUID().toString())
        .algorithm(JWSAlgorithm.RS256)
        .generate();
    } catch (JOSEException e) {
      throw new IllegalStateException("Unable to generate initial RSA key", e);
    }
  }

  public RSAKey current() {
    return keys.get().getFirst();
  }

  public List<RSAKey> all() {
    return keys.get();
  }

  public synchronized void rotate() {
    try {
      RSAKey next = new RSAKeyGenerator(2048)
        .keyID(UUID.randomUUID().toString())
        .algorithm(JWSAlgorithm.RS256)
        .generate();
      List<RSAKey> newList = new ArrayList<>();
      newList.add(next);
      all().stream().limit(1).forEach(newList::add);
      keys.set(List.copyOf(newList));
    } catch (JOSEException e) {
      throw new IllegalStateException("Key rotation failed", e);
    }
  }

  public JWKSet publicJwkSet() {
    return new JWKSet(all().stream().map(RSAKey::toPublicJWK).toList());
  }
}
