package itm.audio;

/*******************************************************************************
 This file is part of the ITM course 2016
 (c) University of Vienna 2009-2016
 *******************************************************************************/

import itm.model.AudioMedia;
import itm.model.MediaFactory;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * This class reads audio files of various formats and stores some basic audio
 * metadata to text files. It can be called with 3 parameters, an input
 * filename/directory, an output directory and an "overwrite" flag. It will read
 * the input audio file(s), retrieve some metadata and write it to a text file
 * in the output directory. The overwrite flag indicates whether the resulting
 * output file should be overwritten or not.
 * 
 * If the input file or the output directory do not exist, an exception is
 * thrown.
 */
public class AudioMetadataGenerator {

	/**
	 * Constructor.
	 */
	public AudioMetadataGenerator() {
	}

	/**
	 * Processes an audio file directory in a batch process.
	 * 
	 * @param input
	 *            a reference to the audio file directory
	 * @param output
	 *            a reference to the output directory
	 * @param overwrite
	 *            indicates whether existing metadata files should be
	 *            overwritten or not
	 * @return a list of the created media objects (images)
	 */
	public ArrayList<AudioMedia> batchProcessAudio(File input, File output,
			boolean overwrite) throws IOException {
		if (!input.exists())
			throw new IOException("Input file " + input + " was not found!");
		if (!output.exists())
			throw new IOException("Output directory " + output + " not found!");
		if (!output.isDirectory())
			throw new IOException(output + " is not a directory!");

		ArrayList<AudioMedia> ret = new ArrayList<AudioMedia>();

		if (input.isDirectory()) {
			File[] files = input.listFiles();
			for (File f : files) {

				String ext = f.getName().substring(
						f.getName().lastIndexOf(".") + 1).toLowerCase();
				if (ext.equals("wav") || ext.equals("mp3") || ext.equals("ogg")) {
					try {
						AudioMedia result = processAudio(f, output, overwrite);
						System.out.println("created metadata for file " + f
								+ " in " + output);
						ret.add(result);
					} catch (Exception e0) {
						System.err
								.println("Error when creating metadata from file "
										+ input + " : " + e0.toString());
					}

				}

			}
		} else {

			String ext = input.getName().substring(
					input.getName().lastIndexOf(".") + 1).toLowerCase();
			if (ext.equals("wav") || ext.equals("mp3") || ext.equals("ogg")) {
				try {
					AudioMedia result = processAudio(input, output, overwrite);
					System.out.println("created metadata for file " + input
							+ " in " + output);
					ret.add(result);
				} catch (Exception e0) {
					System.err
							.println("Error when creating metadata from file "
									+ input + " : " + e0.toString());
					e0.printStackTrace();
				}

			}

		}
		return ret;
	}

