package com.google.sps.data;

import java.util.Date;

// Class to handle comments being pushed to the server.

public final class UserComment {
    private String name;
    private String text;
    private Date date;

    public UserComment(String name, String text) {
        this.name = name;
        this.text = text;
        this.date = new Date();
    }

    /*
     * getDate returns data (Date class) associated with specific UserComment.
     */

    public Date getDate() {
        return this.date;
    } /* getDate() */
    
    /*
     * getName returns the String name associated with the UserComment.
     */

    public String getName() {
        return this.name;
    } /* getName() */

    /*
    * getName returns the String text associated with the UserComment.
    */

    public String getText() {
        return this.text;
    } /* getText() */

    /*
    * setText sets the String text associated with the UserComment.
    * Not used at the moment.
    */

    public void setText(String text) {
        this.text = text;
    } /* setText() */

    /*
    * setName sets the String name associated with the UserComment.
    * Not used at the moment.
    */

    public void setName(String name) {
        this.name = name;
    } /* setName() */
}