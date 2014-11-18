package edu.uga.dawgtrades.persist.impl;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.NoSuchElementException;


import edu.uga.dawgtrades.model.DTException;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.Attribute;

public class AttributeIterator implements Iterator<Attribute> {
	
	private ResultSet rs = null;
	private ObjectModel objectModel = null;
	private boolean more = false;
	
	public AttributeIterator(ResultSet rs, ObjectModel objectModel) throws DTException{
		this.rs = rs;
		this.objectModel = objectModel;
		try{
			more = rs.next();
		}
		catch(Exception e){
			throw new DTException("AttributeIterator: Cannot create Attribute iterator; Root cause: " + e);
		}
	}
	
	@Override
	public boolean hasNext(){
		return more;
	}
	
	@Override
	public Attribute next(){
		long id;
		String value;
		long itemId;
		long attributeTypeId;
		
		if(more){
			try{
				id = rs.getLong(1);
				value = rs.getString(2);
				itemId = rs.getLong(3);
				attributeTypeId = rs.getLong(4);
				
				more = rs.next();
			}
			catch(Exception e){
				throw new NoSuchElementException("AttributeIterator: No next Attribute object; Root cause " + e);
			}
			
			Attribute attribute = objectModel.createAttribute();
			attribute.setId(id);
			attribute.setValue(value);
			attribute.setItemId(itemId);
			attribute.setAttributeType(attributeTypeId);
			
			return attribute;
		}
		else
			throw new NoSuchElementException("AttributeIterator: No nextt Attribute object");
	}
	
	@Override
	public void remove(){
		throw new UnsupportedOperationException();
	}
}
 