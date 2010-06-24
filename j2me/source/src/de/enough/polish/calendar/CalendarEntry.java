/**
 * 
 */
package de.enough.polish.calendar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;

import de.enough.polish.io.Externalizable;
import de.enough.polish.io.Serializer;


/**
 * Calendar Entry class provides access to data in events provided in the calendar.
 * @author Ramakrishna
 * @author Nagendra Sharma
 * 
 */
public class CalendarEntry implements Externalizable {
	
	private static final int VERSION = 100;

	/** this entry does not recur at all */
	public static final int REOCCURENCE_NONE = 0;
	/** this entry recurs daily */
	public static final int REOCCURENCE_DAILY = 1;
	/** this entry recurs weekly */
	public static final int REOCCURENCE_WEEKLY = 2;
	/** this entry recurs monthly */
	public static final int REOCCURENCE_MONTHLY = 3;
	/** this entry recurs yearly */
	public static final int REOCCURENCE_YEARLY = 4;
	
	
	
	/**
	 * field to contain starting date of calendar event 
	 */
	private Date startDate;
	
	/**
	 * field to contain ending date of calendar event
	 */
	private Date endDate;
	
	/**
	 * field to contain date of creation of calendar event
	 */
	private Date createdDate;
	
	/**
	 * field to contain last modified date of calendar event
	 */
	private Date lastModifiedDate;
	
	/**
	 * field to contain time stamp of calendar event
	 */
	private Date timeStamp;
	
	/**
	 * field to contain whether is all day of calendar event
	 */
	private boolean isAllday;
	
	/**
	 * field to contain duration of minutes of calendar event
	 */
	private int durationInMinutes;
	
	/**
	 * field to contain reoccurence of calendar event
	 */
	private int reoccurence;
	
	/**
	 * field to contain sequence of calendar event
	 */
	private int sequence;
	
	/**
	 * field to contain summary of calendar event
	 */
	private String summary;
	
	
	/**
	 * field to contain alarm interval value 
	*/
	private int alarm;
	
	/**
	 * field to contain notes of calendar entry
	 */
	private String notes;
	
	/**
	 * a collection variable to hold any device specific fields in the form of name/value pairs.
	 */
	private Hashtable otherFields = new Hashtable();
	
	/**
	 * field to contain location of calendar event
	 */
	private String location;
	
	/**
	 * field to contain description of calendar event
	 */
	private String description;
	
	/**
	 * field to contain organizer of calendar event
	 */
	private String organizer;
	
	/**
	 * field to contain status of calendar event
	 */
	private String status;
	
	/**
	 * field to contain type of calendar event
	 */
	private String type;
	
	/**
	 * field to contain id of calendar event
	 */
	private String id;
	
	/**
	 * field to contain a users id of calendar event (e.g. for birthdays and anniversaries)
	 */
	private String userId;
	
	/**
	 * field to contain classType of calendar event
	 */
	private String classType;
	
	/**
	 * field to contain category of calendar event
	 */
	private CalendarCategory category;
	
	/**
	 * field to contain details of alarm 
	 */
	private CalendarAlarm calendarAlarm;



	/**
	 * field to contain the details for repeat rule
	 */
	transient private EventRepeatRule eventRepeatRule;
	
	
	/**
	 * field to contain timeZone of calendar event
	 */
	private transient TimeZone timeZone;
	
	
	/**
	 * Constructor for CalendarEntry
	 */
	public CalendarEntry() {
        
	}

	public CalendarEntry(String summary, CalendarCategory category, int year, int month, int day){
		this(summary, category, year, month, day, null);
	}
	
	public CalendarEntry(String summary, CalendarCategory category, int year, int month, int day, int reoccurence){
		this(summary, category, year, month, day, null, reoccurence);
	}
	
	public CalendarEntry(String summary, CalendarCategory category, int year, int month, int day, String description){
		this(summary, category, year, month, day, null, CalendarEntry.REOCCURENCE_NONE);
	}
	
	public CalendarEntry(String summary, CalendarCategory category, int year, int month, int day, String description, int reoccurence){
		this.summary = summary;
		this.category = category;
		this.startDate = CalendarHelper.getDate(year, month, day);
		this.description = description;
		this.reoccurence = reoccurence;
	}
	
