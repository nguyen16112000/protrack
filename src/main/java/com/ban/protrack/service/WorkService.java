package com.ban.protrack.service;

import com.ban.protrack.model.Work;

import java.util.Collection;

public interface WorkService {
    Work create(Work work);
    Collection<Work> list(int page, int limit);
    Collection<Work> list(int page, int limit, Long id);
    Work getById(Long id);
    Work update(Work work);
    Boolean deleteById(Long id);
}
