package edu.uga.dawgtrades.model.impl;

import edu.uga.dawgtrades.model.AttributeType;

public class AttributeTypeImpl extends Persistent implements AttributeType {
	private String name;
	private long categoryId;
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public long getCategoryId() {
		return this.categoryId;
	}

	@Override
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

}
