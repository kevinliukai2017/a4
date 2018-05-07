package com.accenture.ai.model.dingdong;

import java.io.Serializable;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class TaskResult implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3502612342627394493L;
	
	private String versionid;
	@SerializedName("is_end")
	private Boolean isEnd;
	private String sequence;
	private Long timestamp;
	@SerializedName("need_slot")
	private String needSlot;
	private Directive directive;
	@SerializedName("push_to_app")
	private Card pushToApp;
	@SerializedName("repeat_directive")
	private Directive repeatDirective;
	private Map<String,String> extend;
	public String getVersionid() {
		return versionid;
	}
	public void setVersionid(String versionid) {
		this.versionid = versionid;
	}
	public Boolean getIsEnd() {
		return isEnd;
	}
	public void setIsEnd(Boolean isEnd) {
		this.isEnd = isEnd;
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
	public String getNeedSlot() {
		return needSlot;
	}
	public void setNeedSlot(String needSlot) {
		this.needSlot = needSlot;
	}
	public Directive getDirective() {
		return directive;
	}
	public void setDirective(Directive directive) {
		this.directive = directive;
	}
	public Card getPushToApp() {
		return pushToApp;
	}
	public void setPushToApp(Card pushToApp) {
		this.pushToApp = pushToApp;
	}
	public Directive getRepeatDirective() {
		return repeatDirective;
	}
	public void setRepeatDirective(Directive repeatDirective) {
		this.repeatDirective = repeatDirective;
	}
	public Map<String, String> getExtend() {
		return extend;
	}
	public void setExtend(Map<String, String> extend) {
		this.extend = extend;
	}
	

}
