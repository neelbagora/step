package com.google.sps.data;

import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Locale;

// Class to handle comments being pushed to the server.
public final class UserComment {
	private long id;
	private String name;
	private String text;
	private String date;
  private long originalTimeStamp;
  private String user_id;

	/**
	 * Constructor for UserComment Class
	 *
	 * @param id        Long to initialize (server) id instance variable.
	 * @param name      String to initialize name instance variable.
	 * @param text      String to initiailize text instance variable.
	 * @param timestamp Long value to be converted into readable date format.
   * @param user_id   String id of original poster.
   * @param edited   boolean representing if the comment is edited.
	 */
	public UserComment(long id, String name, String text, long timestamp, String user_id, boolean edited) {
		this.id = id;
		this.name = name;
		this.text = text; 
		this.date = convertTime(timestamp);
    if (edited) {
      this.date = "Edited on " + this.date;
    }
    originalTimeStamp = timestamp;
    this.user_id = user_id;
  }

	/**
	 * Constructor for UserComment Class
	 *
	 * @param id        Long to initialize (server) id instance variable.
	 * @param name      String to initialize name instance variable.
	 * @param text      String to initiailize text instance variable.
	 * @param timestamp Long value to be converted into readable date format.
   * @param user_id   String id of original poster.
	 */
  public UserComment(long id, String name, String text, long timestamp, String user_id) {
		this.id = id;
		this.name = name;
		this.text = text; 
		this.date = convertTime(timestamp);
    originalTimeStamp = timestamp;
    this.user_id = user_id;
  }
	
	/**
	 * Constructor for UserComment Class
	 * Default value for id = 0 and date = "0"
	 *
	 * @param name String to initialize name instance variable.
	 * @param text String to initiailize text instance variable.
	 */
	public UserComment(String name, String text) {
		this.id = 0;
		this.name = name;
		this.text = text;
		this.date = "0";
    originalTimeStamp = 0;
	}

	/**
	 * convertTime takes in the long representation of the current time
	 * in milliseconds and converts it to a readable date format in the
	 * form "MM/dd/yyyy hh:mm:ss [timezone]".
	 *
	 * @param timestamp long representing timestamp in milliseconds
	 * @return          String date converted from long.
	 */
	private String convertTime(long timestamp) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss  a", Locale.US);
		String timezone = "PST";
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
		Date date = new Date(timestamp);
		return simpleDateFormat.format(date) + " " + timezone;
	}

	/**
	 * getDate returns data (Date class) associated with specific UserComment.
	 *
	 * @return Returns value contained by date.
	 */
	public String getDate() {
		return this.date;
	}
	
	/**
	 * getName returns the String name associated with the UserComment.
	 *
	 * @return Returns value contained by name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * getName returns the String text associated with the UserComment.
	 *
	 * @return Returns value contained by text.
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * setText sets the String text associated with the UserComment.
	 * Not used at the moment.
	 *
	 * @param text Message that will replace value held by text variable.
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * setName sets the String name associated with the UserComment.
	 * Not used at the moment.
	 *
	 * @param name Name that will replace value held by name variable.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * setDate sets the String date associated with the UserComment.
	 *
	 * @param name Name that will replace value held by name variable.
	 */
  public void setDate(String date) {
    this.date = date;
  }

  /**
	 * returns the String datatype representing the user posting the comment.
	 */
  public String getUserId() {
    return this.user_id;
  }
}
