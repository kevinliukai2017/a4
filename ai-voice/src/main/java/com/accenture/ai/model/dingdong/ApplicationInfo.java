package com.accenture.ai.model.dingdong;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class ApplicationInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7360032179903988543L;

	@SerializedName("application_name")
	private String applicationName;
	@SerializedName("application_id")
	private String applicationId;
	@SerializedName("application_version")
	private String applicationVersion;

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getApplicationVersion() {
		return applicationVersion;
	}

	public void setApplicationVersion(String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}

}
