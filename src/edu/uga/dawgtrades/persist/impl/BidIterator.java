package edu.uga.dawgtrades.persist.impl;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.uga.dawgtrades.model.Auction;
import edu.uga.dawgtrades.model.DTException;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.Bid;
import edu.uga.dawgtrades.model.RegisteredUser;

public class BidIterator implements Iterator<Bid>{

	private ResultSet rs = null;
	private ObjectModel objectModel = null;
	private boolean more = false;

	public BidIterator(ResultSet rs, ObjectModel objectModel) throws DTException{
		this.rs = rs;
		this.objectModel = objectModel;
		try{
			more = rs.next();
		}
		catch(Exception e){
			throw new DTException("BidIterator: Cannot create Bid iterator; Root cause: " + e);
		}
	}

	@Override
	public boolean hasNext(){
		return more;
	}

	@Override
	public Bid next(){
		long id;
		float amount;
		long userId;
		long auctionId;

		if(more){
			try{
				id = rs.getLong(1);
				amount = rs.getFloat(2);
				userId = rs.getLong(3);
				auctionId = rs.getLong(4);

				more = rs.next();
			}
			catch(Exception e){
				throw new NoSuchElementException("BidIterator: No next Bid object; Root cause " + e);
			}

			Bid bid = objectModel.createBid();
			bid.setId(id);
			bid.setAmount(amount);
	
				RegisteredUser user = objectModel.createRegisteredUser();
				user.setId(userId);
				bid.setRegisteredUser(user);
		
			
			
				Auction auction = objectModel.createAuction();
				auction.setId(auctionId);
				bid.setAuction(auction);
			
		
		
			
			return bid;
		}
		else
			throw new NoSuchElementException("BidIterator: No next Bid object");
	}

	@Override
	public void remove(){
		throw new UnsupportedOperationException();
	}
}
