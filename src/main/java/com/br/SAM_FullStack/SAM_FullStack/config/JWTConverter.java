package com.br.SAM_FullStack.SAM_FullStack.config;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JWTConverter implements Converter<Jwt, AbstractAuthenticationToken>{

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess == null || !realmAccess.containsKey("roles")) {
            return new JwtAuthenticationToken(jwt, java.util.Collections.emptyList());
        }

        Object rolesObj = realmAccess.get("roles");

        List<SimpleGrantedAuthority> grants = ((Collection<?>) rolesObj).stream()
                .filter(r -> r instanceof String)
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .toList();

        return new JwtAuthenticationToken(jwt, grants);
    }

}
