package edu.uga.dawgtrades.persist.impl;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.uga.dawgtrades.model.DTException;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.AttributeType;

public class AttributeTypeIterator implements Iterator<AttributeType>{

	private ResultSet rs = null;
	private ObjectModel objectModel = null;
	private boolean more = false;
	
	public AttributeTypeIterator(ResultSet rs, ObjectModel objectModel) throws DTException{
		this.rs = rs;
		this.objectModel = objectModel;
		try{
			more = rs.next();
		}
		catch(Exception e){
			throw new DTException("AttributeTypeIterator: Cannot create Attribute iterator; Root cause: " + e);
		}
	}
	
	@Override
	public boolean hasNext(){
		return more;
	}
	
	@Override
	public AttributeType next(){
		long id;
		String name;
		long categoryId;
		
		if(more){
			try{
				id = rs.getLong(1);
				name = rs.getString(2);
				categoryId = rs.getLong(3);
							
				more = rs.next();
			}
			catch(Exception e){
				throw new NoSuchElementException("AttributeTypeIterator: No next AttributeType object; Root cause " + e);
			}
			
			AttributeType attributeType = objectModel.createAttributeType();
			attributeType.setId(id);
			attributeType.setName(name);
			attributeType.setCategoryId(categoryId);
			
			return attributeType;
		}
		else
			throw new NoSuchElementException("AttributeTypeIterator: No next AttributeType object");
	}
	
	@Override
	public void remove(){
		throw new UnsupportedOperationException();
	}
}
