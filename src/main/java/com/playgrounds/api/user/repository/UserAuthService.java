package com.playgrounds.api.user.repository;

import com.playgrounds.api.user.model.User;
import com.playgrounds.api.user.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;


public class UserAuthService implements UserDetailsService {
    private UserRepository userRepository;

    public UserAuthService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findById(username);
        if(user != null){
            List<GrantedAuthority> authorities =
                    new ArrayList<GrantedAuthority>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            return new org.springframework.security.core.userdetails.User(
                    user.getId(),
                    "password",
                    authorities
            );
        }
        throw new UsernameNotFoundException(
                "User '" + username + "' not found.");
    }
}
