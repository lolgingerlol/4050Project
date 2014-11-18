package edu.uga.dawgtrades.model.impl;

import edu.uga.dawgtrades.model.Attribute;
import edu.uga.dawgtrades.model.AttributeType;
import edu.uga.dawgtrades.model.Item;

public class AttributeImpl extends Persistent implements Attribute {
		private String value;
		private long itemId;
		private long attributeId;
	public AttributeImpl(AttributeType attributeType, Item item,
				String value) {
			this.attributeId = attributeType.getId();
			this.itemId = item.getId();
			this.value = value;
		}

	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public void setValue(String value) {
		this.value = value;

	}

	@Override
	public long getItemId() {
		return this.itemId;
	}

	@Override
	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	@Override
	public long getAttributeType() {
		return this.attributeId;
	}

	@Override
	public void setAttributeType(long attributeId) {
		this.attributeId = attributeId;
	}

}
