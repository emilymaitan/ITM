package itm.video;

/*******************************************************************************
 This file is part of the ITM course 2016
 (c) University of Vienna 2009-2016
 *******************************************************************************/

import itm.model.MediaFactory;
import itm.model.VideoMedia;
import itm.util.IOUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Manager;
import javax.media.Processor;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;

import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.ICodec.Type;

/**
 * This class reads video files, extracts metadata for both the audio and the
 * video track, and writes these metadata to a file.
 * 
 * It can be called with 3 parameters, an input filename/directory, an output
 * directory and an "overwrite" flag. It will read the input video file(s),
 * retrieve the metadata and write it to a text file in the output directory.
 * The overwrite flag indicates whether the resulting output file should be
 * overwritten or not.
 * 
 * If the input file or the output directory do not exist, an exception is
 * thrown.
 */
public class VideoMetadataGenerator {

	/**
	 * Constructor.
	 */
	public VideoMetadataGenerator() {
	}

	/**
	 * Processes a video file directory in a batch process.
	 * 
	 * @param input
	 *            a reference to the video file directory
	 * @param output
	 *            a reference to the output directory
	 * @param overwrite
	 *            indicates whether existing metadata files should be
	 *            overwritten or not
	 * @return a list of the created media objects (videos)
	 */
	public ArrayList<VideoMedia> batchProcessVideoFiles(File input, File output, boolean overwrite) throws IOException {
		if (!input.exists())
			throw new IOException("Input file " + input + " was not found!");
		if (!output.exists())
			throw new IOException("Output directory " + output + " not found!");
		if (!output.isDirectory())
			throw new IOException(output + " is not a directory!");

		ArrayList<VideoMedia> ret = new ArrayList<VideoMedia>();

		if (input.isDirectory()) {
			File[] files = input.listFiles();
			for (File f : files) {

				String ext = f.getName().substring(f.getName().lastIndexOf(".") + 1).toLowerCase();
				if (ext.equals("avi") || ext.equals("swf") || ext.equals("asf") || ext.equals("flv")
						|| ext.equals("mp4"))
					try {
						VideoMedia result = processVideo(f, output, overwrite);
						System.out.println("created metadata for file " + f + " in " + output);
						ret.add(result);
					} catch (Exception e0) {
						System.err.println("Error when creating metadata from file " + input + " : " + e0.toString());
					}

			}
		} else {

			String ext = input.getName().substring(input.getName().lastIndexOf(".") + 1).toLowerCase();
			if (ext.equals("avi") || ext.equals("swf") || ext.equals("asf") || ext.equals("flv") || ext.equals("mp4"))
				try {
					VideoMedia result = processVideo(input, output, overwrite);
					System.out.println("created metadata for file " + input + " in " + output);
					ret.add(result);
				} catch (Exception e0) {
					System.err.println("Error when creating metadata from file " + input + " : " + e0.toString());
				}

		}
		return ret;
	}

