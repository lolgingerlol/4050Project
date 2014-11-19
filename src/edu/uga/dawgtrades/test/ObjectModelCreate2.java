package edu.uga.dawgtrades.test;


import java.sql.Connection;
import java.util.Date;
import java.util.Iterator;

import edu.uga.dawgtrades.model.*;
import edu.uga.dawgtrades.persist.*;
import edu.uga.dawgtrades.persist.impl.*;
import edu.uga.dawgtrades.model.impl.*;

//This is a simple class to test the creation of the entity classes
//and associations in the Clubs example.
//
public class ObjectModelCreate {
	
 public static void main(String[] args)
 {
      Connection conn = null;
      ObjectModel objectModel = null;
      Persistence persistence = null;
      
      Attribute			attribute1, attribute2, attribute3;
      AttributeType		attributeType1, attributeType2, attributeType3;
      Auction			auction1, auction2, auction3;
      Bid				bid1, bid2, bid3;
      Category			category1, category2, category3;
      ExperienceReport	expReport1, expReport2, expReport3;
      Item				item1, item2, item3;
      Membership		membership1, membership2, membership3;
      RegisteredUser	rUser1, rUser2, rUser3;

      
      // get a database connection
      try {
          conn = DbUtils.connect();
      } 
      catch (Exception seq) {
          System.err.println( "ObjectModelDelete: Unable to obtain a database connection" );
      }
      
      // obtain a reference to the ObjectModel module      
      objectModel = new ObjectModelImpl();
      // obtain a reference to Persistence module and connect it to the ObjectModel        
      persistence = new PersistenceImpl( conn, objectModel ); 
      // connect the ObjectModel module to the Persistence module
      objectModel.setPersistence( persistence );

      try {
    	  rUser1 = objectModel.createRegisteredUser("Ben", "Ben", "Pipkin", "password123", true, "bpipkin@email.com", "5551234567", true);
    	  objectModel.storeRegisteredUser(rUser1);
   
    	  rUser2 = objectModel.createRegisteredUser("Evan", "Evan", "Carter", "Passwordyo432", false, "evan@email.com", "12", false);
    	  objectModel.storeRegisteredUser(rUser2);
    	  
    	  rUser3 = objectModel.createRegisteredUser("obama", "Barack", "Obama", "America", true, "leaderofthefreeworld@usa.com", "69", true);
          objectModel.storeRegisteredUser(rUser3);
    	  
          category1 = objectModel.createCategory(null, "category1");
          objectModel.storeCategory(category1);
          
          category2 = objectModel.createCategory(category1, "category2");
          objectModel.storeCategory(category2);
          
          category3 = objectModel.createCategory(category2, "category3");
          objectModel.storeCategory(category3);
    	  
    	  attributeType1 = objectModel.createAttributeType(category1, "attributeType1");
    	  objectModel.storeAttributeType(attributeType1);
    	  
    	  attributeType2 = objectModel.createAttributeType(category2, "attributeType2");
    	  objectModel.storeAttributeType(attributeType2);
    	  
    	  attributeType3 = objectModel.createAttributeType(category3, "attributeType3");
    	  objectModel.storeAttributeType(attributeType3);
    	  
    	  item1 = objectModel.createItem(category1, rUser1, "id1", "name1", "desc1");
    	  objectModel.storeItem(item1);
    	  
    	  item2 = objectModel.createItem(category2, rUser2, "id2", "name2", "desc2");
    	  objectModel.storeItem(item2);
    	  
    	  item3 = objectModel.createItem(category3, rUser3, "id3", "name3", "desc3");
    	  objectModel.storeItem(item3);
    	  
    	  attribute1 = objectModel.createAttribute(attributeType1, item1, "value1");
    	  objectModel.storeAttribute(attribute1);
    	  
    	  attribute2 = objectModel.createAttribute(attributeType2, item2, "value2");
    	  objectModel.storeAttribute(attribute2);
    	  
    	  attribute3 = objectModel.createAttribute(attributeType3, item3, "value3");
    	  objectModel.storeAttribute(attribute3);
    	  
    	  auction1 = objectModel.createAuction(item1, 12, new Date("12/12/2015"));
    	  objectModel.storeAuction(auction1);
    	  
    	  auction2 = objectModel.createAuction(item2, 23, new Date("12/12/2000"));
    	  objectModel.storeAuction(auction2);
    	  
    	  auction3 = objectModel.createAuction(item3, 34, new Date("12/12/2014"));
    	  objectModel.storeAuction(auction3);
    	  
    	  bid1 = objectModel.createBid(auction1, rUser1, 13);
    	  objectModel.storeBid(bid1);
    	  
    	  bid2 = objectModel.createBid(auction2, rUser2, 24);
    	  objectModel.storeBid(bid2);
    	  
    	  bid3 = objectModel.createBid(auction3, rUser3, 35);
    	  objectModel.storeBid(bid3);
    	  
    	  expReport1 = objectModel.createExperienceReport(rUser1, rUser2, 5, "test1", new Date("1/2/1999"));
    	  objectModel.storeExperienceReport(expReport1);
          
    	  expReport2 = objectModel.createExperienceReport(rUser2, rUser3, 4, "test2", new Date("1/3/2014"));
    	  objectModel.storeExperienceReport(expReport2);
    	  
    	  expReport3 = objectModel.createExperienceReport(rUser3, rUser1, 3, "test3", new Date("1/4/2009"));
    	  objectModel.storeExperienceReport(expReport3);
    	  
    	  membership1 = objectModel.createMembership(50, new Date("7/11/1993"));
    	  objectModel.storeMembership(membership1);

    	  membership2 = objectModel.createMembership(100, new Date("12/3/1991"));
    	  objectModel.storeMembership(membership2);
    	  
    	  membership3 = objectModel.createMembership(150, new Date("4/13/1996"));
    	  objectModel.storeMembership(membership3);
    	  
          
          System.out.println( "Entity objects created and saved in the persistence module" );
          
      }
      catch( DTException ce) {
          System.err.println( "Exception: " + ce );
          ce.printStackTrace();
      }
      catch( Exception e ) {
          e.printStackTrace();
      }
      finally {
          // close the connection
          try {
              conn.close();
          }
          catch( Exception e ) {
              System.err.println( "Exception: " + e );
		 e.printStackTrace();
          }
      }
 }  
}
