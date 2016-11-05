package com.pangbohao.server.db.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * The persistent class for the auth_user database table.
 * 
 */
@Entity
@Table(name = "auth_user")
@NamedQuery(name = "AuthUser.findAll", query = "SELECT a FROM AuthUser a")
public class AuthUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_joined")
	private Date dateJoined;

	@Column(name = "email")
	private String email = "";

	@Column(name = "nickname")
	private String nickname = "";

	@Column(name = "is_active")
	private Boolean isActive = true;

	@Column(name = "is_staff")
	private Boolean isStaff = false;

	@Column(name = "is_superuser")
	private Boolean isSuperuser = false;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_login")
	private Date lastLogin;

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String pass;

	@Column(name = "userKey")
	private String userKey;

	@Column(name = "sex")
	private int sex;

	@Column(name = "token")
	private String token;

	@Column(name = "birth")
	private String birth;

	@Column(name = "headimgurl")
	private String headimgurl;

	@Column(name = "registedfrom")
	private String registedfrom;

	@Transient
	private Boolean isNewuser;

	private String openId;

	public AuthUser() {
	}

	public Integer getId() {
		return id;
	}

	public Date getDateJoined() {
		return this.dateJoined;
	}

	public void setDateJoined(Date dateJoined) {
		this.dateJoined = dateJoined;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsStaff() {
		return isStaff;
	}

	public void setIsStaff(Boolean isStaff) {
		this.isStaff = isStaff;
	}

	public Boolean getIsSuperuser() {
		return isSuperuser;
	}

	public void setIsSuperuser(Boolean isSuperuser) {
		this.isSuperuser = isSuperuser;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getLastLogin() {
		return this.lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public Boolean getActive() {
		return isActive;
	}

	public void setActive(Boolean active) {
		isActive = active;
	}

	public Boolean getStaff() {
		return isStaff;
	}

	public void setStaff(Boolean staff) {
		isStaff = staff;
	}

	public Boolean getSuperuser() {
		return isSuperuser;
	}

	public void setSuperuser(Boolean superuser) {
		isSuperuser = superuser;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserKey() {
		return userKey;
	}

	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public String getRegistedfrom() {
		return registedfrom;
	}

	public void setRegistedfrom(String registedfrom) {
		this.registedfrom = registedfrom;
	}

	public Boolean getNewuser() {
		return isNewuser;
	}

	public void setNewuser(Boolean newuser) {
		isNewuser = newuser;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}
}