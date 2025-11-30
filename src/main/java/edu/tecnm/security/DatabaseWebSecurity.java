package edu.tecnm.security;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class DatabaseWebSecurity {

	@Bean
    public UserDetailsManager usuarios(DataSource dataSource) {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        
        // 1. Query para buscar al usuario por username
        // Busca: nombre de usuario, contraseña y si está activo (estatus)
        users.setUsersByUsernameQuery(
            "select username, password, estatus from Usuario where username = ?"
        );
        
        // 2. Query para buscar los perfiles (roles)
        // Hace los JOINs entre Usuario -> UsuarioPerfil -> Perfil
        users.setAuthoritiesByUsernameQuery(
            "select u.username, p.perfil from UsuarioPerfil up " +
            "inner join Usuario u on u.id = up.idusuario " +
            "inner join Perfil p on p.id = up.idperfil " +
            "where u.username = ?"
        );
        
        return users;
    }
    
	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            
            // 1. RECURSOS ESTÁTICOS (Públicos)
            .requestMatchers(
                "/css/**", 
                "/imagen/**", 
                "/imagenrestaurant/**",
                "/producto-images/**"
            ).permitAll()
            
            // 2. PÁGINAS PÚBLICAS (Sin loguearse)
            .requestMatchers(
                "/",                            // -> InicioRestaurante.html
                "/login",                       // -> login.html
                "/productos",                   // -> InicioRestaurante.html
                "/productos/detalles/**",       
                "/usuarios/crear",              // -> Formulario de registro público
                "/usuarios/guardar"             // -> Endpoint de registro público
            ).permitAll()
            

            // REGLA: CLIENTE
            // Cliente puede crear y ver la lista (sus propias reservaciones)
            .requestMatchers("/reservaciones/crear", "/reservaciones", "/reservaciones/guardar")
                .hasAnyAuthority("ADMIN", "CAJERO", "CLIENTE")

            // REGLA: CAJERO
            // Gestiona Clientes, Ventas y Reservaciones
            .requestMatchers("/lista", "/crear", "/busquedas", "/reservaciones/**")
                .hasAnyAuthority("ADMIN", "CAJERO")

            // REGLA: MESERO / COCINERO
            // Ven pedidos, Mesero puede editar
            .requestMatchers("/pedidos/**")
                .hasAnyAuthority("ADMIN", "CAJERO", "COCINERO", "MESERO", "CLIENTE")
                
                .requestMatchers(HttpMethod.POST, "/empleados/guardar")
                .hasAnyAuthority("ADMIN", "SUPERVISOR",
                                 "CAJERO", "COCINERO", "MESERO", "EMPLEADO")

            // REGLA: SUPERVISOR
            // Mesas, Productos, Empleados
            .requestMatchers("/mesas/**", "/empleados/**", "/crearProducto", "/productos/admin")
                .hasAnyAuthority("ADMIN", "SUPERVISOR")
            
            // REGLA: ADMIN
            .requestMatchers("/usuarios/**")
                .hasAuthority("ADMIN")
                
             // ACCIONES ADMINISTRATIVAS DE RESERVACIONES (Confirmar, Modificar, Cancelar)
                // Estas deben ir ANTES de la regla general para que tengan prioridad.
                .requestMatchers(
                    "/reservaciones/confirmar/**", 
                    "/reservaciones/modificar/**", 
                    "/reservaciones/cancelar/**",
                    "/reservaciones/eliminar/**"
                ).hasAnyAuthority("ADMIN", "CAJERO")

                
            
            // 4. REGLA FINAL: Cierra todo lo demás
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/", true)
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout")
            .permitAll()
        );

        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}