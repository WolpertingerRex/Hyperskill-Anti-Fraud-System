package antifraud.security;

import antifraud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userService)
                .passwordEncoder(getEncoder());
    }


    public void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint()) // Handles auth error
                .and()
                .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console
                .and()
                .authorizeRequests()

               // manage access
                .antMatchers("/api/auth/access/**", "/api/auth/role/**")
                .hasRole(Role.ADMINISTRATOR.name())
                .antMatchers(HttpMethod.POST, "/api/antifraud/transaction/**")
                .hasRole(Role.MERCHANT.name())
                .antMatchers("/api/antifraud/transaction/**")
                .hasRole(Role.SUPPORT.name())
                .antMatchers("/api/antifraud/history/**")
                .hasRole(Role.SUPPORT.name())
                .antMatchers("/api/auth/list/**")
                .hasAnyRole(Role.ADMINISTRATOR.name(), Role.SUPPORT.name())
                .antMatchers(HttpMethod.DELETE, "/api/auth/user/**")
                .hasRole(Role.ADMINISTRATOR.name())
                .antMatchers("/api/antifraud/suspicious-ip/**", "/api/antifraud/stolencard/**")
                .hasRole(Role.SUPPORT.name())

                .antMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                .antMatchers("/actuator/shutdown").permitAll() // needs to run test
                .antMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()


                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session
    }
    @Bean
   public RestAuthenticationEntryPoint getRestAuthenticationEntryPoint(){
        return new RestAuthenticationEntryPoint();
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder(13);
    }

}
