package itm.model;

/*******************************************************************************
 This file is part of the ITM course 2016
 (c) University of Vienna 2009-2016
 *******************************************************************************/

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class AudioMedia extends AbstractMedia {

	private String encoding = null;
	private long duration = 0;
	private String author = null;
	private String title = null;
	private Date date = null;
	private String comment = null;
	private String album = null;
	private int track = 0;
	private String composer = null;
	private String genre = null;
	private int frequency = 0;
	private int bitrate = 0;
	private int channels = 0;

	/**
	 * Constructor.
	 */
	public AudioMedia() {
		super();
	}

	/**
	 * Constructor.
	 */
	public AudioMedia(File instance) {
		super(instance);
	}

	/* (de-)serialization */

	/**
	 * Serializes this object to the passed file.
	 * 
	 */
	@Override
	public StringBuffer serializeObject() throws IOException {
		StringWriter data = new StringWriter();
		PrintWriter out = new PrintWriter(data);
		out.println("type: audio");
		StringBuffer sup = super.serializeObject();
		out.print(sup);

		// ***************************************************************
		// Fill in your code here!
		// ***************************************************************

		out.println("encoding: " + getEncoding());
		out.println("duration: " + getDuration());
		if (getAuthor() != null)
			out.println("author: " + getAuthor());
		if (getTitle() != null)
			out.println("title: " + getTitle());
		if (getDate() != null) {
			DateFormat df = DateFormat.getInstance();
			out.println("date: " + df.format(getDate()));
		}
		if (getComment() != null)
			out.println("comment: " + getComment());
		if (getAlbum() != null)
			out.println("album: " + getAlbum());
		if (getTrack() != 0)
			out.println("track: " + getTrack());
		if (getComposer() != null)
			out.println("composer: " + getComposer());
		if (getGenre() != null)
			out.println("genre: " + getGenre());
		out.println("frequency: " + getFrequency());
		out.println("bitrate: " + getBitrate());
		out.println("channels: " + getChannels());

		return data.getBuffer();
	}

	/**
	 * Deserializes this object from the passed string buffer.
	 */
	@Override
	public void deserializeObject(String data) throws IOException {
		super.deserializeObject(data);

		StringReader sr = new StringReader(data);
		BufferedReader br = new BufferedReader(sr);
		String line = null;
		while ((line = br.readLine()) != null) {

			// ***************************************************************
			// Fill in your code here!
			// ***************************************************************
			if (line.startsWith("encoding: ")) {
				setEncoding(line.substring("encoding: ".length()));
			} else if (line.startsWith("duration: ")) {
				setDuration(Integer.parseInt(line.substring("duration: "
						.length())));
			} else if (line.startsWith("author: ")) {
        		setAuthor(line.substring("author: ".length()));
        	} else if (line.startsWith("title: ")) {
        		setTitle(line.substring("title: ".length()));
        	} else if (line.startsWith("date: ")) {
        		DateFormat df = DateFormat.getInstance();
        		try {
					this.setDate(df.parse(line.substring("date: ".length())));
				} catch (ParseException e) {
				}        		
        	} else if (line.startsWith("comment: ")) {
        		this.setComment(line.substring("comment: ".length()));
        	} else if (line.startsWith("album: ")) {
        		this.setAlbum(line.substring("album: ".length()));
        	} else if (line.startsWith("track: ")) {
        		this.setTrack(Integer.parseInt(line.substring("track: ".length())));
        	} else if (line.startsWith("composer: ")) {
        		this.setComposer(line.substring("composer: ".length()));
        	} else if (line.startsWith("genre: ")) {
        		this.setGenre(line.substring("genre: ".length()));
        	} else if (line.startsWith("frequency: ")) {
        		this.setFrequency(Integer.parseInt(line.substring("frequency: ".length())));
        	} else if (line.startsWith("bitrate: ")) {
        		this.setBitrate(Integer.parseInt(line.substring("bitrate: ".length())));
        	} else if (line.startsWith("channels: ")) {
        		this.setChannels(Integer.parseInt(line.substring("channels: ".length())));
        	}

		}
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public int getTrack() {
		return track;
	}

	public void setTrack(int track) {
		this.track = track;
	}

	public String getComposer() {
		return composer;
	}

	public void setComposer(String composer) {
		this.composer = composer;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public int getBitrate() {
		return bitrate;
	}

	public void setBitrate(int bitrate) {
		this.bitrate = bitrate;
	}

	public int getChannels() {
		return channels;
	}

	public void setChannels(int channels) {
		this.channels = channels;
	}

}
