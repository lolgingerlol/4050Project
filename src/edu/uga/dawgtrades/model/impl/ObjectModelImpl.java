package edu.uga.dawgtrades.model.impl;

import java.util.Date;
import java.util.Iterator;

import edu.uga.dawgtrades.model.Attribute;
import edu.uga.dawgtrades.model.AttributeType;
import edu.uga.dawgtrades.model.Auction;
import edu.uga.dawgtrades.model.Bid;
import edu.uga.dawgtrades.model.Category;
import edu.uga.dawgtrades.model.DTException;
import edu.uga.dawgtrades.model.ExperienceReport;
import edu.uga.dawgtrades.model.Item;
import edu.uga.dawgtrades.model.Membership;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.RegisteredUser;
import edu.uga.dawgtrades.persist.Persistence;

public class ObjectModelImpl implements ObjectModel {
	
	 Persistence persistence = null;
	    
	    public ObjectModelImpl()
	    {
	        this.persistence = null;
	    }
	    
	    public ObjectModelImpl( Persistence persistence )
	    {
	        this.persistence = persistence;
	    }
	

	@Override
	public Category createCategory(Category parent, String name)
			throws DTException {
		Category category = new CategoryImpl();
		category.setParentId(parent.getId());
		category.setName(name);
		return category;
	}

	@Override
	public Category createCategory() {
		Category category = new CategoryImpl();
		category.setId(-1);
		return category;
	}

	@Override
	public Iterator<Category> findCategory(Category modelCategory)
			throws DTException {
		return persistence.restoreCategory(modelCategory);
	}

	@Override
	public void storeCategory(Category category) throws DTException {
		persistence.saveCategory(category);
	}

	@Override
	public void deleteCategory(Category category) throws DTException {
		persistence.deleteCategory(category);

	}

	@Override
	public AttributeType createAttributeType(Category category, String name)
			throws DTException {
		AttributeType attributeType = new AttributeTypeImpl();
		attributeType.setCategoryId(category.getId());
		attributeType.setName(name);
		return attributeType;
	}

	@Override
	public AttributeType createAttributeType() {
		AttributeType attributeType = new AttributeTypeImpl();
		attributeType.setId(-1);
		return attributeType;
	}

	@Override
	public void storeAttributeType(AttributeType attributeType)
			throws DTException {
		persistence.saveAttributeType(attributeType);

	}

	@Override
	public void deleteAttributeType(AttributeType attributeType)
			throws DTException {
		persistence.deleteAttributeType(attributeType);

	}

	@Override
	public Item createItem(Category category, RegisteredUser user,
			String identifier, String name, String description)
			throws DTException {
		Item item = new ItemImpl(category, user, identifier,name,description);
		return item;
	}

	@Override
	public Item createItem() {
		Item item = new ItemImpl(null,null,"-1", null,null);
		item.setId(-1);
		return item;
		
	}

	@Override
	public Iterator<Item> findItem(Item modelItem) throws DTException {
		return persistence.restoreItem(modelItem);
	}

	@Override
	public void storeItem(Item item) throws DTException {
		persistence.saveItem(item);

	}

	@Override
	public void deleteItem(Item item) throws DTException {
		persistence.deleteItem(item);

	}

	@Override
	public Attribute createAttribute(AttributeType attributeType, Item item,
			String value) throws DTException {

		Attribute attribute = new AttributeImpl(attributeType, item, value);
		return attribute;
	}

	@Override
	public Attribute createAttribute() {
		Attribute attribute = new AttributeImpl(null, null, null);
		attribute.setId(-1);
		return attribute;
	}

	@Override
	public void storeAttribute(Attribute attribute) throws DTException {
		persistence.saveAttribute(attribute);
	}

	@Override
	public void deleteAttribute(Attribute attribute) throws DTException {
		persistence.deleteAttribute(attribute);

	}

	@Override
	public Auction createAuction(Item item, float minPrice, Date expiration)
			throws DTException {
		Auction auction = new AuctionImpl(item,minPrice,expiration);
		return auction;
	}

	@Override
	public Auction createAuction() {
		Auction auction = new AuctionImpl(null, 0, null);
		auction.setId(-1);
		return auction;
	}

	@Override
	public Iterator<Auction> findAuction(Auction modelAuction)
			throws DTException {
		return persistence.restoreAuction(modelAuction);
	}

	@Override
	public void storeAuction(Auction auction) throws DTException {
		persistence.saveAuction(auction);
	}

	@Override
	public void deleteAuction(Auction auction) throws DTException {
		persistence.deleteAuction(auction);

	}

	@Override
	public RegisteredUser createRegisteredUser(String name, String firstName,
			String lastName, String password, boolean isAdmin, String email,
			String phone, boolean canText) throws DTException {
		RegisteredUser user = new RegisteredUserImpl(name, firstName, lastName, password, isAdmin, email, phone, canText);
		return user;
	}

