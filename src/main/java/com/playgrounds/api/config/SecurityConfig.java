package com.playgrounds.api.config;

import com.playgrounds.api.user.repository.UserRepository;
import com.playgrounds.api.user.repository.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;


@Configuration
@EnableWebSecurity
@ComponentScan
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public UserRepository userRepository;

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth
            .userDetailsService(new UserAuthService(userRepository));
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/playgrounds/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .httpBasic().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }

}