	/**
	 * Processes the passed input audio file and stores the extracted metadata
	 * to a textfile in the output directory.
	 * 
	 * @param input
	 *            a reference to the input audio file
	 * @param output
	 *            a reference to the output directory
	 * @param overwrite
	 *            indicates whether existing metadata files should be
	 *            overwritten or not
	 * @return the created image media object
	 */
	protected AudioMedia processAudio(File input, File output, boolean overwrite)
			throws IOException, IllegalArgumentException {
		if (!input.exists())
			throw new IOException("Input file " + input + " was not found!");
		if (input.isDirectory())
			throw new IOException("Input file " + input + " is a directory!");
		if (!output.exists())
			throw new IOException("Output directory " + output + " not found!");
		if (!output.isDirectory())
			throw new IOException(output + " is not a directory!");

		// create outputfilename and check whether thumb already exists. All
		// image metadata files have to start with "aud_" - this is used by the
		// mediafactory!
		File outputFile = new File(output, "aud_" + input.getName() + ".txt");
		if (outputFile.exists())
			if (!overwrite) {
				// load from file
				AudioMedia media = new AudioMedia();
				media.readFromFile(outputFile);
				return media;
			}

		
		// ***************************************************************
		// Fill in your code here!
		// ***************************************************************

		// create an audio metadata object
		AudioMedia media = (AudioMedia) MediaFactory.createMedia(input);		
		
		// load the input audio file, do not decode
		// you might have to distinguish what properties are available for what audio format
		try {
			// read AudioFormat properties
			AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(input);						
			AudioFormat audioFormat = fileFormat.getFormat();
			
//			System.out.println("file: " + fileFormat.toString());
//			System.out.println("audio: " + audioFormat.toString());
			
			media.addTag(fileFormat.getType().getExtension());
			
			media.setEncoding(audioFormat.getEncoding().toString());
			media.setFrequency((int)audioFormat.getSampleRate());
			media.setChannels(audioFormat.getChannels());
			//media.setBitrate(audioFormat.getSampleSizeInBits());
			
			if (fileFormat.getFrameLength() < 0) {
				// it's probably the JAR-bug again (full issue see below)
				// there's nothing I can do but notify the user via -1 that it went wrong
				media.setDuration(-1);
				
				// they're all negative except the filesize
//				AudioInputStream ais = AudioSystem.getAudioInputStream(input);
//				System.out.println(
//						"Filesize: " + input.length() + 
//						"\n Framesize: " + ais.getFormat().getFrameSize() + 
//						"\n Framerate: " + ais.getFormat().getFrameRate() + 
//						"\n Framelength: " + ais.getFrameLength()
//						);
			} else 
				media.setDuration((int)(fileFormat.getFrameLength() / (audioFormat.getSampleRate())));
						
			if (fileFormat.properties() != null && !(fileFormat.properties().isEmpty())) {

				//System.out.println(fileFormat.properties().toString());
				
				media.setAuthor((String)fileFormat.properties().get("author"));
				media.setTitle((String)fileFormat.properties().get("title"));
				
				String datestr = (String)fileFormat.properties().get("date");
				if (datestr != null) {
					try {
						media.setDate(DateFormat.getInstance().parse(datestr));
					} catch (ParseException e) { media.setDate(null); }
				}
			
				Long duration;
				if ((duration = (Long)fileFormat.properties().get("duration")) != null) 
					media.setDuration(((long)duration)/1000000);
				
				media.setComment((String)fileFormat.properties().get("comment"));
				media.setAlbum((String)fileFormat.properties().get("album"));
				media.setComposer((String)fileFormat.properties().get("composer"));
				
				if (fileFormat.getType().getExtension().equalsIgnoreCase("mp3")) {
					try {
						media.setTrack(Integer.parseInt((String)fileFormat.properties().get("mp3.id3tag.track")));
					} catch (NumberFormatException e) {};
					
					media.setGenre((String)fileFormat.properties().get("mp3.id3tag.genre"));
					
					Integer frequency = (Integer)fileFormat.properties().get("mp3.frequency.hz");
					if (frequency != null) media.setFrequency(frequency);
					
					Integer bitrate = (Integer)fileFormat.properties().get("mp3.bitrate.nominal.bps");
					if (bitrate != null) media.setBitrate(bitrate);
					
					Integer channels = (Integer)fileFormat.properties().get("mp3.channels");
					if (channels != null) media.setChannels(channels);
					
				} else if (fileFormat.getType().getExtension().equalsIgnoreCase("ogg")) {
					try {
						media.setTrack(Integer.parseInt((String)fileFormat.properties().get("ogg.comment.track")));
					} catch (NumberFormatException e) {};
					media.setChannels((Integer)fileFormat.properties().get("ogg.channels"));
					media.setGenre((String)fileFormat.properties().get("ogg.comment.genre"));
					media.setFrequency((int)fileFormat.properties().get("ogg.frequency.hz"));
					media.setBitrate((int)fileFormat.properties().get("ogg.bitrate.nominal.bps"));
				} else if (fileFormat.getType().getExtension().equalsIgnoreCase("wav")) {
					// my experience tells me: this is dead code. wav fileformat.properties() maps ARE empty
				}

			} else { // it's most likely either ogg or wav with tritonis_vorbis?
				
				System.err.println("Please try a different JAR. OR IDE. Or classpath.");
				/** WEIRD OBSERVATIONS when using Eclipse
				 * - the "faulty" vorbisspi always seems to work and never gives nullptrs
				 * 		- whereas the tritonus_jorbis does not seem to work here
				 * - however, if you use vorbisspi, the thumbgenerator produces empty files for ogg -> wav
				 * 		- ... except if you use BOTH of them in IntelliJ (why?!)
				 * 			- the dependencies are different... probably... IJ does it better?
				 *  	- using both in Eclipse still produces empty files
				 * 
				 * */
			}

		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		// add a "audio" tag
		media.addTag("audio");
		
		System.out.println(media);
		
		// close the audio and write the md file.
		media.writeToFile(outputFile);
		return media;
	}

	/**
	 * Main method. Parses the commandline parameters and prints usage
	 * information if required.
	 */
	public static void main(String[] args) throws Exception {

		//args = new String[] { "./media/audio", "./media/md" };
		
		if (args.length < 2) {
			System.out
					.println("usage: java itm.image.AudioMetadataGenerator <input-image> <output-directory>");
			System.out
					.println("usage: java itm.image.AudioMetadataGenerator <input-directory> <output-directory>");
			System.exit(1);
		}
		File fi = new File(args[0]);
		File fo = new File(args[1]);
		AudioMetadataGenerator audioMd = new AudioMetadataGenerator();
		audioMd.batchProcessAudio(fi, fo, true);
	}
}
