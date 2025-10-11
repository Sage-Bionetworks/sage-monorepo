package org.sagebionetworks.bixarena.api.security.keys;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Component;

/**
 * In-memory key store that holds the current signing key plus (optionally) the previous key to
 * support short overlap during rotation. For now this is ephemeral and will regenerate on restart.
 * Later we can persist keys or load them from an external secret manager.
 */
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
      // Keep only the most recent previous key for a brief overlap window
      all().stream().limit(1).forEach(newList::add);
      keys.set(List.copyOf(newList));
    } catch (JOSEException e) {
      throw new IllegalStateException("Key rotation failed", e);
    }
  }

  public JWKSet publicJwkSet() {
    var jwks = all().stream().map(k -> (JWK) k.toPublicJWK()).toList();
    return new JWKSet(jwks);
  }
}
