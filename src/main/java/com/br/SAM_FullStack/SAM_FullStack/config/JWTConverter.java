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
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess == null) {
            return new JwtAuthenticationToken(jwt, java.util.Collections.emptyList());
        }
        Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("sam_app");

        if (clientAccess == null || !clientAccess.containsKey("roles")) {
            return new JwtAuthenticationToken(jwt, java.util.Collections.emptyList());
        }
        Collection<String> roles = (Collection<String>) clientAccess.get("roles");

        var grants = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();

        return new JwtAuthenticationToken(jwt, grants);
    }

}
