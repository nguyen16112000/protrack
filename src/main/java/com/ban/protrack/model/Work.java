package com.ban.protrack.model;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Work {
    @Id
    @Column(unique = true, nullable = false)
    private String id;

    private String name;

    private String detail;

    private Long work_time;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate es_date;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ef_date;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ls_date;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lf_date;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate s_date;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate f_date;

//    File storage
    private String proof;

    private boolean approved;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @JsonBackReference
    private Project project;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="user_id")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "work_before")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Collection<WorkOrder> work_after;

    @OneToMany(mappedBy = "work_after")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Collection<WorkOrder> work_before;

    public Work(String id, Long work_time){
        this.id = id;
        this.work_time = work_time;
    }

    public Work(String id, LocalDate es, LocalDate ef, LocalDate ls, LocalDate lf, Long work_time){
        this.id = id;
        this.es_date = es;
        this.ef_date = ef;
        this.ls_date = ls;
        this.lf_date = lf;
        this.work_time = work_time;
    }

    @JsonGetter("work_after")
    public Collection<String> getWorkAfter(){
        Collection<String> workafter = new ArrayList<>();
        work_after.forEach(work -> {
            workafter.add(work.getWork_after().getId());
        });
        return workafter;
    }

    @JsonGetter("work_before")
    public Collection<String> getWorkBefore(){
        Collection<String> workbefore = new ArrayList<>();
        work_before.forEach(work -> {
            workbefore.add(work.getWork_before().getId());
        });
        return workbefore;
    }

    @Override
    public String toString(){
        return "Work{" +
                "id=" + id +
                ", t=" + work_time +
                ", es=" + es_date +
                ", ef=" + ef_date +
                ", ls=" + ls_date +
                ", lf=" + lf_date +
                '}';
    }

}
