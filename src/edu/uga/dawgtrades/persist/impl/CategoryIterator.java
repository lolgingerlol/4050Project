package edu.uga.dawgtrades.persist.impl;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.uga.dawgtrades.model.Category;
import edu.uga.dawgtrades.model.DTException;
import edu.uga.dawgtrades.model.ObjectModel;

public class CategoryIterator implements Iterator<Category>{
	
    private ResultSet   rs = null;
    private ObjectModel objectModel = null;
    private boolean     more = false;
	
	public CategoryIterator( ResultSet rs, ObjectModel objectModel ) throws DTException {
		this.rs = rs;
		this.objectModel = objectModel;
		
		try {
			more = rs.next();
		} catch (Exception e) {
			throw new DTException("CategoryIterator: Cannot create auction. Root cause: " + e);
		}
		
	}//end constructor

	@Override
	public boolean hasNext() {
		return more;
	}//end hasNext()

	@Override
	public Category next() {
		
		if (more) {
			String name;
			long parentId;;
			long ID;
			
			try {
				ID = rs.getLong(1);
				name = rs.getString(2);
				parentId = rs.getLong(3);
				
				more = rs.next();
			} catch (Exception e) {
				throw new NoSuchElementException( "CategoryIterator: No next Category object. Root cause: " + e );
			}
			
			Category categoryFound = objectModel.createCategory();
			categoryFound.setName(name);
			categoryFound.setId(ID);
			categoryFound.setParentId(parentId);
			
			
			return categoryFound;
		} else
			throw new NoSuchElementException( "CategoryIterator: No next Category object" );
	}//end next()

	@Override
	public void remove() {
        throw new UnsupportedOperationException();
	}//end remove()
	
	

}
