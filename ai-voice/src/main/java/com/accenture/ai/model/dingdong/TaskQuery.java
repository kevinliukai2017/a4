package com.accenture.ai.model.dingdong;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.alibaba.da.coin.ide.spi.meta.ConversationRecord;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TaskQuery implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3007067954633577670L;

	private String versionid;
	private String status;
	private String sequence;
	private Long timestamp;
	@SerializedName("application_info")
	private ApplicationInfo applicationInfo;
	private Session session;
	private User user;
	@SerializedName("input_text")
	private String inputText;
	private Map<String, String> slots;
	
	@SerializedName("ended_reason")
	private String endedReason;
	
	
	@SerializedName("notice_type")
	private String noticeType;
	private Map<String,String> extend;
	
	@Expose(serialize = false, deserialize = false)
	private List<ConversationRecord> conversationRecords = null;

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

	public Map<String, String> getExtend() {
		return extend;
	}

	public void setExtend(Map<String, String> extend) {
		this.extend = extend;
	}

	public List<ConversationRecord> getConversationRecords() {
		return conversationRecords;
	}

	public void setConversationRecords(List<ConversationRecord> conversationRecords) {
		this.conversationRecords = conversationRecords;
	}
	
	
	

}
