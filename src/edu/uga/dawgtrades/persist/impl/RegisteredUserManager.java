package edu.uga.dawgtrades.persist.impl;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.sql.PreparedStatement;

import edu.uga.dawgtrades.model.Bid;
import edu.uga.dawgtrades.model.DTException;
import edu.uga.dawgtrades.model.Item;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.RegisteredUser;

class RegisteredUserManager
{
    private ObjectModel objectModel = null;
    private Connection  conn = null;
    
    public RegisteredUserManager( Connection conn, ObjectModel objectModel )
    {
        this.conn = conn;
        this.objectModel = objectModel;
    }
    
    public void save( RegisteredUser user ) 
            throws DTException
    {						

        String               insertUserSql = "insert into registered_user ( username, first_name, last_name, password, email, phone, isAdmin, canText ) values ( ?, ?, ?, ?, ?, ?, ?, ? )";              
        String               updateUserSql = "update registered_user  set username = ?, first_name = ?, last_name = ?, password = ?, email = ?, phone = ?, isAdmin = ?, canText = ? where id = ?";              
        PreparedStatement    stmt;
        int                  inscnt;
        long                 userId;
        
        try {
            
            if( !user.isPersistent() )
                stmt = (PreparedStatement) conn.prepareStatement( insertUserSql );
            else
                stmt = (PreparedStatement) conn.prepareStatement( updateUserSql );
            
            if( user.getName() != null ) // user is unique, so it is sufficient to get a user
                stmt.setString( 1, user.getName() );
            else 
                throw new DTException( "RegisteredUserManager.save: can't save a User: userName undefined" );
            
            if( user.getFirstName() != null ) // user is unique, so it is sufficient to get a user
                stmt.setString( 2, user.getFirstName() );
            else 
                throw new DTException( "RegisteredUserManager.save: can't save a User: firstName undefined" );
            
            if( user.getLastName() != null ) // user is unique, so it is sufficient to get a user
                stmt.setString( 3, user.getLastName() );
            else 
                throw new DTException( "RegisteredUserManager.save: can't save a User: lastName undefined" );

            
            if( user.getPassword() != null )
                stmt.setString( 4, user.getPassword() );
            else
                throw new DTException( "RegisteredUserManager.save: can't save a User: password undefined" );

            if( user.getEmail() != null )
                stmt.setString( 5,  user.getEmail() );
            else
                throw new DTException( "RegisteredUserManager.save: can't save a RegisteredUser: email undefined" );

            if( user.getPhone() != null )
                stmt.setString( 6, user.getPhone() );
            else
                throw new DTException( "RegisteredUserManager.save: can't save a RegisteredUser: phone undefined" );

                stmt.setBoolean( 7, user.getIsAdmin() );
                stmt.setBoolean( 8, user.getCanText() );
                
              
            if( user.isPersistent() )
                stmt.setLong( 9, user.getId() );

            inscnt = 	 stmt.executeUpdate();

            if( !user.isPersistent() ) {
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
                            userId = r.getLong( 1 );
                            if( userId > 0 )
                                user.setId( userId ); // set this user's db id (proxy object)
                        }
                    }
                }
            }
            else {
                if( inscnt < 1 )
                    throw new DTException( "RegisteredUserManager.save: failed to save a RegisteredUser" ); 
            }
        }
        catch( SQLException e ) {
            e.printStackTrace();
            throw new DTException( "RegisteredUserManager.save: failed to save a RegisteredUser: " + e );
        }
    }

    public Iterator<RegisteredUser> restore( RegisteredUser modelUser ) 
            throws DTException
    {
        String       selectUserSql = "select id, name, first_name, last_name, password, email, phone, isAdmin, canText from registered_user";              
        Statement    stmt = null;
        StringBuffer query = new StringBuffer( 100 );
        StringBuffer condition = new StringBuffer( 100 );

        condition.setLength( 0 );
        
        // form the query based on the given User object instance
        query.append( selectUserSql );
        
        if( modelUser != null ) {
            if( modelUser.getId() >= 0 ) // id is unique, so it is sufficient to get a user
                query.append( " where id = " + modelUser.getId() );
            else if( modelUser.getName() != null ) // userName is unique, so it is sufficient to get a person
                query.append( " where username = '" + modelUser.getName() + "'" );
            else {
                if( modelUser.getPassword() != null )
                    condition.append( "password = '" + modelUser.getPassword() + "'" );

                if( modelUser.getEmail() != null ) {
                    if( condition.length() > 0 )
                        condition.append( " and " );
                    condition.append( "email = '" + modelUser.getEmail() + "'" );
                }

                if( modelUser.getFirstName() != null ) {
                    if( condition.length() > 0 )
                        condition.append( " and " );
                    condition.append( "first_name = '" + modelUser.getFirstName() + "'" );
                }

                if( modelUser.getLastName() != null ) {
                    if( condition.length() > 0 )
                        condition.append( " and " );
                    condition.append( "last_name = '" + modelUser.getLastName() + "'" );
                }
      

                if( modelUser.getPhone() != null ) {
                    if( condition.length() > 0 )
                        condition.append( " and " );
                    condition.append( "phone = '" + modelUser.getPhone() + "'" );
                }

                if( condition.length() > 0 ) {
                    query.append(  " where " );
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
                return new RegisteredUserIterator( r, objectModel );
            }
        }
        catch( Exception e ) {      // just in case...
            throw new DTException( "RegisteredUserManager.restore: Could not restore persistent User object; Root cause: " + e );
        }
        
        // if we get to this point, it's an error
        throw new DTException( "RegisteredUserManager.restore: Could not restore persistent User object" );
    }
    
    public Iterator<Item> restoreOwns( RegisteredUser user ) 
            throws DTException
    {
        String       selectUserSql = "select i.id, i.name, i.description, i.user_id, i.category_id from registered_user r, item i where i.user_id = r.id";              
        Statement    stmt = null;
        StringBuffer query = new StringBuffer( 100 );
        StringBuffer condition = new StringBuffer( 100 );

        condition.setLength( 0 );
        
        // form the query based on the given User object instance
        query.append( selectUserSql );
        
        if( user != null ) {
            if( user.getId() >= 0 ) // id is unique, so it is sufficient to get a user
                query.append( " and r.id = " + user.getId() );
            else if( user.getName() != null ) // userName is unique, so it is sufficient to get a user
                query.append( " and r.username = '" + user.getName() + "'" );
            else {
                if( user.getPassword() != null )
                    condition.append( " r.password = '" + user.getPassword() + "'" );

                if( user.getEmail() != null && condition.length() == 0 )
                    condition.append( " r.email = '" + user.getEmail() + "'" );
                else
                    condition.append( " AND r.email = '" + user.getEmail() + "'" );

                if( user.getFirstName() != null && condition.length() == 0 )
                    condition.append( " r.firstname = '" + user.getFirstName() + "'" );
                else
                    condition.append( " AND r.firstname = '" + user.getFirstName() + "'" );

                if( user.getLastName() != null && condition.length() == 0 )
                    condition.append( " r.lastname = '" + user.getLastName() + "'" );
                else
                    condition.append( " AND r.lastname = '" + user.getLastName() + "'" );

                if( user.getPhone() != null && condition.length() == 0 )
                    condition.append( " r.phone = '" + user.getPhone() + "'" );
                else
                    condition.append( " AND r.phone = '" + user.getPhone() + "'" );
                
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
                return new ItemIterator( r, objectModel );
            }
        }
        catch( Exception e ) {      // just in case...
            throw new DTException( "RegisteredUserManager.restoreOwns: Could not restore persistent Item objects; Root cause: " + e );
        }

        throw new DTException( "RegisteredUserManager.restoreOwns: Could not restore persistent Item objects" );
    }
    
    public Iterator<Bid> restoreBids( RegisteredUser user ) 
            throws DTException
    {
        String       selectUserSql = "select b.id, b.amount, b.user_id, b.auction_id from bid b, registered_user r where b.user_id = r.id order by b.user_id desc";              
        Statement    stmt = null;
        StringBuffer query = new StringBuffer( 100 );
        StringBuffer condition = new StringBuffer( 100 );

        condition.setLength( 0 );
        
        // form the query based on the given User object instance
        query.append( selectUserSql );
        
        if( user != null ) {
            if( user.getId() >= 0 ) // id is unique, so it is sufficient to get a user
                query.append( " and r.id = " + user.getId() );
            else if( user.getName() != null ) // userName is unique, so it is sufficient to get a user
                query.append( " and r.username = '" + user.getName() + "'" );
            else {
                if( user.getPassword() != null )
                    condition.append( " r.password = '" + user.getPassword() + "'" );

                if( user.getEmail() != null && condition.length() == 0 )
                    condition.append( " r.email = '" + user.getEmail() + "'" );
                else
                    condition.append( " AND r.email = '" + user.getEmail() + "'" );

                if( user.getFirstName() != null && condition.length() == 0 )
                    condition.append( " r.firstname = '" + user.getFirstName() + "'" );
                else
                    condition.append( " AND r.firstname = '" + user.getFirstName() + "'" );

                if( user.getLastName() != null && condition.length() == 0 )
                    condition.append( " r.lastname = '" + user.getLastName() + "'" );
                else
                    condition.append( " AND r.lastname = '" + user.getLastName() + "'" );

                if( user.getPhone() != null && condition.length() == 0 )
                    condition.append( " r.phone = '" + user.getPhone() + "'" );
                else
                    condition.append( " AND r.phone = '" + user.getPhone() + "'" );
                
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
                return new BidIterator( r, objectModel );
            }
        }
        catch( Exception e ) {      // just in case...
            throw new DTException( "RegisteredUserManager.restoreBids: Could not restore persistent Bid objects; Root cause: " + e );
        }

        throw new DTException( "RegisteredUserManager.restoreBids: Could not restore persistent Bid objects" );
    }
    
    public void delete( RegisteredUser user ) 
            throws DTException
    {
        String               deleteUserSql = "delete from registered_user where id = ?";              
        PreparedStatement    stmt = null;
        int                  inscnt;
        
        // form the query based on the given User object instance
        if( !user.isPersistent() ) // is the User object persistent?  If not, nothing to actually delete
            return;
        
        try {
            
            //DELETE t1, t2 FROM t1, t2 WHERE t1.id = t2.id;
            //DELETE FROM t1, t2 USING t1, t2 WHERE t1.id = t2.id;
            stmt = (PreparedStatement) conn.prepareStatement( deleteUserSql );
            
            stmt.setLong( 1, user.getId() );
            
            inscnt = stmt.executeUpdate();
            
            if( inscnt == 0 ) {
                throw new DTException( "RegisteredUserManager.delete: failed to delete this RegisteredUser" );
            }
        }
        catch( SQLException e ) {
            throw new DTException( "RegisteredUserManager.delete: failed to delete this RegisteredUser: " + e.getMessage() );
        }
    }
}
