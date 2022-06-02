package com.ban.protrack.service.implementation;

import com.ban.protrack.model.User;
import com.ban.protrack.repository.UserRepository;
import com.ban.protrack.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;

import static java.lang.Boolean.TRUE;

@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;

    @Override
    public User create(User user) {
        user.setImageUrl(setUserImageUrl());
        return userRepo.save(user);
    }

    @Override
    public Collection<User> list(int page, int limit) {
        return userRepo.findAll(PageRequest.of(page, limit)).toList();
    }

    @Override
    public User getById(Long id) {
        return userRepo.findById(id).get();
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
