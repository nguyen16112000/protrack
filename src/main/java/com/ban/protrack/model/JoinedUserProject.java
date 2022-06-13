package com.ban.protrack.model;

import com.ban.protrack.enumeration.Role;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity(name="user_project")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinedUserProject {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="user_id")
    @JsonBackReference
        private User user;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @JsonBackReference
    private Project project;

    @Enumerated(EnumType.STRING)
    private Role role;

}
