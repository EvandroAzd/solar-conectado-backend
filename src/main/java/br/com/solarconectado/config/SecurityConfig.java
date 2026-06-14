package br.com.solarconectado.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/usuarios/cadastrar", "/usuarios/login").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/usuarios/perfil").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/usuarios/perfil").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/usuarios").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/usuarios/*").hasRole("MASTER")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/usuarios/*/inativar").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/usuarios/*/reativar").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/usuarios/*").hasRole("MASTER")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/adm").hasRole("MASTER")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/adm/promover/*").hasRole("MASTER")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/adm/*/inativar").hasRole("MASTER")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/adm/*/reativar").hasRole("MASTER")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/adm/*").hasRole("MASTER")
                        .requestMatchers("/doadores/**").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/promessas/campanha/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/promessas/pendentes").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/promessas/espontanea").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/promessas/{id}/confirmar").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/promessas/{id}/cancelar").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/campanhas").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/campanhas/*/compartilhar").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/campanhas/adm").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/campanhas/*").hasRole("MASTER")
                        .requestMatchers("/campanhas/**").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/categorias").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/categorias").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/categorias/*/inativar").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/categorias/*/reativar").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/categorias/*").hasRole("MASTER")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/produtos").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/produtos").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/produtos/*").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/produtos/*/inativar").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/produtos/*/reativar").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/produtos/*").hasRole("MASTER")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/config/gamificacao").hasRole("MASTER")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/config/gamificacao").hasRole("MASTER")
                        // Centro de Custo
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/centros-custo").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/centros-custo").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/centros-custo/*/inativar").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/centros-custo/*/reativar").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/centros-custo/*").hasRole("MASTER")
                        // Itens do Bazar
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/itens-bazar").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/itens-bazar").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/itens-bazar/*").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/itens-bazar/*").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/itens-bazar/*/ativar").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/itens-bazar/*/inativar").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/itens-bazar/*").hasRole("MASTER")
                        // Saída Interna
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/saidas-internas").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/saidas-internas").hasRole("ADM")
                        // Bazar público
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/bazar/itens").permitAll()
                        // Pedidos do Bazar
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/bazar/pedidos").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/bazar/pedidos").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/bazar/pedidos/*/confirmar").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/bazar/pedidos/*/cancelar").hasRole("ADM")
                        // Relatórios
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/bazar/relatorios").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/comentarios").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/comentarios/aprovados").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/comentarios/pendentes").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/comentarios/*/aprovar").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/comentarios/*/rejeitar").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/comentarios/*").hasRole("MASTER")
                        .requestMatchers("/comentarios/*/curtir").authenticated()
                        // Empresa
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/empresas/login").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/empresas/adm").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/empresas/minhas").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/empresas").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/empresas/*").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/empresas").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/empresas/*/aprovar").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/empresas/*/inativar").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/empresas/*/rejeitar").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/empresas/*").hasAnyRole("MASTER", "REPRESENTANTE")
                        // Upload de imagens
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/upload/campanha").hasRole("ADM")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/upload/perfil").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/upload/empresa").authenticated()
                        // Representantes
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/empresas/*/representantes").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/empresas/*/representantes").hasAnyRole("ADM", "REPRESENTANTE")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/representantes/*/aprovar").hasRole("REPRESENTANTE")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/representantes/*/inativar").hasAnyRole("ADM", "REPRESENTANTE")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/representantes/*").hasAnyRole("MASTER", "REPRESENTANTE")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
