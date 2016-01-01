package top.rainj2013.bean;

import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

@Table(value = "user")
public class User {
	@Column
	private String uid;
	@Column
	private String username;
	@Column
	private String bio;
	@Column
	private String employment;
	@Column
	private String position;
	@Column
	private String education;
	@Column
	private String edu_extra;
	@Column
	private String followees;
	@Column
	private String followers;
	@Column
	private String asks;
	@Column
	private String answers;
	@Column
	private String posts;
	@Column
	private String collections;
	@Column
	private String logs;
	@Column
	private Date captureTime;
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public String getEmployment() {
		return employment;
	}
	public void setEmployment(String employment) {
		this.employment = employment;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	public String getEdu_extra() {
		return edu_extra;
	}
	public void setEdu_extra(String edu_extra) {
		this.edu_extra = edu_extra;
	}
	public String getFollowees() {
		return followees;
	}
	public void setFollowees(String followees) {
		this.followees = followees;
	}
	public String getFollowers() {
		return followers;
	}
	public void setFollowers(String followers) {
		this.followers = followers;
	}
	public String getAsks() {
		return asks;
	}
	public void setAsks(String asks) {
		this.asks = asks;
	}
	public String getAnswers() {
		return answers;
	}
	public void setAnswers(String answers) {
		this.answers = answers;
	}
	public String getPosts() {
		return posts;
	}
	public void setPosts(String posts) {
		this.posts = posts;
	}
	public String getCollections() {
		return collections;
	}
	public void setCollections(String collections) {
		this.collections = collections;
	}
	public String getLogs() {
		return logs;
	}
	public void setLogs(String logs) {
		this.logs = logs;
	}
	public Date getCaptureTime() {
		return captureTime;
	}
	public void setCaptureTime(Date captureTime) {
		this.captureTime = captureTime;
	}
	
	public User(){}
	public User(String uid, String username, String bio, String employment, String position, String education,
			String edu_extra, String followees, String followers, String asks, String answers, String posts,
			String collections, String logs) {
		super();
		this.uid = uid;
		this.username = username;
		this.bio = bio;
		this.employment = employment;
		this.position = position;
		this.education = education;
		this.edu_extra = edu_extra;
		this.followees = followees;
		this.followers = followers;
		this.asks = asks;
		this.answers = answers;
		this.posts = posts;
		this.collections = collections;
		this.logs = logs;
		this.captureTime = new Date();
	}
	@Override
	public String toString() {
		return "User [uid=" + uid + ", username=" + username + ", bio=" + bio + ", employment=" + employment
				+ ", position=" + position + ", education=" + education + ", edu_extra=" + edu_extra + ", followees="
				+ followees + ", followers=" + followers + ", asks=" + asks + ", answers=" + answers + ", posts="
				+ posts + ", collections=" + collections + ", logs=" + logs + "]";
	}
}
