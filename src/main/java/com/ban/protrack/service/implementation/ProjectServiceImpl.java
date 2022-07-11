package com.ban.protrack.service.implementation;

import com.ban.protrack.component.DummyWork;
import com.ban.protrack.component.Graph;
import com.ban.protrack.component.Pair;
import com.ban.protrack.model.*;
import com.ban.protrack.repository.NotificationRepository;
import com.ban.protrack.repository.ProjectRepository;
import com.ban.protrack.repository.UserRepository;
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

    private final UserRepository userRepo;

    private final WorkRepository workRepo;

    private final NotificationRepository notiRepo;

//    sowwi
    private final FilesStorageServiceImpl filesStorageService;

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

//    public boolean isAllowed(Collection<SimpleGrantedAuthority> authorities, Long id){
//        return authorities.contains(new SimpleGrantedAuthority("GROUP_" + id + "_ADMIN"))
//                || authorities.contains(new SimpleGrantedAuthority("GROUP_" + id + "_USER"));
//    };

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

    public String getUserRole(String username, Long project_id){
        return projectRepo.getUserRole(userRepo.findByUsername(username).getId(), project_id);
    }

    public boolean addUsertoProject(String username, Long project_id) {
        Long user_id = userRepo.findByUsername(username).getId();
        if (projectRepo.getUserRole(user_id, project_id) != null)
            return false;

        projectRepo.addUsertoProject(user_id, project_id, "ROLE_USER");

        return true;
    }

    @Override
    public Long addWorksToProject(String username, String project_name, LocalDate start_date, Map<String, Object> workMap, Map<String, Object> workOrderMap) {
        Long user_id = userRepo.findByUsername(username).getId();
        Project project = create(new Project(project_name));
        String project_id = String.valueOf(project.getId());
        projectRepo.addUsertoProject(user_id, project.getId(), "ROLE_ADMIN");
        workMap.forEach((key, value) -> {
//            works.add(new Work(id.toString() + "_" + key, value));
            if (value.getClass() == ArrayList.class){
                ArrayList<String> work_value = (ArrayList<String>) value;
                if (((ArrayList<?>) value).size() > 0){
                    Work work = new Work(project_id + "_" + key, work_value.get(0), Long.parseLong(work_value.get(1)));
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

    public List<Project> getProjectsOfUser(String username) {
        return projectRepo.getProjectsByUser(userRepo.findByUsername(username).getId());
    }

    public void evaluateWorkTime(Long project_id, LocalDate start_date){
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
        dummyWorks.forEach((id, dummyWork) -> {
            LocalDate es = start_date.plusDays(dummyWork.getEs());
            LocalDate ef = start_date.plusDays(dummyWork.getEf() - 1);
            LocalDate ls = start_date.plusDays(dummyWork.getLs());
            LocalDate lf = start_date.plusDays(dummyWork.getLf() - 1);
            workRepo.updateAfterEvaluate(id, es, ef, ls, lf);
        });

    }

    public LocalDate plusTimeWithoutWeekend(LocalDate start_date, int work_time){
        int days = 0;
        switch (start_date.getDayOfWeek().toString()) {
            case "SATURDAY" -> days = 2;
            case "SUNDAY" -> days = 1;
        }
        int work_days = (work_time / 5) * 7 + work_time % 5;
        return start_date.plusDays(work_days + days - 1);
    }

    public LocalDate plusTimeWithWeekend(LocalDate start_date, int work_time){
        return start_date.plusDays(work_time - 1);
    }

    public String getProofURL(Long project_id, String work_id, String username) {
        Work work = workRepo.getWorkByProject(project_id, work_id);
        Project project = work.getProject();
        String user = work.getUser();
        if (Objects.equals(project.getId(), project_id) && Objects.equals(username, user)) {
            if (work.getProof() == null)
                return "";
            return work.getProof();
        }
        return null;
    }

    public String getProofURL(Long project_id, String work_id, Boolean is_admin) {
        Work work = workRepo.getWorkByProject(project_id, work_id);
        if (work.getProof() == null)
            return "";
        return work.getProof();
    }

    public void setProofURL(Long project_id, String work_id, String fileName, String worker) {
        Work work = workRepo.getWorkByProject(project_id, work_id);
        work.setProof(fileName);
        workRepo.save(work);
        User admin = userRepo.getById(getProjectAdmin(project_id));
        User user = userRepo.findByUsername(worker);
        notiRepo.save(new Notification(admin, worker + " had submitted their works for work: " + work.getName()));
//        notiRepo.save(new Notification(admin, worker + " had submitted their works for work: " + work.getName(), "approval/" + project_id + "/" + work_id + "/" + user.getId(), 0));
    }

    public void approveWork(Long project_id, String work_id, Boolean accept) {
        Work work = workRepo.getWorkByProject(project_id, work_id);
        if (accept) {
            work.setF_date(LocalDate.now());
            work.setApproved(true);
        }
        else {
            String file = work.getProof();
            work.setProof(null);
            User user = userRepo.findByUsername(work.getUser());
            notiRepo.save(new Notification(user, "Project admin declined your submit for work: " + work.getName()));
            try {
                filesStorageService.delete(file);
            }
            catch (Exception ex) {
                System.out.println("File not found");
            }
        }
        workRepo.save(work);
//        notificationRepo.save(new Notification(sender, username + " accepted your work."));
    }

    public Long getProjectAdmin(Long project_id) {
        return projectRepo.getProjectAdmin(project_id);
    }

}
