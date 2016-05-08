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
	 * Integer representation of the codec id.
	 */
	protected Integer codecID;
	/**
	 * The framerate (fps?).
	 */
	protected Double videoFrameRate;
	/**
	 * The length of the video in seconds.
	 */
	protected Long videoLength;
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
	
	public Integer getCodecID() {
		return codecID;
	}

	public void setCodecID(Integer codecID) {
		this.codecID = codecID;
	}
	
	public Double getVideoFrameRate() {
		return videoFrameRate;
	}

	public void setVideoFrameRate(Double videoFrameRate) {
		this.videoFrameRate = videoFrameRate;
	}

	public Long getVideoLength() {
		return videoLength;
	}

	public void setVideoLength(Long videoLength) {
		this.videoLength = videoLength;
	}

	public Integer getVideoHeight() {
		return videoHeight;
	}

	public void setVideoHeight(Integer videoHeight) {
		this.videoHeight = videoHeight;
	}

	public Integer getVideoWidth() {
		return videoWidth;
	}

	public void setVideoWidth(Integer videoWidth) {
		this.videoWidth = videoWidth;
	}

	public String getAudioCodec() {
		return audioCodec;
	}

	public void setAudioCodec(String audioCodec) {
		this.audioCodec = audioCodec;
	}

	public Integer getAudiocCodecID() {
		return audiocCodecID;
	}

	public void setAudiocCodecID(Integer audiocCodecID) {
		this.audiocCodecID = audiocCodecID;
	}

	public Integer getAudioChannels() {
		return audioChannels;
	}

	public void setAudioChannels(Integer audioChannels) {
		this.audioChannels = audioChannels;
	}

	public Integer getAudioSampleRate() {
		return audioSampleRate;
	}

	public void setAudioSampleRate(Integer audioSampleRate) {
		this.audioSampleRate = audioSampleRate;
	}

	public Integer getAudioBitRate() {
		return audioBitRate;
	}

	public void setAudioBitRate(Integer audioBitRate) {
		this.audioBitRate = audioBitRate;
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
		out.println("videoCodec: " + this.getVideoCodec());
		out.println("codecID: " + this.getCodecID());
		out.println("videoFrameRate: " + this.getVideoFrameRate());
		out.println("videoLength: " + this.getVideoLength());
		out.println("videoWidth: " + this.getVideoWidth());
		out.println("videoHeight: " + this.getVideoHeight());
		out.println();
		out.println("audioCodec: " + this.getAudioCodec());
		out.println("audioCodecID: " + this.getAudiocCodecID());
		out.println("audioChannels: " + this.getAudioChannels());
		out.println("audioSampleRate: " + this.getAudioSampleRate());
		out.println("audioBitRate: " + this.getAudioBitRate());
		
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
