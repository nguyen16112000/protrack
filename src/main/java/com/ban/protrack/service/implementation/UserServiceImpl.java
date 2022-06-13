package com.ban.protrack.service.implementation;

import com.ban.protrack.model.User;
import com.ban.protrack.repository.UserRepository;
import com.ban.protrack.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;

import static java.lang.Boolean.TRUE;

@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        if(user == null)
            throw  new UsernameNotFoundException("Username not found");
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().getRole()));
        user.getUserProject().forEach(project -> {
            authorities.add(new SimpleGrantedAuthority(
                    "GROUP_" + project.getProject().getId()
                            + "_" + project.getRole().getRole().substring("ROLE_".length())));
        });
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public User create(User user) {
        user.setImageUrl(setUserImageUrl());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Override
    public Collection<User> list(int page, int limit) {
        return userRepo.findAll(PageRequest.of(page, limit)).toList();
    }

    @Override
    public User getById(Long id) {
        return userRepo.getById(id);
    }

    @Override
    public User getByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public User update(User user) {
        return userRepo.save(user);
    }

    @Override
    public Boolean deleteById(Long id) {
        userRepo.deleteById(id);
        return TRUE;
    }

    @Override
    public Boolean deleteByUsername(String username) {
        userRepo.delete(userRepo.findByUsername(username));
        return TRUE;
    }

    private String setUserImageUrl() {
        return null;
    }

}
