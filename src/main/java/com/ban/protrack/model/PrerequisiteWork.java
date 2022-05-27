package com.ban.protrack.model;

import javax.persistence.*;

@Entity
@Table(name = "prerequisite_work", indexes = {
        @Index(name = "post_work_id_idx", columnList = "post_work_id"),
        @Index(name = "pre_work_id_idx", columnList = "pre_work_id")
})
public class PrerequisiteWork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prerequisite_work_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_work_id", nullable = false)
    private Work work;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pre_work_id", nullable = false)
    private Work work1;

    public Work getWork1() {
        return work1;
    }

    public void setWork1(Work work1) {
        this.work1 = work1;
    }

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}