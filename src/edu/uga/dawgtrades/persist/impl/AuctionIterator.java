package edu.uga.dawgtrades.persist.impl;

import java.sql.ResultSet;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.uga.dawgtrades.model.Auction;
import edu.uga.dawgtrades.model.DTException;
import edu.uga.dawgtrades.model.ObjectModel;

public class AuctionIterator implements Iterator<Auction>{
	
    private ResultSet   rs = null;
    private ObjectModel objectModel = null;
    private boolean     more = false;
	
	public AuctionIterator( ResultSet rs, ObjectModel objectModel ) throws DTException {
		this.rs = rs;
		this.objectModel = objectModel;
		
		try {
			more = rs.next();
		} catch (Exception e) {
			throw new DTException("AuctionIterator: Cannot create auction. Root cause: " + e);
		}
		
	}//end constructor

	@Override
	public boolean hasNext() {
		return more;
	}//end hasNext()

	@Override
	public Auction next() {
		
		if (more) {
			long ID;
			Date expirationDate = null;
			
			long itemID;
			float minPrice;
			
			try {
				ID = rs.getLong(1);
				expirationDate = rs.getDate(2);
				itemID = rs.getLong(3);
				minPrice = rs.getFloat(4);
				
				more = rs.next();
			} catch (Exception e) {
				throw new NoSuchElementException( "AuctionIterator: No next Auction object. Root cause: " + e );
			}
			
			Auction auctionFound = objectModel.createAuction();
			auctionFound.setId(ID);
			auctionFound.setExpiration(expirationDate);
			auctionFound.setItemId(itemID);
			auctionFound.setMinPrice(minPrice);
			
			return auctionFound;
		} else
			throw new NoSuchElementException( "AuctionIterator: No next Auction object" );
	}//end next()

	@Override
	public void remove() {
        throw new UnsupportedOperationException();
	}//end remove()
	
	

}
