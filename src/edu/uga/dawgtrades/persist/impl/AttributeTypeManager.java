package edu.uga.dawgtrades.persist.impl;

import java.util.Iterator;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

import edu.uga.dawgtrades.model.DTException;
import edu.uga.dawgtrades.model.Attribute;
import edu.uga.dawgtrades.model.AttributeType;
import edu.uga.dawgtrades.model.Category;
import edu.uga.dawgtrades.model.ObjectModel;

public class AttributeTypeManager {
	
	private ObjectModel objectModel = null;
	private Connection conn = null;
	
	public AttributeTypeManager(Connection conn, ObjectModel objectModel){
		this.conn = conn;
		this.objectModel = objectModel;
	}
	
	public void saveAttributeType(AttributeType attributeType) throws DTException{
		String insertAttributeTypeSql = "insert into attribute_type (name, category_id) values (?, ?)";
		String updateAttributeTypeSql = "update attribute_type set name = ?, category_id = ? where id = ?";
		PreparedStatement stmt = null;
		int inscnt;
		long attributeTypeId;
		
		if(attributeType.getCategoryId() == -1)
			throw new DTException("AttributeTypeManager.save: Attemptingg to save an attribute type without a category");
		try{
			if(!attributeType.isPersistent())
				stmt = conn.prepareStatement(insertAttributeTypeSql);
			else
				stmt = conn.prepareStatement(updateAttributeTypeSql);
			
			if(attributeType.getName() != null)
				stmt.setString(1, attributeType.getName());
			else
				throw new DTException("AttributeTypeManager.save: can't save an AttributeType: name undefined");
			
			if(attributeType.getCategoryId() != -1)
				stmt.setLong(2, attributeType.getCategoryId());
			else
				stmt.setNull(2, java.sql.Types.INTEGER);
			
			if(attributeType.isPersistent())
				stmt.setLong(3, attributeType.getId());
			
			inscnt = stmt.executeUpdate();
			
			if(!attributeType.isPersistent()){
				if(inscnt >= 1){
					String sql = "select last_insert_id()";
					if(stmt.execute(sql)){
						ResultSet r = stmt.getResultSet();
						while(r.next()){
							attributeTypeId = r.getLong(1);
							if(attributeTypeId > 0)
								attributeType.setId(attributeTypeId);
						}
					}
				}
				else
					throw new DTException("AttributeTypeManager.save: failed to save an AttributeType");
			}
			else{
				if(inscnt < 1)
					throw new DTException("AttributeTypeManager.save: failed to save an AttributeType");
			}
		}
		catch(SQLException e){
			e.printStackTrace();
			throw new DTException("AttributeTypeManager.save: failed to save an AttributeType" + e);
		}
	}
	
