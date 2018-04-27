package com.accenture.ai.model.dingdong;

import java.io.Serializable;

public class ApplicationInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7360032179903988543L;

	private String applicationName;
	private String applicationId;
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
