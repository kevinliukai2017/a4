package com.accenture.ai.model.dingdong;

import java.io.Serializable;
import java.util.Map;

public class TaskQuery implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3007067954633577670L;

	private String versionid;
	private String status;
	private String sequence;
	private Long timestamp;
	private ApplicationInfo applicationInfo;
	private Session session;
	private User user;
	private String inputText;
	private Map<String, String> slots;
	private String endedReason;
	private String noticeType;

	public String getVersionid() {
		return versionid;
	}

	public void setVersionid(String versionid) {
		this.versionid = versionid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public ApplicationInfo getApplicationInfo() {
		return applicationInfo;
	}

	public void setApplicationInfo(ApplicationInfo applicationInfo) {
		this.applicationInfo = applicationInfo;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getInputText() {
		return inputText;
	}

	public void setInputText(String inputText) {
		this.inputText = inputText;
	}

	public Map<String, String> getSlots() {
		return slots;
	}

	public void setSlots(Map<String, String> slots) {
		this.slots = slots;
	}

	public String getEndedReason() {
		return endedReason;
	}

	public void setEndedReason(String endedReason) {
		this.endedReason = endedReason;
	}

	public String getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(String noticeType) {
		this.noticeType = noticeType;
	}

}
