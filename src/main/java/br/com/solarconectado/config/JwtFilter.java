package br.com.solarconectado.config;

import br.com.solarconectado.entity.Usuario;
import br.com.solarconectado.repository.AdmRepository;
import br.com.solarconectado.repository.UsuarioRepository;
import br.com.solarconectado.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final AdmRepository admRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String email = jwtService.extrairEmail(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

            if (usuario != null && jwtService.tokenValido(token, email)) {
                String empresaId = jwtService.extrairEmpresaId(token);

                List<SimpleGrantedAuthority> authorities;
                Object details;

                if (empresaId != null) {
                    authorities = List.of(new SimpleGrantedAuthority("ROLE_REPRESENTANTE"));
                    details = empresaId;
                } else {
                    var admOpt = admRepository.findByUsuarioEmail(email);
                    boolean isAdm = admOpt.map(adm -> adm.getAtivo()).orElse(false);
                    boolean isMaster = admOpt.map(adm -> adm.getIsMaster()).orElse(false);

                    if (isMaster) {
                        authorities = List.of(
                                new SimpleGrantedAuthority("ROLE_MASTER"),
                                new SimpleGrantedAuthority("ROLE_ADM")
                        );
                    } else if (isAdm) {
                        authorities = List.of(new SimpleGrantedAuthority("ROLE_ADM"));
                    } else {
                        authorities = List.of(new SimpleGrantedAuthority("ROLE_USUARIO"));
                    }
                    details = new WebAuthenticationDetailsSource().buildDetails(request);
                }

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(usuario, null, authorities);
                auth.setDetails(details);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
