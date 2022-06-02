package com.ban.protrack.service;

import com.ban.protrack.model.User;

import java.util.Collection;

public interface UserService {
    User create(User user);
    Collection<User> list(int page, int limit);
    User getById(Long id);
    User getByUsername(String username);
    User update(User user);
    Boolean deleteById(Long id);
    Boolean deleteByUsername(String username);
}
