package edu.uga.dawgtrades.persist.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.sql.PreparedStatement;

import edu.uga.dawgtrades.model.DTException;
import edu.uga.dawgtrades.model.Attribute;
import edu.uga.dawgtrades.model.AttributeType;
import edu.uga.dawgtrades.model.Item;
import edu.uga.dawgtrades.model.ObjectModel;

public class AttributeManager {

	private ObjectModel objectModel = null;
	private Connection conn = null;
	
	public AttributeManager(Connection conn, ObjectModel objectModel){
		this.conn = conn;
		this.objectModel = objectModel;
	}
	
	public void save(Attribute attribute) throws DTException{
		String insertAttributeSql = "insert into attribute (value, item_id, attribute_type_id) values (?, ?, ?)";
		String updateAttributeSql = "update attribute set value = ?, item_id = ?, attribute_type_id = ? where id = ?";
		PreparedStatement stmt = null;
		int inscnt;
		long attributeId;
		
		if(attribute.getItemId() == -1)
			throw new DTException("AttributeManager.save: Attempting to save an attribute without an item");
		
		try{
			
			if(!attribute.isPersistent())
				stmt = conn.prepareStatement(insertAttributeSql);
			else
				stmt = conn.prepareStatement(updateAttributeSql);
			
			if(attribute.getValue() != null)
				stmt.setString(1, attribute.getValue());
			else
				throw new DTException("AttributeManager.save: can't save an Attribute: value undefined");
		
			if(attribute.getItemId() != -1)
				stmt.setString(2, Long.toString(attribute.getItemId()));
			else
				stmt.setNull(2, java.sql.Types.INTEGER);
			
			if(attribute.getAttributeType() != -1)
				stmt.setString(3, Long.toString(attribute.getAttributeType()));
			else
				stmt.setNull(3, java.sql.Types.INTEGER);
			
			if(attribute.isPersistent())
				stmt.setLong(4, attribute.getId());
			
			inscnt = stmt.executeUpdate();
			
			if(!attribute.isPersistent()){
				if(inscnt >= 1){
					String sql = "select last_insert_id()";
					if(stmt.execute(sql)){
						ResultSet r = stmt.getResultSet();
						while(r.next()){
							attributeId = r.getLong(1);
							if(attributeId > 0)
								attribute.setId(attributeId);
						}
					}
				}
				else
					throw new DTException("AttributeManager.save: failed to save an Attribute");
			}
			else{
				if(inscnt < 1)
					throw new DTException("AttributeManager.save: failed to save an Attribute");
			}
		}
		catch(SQLException e){
			e.printStackTrace();
			throw new DTException("AttributeManager.save: failed to save an Attribute: " + e);
		}
	}

