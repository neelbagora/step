package com.google.sps.data;

import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

// Class to handle comments being pushed to the server.
public final class UserComment {
    private long id;
    private String name;
    private String text;
    private String date;

    public UserComment(long id, String name, String text, long timestamp) {
        this.id = id;
        this.name = name;
        this.text = text; 
        this.date = convertTime(timestamp);
    }
    
    public UserComment(String name, String text) {
        this.id = 0;
        this.name = name;
        this.text = text;
        this.date = "0";
    }

    private String convertTime(long timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
        Date date = new Date(timestamp);
        return simpleDateFormat.format(date);
    }

    /*
     * getDate returns data (Date class) associated with specific UserComment.
     */
    public String getDate() {
        return this.date;
    }
    
    /*
     * getName returns the String name associated with the UserComment.
     */
    public String getName() {
        return this.name;
    }

    /*
    * getName returns the String text associated with the UserComment.
    */
    public String getText() {
        return this.text;
    }

    /*
    * setText sets the String text associated with the UserComment.
    * Not used at the moment.
    */
    public void setText(String text) {
        this.text = text;
    }

    /*
    * setName sets the String name associated with the UserComment.
    * Not used at the moment.
    */
    public void setName(String name) {
        this.name = name;
    }

}