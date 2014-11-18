package edu.uga.dawgtrades.model.impl;

import java.util.Date;

import edu.uga.dawgtrades.model.Auction;
import edu.uga.dawgtrades.model.Bid;
import edu.uga.dawgtrades.model.RegisteredUser;

public class BidImpl extends Persistent implements Bid {
	
	private float amount;
	private Date date;
	private Auction auction;
	private RegisteredUser registeredUser;
	

	
	public BidImpl(Auction auction, RegisteredUser user, float price) {
		this.auction = auction;
		this.registeredUser = user;
		this.amount = price;
	}

	@Override
	public float getAmount() {
		return this.amount;
	}

	@Override
	public void setAmount(float amount) {
		this.amount = amount;
	}

	@Override
	public Date getDate() {
		return this.date;
	}

	@Override
	public void setDate(Date date) {
		this.date = date;
		
	}

	@Override
	public boolean isWinning() {
		return auction.getSellingPrice() == this.amount;
	}

	@Override
	public Auction getAuction() {
		return this.auction;
	}

	@Override
	public RegisteredUser getRegisteredUser() {
		return this.registeredUser;
	}
	
	public void setRegisteredUser(RegisteredUser registeredUser) {
		this.registeredUser = registeredUser;
	}

	@Override
	public void setAuction(Auction auction) {
		this.auction = auction;
		
	}

}