	public Iterator<Attribute> restore(Attribute attribute) throws DTException{
		String selectAttributeSql = "select id, value, item_id, attribute_type_id from attribute";
		Statement stmt = null;
		StringBuffer query = new StringBuffer(100);
		StringBuffer condition = new StringBuffer(100);
		
		condition.setLength(0);
		
		query.append(selectAttributeSql);
		
		if(attribute != null){
			if(attribute.getId() >= 0)
				query.append(" where id = " + attribute.getId());
			else{
				 if(attribute.getValue() != null)
						condition.append(" and value = " + attribute.getValue() + "'");
				if(attribute.getItemId() != -1){
					 if( condition.length() > 0 )
	                    	condition.append(" and ");
					condition.append("item_id = '" + attribute.getItemId() + "'");
				}
				if(attribute.getAttributeType() != -1) {
					 if( condition.length() > 0 )
	                    	condition.append(" and ");
					condition.append("attribute_type_id = '" + attribute.getAttributeType() + "'");
				}
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
				return new AttributeIterator(r, objectModel);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			throw new DTException("AttributeManager.restore: Could not restore persistent Attribute object; Root cause: " + e);
		}
		
		throw new DTException("AttributeManager.restore: Could not restore persistent Attribute object");
	}
	
	
	
	public void delete(Attribute attribute) throws DTException{
		String deleteAttributeSql = "delete from attribute where id = ?";
		PreparedStatement stmt = null;
		int inscnt;
		
		if(!attribute.isPersistent())
			return;
		try{
			stmt = conn.prepareStatement(deleteAttributeSql);
			stmt.setLong(1, attribute.getId());
			inscnt = stmt.executeUpdate();
			if(inscnt == 1){
				return;
			}
			else
				throw new DTException("AttributeManager.delete: failed to delete an Attribute");
		}
		catch(SQLException e){
			e.printStackTrace();
			throw new DTException("AttributeManager.delete: failed to delete an Attribute" + e);
		}
	}

	public Item restoreHasAttribute(Attribute attribute) throws DTException{
		String selectItemSql = "select i.id, i.name, i.description, i.user_id, i.category_id from item i, attribute a where a.item_id = i.id";
		Statement stmt = null;
		StringBuffer query = new StringBuffer(100);
		StringBuffer condition = new StringBuffer(100);
		
		condition.setLength(0);
		
		query.append(selectItemSql);
		
		if(attribute != null){
			if(attribute.getId() >= 0)
				query.append(" and a.id = " + attribute.getId());
			else{
				 if(attribute.getValue() != null)
					 condition.append(" and a.value = '" + attribute.getValue() + "'");
				if(attribute.getItemId() != 0)
					condition.append(" and a.item_id = '" + attribute.getItemId() + "'");
				if(attribute.getAttributeType() != 0)
					condition.append(" and a.attribute_type_id = '" + attribute.getAttributeType() + "'");
				if(condition.length() > 0){
					query.append(condition);
				}
			}
		}
		try{
			stmt = conn.createStatement();
			
			if(stmt.execute(query.toString())){
				ResultSet r = stmt.getResultSet();
				Iterator<Item> itemIter = new ItemIterator(r, objectModel);
				if(itemIter != null && itemIter.hasNext()){
					return itemIter.next();
				}
				else
					return null;
			}
		}
		catch(Exception e){
			throw new DTException("AttributeManager.restoreHasAttribute: Could not restore persistent Item object; Root cause: " + e);
		}
		
		throw new DTException("AttributeManager.restoreHasAttribute: Could not restore persistent Item object");
	}

	public AttributeType restoreHasType(Attribute attribute) throws DTException{
		String selectTypeSql = "select at.id, at.name, at.category_id from item i, attribute a where a.attribute_type_id = at.id";
		Statement stmt = null;
		StringBuffer query = new StringBuffer(100);
		StringBuffer condition = new StringBuffer(100);
		
		condition.setLength(0);
		
		query.append(selectTypeSql);
		
		if(attribute != null){
			if(attribute.getId() >= 0)
				query.append(" and a.id = " + attribute.getId());
			else{
				 if(attribute.getValue() != null)
						condition.append(" and a.value = '" + attribute.getValue() + "'");
				if(attribute.getItemId() != 0)
					condition.append(" and a.item_id = '" + attribute.getItemId() + "'");
				if(attribute.getAttributeType() != 0)
					condition.append(" and a.attribute_type_id = '" + attribute.getAttributeType() + "'");
				if(condition.length() > 0){
					query.append(condition);
				}
			}
		}
		try{
			stmt = conn.createStatement();
			
			if(stmt.execute(query.toString())){
				ResultSet r = stmt.getResultSet();
				Iterator<AttributeType> attributeTypeIter = new AttributeTypeIterator(r, objectModel);
				if(attributeTypeIter != null && attributeTypeIter.hasNext()){
					return attributeTypeIter.next();
				}
				else
					return null;
			}
		}
		catch(Exception e){
			throw new DTException("AttributeManager.restoreHasType: Could not restore persistent AttributeType object; Root cause: " + e);
		}
		
		throw new DTException("AttributeManager.restoreHasType: Could not restore persistent AttributeType object");
	}

}
