// Gnu Emacs C++ mode:  -*- Java -*-
//
// Class:	ItemIteratorImpl
//
// S.J. DeBrock
//
//
//

package edu.uga.dawgtrades.persist.impl;



import java.sql.ResultSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.uga.dawgtrades.model.DTException;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.Item;

public class ItemIterator
    implements Iterator<Item>
{
    private ResultSet   rs = null;
    private ObjectModel objectModel = null;
    private boolean     more = false;

    // these two will be used to create a new object
    //
    public ItemIterator( ResultSet rs, ObjectModel objectModel )
            throws DTException
    { 
        this.rs = rs;
        this.objectModel = objectModel;
        try {
            more = rs.next();
        }
        catch( Exception e ) {	// just in case...
            throw new DTException( "ItemIterator: Cannot create Item iterator; root cause: " + e );
        }
    }

    public boolean hasNext() 
    { 
        return more; 
    }

    public Item next()
    {
        long id;
        String name;
        String description;
        long ownerId;
        long categoryId;

        if( more ) {

            try {
                id = rs.getLong( 1 );
                name = rs.getString( 2 );
                description = rs.getString( 3 );
                ownerId = rs.getLong( 4 );
                categoryId = rs.getLong( 5 );

                more = rs.next();
            }
            catch( Exception e ) {	// just in case...
                throw new NoSuchElementException( "ItemIterator: No next Item object; root cause: " + e );
            }
            
            Item item = objectModel.createItem();
            item.setId( id );
            item.setName(name);
            item.setDescription(description);
            item.setOwnerId(ownerId);
            item.setCategoryId(categoryId);
            
            return item;
        }
        else {
            throw new NoSuchElementException( "ItemIterator: No next Item object" );
        }
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }

};
