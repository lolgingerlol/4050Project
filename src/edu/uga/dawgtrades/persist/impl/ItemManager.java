package edu.uga.dawgtrades.persistence.impl;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import com.mysql.jdbc.PreparedStatement;

import edu.uga.dawgtrades.DTException;
import edu.uga.dawgtrades.model.dawgtrades;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.Item;

class ItemManager
{
    private ObjectModel objectModel = null;
    private Connection  conn = null;
    
    public ItemManager( Connection conn, ObjectModel objectModel )
    {
        this.conn = conn;
        this.objectModel = objectModel;
    }
    
    public void save( Item item )
            throws DawgTradesException
    {
        String               insertItemSql = "insert into item ( user_id, name, description ) values ( ?, ?, ? )";
        String               updateItemSql = "update item  set user_id = ?, name = ?, description = ? where id = ?";
        PreparedStatement    stmt;
        int                  inscnt;
        long               itemId;
        
        try {
            
            if( !item.isPersistent() )
                stmt = (PreparedStatement) conn.prepareStatement( insertItemSql );
            else
                stmt = (PreparedStatement) conn.prepareStatement( updateItemSql );

            if( item.getName() != null ) 
                stmt.setString( 1, item.getName() );
            else
                throw new DTException( "ItemManager.save: can't save an Item: name undefined" );
            
            if( item.getDescription() != null )
                stmt.setString( 2, item.getDescription() );
            else
                throw new DTException( "ItemManager.save: can't save an Item: description undefined" );
            
            if( item.getOwnerId() != null )
                stmt.setString( 3, item.getOwnerId() );
            else
                throw new DTException( "ItemManager.save: can't save an Item: user_id undefined" );
            
            if( item.getCategoryId() != null )
                stmt.setString( 4, item.getCategoryId() );
            else
                throw new DTException( "ItemManager.save: can't save an Item: category_id undefined" );


            inscnt = stmt.executeUpdate();

            if( !item.isPersistent() ) {
                // in case this this object is stored for the first time,
                // we need to establish its persistent identifier (primary key)
                if( inscnt == 1 ) {
                    String sql = "select last_insert_id()";
                    if( stmt.execute( sql ) ) { // statement returned a result
                        // retrieve the result
                        ResultSet r = stmt.getResultSet();
                        // we will use only the first row!
                        while( r.next() ) {
                            // retrieve the last insert auto_increment value
                            itemId = r.getLong( 1 );
                            if( itemId > 0 )
                                item.setId( itemId ); // set this item's db id (proxy object)
                        }
                    }
                }
            }
            else {
                if( inscnt < 1 )
                    throw new DTException( "ItemManager.save: failed to save an Item" );
            }
        }
        catch( SQLException e ) {
            e.printStackTrace();
            throw new DTException( "ItemManager.save: failed to save an Item: " + e );
        }
    }

    public Iterator<Item> restore( Item modelItem )
            throws DTException
    {
        String       selectItemSql = "select id, user_id, name, description from item";
        Statement    stmt = null;
        StringBuffer query = new StringBuffer( 100 );
        StringBuffer condition = new StringBuffer( 100 );

        condition.setLength( 0 );
        
        // form the query based on the given Item object instance
        query.append( selectItemSql );
        
        if( modelItem != null ) {
            if( modelItem.getId() >= 0 ) // id is unique, so it is sufficient to get an item
                query.append( " where id = " + modelItem.getId() );
            else {
                
                if( modelItem.getName() != null )
                    condition.append( " name = '" + modelItem.getName() + "'" );

                if( modelItem.getDescription() != null )
                    condition.append( " description = '" + modeItem.getDescription() + "'" );
                
                if( modelItem.getOwnerId() != null )
                    condition.append( " user_id = '" + modelItem.getOwnerId() + "'" );
                
                if( modelItem.getCategoryId() != null )
                    condition.append( " category_id = '" + modelItem.getCategoryId() + "'" );

                if( condition.length() > 0 ) {
                    query.append(  " where " );
                    query.append( condition );
                }
            }
        }
        
        try {

            stmt = conn.createStatement();

            // retrieve the persistent Item object
            //
            if( stmt.execute( query.toString() ) ) { // statement returned a result
                ResultSet r = stmt.getResultSet();
                return new ItemIterator( r, objectModel );
            }
        }
        catch( Exception e ) {      // just in case...
            throw new DTException( "ItemManager.restore: Could not restore persistent Item object; Root cause: " + e );
        }
        
        // if we get to this point, it's an error
        throw new DTException( "ItemManager.restore: Could not restore persistent Item object" );
    }
    
    
    public void delete( Item item )
            throws DTException
    {
        String               deleteItemSql = "delete from item where id = ?";
        PreparedStatement    stmt = null;
        int                  inscnt;
        
        // form the query based on the given Item object instance
        if( !item.isPersistent() ) // is the Item object persistent?  If not, nothing to actually delete
            return;
        
        try {
            
            //DELETE t1, t2 FROM t1, t2 WHERE t1.id = t2.id;
            //DELETE FROM t1, t2 USING t1, t2 WHERE t1.id = t2.id;
            stmt = (PreparedStatement) conn.prepareStatement( deleteItemSql );
            
            stmt.setLong( 1, item.getId() );
            
            inscnt = stmt.executeUpdate();
            
            if( inscnt == 0 ) {
                throw new DTException( "ItemManager.delete: failed to delete this Item" );
            }
        }
        catch( SQLException e ) {
            throw new DTException( "ItemManager.delete: failed to delete this Item: " + e.getMessage() );
        }
    }
    
