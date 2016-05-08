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

public class VideoMedia extends AbstractMedia {

	// ***************************************************************
	// Fill in your code here!
	// ***************************************************************

	/* video format metadata */
	/**
	 * String representation of the video codec.
	 */
	protected String videoCodec;
	/**
	 * The codec id.
	 */
	protected Integer codecID;
	/**
	 * The framerate in seconds.
	 */
	protected Integer videoFrameRate;
	/**
	 * Height of the video in pixels.
	 */
	protected Integer videoHeight;
	/**
	 * Width of the video in pixels.
	 */
	protected Integer videoWidth;
	

	/* audio format metadata */
	/**
	 * The video's audio codec.
	 */
	protected String audioCodec;
	/**
	 * The video's audio codec.
	 */
	protected Integer audiocCodecID;
	/**
	 * The number of the video's audio channels.
	 */
	protected Integer audioChannels;
	/**
	 * The video's audio sample rate in Hz.
	 */
	protected Integer audioSampleRate;
	/**
	 * The video's audio bitrate in kb/s.
	 */
	protected Integer audioBitRate;

	/**
	 * Constructor.
	 */
	public VideoMedia() {
		super();
	}

	/**
	 * Constructor.
	 */
	public VideoMedia(File instance) {
		super(instance);
	}

	/* GET / SET methods */

	// ***************************************************************
	// Fill in your code here!
	// ***************************************************************

	public String getVideoCodec() {
		return videoCodec;
	}

	public void setVideoCodec(String videoCodec) {
		this.videoCodec = videoCodec;
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
		out.println("type: video");
		StringBuffer sup = super.serializeObject();
		out.print(sup);

		/* video fields */

		// ***************************************************************
		// Fill in your code here!
		// ***************************************************************
		out.println("videoCodec: " + videoCodec);
		
		
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

			/* video fields */
			// ***************************************************************
			// Fill in your code here!
			// ***************************************************************
		}
	}

}
