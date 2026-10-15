package com.pedidos360.rabbitadmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.SecurityFilterChain;
@Configuration public class SecurityConfig {
 @Bean SecurityFilterChain security(HttpSecurity http) throws Exception {
  return http.csrf(csrf->csrf.disable()).authorizeHttpRequests(a->a.anyRequest().hasRole("ADMIN"))
    .httpBasic(org.springframework.security.config.Customizer.withDefaults()).build();
 }
 @Bean UserDetailsService users(@Value("${admin.user}") String user,@Value("${admin.password}") String pass) {
  return new InMemoryUserDetailsManager(User.withUsername(user)
   .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(pass)).roles("ADMIN").build());
 }
}
