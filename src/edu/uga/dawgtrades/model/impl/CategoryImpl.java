package edu.uga.dawgtrades.model.impl;

import edu.uga.dawgtrades.model.Category;

public class CategoryImpl extends Persistent implements Category {
	
	private String name;
	private long parentId;
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;

	}

	@Override
	public long getParentId() {
		return this.parentId;
	}

	@Override
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

}
