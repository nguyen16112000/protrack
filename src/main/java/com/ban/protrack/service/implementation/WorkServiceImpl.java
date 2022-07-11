package com.ban.protrack.service.implementation;

import com.ban.protrack.model.Project;
import com.ban.protrack.model.User;
import com.ban.protrack.model.Work;
import com.ban.protrack.model.WorkOrder;
import com.ban.protrack.repository.ProjectRepository;
import com.ban.protrack.repository.UserRepository;
import com.ban.protrack.repository.WorkRepository;
import com.ban.protrack.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static java.time.LocalDate.now;

@RequiredArgsConstructor
@Service
@Transactional
public class WorkServiceImpl implements WorkService {

    private final WorkRepository workRepo;

    private final UserRepository userRepo;

    private final ProjectRepository projectRepo;

    @Override
    public Work create(Work work) {
        return workRepo.save(work);
    }

    @Override
    public Collection<Work> list(int page, int limit) {
        return workRepo.findAll(PageRequest.of(page, limit)).toList();
    }

    @Override
    public Collection<Work> list(int page, int limit, Long id) {
        return null;
    }

    @Override
    public Work getById(Long id) {
        return null;
    }

    @Override
    public Work update(Long project_id, String work_id, Map<String, Object> request) {
        Work work = workRepo.getWorkByProject(project_id, work_id);
        if (work == null) {
            work = new Work(work_id, "", 1L);
            work.setProject(projectRepo.getById(project_id));
        }
        if (request.containsKey("name"))
            work.setName((String) request.get(("name")));
        if (request.containsKey("detail")) {
            if (Objects.equals(request.get(("detail")), "null"))
                work.setDetail("");
            else
                work.setDetail((String) request.get(("detail")));
        }
        if (request.containsKey("work_time"))
            work.setWork_time(Long.valueOf((String) request.get("work_time")));
        if (request.containsKey("es_date"))
            work.setEs_date(LocalDate.parse((String) request.get("es_date")));
        if (request.containsKey("lf_date"))
            work.setLf_date(LocalDate.parse((String) request.get("lf_date")));
        if (request.containsKey("worker")) {
            User user = userRepo.findByUsername((String) request.get("worker"));
            work.setUser(user);
        }
        workRepo.save(work);
        return work;
    }

    @Override
    public Boolean deleteById(Long id) {
        return null;
    }

    public String getWorker(String id) {
        User user = userRepo.getById(workRepo.getWorker(id));
        return user.getUsername();
    }

    public Boolean isStartable(Long project_id, String work_id) {
        Work work = workRepo.getWorkByProject(project_id, work_id);
        if (Objects.equals(work.getUser(), ""))
            return false;
        for (WorkOrder workOrder: work.getWork_before()) {
            if (!workOrder.getWork_before().isApproved())
                return false;
        }
        return true;
    }

    public Boolean startWork(Long project_id, String work_id) {
        if (isStartable(project_id, work_id)) {
            LocalDate date = now();
            Work work = workRepo.getWorkByProject(project_id, work_id);
            work.setS_date(date);
            workRepo.save(work);
            return true;
        }
        else
            return false;

    }
}
