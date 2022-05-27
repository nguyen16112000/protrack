package com.ban.protrack.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "user", indexes = {
        @Index(name = "username_UNIQUE", columnList = "username", unique = true)
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Integer id;

    @Column(name = "username", nullable = false, length = 70)
    private String username;

    @JsonIgnore
    @Column(name = "password", length = 70)
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<Message> messages = new LinkedHashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<Notification> notifications = new LinkedHashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<GroupMember> groupMembers = new LinkedHashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<Work> works = new LinkedHashSet<>();

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user", cascade = {CascadeType.ALL})
    private UserDetail userDetail;

    public UserDetail getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetail userDetail) {
        this.userDetail = userDetail;
    }

    public Set<Work> getWorks() {
        return works;
    }

    public void setWorks(Set<Work> works) {
        this.works = works;
    }

    public Set<GroupMember> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(Set<GroupMember> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public Set<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(Set<Notification> notifications) {
        this.notifications = notifications;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}