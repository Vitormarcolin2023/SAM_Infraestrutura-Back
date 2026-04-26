package com.br.SAM_FullStack.SAM_FullStack.config;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.Map;

public class JWTConverter implements Converter<Jwt, AbstractAuthenticationToken>{

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // 1. Acessa o resource_access
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess == null) {
            return new JwtAuthenticationToken(jwt, java.util.Collections.emptyList());
        }

        // 2. Acessa o seu client específico (sam_app)
        Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("sam_app");

        if (clientAccess == null || !clientAccess.containsKey("roles")) {
            return new JwtAuthenticationToken(jwt, java.util.Collections.emptyList());
        }

        // 3. Extrai a lista de roles
        Collection<String> roles = (Collection<String>) clientAccess.get("roles");

        var grants = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();

        return new JwtAuthenticationToken(jwt, grants);
    }

}
