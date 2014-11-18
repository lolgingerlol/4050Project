package edu.uga.dawgtrades.persist;

//Gnu Emacs C++ mode:  -*- Java -*-
//
//Interface:	PersistenceModule.java
//
//K.J. Kochut
//
//
//


import java.util.Iterator;

import edu.uga.dawgtrades.model.DTException;
import edu.uga.dawgtrades.model.RegisteredUser;
import edu.uga.dawgtrades.model.Membership;

import edu.uga.dawgtrades.model.Attribute;
import edu.uga.dawgtrades.model.AttributeType;
import edu.uga.dawgtrades.model.Auction;
import edu.uga.dawgtrades.model.Bid;
import edu.uga.dawgtrades.model.Category;
import edu.uga.dawgtrades.model.ExperienceReport;
import edu.uga.dawgtrades.model.Item;


public interface Persistence {

	public void                    saveAttribute( Attribute attribute) throws DTException;
	public Iterator<Attribute>          restoreAttribute( Attribute attribute) throws DTException;
	public void		         deleteAttribute( Attribute attribute ) throws DTException;

	public void                    saveAttributeType( AttributeType attributeType) throws DTException;
	public Iterator<AttributeType>          restoreAttributeType( AttributeType attributeType) throws DTException;
	public void		         deleteAttributeType( AttributeType attributeType ) throws DTException;

	public void                    saveAuction( Auction auction) throws DTException;
	public Iterator<Auction>          restoreAuction( Auction auction) throws DTException;
	public void		         deleteAuction( Auction auction ) throws DTException;	

	public void                    saveBid( Bid bid) throws DTException;
	public Iterator<Bid>          restoreBid( Bid bid) throws DTException;
	public void		         deleteBid( Bid bid) throws DTException;
	
	public void                    saveCategory( Category category) throws DTException;
	public Iterator<Category>          restoreCategory( Category category) throws DTException;
	public void		         deleteCategory( Category category) throws DTException;

	public void                    saveExperienceReport( ExperienceReport experienceReport) throws DTException;
	public Iterator<ExperienceReport>          restoreExperienceReport( ExperienceReport experienceReport) throws DTException;
	public void		         deleteExperienceReport( ExperienceReport experienceReport) throws DTException;
	
	public void  saveItem( Item item) throws DTException;
	public Iterator<Item>  restoreItem( Item item) throws DTException;
	public void	deleteItem( Item item) throws DTException;
	
	public void  saveMembership( Membership membership) throws DTException;
	public Iterator<Membership> restoreMembership(Membership members) throws DTException;
	public void	 deleteMembership( Membership membership) throws DTException;
	
public void      saveRegisteredUser( RegisteredUser user) throws DTException;
public Iterator<RegisteredUser>  restoreRegisteredUser( RegisteredUser user) throws DTException;
public void		deleteRegisteredUser( RegisteredUser user ) throws DTException;
Iterator<Item> restoreOwns(RegisteredUser user) throws DTException;
RegisteredUser restoreOwns(Item item) throws DTException;
Category restoreHasChild(Category category) throws DTException;
Iterator<Category> restoreHasParent(Category category) throws DTException;
Category restoreDescribedBy(AttributeType attributeType) throws DTException;
Iterator<AttributeType> restoreDescribedBy(Category category)
		throws DTException;
Category restoreIsOfType(Item item) throws DTException;
Iterator<Item> restoreIsOfType(Category category) throws DTException;
AttributeType restoreHasType(Attribute attribute) throws DTException;
Iterator<Attribute> restoreHasType(AttributeType attributeType)
		throws DTException;
Item restoreHasAttribute(Attribute attribute) throws DTException;
Iterator<Attribute> restoreHasAttribute(Item item) throws DTException;
Item restoreIsSoldAt(Auction auction) throws DTException;
Auction restoreIsSoldAt(Item item) throws DTException;

};