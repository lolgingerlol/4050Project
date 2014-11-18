package edu.uga.dawgtrades.persist.impl;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.uga.dawgtrades.model.DTException;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.RegisteredUser;


public class RegisteredUserIterator 
    implements Iterator<RegisteredUser>
{
    private ResultSet   rs = null;
    private ObjectModel objectModel = null;
    private boolean     more = false;

    // these two will be used to create a new object
    //
    public RegisteredUserIterator( ResultSet rs, ObjectModel objectModel )
            throws DTException
    { 
        this.rs = rs;
        this.objectModel = objectModel;
        try {
            more = rs.next();
        }
        catch( Exception e ) {	// just in case...
            throw new DTException( "RegisteredUserIterator: Cannot create RegisteredUser iterator; root cause: " + e );
        }
    }

    public boolean hasNext() 
    { 
        return more; 
    }

    public RegisteredUser next() 
    {
        long   id;
        String userName;
        String firstName;
        String lastName;
        String password;
        String email;
        String phone;
        boolean isAdmin;
        boolean canText;

        if( more ) {

            try {
                id = rs.getLong( 1 );
                userName = rs.getString( 2 );
                firstName = rs.getString( 3 );
                lastName = rs.getString( 4 );
                password = rs.getString( 5 );
                email = rs.getString( 6 );
                phone = rs.getString( 7 );
                isAdmin = rs.getBoolean(8);
                canText = rs.getBoolean(9);

                more = rs.next();
            }
            catch( Exception e ) {	// just in case...
                throw new NoSuchElementException( "RegisteredUserIterator: No next RegisteredUser object; root cause: " + e );
            }
            
            RegisteredUser person = objectModel.createRegisteredUser( );
            person.setId( id );
            person.setName(userName);
            person.setFirstName(firstName);
            person.setLastName(lastName);
            person.setPassword(password);
            person.setEmail(email);
            person.setPhone(phone);
            person.setIsAdmin(isAdmin);
            person.setCanText(canText);
            
            return person;
        }
        else {
            throw new NoSuchElementException( "RegisteredUserIterator: No next RegisteredUser object" );
        }
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }

};