    public Category restoreIsOfType(Item item) throws DTException{
        String selectItemSql = "select c.id, c.name, c.parent_id from item i, category c where c.id = i.category_id";
        Statement    stmt = null;
        StringBuffer query = new StringBuffer( 100 );
        StringBuffer condition = new StringBuffer( 100 );
        
        condition.setLength( 0 );
        
        query.append( selectItemSql );
        
        if( item != null ) {
            if( item.getId() >= 0 ) // id is unique, so it is sufficient to get an item
                query.append( " and i.id = " + item.getId() );
            else {
                if( item.getName() != null )
                    condition.append( " i.name = '" + item.getName() + "'" );
                
                if( item.getDescription() != null && condition.length() == 0 )
                    condition.append( " i.description = '" + item.getDescription() + "'" );
                else
                    condition.append( " AND i.description = '" + item.getDescription() + "'" );
                
                if( item.getOwnerId() != null && condition.length() == 0 )
                    condition.append( " i.user_id = '" + item.getOwnerId() + "'" );
                else
                    condition.append( " AND i.user_id = '" + item.getOwnerId() + "'" );
                
                if( item.getCategoryId() != null && condition.length() == 0 )
                    condition.append( " i.category_id = '" + item.getCategoryId() + "'" );
                else
                    condition.append( " AND i.category_id = '" + item.getCategoryId() + "'" );
                
                if( condition.length() > 0 ) {
                    query.append( condition );
                }
            }
        }
        
        try {
            
            stmt = conn.createStatement();
            
            // retrieve the persistent Category object
            //
            if( stmt.execute( query.toString() ) ) { // statement returned a result
                ResultSet r = stmt.getResultSet();
                return new CategoryIterator( r, objectModel ).next();
            }
        }
        catch( Exception e ) {      // just in case...
            throw new DTException( "ItemManager.restoreIsOfType: Could not restore persistent Category object; Root cause: " + e );
        }
        
        throw new DTException( "ItemManager.restoreIsOfType: Could not restore persistent Category object" );
        
    }
    
    public Iterator<Attribute> restoreHasAttribute(Item item) throws DTException{
        String selectItemSql = "select a.id, a.value, a.item_id, a.attribute_type_id from item i, attribute a where a.item_id = i.id";
        Statement    stmt = null;
        StringBuffer query = new StringBuffer( 100 );
        StringBuffer condition = new StringBuffer( 100 );
        
        condition.setLength( 0 );
        
        query.append( selectItemSql );
        
        if( item != null ) {
            if( item.getId() >= 0 ) // id is unique, so it is sufficient to get an item
                query.append( " and i.id = " + item.getId() );
            else {
                if( item.getName() != null )
                    condition.append( " i.name = '" + item.getName() + "'" );
                
                if( item.getDescription() != null && condition.length() == 0 )
                    condition.append( " i.description = '" + item.getDescription() + "'" );
                else
                    condition.append( " AND i.description = '" + item.getDescription() + "'" );
                
                if( item.getOwnerId() != null && condition.length() == 0 )
                    condition.append( " i.user_id = '" + item.getOwnerId() + "'" );
                else
                    condition.append( " AND i.user_id = '" + item.getOwnerId() + "'" );
                
                if( item.getCategoryId() != null && condition.length() == 0 )
                    condition.append( " i.category_id = '" + item.getCategoryId() + "'" );
                else
                    condition.append( " AND i.category_id = '" + item.getCategoryId() + "'" );
                
                if( condition.length() > 0 ) {
                    query.append( condition );
                }
            }
        }
        
        try {
            
            stmt = conn.createStatement();
            
            // retrieve the persistent Attribute objects
            //
            if( stmt.execute( query.toString() ) ) { // statement returned a result
                ResultSet r = stmt.getResultSet();
                return new AttributeIterator( r, objectModel );
            }
        }
        catch( Exception e ) {      // just in case...
            throw new DTException( "ItemManager.restoreHasAttribute: Could not restore persistent Attribute objects; Root cause: " + e );
        }
        
        throw new DTException( "ItemManager.restoreHasAttribute: Could not restore persistent Attribute objects" );
        
    }// end restoreHasAttribute()
    
