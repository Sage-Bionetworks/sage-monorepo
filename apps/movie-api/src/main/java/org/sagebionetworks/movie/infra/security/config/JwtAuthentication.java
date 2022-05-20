package org.sagebionetworks.movie.infra.security.config;

import org.sagebionetworks.movie.infra.security.AccessToken;
import lombok.ToString;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@ToString
public class JwtAuthentication extends AbstractAuthenticationToken {

    private final AccessToken accessToken;

    public JwtAuthentication(AccessToken accessToken) {
        super(accessToken.getAuthorities());
        this.accessToken = accessToken;
    }

    @Override
    public Object getCredentials() {
        return accessToken.getValue();
    }

    @Override
    public Object getPrincipal() {
        return accessToken.getUsername();
    }
}
