package com.company.enroller.security;

import com.company.enroller.App;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/participants").permitAll()
                .antMatchers("/tokens").permitAll()
                .antMatchers("/**").authenticated()
                .and()
                .addFilterBefore(
                        new JWTAuthenticationFilter(
                                authenticationManager(),
                                secret,
                                issuer,
                                tokenExpiration),
                        UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), secret));
    }

    @Autowired
    private ParticipantProvider participantProvider;
    @Autowired
    private App app;

    @Value("${security.secret}")
    String secret;
    @Value("${security.issuer}")
    String issuer;
    @Value("${security.token_expiration_in_seconds}")
    int tokenExpiration;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(participantProvider)
                .passwordEncoder(app.passwordEncoder());
    }
}