	/**
	 * Processes the passed input video file and stores the extracted metadata
	 * to a textfile in the output directory.
	 * 
	 * @param input
	 *            a reference to the input video file
	 * @param output
	 *            a reference to the output directory
	 * @param overwrite
	 *            indicates whether existing metadata files should be
	 *            overwritten or not
	 * @return the created video media object
	 */
	protected VideoMedia processVideo(File input, File output, boolean overwrite) throws Exception {
		if (!input.exists())
			throw new IOException("Input file " + input + " was not found!");
		if (input.isDirectory())
			throw new IOException("Input file " + input + " is a directory!");
		if (!output.exists())
			throw new IOException("Output directory " + output + " not found!");
		if (!output.isDirectory())
			throw new IOException(output + " is not a directory!");

		// create outputfilename and check whether thumb already exists.
		// all video metadata files have to start with "vid_" - this is used by
		// the
		// mediafactory!
		File outputFile = new File(output, "vid_" + input.getName() + ".txt");
		if (outputFile.exists())
			if (!overwrite) {
				// load from file
				VideoMedia media = new VideoMedia();
				media.readFromFile(outputFile);
				return media;
			}

		// ***************************************************************
		// Fill in your code here!
		// ***************************************************************
		
		
		// create video media object
		VideoMedia media = (VideoMedia) MediaFactory.createMedia(input);

		// set video and audio stream metadata
			// sources for the following code: xuggler API and http://web.archive.org/web/20130404071015/http://www.javacodegeeks.com/2011/02/introduction-xuggler-video-manipulation.html
			
		/* STUDY AID - XUGGLER CLASSES (taken straight from the API doc)
		 * 		So that you know what you are doing here :)
		 *
		 * IContainer: a file that contains one or more streams of audio and video data
		 *
		 * IStream: a virtual concept, because IContainers just contain IPackets
		 *   each Packet has a StreamId, and all Packets with same ID represent same type of data (usually timebased)
		 *   is mainly used to get get a proper IStreamCoder for decoder
		 *
		 * IStreamCoder: the "work horse"
		 *   takes IPacket data from IContainer and takes ICodec for de-/encoding the data
		 *
		 * IPacket: represents an encoded piece of data for decoding; read it out from IContainer do decode, 
		 *   and pass it to IstreamCoder to en/decode.
		 *   the timestamp in an IPacket depends on IContainer; eg FLV is milliseconds
		 *   Xuggler API uses microsecs for raw data (eg IVideoPicture) and converts when decoding
		 *
		 * IVideoPicture: an unencoded picture; use ConverterFactory to decode
		 *
		 * */
		
		// first we'll need container and Co for extracting the data
					// -> the imports were already there, so I have an idea what to use :) Thanks!
		IContainer container = IContainer.make();
		if (container.open(input.getAbsolutePath(), IContainer.Type.READ, null) < 0) 
			throw new RuntimeException("OOPS! Something went wrong reading the file...");
		
		for (int i = 0; i < container.getNumStreams(); i++) {
			IStream stream = container.getStream(i);
			if (stream == null) throw new RuntimeException("OOPS! Could not extract stream for stream @id " + i);
			IStreamCoder coder = stream.getStreamCoder();
			if (coder == null) throw new RuntimeException("OOPS! Could not extract coder for stream @id " + i);
			
			// -> now we've got what we need to extract stuff; first check which stream it is
			
			switch (coder.getCodecType()) {
			case CODEC_TYPE_AUDIO:
				media.setAudioCodec(coder.getCodec().getName());
				media.setAudiocCodecID(Integer.valueOf(coder.getCodec().getIDAsInt()));
				media.setAudioChannels(Integer.valueOf(coder.getChannels()));
				media.setAudioSampleRate(Integer.valueOf(coder.getSampleRate()));
				media.setAudioBitRate(Integer.valueOf(coder.getBitRate()/1000)); // converting to kb/sec
				break;
			case CODEC_TYPE_VIDEO:
				media.setVideoCodec(coder.getCodec().getName());
				media.setVideoCodecID(Integer.valueOf(coder.getCodec().getIDAsInt()));
				media.setVideoFrameRate(Double.valueOf(coder.getFrameRate().getDouble()));
				media.setVideoLength(Long.valueOf((stream.getDuration()*coder.getTimeBase().getNumerator()) / coder.getTimeBase().getDenominator()));
				media.setVideoWidth(Integer.valueOf(coder.getWidth()));
				media.setVideoHeight(Integer.valueOf(coder.getHeight()));
				break;
			default:
				break; 
			}
			
		}
		
		
		// add video tag
		media.addTag("video");

		// write metadata
		StringBuffer buf = media.serializeObject();
		IOUtil.writeFile(buf, outputFile);
		
		// counter-test: deserialize the thing from the freshly created file
/*		VideoMedia copy = new VideoMedia();
		copy.readFromFile(outputFile);
		System.out.println(copy.toString()); */
		
		
		return media;
	}

	/**
	 * Main method. Parses the commandline parameters and prints usage
	 * information if required.
	 */
	public static void main(String[] args) throws Exception {

		// args = new String[] {"./media/video", "./media/md"};

		if (args.length < 2) {
			System.out.println("usage: java itm.video.VideoMetadataGenerator <input-video> <output-directory>");
			System.out.println("usage: java itm.video.VideoMetadataGenerator <input-directory> <output-directory>");
			System.exit(1);
		}
		File fi = new File(args[0]);
		File fo = new File(args[1]);
		VideoMetadataGenerator videoMd = new VideoMetadataGenerator();
		videoMd.batchProcessVideoFiles(fi, fo, true);
	}
}
