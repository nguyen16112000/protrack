package com.ban.protrack.service;

import com.ban.protrack.model.Work;

import java.util.Collection;
import java.util.Map;

public interface WorkService {
    Work create(Work work);
    Collection<Work> list(int page, int limit);
    Collection<Work> list(int page, int limit, Long id);
    Work getById(Long id);
    Work update(Long project_id, String work_id, Map<String, Object> request);
    Boolean deleteById(Long id);
}
