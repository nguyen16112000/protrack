package com.ban.protrack.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDate;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String message;

    private LocalDate createdAt;

    private boolean isRead;

    private String type;

    @JsonIgnore
    private Integer status;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="user_id")
    @JsonBackReference
    private User user;

    public Notification(String message) {
        this.message = message;
        this.createdAt = LocalDate.now();
        this.isRead = false;
    }

    public Notification(User user, String message) {
        this.message = message;
        this.createdAt = LocalDate.now();
        this.isRead = false;
        this.user = user;
    }

    public Notification(User user, String message, String type, Integer status) {
        this.message = message;
        this.createdAt = LocalDate.now();
        this.isRead = false;
        this.user = user;
        this.type = type;
        this.status = status;
    }

    @JsonGetter("type")
    public String getProcessedType(){
        if (type == null) return "";
        if (type.contains("/"))
            return type.split("/")[0];
        return type;
    }
}
