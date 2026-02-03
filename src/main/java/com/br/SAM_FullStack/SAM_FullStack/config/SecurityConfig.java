	package com.br.SAM_FullStack.SAM_FullStack.config;

	import java.util.Arrays;

	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.context.annotation.Bean;
	import org.springframework.context.annotation.Configuration;
	import org.springframework.http.HttpHeaders;
	import org.springframework.http.HttpMethod;
	import org.springframework.security.authentication.AuthenticationManager;
	import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
	import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
	import org.springframework.security.config.annotation.web.builders.HttpSecurity;
	import org.springframework.security.config.http.SessionCreationPolicy;
	import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
	import org.springframework.security.crypto.password.PasswordEncoder;
	import org.springframework.security.web.SecurityFilterChain;
	import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
	import org.springframework.web.cors.CorsConfiguration;
	import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

	import com.br.SAM_FullStack.SAM_FullStack.autenticacao.CustomUserDetailsService;
	import com.br.SAM_FullStack.SAM_FullStack.config.JwtAuthenticationFilter;

	@Configuration
	@EnableMethodSecurity
	public class SecurityConfig {

		@Autowired
		private JwtAuthenticationFilter jwtAuthFilter;

		@Autowired
		private CustomUserDetailsService userDetailsService;

		@Bean
		public PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}

		@Bean
		public DaoAuthenticationProvider authenticationProvider() {
			DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
			authProvider.setUserDetailsService(userDetailsService);
			authProvider.setPasswordEncoder(passwordEncoder());
			return authProvider;
		}


		@Bean
		public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
			return http.getSharedObject(AuthenticationManager.class);
		}

		@Bean
		public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
			http
					.csrf(csrf -> csrf.disable())
					.cors(cors -> {})
					.authorizeHttpRequests(auth -> auth
							// rotas livres
							.requestMatchers("/auth/login/**").permitAll()
							.requestMatchers("/areas/findAll").permitAll()
							.requestMatchers("/mentores/save").permitAll()
							.requestMatchers("/alunos/save").permitAll()
							.requestMatchers("/api/coordenador/save").permitAll()
							.requestMatchers("/api/professor/save").permitAll()
							.requestMatchers("/grupos/save").permitAll()
							.requestMatchers("/cursos/findAll").permitAll()
							// rotas guardadas por Role
							.requestMatchers("/alunos/findAll").hasAnyRole("COORDENADOR", "PROFESSOR")
							.requestMatchers("/alunos/**").hasRole("ALUNO")
							.requestMatchers("/api/coordenador/**").hasRole("COORDENADOR")
							.requestMatchers("/api/professor/findAll").hasRole("ALUNO")
							.requestMatchers("/api/professor/**").hasRole("PROFESSOR")
							.requestMatchers("/mentores/area/**").hasRole("ALUNO")
							.requestMatchers("/mentores/findAll").hasAnyRole("ALUNO", "COORDENADOR")
							.requestMatchers("/mentores/**").hasRole("MENTOR")
							.requestMatchers("/avaliacoes/**").hasAnyRole("ALUNO", "COORDENADOR")
							.requestMatchers("/reunioes/**").hasAnyRole("MENTOR", "ALUNO")
							.requestMatchers("/projetos/findById/**").hasAnyRole("ALUNO", "MENTOR", "PROFESSOR", "COORDENADOR")
							.requestMatchers("/projetos/buscar-por-atuacao/**").hasAnyRole("COORDENADOR", "PROFESSOR")
							.requestMatchers("/projetos/buscar-projetos-ativos-mentor/**").hasRole("MENTOR")
							.requestMatchers("/projetos/buscar-projetos-nao-avaliados-mentor/**").hasRole("MENTOR")
							.requestMatchers("/projetos/mentor/**").hasRole("MENTOR")
							.requestMatchers("/projetos/professor/**").hasRole("PROFESSOR")
							.requestMatchers("/projetos/**").hasRole("ALUNO")
							.requestMatchers("/grupos/professor/**").hasRole("PROFESSOR")
							.requestMatchers("/grupos/**").hasRole("ALUNO")


							.anyRequest().authenticated()
					)
					.authenticationProvider(authenticationProvider())
					.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
					.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

			return http.build();
		}

		@Bean
		public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
			CorsConfiguration config = new CorsConfiguration();
			config.setAllowedOriginPatterns(Arrays.asList("*")); // permite Angular
			config.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE"));
			config.setAllowedHeaders(Arrays.asList("*"));
			config.setAllowCredentials(true);

			UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
			source.registerCorsConfiguration("/**", config);
			return source;
		}
	}
