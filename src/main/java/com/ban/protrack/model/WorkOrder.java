package com.ban.protrack.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "work_after_id")
    @JsonBackReference
    private Work work_after;

    @ManyToOne
    @JoinColumn(name = "work_before_id")
    @JsonBackReference
    private Work work_before;

}
