package com.ban.protrack.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.*;

import javax.persistence.*;
import java.util.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String name;


    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Collection<JoinedUserProject> userProject;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Collection<Work> works;

    public Project(String name) {
        this.name = name;
    }

    @JsonGetter("userProject")
    public Map<String, String> getUserProject(){
        Map<String, String> up = new HashMap<>();
        userProject.forEach(item -> {
            up.put(item.getUser().getUsername(), item.getRole().toString());
        });
        return up;
    }



    @JsonGetter("works")
    public List<Work> getWorks(){
        List<Work> workList = (List<Work>) works;
        Comparator<Work> esDateComparator = new Comparator<Work>() {
            @Override
            public int compare(Work w1, Work w2) {
//                if (w1.getEs_date().isEqual(w2.getEs_date()))
//                    return w1.getWork_time().compareTo(w2.getWork_time());
//                return (w1.getEs_date()).compareTo(w2.getEs_date());
                return (w1.getId().compareTo(w2.getId()));
            }
        };
        workList.sort(esDateComparator);
        return workList;
    }

}
