package edu.uga.dawgtrades.model.impl;

import edu.uga.dawgtrades.model.Category;
import edu.uga.dawgtrades.model.Item;
import edu.uga.dawgtrades.model.RegisteredUser;

public class ItemImpl extends Persistent implements Item {
	
	private String name;
	private String description;
	private long ownerId;
	private long categoryId;
	
	public ItemImpl(Category category, RegisteredUser user, String identifier,
			String name, String description) {
		this.categoryId = category.getId();
		this.ownerId = user.getId();
		this.name = name;
		this.description = description;
		this.setId(Long.parseLong(identifier));
	
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public long getOwnerId() {
		return this.ownerId;
	}

	@Override
	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
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
