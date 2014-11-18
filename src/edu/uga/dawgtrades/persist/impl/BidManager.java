package edu.uga.dawgtrades.persist.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import edu.uga.dawgtrades.model.Bid;
import edu.uga.dawgtrades.model.DTException;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.RegisteredUser;

public class BidManager {
	private ObjectModel objectModel = null;
	private Connection conn = null;
	
	public BidManager(Connection conn, ObjectModel objectModel){
		this.conn = conn;
		this.objectModel = objectModel;
	}
	
	public void save(Bid bid) throws DTException{
		String insertBidSql = "insert into bid (amount, user_id, auction_id) values (?, ?, ?)";
		String updateBidSql = "update bid set amount = ?, user_id = ?, auction_id = ? where id = ?";
		PreparedStatement stmt = null;
		int inscnt;
		long bidId;
		
		if(bid.getAuction() == null)
			throw new DTException("BidManager.save: Attempting to save a Bid without an auction");
		
		try{
			
			if(!bid.isPersistent())
				stmt = conn.prepareStatement(insertBidSql);
			else
				stmt = conn.prepareStatement(updateBidSql);
			
			if(bid.getAmount() != 0)
				stmt.setString(1, Float.toString(bid.getAmount()));
			else
				throw new DTException("BidManager.save: can't save a Bid: value undefined");
		
			if(bid.getRegisteredUser() != null)
				stmt.setString(2, Long.toString(bid.getRegisteredUser().getId()));
			else
				stmt.setNull(2, java.sql.Types.INTEGER);
			
			if(bid.getAuction() != null)
				stmt.setString(3, Long.toString(bid.getAuction().getId()));
			else
				stmt.setNull(3, java.sql.Types.INTEGER);
			
			if(bid.isPersistent())
				stmt.setLong(4, bid.getId());
			
			inscnt = stmt.executeUpdate();
			
			if(!bid.isPersistent()){
				if(inscnt >= 1){
					String sql = "select last_insert_id()";
					if(stmt.execute(sql)){
						ResultSet r = stmt.getResultSet();
						while(r.next()){
							bidId = r.getLong(1);
							if(bidId > 0)
								bid.setId(bidId);
						}
					}
				}
				else
					throw new DTException("BidManager.save: failed to save an Bid");
			}
			else{
				if(inscnt < 1)
					throw new DTException("BidManager.save: failed to save an Bid");
			}
		}
		catch(SQLException e){
			e.printStackTrace();
			throw new DTException("BidManager.save: failed to save an Bid: " + e);
		}
	}

	public Iterator<Bid> restore(Bid bid) throws DTException{
		String selectBidSql = "select id, amount, user_id, auction_id from bid";
		Statement stmt = null;
		StringBuffer query = new StringBuffer(100);
		StringBuffer condition = new StringBuffer(100);
		
		condition.setLength(0);
		
		query.append(selectBidSql);
		
		if(bid != null){
			if(bid.getId() >= 0)
				query.append(" where id = " + bid.getId());
			else if(bid.getAmount() != 0)
				query.append(" where value = " + bid.getAmount() + "'");
			else{
				if(bid.getRegisteredUser() != null)
					condition.append(" user_id = '" + bid.getRegisteredUser().getId() + "'");
				
				if(bid.getAuction() != null){
					 if( condition.length() > 0 )
                    	condition.append(" and ");
					 condition.append(" auction_id = '" + bid.getAuction().getId() + "'");
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
				return new BidIterator(r, objectModel);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			throw new DTException("BidManager.restore: Could not restore persistent Bid object; Root cause: " + e);
		}
		
		throw new DTException("BidManager.restore: Could not restore persistent Bid object");
	}
	
	public Iterator<RegisteredUser> restoreBidder(Bid bid) 
		throws DTException {
		
		        String       selectPersonSql = "select r.id, r.username, r.first_name, r.last_name, r.password, r.email, r.phone, r.isAdmin, r.canText from registered_user r, bid b where b.user_id = r.id order by b.user_id desc";              
		        Statement    stmt = null;
		        StringBuffer query = new StringBuffer( 100 );
		        StringBuffer condition = new StringBuffer( 100 );

		        condition.setLength( 0 );
		        
		        // form the query based on the given Person object instance
		        query.append( selectPersonSql );
		        
		        if( bid != null ) {
		             if( bid.getAmount() != 0 ) // userName is unique, so it is sufficient to get a bid
		                condition.append( " and b.amount = '" + bid.getAmount() + "'" );
		         
		                if( bid.getRegisteredUser() != null )
		                    condition.append( " and b.user_id = '" + bid.getRegisteredUser().getId() + "'" );
		               
		                if( bid.getAuction() != null  )
		                    condition.append( " and b.auction_id = '" + bid.getAuction().getId() + "'" );

		                if( condition.length() > 0 ) {
		                    query.append( condition );
		                }
		            }
		        
		                
		        try {

		            stmt = conn.createStatement();

		            // retrieve the persistent Person object
		            //
		            if( stmt.execute( query.toString() ) ) { // statement returned a result
		                ResultSet r = stmt.getResultSet();
		                return new RegisteredUserIterator( r, objectModel );
		            }
		        }
		        catch( Exception e ) {      // just in case...
		            throw new DTException( "BidManager.restoreBidder: Could not restore persistent RegisteredUser object; Root cause: " + e );
		        }

		        throw new DTException( "BidManager.restoreBidder: Could not restore persistent RegisteredUser object" );
		    }
		    
	
	
	public void delete(Bid bid) throws DTException{
		String deleteBidSql = "delete from bid where id = ?";
		PreparedStatement stmt = null;
		int inscnt;
		
		if(!bid.isPersistent())
			return;
		try{
			stmt = conn.prepareStatement(deleteBidSql);
			stmt.setLong(1, bid.getId());
			inscnt = stmt.executeUpdate();
			if(inscnt == 1){
				return;
			}
			else
				throw new DTException("BidManager.delete: failed to delete an Bid");
		}
		catch(SQLException e){
			e.printStackTrace();
			throw new DTException("BidManager.delete: failed to delete an Bid" + e);
		}
	}

	
}