	@Override
	public RegisteredUser createRegisteredUser() {
		RegisteredUser user = new RegisteredUserImpl(null, null, null, null, false, null, null, false);
		user.setId(-1);
		return user;
	}

	@Override
	public Iterator<RegisteredUser> findRegisteredUser(
			RegisteredUser modelRegisteredUser) throws DTException {
		return persistence.restoreRegisteredUser(modelRegisteredUser);
	}

	@Override
	public void storeRegisteredUser(RegisteredUser registeredUser)
			throws DTException {
		persistence.saveRegisteredUser(registeredUser);

	}

	@Override
	public void deleteRegisteredUser(RegisteredUser registeredUser)
			throws DTException {
		persistence.deleteRegisteredUser(registeredUser);

	}

	@Override
	public Bid createBid(Auction auction, RegisteredUser user, float price)
			throws DTException {
		//TODO: everything above this shouldn't have id variables?
		Bid bid = new BidImpl(auction, user, price);
		return bid;
	}

	@Override
	public Bid createBid() {
		Bid bid = new BidImpl(null, null, 0);
		bid.setId(-1);
		return bid;
	}

	@Override
	public Iterator<Bid> findBid(Bid modelBid) throws DTException {
		return persistence.restoreBid(modelBid);
	}

	@Override
	public void storeBid(Bid bid) throws DTException {
		persistence.saveBid(bid);

	}

	@Override
	public void deleteBid(Bid bid) throws DTException {
		persistence.deleteBid(bid);
	}

	@Override
	public ExperienceReport createExperienceReport(RegisteredUser reviewer,
			RegisteredUser reviewed, int rating, String report, Date date)
			throws DTException {
		ExperienceReport experienceReport = new ExperienceReportImpl(reviewer, reviewed, rating, report, date);
		return experienceReport;
	}

	@Override
	public ExperienceReport createExperienceReport() {
		ExperienceReport experienceReport = new ExperienceReportImpl(null, null, 0, null, null);
		experienceReport.setId(-1);
		return experienceReport;
	}

	@Override
	public Iterator<ExperienceReport> findExperienceReport(
			ExperienceReport modelExperienceReport) throws DTException {
		return persistence.restoreExperienceReport(modelExperienceReport);
	}

	@Override
	public void storeExperienceReport(ExperienceReport experienceReport)
			throws DTException {
		persistence.saveExperienceReport(experienceReport);

	}

	@Override
	public void deleteExperienceReport(ExperienceReport experienceReport)
			throws DTException {
		persistence.deleteExperienceReport(experienceReport);

	}

	@Override
	public Membership createMembership(float price, Date date)
			throws DTException {
		Membership membership = new MembershipImpl(price,date);
		return membership;
	}

	@Override
	public Membership createMembership() {
		Membership membership = new MembershipImpl(0, null);
		membership.setId(-1);
		return membership;
	}

	@Override
	public Membership findMembership() throws DTException {
	   Membership members = new MembershipImpl(0, null);
	   members.setId(-1);
		return persistence.restoreMembership(members).next();
	}

	@Override
	public void storeMembership(Membership membership) throws DTException {
		persistence.saveMembership(membership);

	}

	@Override
	public Category getParent(Category category) throws DTException {
		Category parent = new CategoryImpl();
		parent.setId(category.getParentId());
		return persistence.restoreCategory(parent).next();
	}

	@Override
	public Iterator<Category> getChild(Category category) throws DTException {
		Category child = new CategoryImpl();
		child.setId(-1);
		child.setParentId(category.getId());
		return persistence.restoreCategory(child);
	}

	@Override
	public Category getCategory(AttributeType attributeType) throws DTException {
		return persistence.restoreDescribedBy(attributeType);
	}

	@Override
	public Iterator<AttributeType> getAttributeType(Category category)
			throws DTException {
		return persistence.restoreDescribedBy(category);
	}

	@Override
	public Category getCategory(Item item) throws DTException {
		return persistence.restoreIsOfType(item);
	}

	@Override
	public Iterator<Item> getItem(Category category) throws DTException {
		return persistence.restoreIsOfType(category);
	}

	@Override
	public Item getItem(Attribute attribute) throws DTException {
		return persistence.restoreHasAttribute(attribute);
	}

	@Override
	public Iterator<Attribute> getAttribute(Item item) throws DTException {
		return persistence.restoreHasAttribute(item);
	}

	@Override
	public RegisteredUser getRegisteredUser(Item item) throws DTException {
		return persistence.restoreOwns(item);
	}

	@Override
	public Iterator<Item> getItem(RegisteredUser registeredUser)
			throws DTException {
		return persistence.restoreOwns(registeredUser);
	}

	@Override
	public Item getItem(Auction auction) throws DTException {
		return persistence.restoreIsSoldAt(auction);
	}

	@Override
	public Auction getAuction(Item item) throws DTException {
		return persistence.restoreIsSoldAt(item);
	}

	@Override
	public AttributeType getAttributeType(Attribute attribute)
			throws DTException {
		return persistence.restoreHasType(attribute);
	}

}
