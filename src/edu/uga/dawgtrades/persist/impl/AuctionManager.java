package edu.uga.dawgtrades.persist.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.sql.PreparedStatement;

import edu.uga.dawgtrades.model.Bid;
import edu.uga.dawgtrades.model.DTException;
import edu.uga.dawgtrades.model.Item;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.Auction;

public class AuctionManager {
	
    private ObjectModel objectModel = null;
    private Connection  conn = null;
	
	public AuctionManager( Connection conn, ObjectModel objectModel ) {
        this.conn = conn;
        this.objectModel = objectModel;
	}//end constructor
	
	public void save(Auction auction) throws DTException {
		String 				insertAuctionSql = "insert into auction ( expirationDate, itemID, minPrice, sellingPrice ) values ( ?, ?, ?, ? )";
		String 				updateAuctionSql = "update auction  set expirationDate = ?, set itemId = ?, set minPrice = ?, set sellingPrice = ? where id = ?";
		PreparedStatement 	stmt;
		int					inscnt;
		long				auctionId;
		
		if(auction.getItemId() == -1)
			throw new DTException("Auction.save: Attempting to save an auction without an item");
		
		try {
			
			if (!auction.isPersistent())
				stmt = conn.prepareStatement(insertAuctionSql);
			else
				stmt = conn.prepareStatement(updateAuctionSql);
			
			if (auction.getExpiration() != null)
				stmt.setDate(1, (Date) auction.getExpiration());
			else
				throw new DTException( "Auction.save: can't save an Auction: invalid expiration date");
			
			if (auction.getItemId() != -1)
				stmt.setString( 2, Long.toString(auction.getItemId()));
			else
				stmt.setNull( 2, java.sql.Types.INTEGER);
			
			if (auction.getMinPrice() >= 0)
				stmt.setString(3, Float.toString(auction.getMinPrice()));
			else
				throw new DTException("AuctionManager.save: can't save an Auction: min price invalid");
			
			if (auction.getSellingPrice() >= 0)
				stmt.setString(4, Float.toString(auction.getSellingPrice()));
			else
				throw new DTException("AuctionManager.save: can't save an Auction: selling price invalid");
			
			if (auction.isPersistent())
				stmt.setLong(5, auction.getId());
			
			inscnt = stmt.executeUpdate();
			
			if (!auction.isPersistent()) {
				if (inscnt >= 1) {
					String sql = "select last_insert_id()";
					if (stmt.execute(sql)) {
						ResultSet r = stmt.getResultSet();
						while(r.next()) {
							auctionId = r.getLong(1);
							if (auctionId > 0)
								auction.setId(auctionId);
						}
					}
				}
				else
					throw new DTException("AuctionManager.save: failed to save an Auction");
			}
			else {
				if (inscnt < 1)
					throw new DTException("AuctionManager.save: failed to save an Auction");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DTException("AuctionManager.save: failed to save an Auction");
		}
	}//end save()
	
	public Iterator<Auction> restore(Auction auction) throws DTException {
		String selectAuctionSql = "select id, expirationDate, itemId, minPrice, sellingPrice from auction";
		Statement stmt = null;
		StringBuffer query = new StringBuffer(100);
		StringBuffer condition = new StringBuffer(100);
		
		condition.setLength(0);
		
		query.append(selectAuctionSql);
		
		if (auction != null) {
			if (auction.getId() >= 0)
				query.append(" where id = " + auction.getId());
			else {
				if (auction.getItemId() != -1)
					condition.append("itemId = '" + auction.getItemId() + "'");
				if (condition.length() > 0)
					query.append(" where " + condition);
			}
		}
		
		try {
			stmt = conn.createStatement();
			if (stmt.execute(query.toString())) {
				ResultSet r = stmt.getResultSet();
				return new AuctionIterator(r, objectModel);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DTException("AuctionManager.restore: Could not restore persistent Auction object. Root cause: " + e);
		}
		
		throw new DTException("AuctionManager.restore: Could not restore persistent Auction object.");
	}//end restore()
	
	public void delete(Auction auction) throws DTException{
		String deleteAuctionSql = "delete from auction where id = ?";
		PreparedStatement stmt = null;
		int inscnt;
		
		if (!auction.isPersistent())
			return;
		
		try {
			stmt = conn.prepareStatement(deleteAuctionSql);
			stmt.setLong(1,auction.getId());
			inscnt = stmt.executeUpdate();
			if (inscnt == 1)
				return;
			else
				throw new DTException("AuctionManager.delete: failed to delete an Attribute");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DTException("AuctionManager.delete: failed to delete an Auction. Root Cause: " + e);
		}
	}//end restore
	
	public Item restoreIsSoldAt(Auction auction) throws DTException {
		String selectAuctionSql = "select i.id, i.expirationDate, i.itemId, i.minPrice, i.sellingPrice from item i, item a where a.item_id = i.id";
		Statement stmt = null;
		StringBuffer query = new StringBuffer(100);
		StringBuffer condition = new StringBuffer(100);
		
		condition.setLength(0);
		
		query.append(selectAuctionSql);
		
		if (auction != null) {
			if (auction.getId() >= 0)
				query.append(" and a.id = " + auction.getId());
			else {
				if (auction.getExpiration() != null)
					condition.append( " a.expirationDate = '" + auction.getExpiration() + "'");
				
				if (auction.getItemId() >= 0 && condition.length() == 0)
					condition.append(" a.itemId = '" + auction.getItemId() + "'");
				else if (condition.length() != 0)
					condition.append(" AND a.itemId = '" + auction.getItemId() + "'");
				
				if (auction.getMinPrice() < 0 && condition.length() == 0)
					condition.append(" a.minPrice = '" + auction.getMinPrice() + "'");
				else if (condition.length() != 0)
					condition.append(" AND a.minPrice = '" + auction.getMinPrice() + "'");
				
				if (auction.getSellingPrice() < 0 && condition.length() == 0)
					condition.append(" a.sellingPrice = '" + auction.getSellingPrice() + "'");
				else if (condition.length() != 0)
					condition.append(" AND a.sellingPrice = '" + auction.getSellingPrice() + "'");
				
				if (condition.length() > 0) {
					query.append(condition);
				}
			}
		}
		
		try {
			stmt = conn.createStatement();
			if (stmt.execute(query.toString())) {
				ResultSet r = stmt.getResultSet();
				return new ItemIterator( r, objectModel).next();
			}
		} catch (Exception e) {
			throw new DTException("AuctionManager.restoreIsSoldAt: Could not restore persistent Item objects. Root cause: " + e);
		}
		throw new DTException("AuctionManager.restoreIsSoldAt: Could not restore persistent Item objects.");
	}//end restoreIsSoldAt()
	
	public Bid restoreBids(Auction auction) throws DTException {
		String selectAuctionSql = "select i.id, i.expirationDate, i.itemId, i.minPrice, i.sellingPrice from item i, item a where a.item_id = i.id";
		Statement stmt = null;
		StringBuffer query = new StringBuffer(100);
		StringBuffer condition = new StringBuffer(100);
		
		condition.setLength(0);
		
		query.append(selectAuctionSql);
		
		if (auction != null) {
			if (auction.getId() >= 0)
				query.append(" and a.id = " + auction.getId());
			else {
				if (auction.getExpiration() != null)
					condition.append( " a.expirationDate = '" + auction.getExpiration() + "'");
				
				if (auction.getItemId() >= 0 && condition.length() == 0)
					condition.append(" a.itemId = '" + auction.getItemId() + "'");
				else if (condition.length() != 0)
					condition.append(" AND a.itemId = '" + auction.getItemId() + "'");
				
				if (auction.getMinPrice() < 0 && condition.length() == 0)
					condition.append(" a.minPrice = '" + auction.getMinPrice() + "'");
				else if (condition.length() != 0)
					condition.append(" AND a.minPrice = '" + auction.getMinPrice() + "'");
				
				if (auction.getSellingPrice() < 0 && condition.length() == 0)
					condition.append(" a.sellingPrice = '" + auction.getSellingPrice() + "'");
				else if (condition.length() != 0)
					condition.append(" AND a.sellingPrice = '" + auction.getSellingPrice() + "'");
				
				if (condition.length() > 0) {
					query.append(condition);
				}
			}
		}
		
		try {
			stmt = conn.createStatement();
			if (stmt.execute(query.toString())) {
				ResultSet r = stmt.getResultSet();
				return new BidIterator( r, objectModel).next();
			}
		} catch (Exception e) {
			throw new DTException("AuctionManager.restoreIsSoldAt: Could not restore persistent Item objects. Root cause: " + e);
		}
		throw new DTException("AuctionManager.restoreIsSoldAt: Could not restore persistent Item objects.");
	}//end restoreBids()

}
