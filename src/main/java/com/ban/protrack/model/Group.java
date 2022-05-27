package com.ban.protrack.model;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "`group`")
public class Group {
    @Id
    @Column(name = "group_id", nullable = false, length = 10)
    private String id;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @OneToMany(mappedBy = "group")
    private Set<Message> messages = new LinkedHashSet<>();

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "group")
    private GroupMember groupMember;

    @OneToMany(mappedBy = "group")
    private Set<Work> works = new LinkedHashSet<>();

    public Set<Work> getWorks() {
        return works;
    }

    public void setWorks(Set<Work> works) {
        this.works = works;
    }

    public GroupMember getGroupMember() {
        return groupMember;
    }

    public void setGroupMember(GroupMember groupMember) {
        this.groupMember = groupMember;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}