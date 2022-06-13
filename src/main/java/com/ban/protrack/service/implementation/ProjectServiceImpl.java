package com.ban.protrack.service.implementation;

import com.ban.protrack.component.DummyWork;
import com.ban.protrack.component.Graph;
import com.ban.protrack.component.Pair;
import com.ban.protrack.model.Project;
import com.ban.protrack.model.Work;
import com.ban.protrack.model.WorkOrder;
import com.ban.protrack.repository.ProjectRepository;
import com.ban.protrack.repository.WorkRepository;
import com.ban.protrack.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.hibernate.PropertyValueException;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;

import static java.lang.Boolean.TRUE;

@RequiredArgsConstructor
@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepo;

    private final WorkRepository workRepo;

    @Override
    public Project create(Project project) {
        return projectRepo.save(project);
    }

    @Override
    public Collection<Project> list(int page, int limit) {
        return projectRepo.findAll(PageRequest.of(page, limit)).toList();
    }

    @Override
    public Project getById(Long id){
        return projectRepo.findById(id).get();
    }

    @Override
    public Project update(Project project) {
        return projectRepo.save(project);
    }

    @Override
    public Boolean deleteById(Long id) {
        projectRepo.deleteById(id);
        return TRUE;
    }

    public boolean isAllowed(Collection<SimpleGrantedAuthority> authorities, Long id){
        return authorities.contains(new SimpleGrantedAuthority("GROUP_" + id + "_ADMIN"))
                || authorities.contains(new SimpleGrantedAuthority("GROUP_" + id + "_USER"));
    };

    @Override
    public Work getWorkofProject(Long project_id, String work_id) {
        Work work = workRepo.getWorkByProject(project_id, work_id);
        if (work == null) {
            throw new PropertyValueException("Work do not belong to project", null, null);
        }
        return work;
    }

    @Override
    public List<Work> getWorksofProject(Long project_id) {
        return workRepo.getWorksByProject(project_id);
    }

    @Override
    public Long addWorksToProject(Map<String, Object> workMap, Map<String, Object> workOrderMap) {
        Project project = create(new Project());
        String project_id = String.valueOf(project.getId());
        workMap.forEach((key, value) -> {
//            works.add(new Work(id.toString() + "_" + key, value));
            if (value.getClass() == ArrayList.class){
                ArrayList<String> work_value = (ArrayList<String>) value;
                if (((ArrayList<?>) value).size() == 1){
                    Work work = new Work(project_id + "_" + key,  Long.parseLong(work_value.get(0)));
                    work.setProject(project);
                    workRepo.save(work);
                }
            }
        });
        workOrderMap.forEach((key, value) -> {
            if (value.getClass() == ArrayList.class){
                ((ArrayList<?>) value).forEach(work_before -> {
                    workRepo.addWorkOrdertoProject(project_id + "_" + work_before.toString(), project_id + "_" + key);
                });
            }
        });
//        evaluateWorkTime(id); somehow get error while doing fetch eager
        return project.getId();
    }

    public void evaluateWorkTime(Long project_id){
        List<Work> works = workRepo.getWorksByProject(project_id);

        Set<String> check = new HashSet<>();
        Graph<String> graph = new Graph<>();
        for (Work work : works) {
            work.getWork_after().forEach(workOrder -> {
                graph.addVertex(workOrder.getWork_before().getId(), workOrder.getWork_after().getId());
                check.add(workOrder.getWork_after().getId());
            });
        }
        works.forEach(work -> {
            if (!check.contains(work.getId()))
                graph.addVertex("0", work.getId());
        });

        Map<String, DummyWork> dummyWorks = new HashMap<>();
        dummyWorks.put("0", new DummyWork(0L, 0L, 0L, 0L, 0L));
        works.forEach(work -> {
            dummyWorks.put(work.getId(), new DummyWork(work));
        });

        Long total_work_time = 0L;
        LinkedList<String> startQueue = new LinkedList<String>();
        LinkedList<Pair<String, String>> finishStack = new LinkedList<Pair<String, String>>();

        startQueue.addLast("0");
        while (startQueue.size() != 0){
            String u = startQueue.removeFirst();
            dummyWorks.get(u).setEf(dummyWorks.get(u).getEs() + dummyWorks.get(u).getWork_time());
            total_work_time = total_work_time < dummyWorks.get(u).getEf() ? dummyWorks.get(u).getEf() : total_work_time;
            for (String v: graph.getVertex(u)){
                startQueue.addLast(v);
                finishStack.addFirst(new Pair<>(u, v));
                if (dummyWorks.get(v).getEs() < dummyWorks.get(u).getEf())
                    dummyWorks.get(v).setEs(dummyWorks.get(u).getEf());
            }
        }
        for (DummyWork u: dummyWorks.values())
            u.setLf(total_work_time);

        while (finishStack.size() != 0) {
            Pair<String, String> uv = finishStack.removeFirst();
            String u = uv.getFi(), v = uv.getSe();
            dummyWorks.get(v).setLs(dummyWorks.get(v).getLf() - dummyWorks.get(v).getWork_time());
            if (dummyWorks.get(u).getLf() > dummyWorks.get(v).getLs()){
                dummyWorks.get(u).setLf(dummyWorks.get(v).getLs());
                dummyWorks.get(u).setLs(dummyWorks.get(u).getLf() - dummyWorks.get(u).getWork_time());
            }
        }
        dummyWorks.remove("0");
        LocalDate start_time = LocalDate.now();
        dummyWorks.forEach((id, dummyWork) -> {
            LocalDate es = start_time.plusDays(dummyWork.getEs());
            LocalDate ef = start_time.plusDays(dummyWork.getEf() - 1);
            LocalDate ls = start_time.plusDays(dummyWork.getLs());
            LocalDate lf = start_time.plusDays(dummyWork.getLf() - 1);
            workRepo.updateAfterEvaluate(id, es, ef, ls, lf);
        });

    }


}
