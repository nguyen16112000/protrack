package com.ban.protrack.model;

import javax.persistence.*;

@Entity
@Table(name = "user_detail")
public class UserDetail {
    @Id
    @Column(name = "user_id", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob
    @Column(name = "avatar_img")
    private String avatarImg;

    @Column(name = "email", length = 70)
    private String email;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "is_super")
    private Boolean isSuper;

    public Boolean getIsSuper() {
        return isSuper;
    }

    public void setIsSuper(Boolean isSuper) {
        this.isSuper = isSuper;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarImg() {
        return avatarImg;
    }

    public void setAvatarImg(String avatarImg) {
        this.avatarImg = avatarImg;
    }

//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }

    protected Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        this.id = id;
    }
}