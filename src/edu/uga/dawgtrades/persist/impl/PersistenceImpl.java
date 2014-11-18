package edu.uga.dawgtrades.persist.impl;

import java.sql.Connection;
import java.util.Iterator;

import edu.uga.dawgtrades.model.DTException;
import edu.uga.dawgtrades.model.Attribute;
import edu.uga.dawgtrades.model.AttributeType;
import edu.uga.dawgtrades.model.Auction;
import edu.uga.dawgtrades.model.Bid;
import edu.uga.dawgtrades.model.Category;
import edu.uga.dawgtrades.model.ExperienceReport;
import edu.uga.dawgtrades.model.Item;
import edu.uga.dawgtrades.model.Membership;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.RegisteredUser;
import edu.uga.dawgtrades.persist.*;

public class PersistenceImpl implements Persistence{

	private AttributeManager attributeManager = null;
	private AttributeTypeManager attributeTypeManager = null;
	private AuctionManager auctionManager = null;
	private BidManager bidManager = null;
	private CategoryManager categoryManager = null;
	private ExperienceReportManager experienceReportManager = null;
	private ItemManager itemManager = null;
	private MembershipManager membershipManager = null;
	private RegisteredUserManager registeredUserManager = null;
	
	public PersistenceImpl(Connection conn, ObjectModel objectModel){
		attributeManager = new AttributeManager(conn, objectModel);
		attributeTypeManager = new AttributeTypeManager(conn, objectModel);
		auctionManager = new AuctionManager(conn, objectModel);
		bidManager = new BidManager(conn, objectModel);
		categoryManager = new CategoryManager(conn, objectModel);
		experienceReportManager = new ExperienceReportManager(conn, objectModel);
		itemManager = new ItemManager(conn, objectModel);
		membershipManager = new MembershipManager(conn, objectModel);
		registeredUserManager = new RegisteredUserManager(conn, objectModel);
	}
	
	@Override
	public void saveAttribute(Attribute attribute) throws DTException{
		attributeManager.save(attribute);
	}
	
	@Override
	public Iterator<Attribute> restoreAttribute(Attribute attribute) throws DTException{
		return attributeManager.restore(attribute);
	}
	
	@Override
	public void deleteAttribute(Attribute attribute) throws DTException{
		attributeManager.delete(attribute);
	}
	
	@Override
	public void saveAttributeType(AttributeType attributeType) throws DTException{
		attributeTypeManager.saveAttributeType(attributeType);
	}
	
	@Override
	public Iterator<AttributeType> restoreAttributeType(AttributeType attributeType) throws DTException{
		return attributeTypeManager.restoreAttributeType(attributeType);
	}
	
	@Override
	public void deleteAttributeType(AttributeType attributeType) throws DTException{
		attributeTypeManager.deleteAttributeType(attributeType);
	}
	
	@Override
	public void saveAuction(Auction auction) throws DTException{
		auctionManager.save(auction);
	}
	
	@Override
	public Iterator<Auction> restoreAuction(Auction auction) throws DTException{
		return auctionManager.restore(auction);
	}
	
	@Override
	public void deleteAuction(Auction auction) throws DTException{
		auctionManager.delete(auction);
	}
	
	@Override
	public void saveBid(Bid bid) throws DTException{
		bidManager.save(bid);
	}
	
	@Override
	public Iterator<Bid> restoreBid(Bid bid) throws DTException{
		return bidManager.restore(bid);
	}
	
	@Override
	public void deleteBid(Bid bid) throws DTException{
		bidManager.delete(bid);
	}
	
	@Override
	public void saveCategory(Category category) throws DTException{
		categoryManager.save(category);
	}
	
	@Override
	public Iterator<Category> restoreCategory(Category category) throws DTException{
		return categoryManager.restore(category);
	}
	
	@Override
	public void deleteCategory(Category category) throws DTException{
		categoryManager.delete(category);
	}
	
	@Override
	public void saveExperienceReport(ExperienceReport report) throws DTException{
		experienceReportManager.save(report);
	}
	
	@Override
	public Iterator<ExperienceReport> restoreExperienceReport(ExperienceReport report) throws DTException{
		return experienceReportManager.restore(report);
	}
	
	@Override
	public void deleteExperienceReport(ExperienceReport report) throws DTException{
		experienceReportManager.delete(report);
	}
	
	@Override
	public void saveItem(Item item) throws DTException{
		itemManager.save(item);
	}
	
	@Override
	public Iterator<Item> restoreItem(Item item) throws DTException{
		return itemManager.restore(item);
	}
	
	@Override
	public void deleteItem(Item item) throws DTException{
		itemManager.delete(item);
	}
	
	@Override
	public void saveMembership(Membership membership) throws DTException{
		membershipManager.save(membership);
	}
	
	@Override
	public Iterator<Membership> restoreMembership(Membership membership) throws DTException{
		return membershipManager.restore(membership);
	}
	
	@Override
	public void deleteMembership(Membership membership) throws DTException{
		membershipManager.delete(membership);
	}
	
	@Override
	public void saveRegisteredUser(RegisteredUser user) throws DTException{
		registeredUserManager.save(user);
	}
	
	@Override
	public Iterator<RegisteredUser> restoreRegisteredUser(RegisteredUser user) throws DTException{
		return registeredUserManager.restore(user);
	}
	
	@Override
	public void deleteRegisteredUser(RegisteredUser user) throws DTException{
		registeredUserManager.delete(user);
	}
	
	@Override
	public Category restoreHasChild(Category category) throws DTException{
		return categoryManager.restoreHasChild(category);
	}

	@Override
	public Iterator<Category> restoreHasParent(Category category) throws DTException{
		return categoryManager.restoreHasParent(category);
	}
	
	@Override
	public Category restoreDescribedBy(AttributeType attributeType) throws DTException{
		return attributeTypeManager.restoreIsDescribedBy(attributeType);
	}
	
	@Override
	public Iterator<AttributeType> restoreDescribedBy(Category category) throws DTException{
		return categoryManager.restoreDescribedBy(category);
	}
	
	@Override
	public Category restoreIsOfType(Item item) throws DTException{
		return itemManager.restoreIsOfType(item);
	}
	
	@Override
	public Iterator<Item> restoreIsOfType(Category category) throws DTException{
		return categoryManager.restoreIsOfType(category);
	}
	
	@Override
	public AttributeType restoreHasType(Attribute attribute) throws DTException{
		return attributeManager.restoreHasType(attribute);
	}
	
	@Override
	public Iterator<Attribute> restoreHasType(AttributeType attributeType) throws DTException{
		return attributeTypeManager.restoreHasType(attributeType);
	}
	
	@Override
	public Item restoreHasAttribute(Attribute attribute) throws DTException{
		return attributeManager.restoreHasAttribute(attribute);
	}
	
	@Override
	public Iterator<Attribute> restoreHasAttribute(Item item) throws DTException{
		return itemManager.restoreHasAttribute(item);
	}
	
	@Override
	public RegisteredUser restoreOwns(Item item) throws DTException{
		return itemManager.restoreOwns(item);
	}
	
	@Override
	public Iterator<Item> restoreOwns(RegisteredUser user) throws DTException{
		return registeredUserManager.restoreOwns(user);
	}
	
	@Override
	public Item restoreIsSoldAt(Auction auction) throws DTException{
		return auctionManager.restoreIsSoldAt(auction);
	}
	
	@Override
	public Auction restoreIsSoldAt(Item item) throws DTException{
		return itemManager.restoreIsSoldAt(item);
	}

	
}
