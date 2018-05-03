package com.accenture.ai.model.dingdong;

import java.io.Serializable;

public class Directive implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1300976526299517975L;

	private DirectiveItem[] directiveItems;

	public DirectiveItem[] getDirectiveItems() {
		return directiveItems;
	}

	public void setDirectiveItems(DirectiveItem[] directiveItems) {
		this.directiveItems = directiveItems;
	}
	
	
}