    public RegisteredUser restoreOwns(Item item) throws DTException{
        String selectItemSql = "select r.id, r.username, r.password, r.email, r.firstname, r.lastname, r.phone from item i, registered_user r where r.id = i.user_id";
        Statement    stmt = null;
        StringBuffer query = new StringBuffer( 100 );
        StringBuffer condition = new StringBuffer( 100 );
        
        condition.setLength( 0 );
        
        query.append( selectItemSql );
        
        if( item != null ) {
            if( item.getId() >= 0 ) // id is unique, so it is sufficient to get an item
                query.append( " and i.id = " + item.getId() );
            else {
                if( item.getName() != null )
                    condition.append( " i.name = '" + item.getName() + "'" );
                
                if( item.getDescription() != null && condition.length() == 0 )
                    condition.append( " i.description = '" + item.getDescription() + "'" );
                else
                    condition.append( " AND i.description = '" + item.getDescription() + "'" );
                
                if( item.getOwnerId() != null && condition.length() == 0 )
                    condition.append( " i.user_id = '" + item.getOwnerId() + "'" );
                else
                    condition.append( " AND i.user_id = '" + item.getOwnerId() + "'" );
                
                if( item.getCategoryId() != null && condition.length() == 0 )
                    condition.append( " i.category_id = '" + item.getCategoryId() + "'" );
                else
                    condition.append( " AND i.category_id = '" + item.getCategoryId() + "'" );
                
                if( condition.length() > 0 ) {
                    query.append( condition );
                }
            }
        }
        
        try {
            
            stmt = conn.createStatement();
            
            // retrieve the persistent User object
            //
            if( stmt.execute( query.toString() ) ) { // statement returned a result
                ResultSet r = stmt.getResultSet();
                return new RegisteredUserIterator( r, objectModel ).next();
            }
        }
        catch( Exception e ) {      // just in case...
            throw new DTException( "ItemManager.restoreOwns: Could not restore persistent RegisteredUser object; Root cause: " + e );
        }
        
        throw new DTException( "ItemManager.restoreOwns: Could not restore persistent RegisteredUser object" );
        
    }// end restoreOwns()
    
    public Auction restoreIsSoldAt(Item item) throws DTException {
		String selectItemSql = "select a.id, a.min_price, a.expiration, a.item_id from item i, auction a where a.item_id = i.id";
		Statement stmt = null;
		StringBuffer query = new StringBuffer(100);
		StringBuffer condition = new StringBuffer(100);
		
		condition.setLength(0);
		
		query.append( selectItemSql );
        
        if( item != null ) {
            if( item.getId() >= 0 ) // id is unique, so it is sufficient to get an item
                query.append( " and i.id = " + item.getId() );
            else {
                if( item.getName() != null )
                    condition.append( " i.name = '" + item.getName() + "'" );
                
                if( item.getDescription() != null && condition.length() == 0 )
                    condition.append( " i.description = '" + item.getDescription() + "'" );
                else
                    condition.append( " AND i.description = '" + item.getDescription() + "'" );
                
                if( item.getOwnerId() != null && condition.length() == 0 )
                    condition.append( " i.user_id = '" + item.getOwnerId() + "'" );
                else
                    condition.append( " AND i.user_id = '" + item.getOwnerId() + "'" );
                
                if( item.getCategoryId() != null && condition.length() == 0 )
                    condition.append( " i.category_id = '" + item.getCategoryId() + "'" );
                else
                    condition.append( " AND i.category_id = '" + item.getCategoryId() + "'" );
                
                if( condition.length() > 0 ) {
                    query.append( condition );
                }
            }
        }
        
        try {
            
            stmt = conn.createStatement();
            
            // retrieve the persistent Auction object
            //
            if( stmt.execute( query.toString() ) ) { // statement returned a result
                ResultSet r = stmt.getResultSet();
                return new AuctionIterator( r, objectModel ).next();
            }
        }
        catch( Exception e ) {      // just in case...
            throw new DTException( "ItemManager.restoreIsSoldAt: Could not restore persistent Auction object; Root cause: " + e );
        }
        
        throw new DTException( "ItemManager.restoreIsSoldAt: Could not restore persistent Auction object" );
        
    }//end restoreIsSoldAt()
}