	/**
	 * Overloaded constructor of CalendarEntry to initialize staring date and description of calendar event
	 * @param startDate
	 * @param description
	 */
	public CalendarEntry(Date startDate,String description) {
		this.startDate=startDate;
		this.description = description;
	}
	
	/**
	 * Overloaded constructor of CalendarEntry to initialize the below fields
	 * @param startDate
	 * @param endDate
	 * @param isAllday
	 * @param category
	 * @param description
	 * @param timeZone
	 * @param durationInMinutes
	 */
	CalendarEntry(Date startDate, Date endDate, boolean isAllday,
			CalendarCategory category, String description, TimeZone timeZone,
			int durationInMinutes) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.isAllday = isAllday;
		this.category = category;
		this.description = description;
		this.timeZone = timeZone;
		this.durationInMinutes = durationInMinutes;
		
	}
	
	/**
	 * Overloaded constructor of CalendarEntry to initialize the below fields
	 * @param startDate
	 * @param endDate
	 * @param isAllday
	 * @param category
	 * @param description
	 * @param timeZone
	 * @param durationInMinutes
	 * @param location
	 * @param reoccurence
	 * @param organizer
	 */
	CalendarEntry(Date startDate, Date endDate, boolean isAllday,
			CalendarCategory category, String description, TimeZone timeZone,
			int durationInMinutes,String location,	int reoccurence,String organizer) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.isAllday = isAllday;
		this.category = category;
		this.description = description;
		this.timeZone = timeZone;
		this.durationInMinutes = durationInMinutes;
		this.location=location;
		this.reoccurence=reoccurence;
		this.organizer=organizer;
		
	}
	
	/**
	 * @return returns alarm setting of calendar entry
	 */
	public CalendarAlarm getCalendarAlarm() {
		return this.calendarAlarm;
	}

	/**
	 * @return returns category of calendar entry
	 */
	public CalendarCategory getCategory() {
		return this.category;
	}

	/**
	 * @return returns category of calendar entry
	 */
	public String getDescription() {
		this.description = this.description != null ? this.description: "";
		return this.description;
	}
	
	/**
	 * @return returns starting date of calendar entry
	 */
	public Date getStartDate() {
		return this.startDate;
	}
	
	/**
	 * @return returns ending date of calendar entry
	 */
	public Date getEndDate() {
		return this.endDate;
	}
	
	/**
	 * @return returns time stamp of calendar entry
	 */
	public Date getTimeStamp() {
		return this.timeStamp;
	}
	
	/**
	 * @return returns date of creation of calendar entry
	 */
	public Date getCreatedDate() {
		return this.createdDate;
	}

	/**
	 * @return returns last modified date of calendar entry
	 */
	public Date getLastModifiedDate() {
		return this.lastModifiedDate;
	}

	/**
	 * @return returns local time zone of calendar entry
	 */
	public TimeZone getTimeZone() {
		return this.timeZone;
	}
	
	/**
	 * @return returns duration in minutes of calendar entry
	 */
	public int getDurationInMinutes() {
		return this.durationInMinutes;
	}
	
	/**
	 * @return returns organizer of calendar entry
	 */
	public String getOrganizer() {
		this.organizer = this.organizer != null ? this.organizer: "";
		return this.organizer;
	}
	
	/**
	 * @return returns location of calendar entry
	 */
	public String getLocation() {
		this.location = this.location != null ? this.location: "";
		return this.location;
	}
	
	/**
	 * @return returns reoccurence of calendar entry
	 */
	public int getReoccurence() {
		return this.reoccurence;
	}
	
	/**
	 * @return returns sequence of calendar entry
	 */
	public int getSequence() {
		return this.sequence;
	}
	
	/**
	 * @return returns status of calendar entry
	 */
	public String getStatus() {
		this.status = this.status != null ? this.status: "";
		return this.status;
	}
	
	/**
	 * @return returns type of calendar entry
	 */
	public String getType() {
		this.type = this.type != null ? this.type: "";
		return this.type;
	}

	/**
	 * @return returns isAllday of calendar entry
	 */
	public boolean isAllday() {
		return this.isAllday;
	}
	
		
	/**
	 * @return returns id of calendar entry
	 */
	public String getId() {
		this.id = this.id != null ? this.id: "";
		return this.id;
	}
	
	/**
	 * @return returns classType of calendar entry
	 */
	public String getClassType() {
		this.classType = this.classType != null ? this.classType: "";
		return this.classType;
	}
	
	/**
	 * @return returns summary of calendar entry
	 */
	public String getSummary() {
		this.summary = this.summary != null ? this.summary: "";
		return this.summary;
	}

	/**
	 * setter method for CalendarCategory
	 * @param category
	 */
	public void setCategory(CalendarCategory category) {
		this.category = category;
	}
	
	
	
	/**
	 * setter method for calendarAlarm
	 * @param calendarAlarm
	 */
	public void setCalendarAlarm(CalendarAlarm calendarAlarm) {
		this.calendarAlarm = calendarAlarm;
	}

	/**
	 * setter method to set starting date and duration in minutes for calendar entry
	 * @param startDate
	 * @param durationInMinutes
	 */
	public void setDate(Date startDate, int durationInMinutes) {
		this.startDate = startDate;
		this.durationInMinutes = durationInMinutes;
	}

	/**
	 * setter method for event description
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * setter method for start date 
	 * @param startDate
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * setter method for end date
	 * @param endDate
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	/**
	 * setter method for time stamp details
	 * @param timeStamp
	 */
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	
	/**
	 * setter method for date of creation
	 * @param createdDate
	 */
	public void setCreatedDate(Date createdDate) {
	  this.createdDate=createdDate;
	}

	/**
	 * setter method for last modified date
	 * @param lastModifiedDate
	 */
	public void  setLastModifiedDate(Date lastModifiedDate) {
	 this.lastModifiedDate=lastModifiedDate;
	}

	
	
	/**
	 * setter method for local time zone
	 * @param timeZone
	 */
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	

	/**
	 * setter method for duration in minutes
	 * @param durationInMinutes
	 */
	public void setDurationInMinutes(int durationInMinutes) {
		this.durationInMinutes = durationInMinutes;
	}
	
	

	/**
	 * setter method for organizer
	 * @param organizer
	 */
	public void setOrganizer(String organizer) {
		this.organizer = organizer;
	}

	

	/**
	 * setter method for location
	 * @param location
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	

	/**
	 * setter method for reoccurence
	 * @param reoccurence
	 */
	public void setReoccurence(int reoccurence) {
		this.reoccurence = reoccurence;
	}

	
	/**
	 * setter method for status
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * setter method for type
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	

	/**
	 * setter method for id
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	

	/**
	 * setter method for classType of calendar entry
	 * @param classType
	 */
	public void setClassType(String classType) {
		this.classType = classType;
	}

	/**
	 * setter method for isAllday of calendar entry
	 * @param isAllday
	 */
	public void setAllday(boolean isAllday) {
		this.isAllday = isAllday;
	}

	
	/**
	 * setter method for sequence of calendar entry
	 * @param sequence
	 */
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	
	/**
	 * setter method for summary of calendar entry
	 * @param summary
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * @return returns the repeat rule of calendar entry
	 */
	public EventRepeatRule getRepeat() {
		return this.eventRepeatRule;
	}

	/**
	 * setter method for repeat rule of calendar entry
	 * @param eventRepeatRule
	 */
	public void setRepeat(EventRepeatRule eventRepeatRule) {
		this.eventRepeatRule = eventRepeatRule;
	}

	/**
	 * @return the alarm value
	 */
	public int getAlarm() {
		return this.alarm;
	}

	/**
	 * @param alarm sets the alarm to the given value 
	 */
	public void setAlarm(int alarm) {
		this.alarm = alarm;
	}

	/**
	 * @return notes gets the notes for this calendar entry  
	 */
	public String getNotes() {
		return this.notes;
	}

	/**
	 * @param notes sets the notes for this calendar entry
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * @return otherFields gets the the other fields of this calendar entry
	 */
	public Hashtable getOtherFields() {
		return this.otherFields;
	}

	/**
	 * @param otherFields gets the the other fields of this calendar entry
	 */
	public void setOtherFields(Hashtable otherFields) {
		this.otherFields = otherFields;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}


	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeInt(VERSION);
		long time = -1;
		if (this.startDate != null) {
			time = this.startDate.getTime();
		}
		out.writeLong( time );
		time = -1;
		if (this.endDate != null) {
			time = this.endDate.getTime();
		}
		out.writeLong( time );
		time = -1;
		if (this.createdDate != null) {
			time = this.createdDate.getTime();
		}
		out.writeLong( time );
		time = -1;
		if (this.lastModifiedDate != null) {
			time = this.lastModifiedDate.getTime();
		}
		out.writeLong( time );
		time = -1;
		if (this.timeStamp != null) {
			time = this.timeStamp.getTime();
		}
		out.writeLong( time );
		out.writeBoolean( this.isAllday );
		out.writeInt( this.durationInMinutes );
		out.writeInt( this.reoccurence );
		out.writeInt( this.sequence );
		boolean isNotNull = (this.summary != null);
		out.writeBoolean( isNotNull );
		if (isNotNull) {
			out.writeUTF( this.summary );
		}
		out.writeInt( this.alarm );
		isNotNull = (this.notes != null);
		out.writeBoolean( isNotNull );
		if (isNotNull) {
			out.writeUTF( this.notes );
		}
		isNotNull = (this.otherFields != null);
		out.writeBoolean( isNotNull );
		if (isNotNull) {
			Serializer.serialize(this.otherFields, out);
		}
		isNotNull = (this.location != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeUTF(this.location);
		}
		isNotNull = (this.description != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeUTF(this.description);
		}
		isNotNull = (this.organizer != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeUTF(this.organizer);
		}		
		isNotNull = (this.status != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeUTF(this.status);
		}
		isNotNull = (this.type != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeUTF(this.type);
		}
		isNotNull = (this.id != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeUTF(this.id);
		}
		isNotNull = (this.userId != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeUTF(this.userId);
		}
		isNotNull = (this.classType != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeUTF(this.classType);
		}
		isNotNull = (this.category != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			this.category.write( out );
		}
		isNotNull = (this.calendarAlarm != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			this.calendarAlarm.write( out );
		}
		isNotNull = (this.eventRepeatRule != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			this.eventRepeatRule.write( out );
		}
		isNotNull = (this.timeZone != null);
		out.writeBoolean( isNotNull );
		if (isNotNull) {
			out.writeUTF(this.timeZone.getID());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException {
		int version = in.readInt();
		if (version != VERSION) {
			throw new IOException("unknown version " + version);
		}
		long time = in.readLong();
		if (time != -1) {
			this.startDate = new Date( time );
		}
		time = in.readLong();
		if (time != -1) {
			this.endDate = new Date( time );
		}
		time = in.readLong();
		if (time != -1) {
			this.createdDate = new Date( time );
		}
		time = in.readLong();
		if (time != -1) {
			this.lastModifiedDate = new Date( time );
		}
		time = in.readLong();
		if (time != -1) {
			this.timeStamp = new Date( time );
		}
		this.isAllday = in.readBoolean();
		this.durationInMinutes = in.readInt();
		this.reoccurence = in.readInt();
		this.sequence = in.readInt();
		boolean isNotNull = in.readBoolean();
		if (isNotNull) {
			this.summary = in.readUTF();
		}
		this.alarm = in.readInt();
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.notes = in.readUTF();
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.otherFields = (Hashtable) Serializer.deserialize(in);
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.location = in.readUTF();
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.description = in.readUTF();
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.organizer = in.readUTF();
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.status = in.readUTF();
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.type = in.readUTF();
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.id = in.readUTF();
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.userId = in.readUTF();
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.classType = in.readUTF();
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.category = new CalendarCategory();
			this.category.read(in);
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.calendarAlarm = new CalendarAlarm();
			this.calendarAlarm.read(in);
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.eventRepeatRule = new EventRepeatRule();
			this.eventRepeatRule.read(in);
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			String timeZoneId = in.readUTF();
			this.timeZone = TimeZone.getTimeZone(timeZoneId);
		}
	}
	
	
	
}
