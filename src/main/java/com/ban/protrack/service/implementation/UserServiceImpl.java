package com.ban.protrack.service.implementation;

import com.ban.protrack.model.User;
import com.ban.protrack.repository.UserRepository;
import com.ban.protrack.service.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.PropertyValueException;
import org.hibernate.exception.ConstraintViolationException;
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
import java.util.Map;
import java.util.Objects;

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

    public void updatePassword(Map<String, String> request) {
        if (request.containsKey("username")) {
            User user = userRepo.findByUsername(request.get("username"));
            if (user == null)
                throw new PropertyValueException("Username or password error", "request", "");
            if (request.containsKey("old_password") && request.containsKey("new_password")){
                if (passwordEncoder.matches(request.get("old_password"), user.getPassword())) {
                    user.setPassword(passwordEncoder.encode(request.get("new_password")));
                    userRepo.save(user);
                    return;
                }
                else
                    throw new PropertyValueException("Username or password error", "request", "");
            }
            else if (request.containsKey("new_password")){
                boolean auth = false, has_value = false;
                if (request.containsKey("email"))
                    auth = Objects.equals(user.getEmail(), request.get("email"));

                if (request.containsKey("phone"))
                    auth = auth && Objects.equals(user.getPhone(), request.get("phone"));

                if (auth) {
                    user.setPassword(passwordEncoder.encode(request.get("new_password")));
                    return;
                }
                else
                    throw new PropertyValueException("Username or password error", "request", "");
            }
        }
        throw new PropertyValueException("Missing properties", "request", "username");
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

    public String register(String username, String password, String email, String phone) {
        if (userRepo.findByUsername(username) != null){
            throw new ConstraintViolationException("Username already existed", null, null);
        }
        userRepo.save(new User(username, passwordEncoder.encode(password), email, phone));
        return "Register successfully";
    }
}
