package com.ban.protrack.service.implementation;

import com.ban.protrack.model.Work;
import com.ban.protrack.repository.WorkRepository;
import com.ban.protrack.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Collection;

import static java.time.LocalDate.now;

@RequiredArgsConstructor
@Service
@Transactional
public class WorkServiceImpl implements WorkService {

    private final WorkRepository workRepo;

    @Override
    public Work create(Work work) {
        plusTimeWithoutWeekend(now(), 1);
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
    public Work update(Work work) {
        return null;
    }

    @Override
    public Boolean deleteById(Long id) {
        return null;
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
}
