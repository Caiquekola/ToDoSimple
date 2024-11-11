package com.caiquekola.todosimple.services;

import com.caiquekola.todosimple.models.User;
import com.caiquekola.todosimple.repositories.UserRepository;
import com.caiquekola.todosimple.security.UserSpringSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByUsername(username);
        if(Objects.isNull(user)){
            throw new UsernameNotFoundException(username);
        }

        return new UserSpringSecurity(user.getId(),user.getUsername(), user.getPassword(),user.getProfiles());
    }
}
