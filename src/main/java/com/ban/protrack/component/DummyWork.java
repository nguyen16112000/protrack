package com.ban.protrack.component;

import com.ban.protrack.model.Work;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DummyWork {
    private Long work_time;
    private Long es, ef, ls, lf;

    public DummyWork(Work work){
        this.work_time = work.getWork_time();
        this.es = 0L;
        this.ef = 0L;
        this.lf = 0L;
        this.ls = 0L;
    }

    @Override
    public String toString() {
        return "Work{" +
                "t=" + work_time +
                ", es=" + es +
                ", ef=" + ef +
                ", ls=" + ls +
                ", lf=" + lf +
                '}';
    }
}