	public Iterator<AttributeType> restoreAttributeType(AttributeType attributeType) throws DTException{
		String selectAttributeTypeSql = "select id, name, category_id from attribute_type";
		Statement stmt = null;
		StringBuffer query = new StringBuffer(100);
		StringBuffer condition = new StringBuffer(100);
		
		condition.setLength(0);
		
		query.append(selectAttributeTypeSql);
		
		if(attributeType != null){
			if(attributeType.getId() >= 0)
				query.append(" where id = " + attributeType.getId());
			else if(attributeType.getName() != null)
				query.append(" where name = " + attributeType.getName() + "'");
			else{
				if(attributeType.getCategoryId() != 0)
					condition.append("category_id = '" + attributeType.getCategoryId() + "'");
				if(condition.length() > 0){
					query.append(" where ");
					query.append(condition);
				}
			}
		}
		
		try{
			stmt = conn.createStatement();
			if(stmt.execute(query.toString())){
				ResultSet r = stmt.getResultSet();
				return new AttributeTypeIterator(r, objectModel);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			throw new DTException("AttributeTypeManager.restore: Could not restore persistent AttributeType object; Root cause: " + e);
		}
		
		throw new DTException("AttributeManager.restore: Could not restore persistent AttributeType object");
	}
	
	public void deleteAttributeType(AttributeType attributeType) throws DTException{
		String deleteAttributeSql = "delete from attribute_type where id = ?";
		PreparedStatement stmt = null;
		int inscnt;
		
		if(!attributeType.isPersistent())
			return;
		try{
			stmt = conn.prepareStatement(deleteAttributeSql);
			stmt.setLong(1, attributeType.getId());
			inscnt = stmt.executeUpdate();
			if(inscnt == 1){
				return;
			}
			else
				throw new DTException("AttributeTypeManager.delete: failed to delete an AttributeType");
		}
		catch(SQLException e){
			e.printStackTrace();
			throw new DTException("AttributeTypeManager.delete: failed to delete an AttributeType " + e);
		}
	}
	
	public Category restoreIsDescribedBy(AttributeType attributeType) throws DTException{
		String selectCategorySql = "select c.id, c.name, c.parent_id from category c, attribute_type at where at.category_id = c.id";
		Statement stmt = null;
		StringBuffer query = new StringBuffer(100);
		StringBuffer condition = new StringBuffer(100);
		
		condition.setLength(0);
		
		query.append(selectCategorySql);
		
		if(attributeType != null){
			if(attributeType.getId() >= 0)
				query.append(" and at.id = " + attributeType.getId());
			else{
				if(attributeType.getName() != null)
					condition.append(" and at.name = '" + attributeType.getName() + "'");
				if(attributeType.getCategoryId() != 0)
					condition.append(" and a.category_id = '" + attributeType.getCategoryId() + "'");
				if(condition.length() > 0){
					query.append(condition);
				}
			}
		}
		try{
			stmt = conn.createStatement();
			
			if(stmt.execute(query.toString())){
				ResultSet r = stmt.getResultSet();
				Iterator<Category> categoryIter = new CategoryIterator(r, objectModel);
				if(categoryIter != null && categoryIter.hasNext()){
					return categoryIter.next();
				}
				else
					return null;
			}
		}
		catch(Exception e){
			throw new DTException("AttributeTypeManager.restoreIsDescribedBy: Could not restore persistent Category object; Root cause: " + e);
		}
		
		throw new DTException("AttributeManager.restoreIsDescribedBy: Could not restore persistent Category object");
	}
	
	public Iterator<Attribute> restoreHasType(AttributeType attributeType) throws DTException{
		String selectCategorySql = "select a.id, a.value from attribute a, attribute_type at where a.attribute_type_id = at.id";
		Statement stmt = null;
		StringBuffer query = new StringBuffer(100);
		StringBuffer condition = new StringBuffer(100);
		
		condition.setLength(0);
		
		query.append(selectCategorySql);
		
		if(attributeType != null){
			if(attributeType.getId() >= 0)
				query.append(" and at.id = " + attributeType.getId());
			else{
				if(attributeType.getName() != null)
					condition.append(" and at.name = '" + attributeType.getName() + "'");
				if(attributeType.getCategoryId() != 0)
					condition.append(" and at.category_id = '" + attributeType.getCategoryId() + "'");
				if(condition.length() > 0){
					query.append(condition);
				}
			}
		}
		try{
			stmt = conn.createStatement();
			
			if(stmt.execute(query.toString())){
				ResultSet r = stmt.getResultSet();
				Iterator<Attribute> attributeIter = new AttributeIterator(r, objectModel);
				if(attributeIter != null && attributeIter.hasNext()){
					return attributeIter;
				}
				else
					return null;
			}
		}
		catch(Exception e){
			throw new DTException("AttributeTypeManager.restoreIsDescribedBy: Could not restore persistent Attribute object; Root cause: " + e);
		}
		
		throw new DTException("AttributeManager.restoreIsDescribedBy: Could not restore persistent Attribute object");
	
		
	}
	
	
}

