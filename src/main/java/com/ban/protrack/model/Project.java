package com.ban.protrack.model;

import lombok.*;

import javax.persistence.*;
import java.util.Collection;

@Entity(name="project")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Collection<JoinedUserProject> userProject;

}
