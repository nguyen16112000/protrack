package com.ban.protrack.service;

import com.ban.protrack.model.Project;
import com.ban.protrack.model.Work;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ProjectService {
    Project create(Project project);
    Collection<Project> list(int page, int limit);
    Project getById(Long id);
    Project update(Project project);
    Boolean deleteById(Long id);
    Work getWorkofProject(Long project_id, String work_id);

    List<Work> getWorksofProject(Long project_id);

    Long addWorksToProject(Map<String, Object> workMap, Map<String, Object> workOrderMap);
}
