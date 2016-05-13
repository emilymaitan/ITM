package itm.video;

import java.awt.image.BufferedImage;

/*******************************************************************************
 This file is part of the ITM course 2016
 (c) University of Vienna 2009-2016
 *******************************************************************************/

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import javax.imageio.ImageIO;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

/**
 * 
 * This class creates JPEG thumbnails from from video frames grabbed from the
 * middle of a video stream It can be called with 2 parameters, an input
 * filename/directory and an output directory.
 * 
 * If the input file or the output directory do not exist, an exception is
 * thrown.
 */

public class VideoFrameGrabber {

	/**
	 * Constructor.
	 */
	public VideoFrameGrabber() {
	}

	/**
	 * Processes the passed input video file / video file directory and stores
	 * the processed files in the output directory.
	 * 
	 * @param input
	 *            a reference to the input video file / input directory
	 * @param output
	 *            a reference to the output directory
	 */
	public ArrayList<File> batchProcessVideoFiles(File input, File output) throws IOException {
		if (!input.exists())
			throw new IOException("Input file " + input + " was not found!");
		if (!output.exists())
			throw new IOException("Output directory " + output + " not found!");
		if (!output.isDirectory())
			throw new IOException(output + " is not a directory!");

		ArrayList<File> ret = new ArrayList<File>();

		if (input.isDirectory()) {
			File[] files = input.listFiles();
			for (File f : files) {
				if (f.isDirectory())
					continue;

				String ext = f.getName().substring(f.getName().lastIndexOf(".") + 1).toLowerCase();
				if (ext.equals("avi") || ext.equals("swf") || ext.equals("asf") || ext.equals("flv")
						|| ext.equals("mp4")) {
					File result = processVideo(f, output);
					System.out.println("converted " + f + " to " + result);
					ret.add(result);
				}

			}

		} else {
			String ext = input.getName().substring(input.getName().lastIndexOf(".") + 1).toLowerCase();
			if (ext.equals("avi") || ext.equals("swf") || ext.equals("asf") || ext.equals("flv") || ext.equals("mp4")) {
				File result = processVideo(input, output);
				System.out.println("converted " + input + " to " + result);
				ret.add(result);
			}
		}
		return ret;
	}

	/**
	 * Processes the passed audio file and stores the processed file to the
	 * output directory.
	 * 
	 * @param input
	 *            a reference to the input audio File
	 * @param output
	 *            a reference to the output directory
	 */
	protected File processVideo(File input, File output) throws IOException, IllegalArgumentException {
		if (!input.exists())
			throw new IOException("Input file " + input + " was not found!");
		if (input.isDirectory())
			throw new IOException("Input file " + input + " is a directory!");
		if (!output.exists())
			throw new IOException("Output directory " + output + " not found!");
		if (!output.isDirectory())
			throw new IOException(output + " is not a directory!");

		File outputFile = new File(output, input.getName() + "_thumb.jpg");
		// load the input video file

		// ***************************************************************
		// Fill in your code here!
		// ***************************************************************
		// sources:
		// https://github.com/artclarke/xuggle-xuggler/blob/master/src/com/xuggle/xuggler/demos/DecodeAndCaptureFrames.java
		// and ...
		
		// all my variables go here...
		BufferedImage frame = null;						// the thumbnail
		IContainer container = IContainer.make();		// media container (all streams)
		IStream videoStream = null;						// represents the video stream
		IStreamCoder videoCoder = null;					// video stream coder
		
		// try to open the container
		if (container.open(input.getAbsolutePath(), IContainer.Type.READ, null) < 0) throw new RuntimeException("OOPS! Something went wrong reading the file...");

		// first we need to find out which StreamID the video stream has
		int videoStreamID = -1;
		for (int i = 0; i < container.getNumStreams(); i++) {
			videoStream = container.getStream(i);
			videoCoder = videoStream.getStreamCoder();

			if (videoCoder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) { // check if this stream is video
				videoStreamID = i;
				break;
			}
		}
		if (videoStreamID < 0) throw new RuntimeException("OOPS! Could not find video stream.");
		
		System.out.println("Video Stream: " + videoStream.toString());

		// now calculate which frame we want - maybe we'll need that
		long middleframe = Math.round(
				((videoStream.getDuration() 
						* videoCoder.getTimeBase().getNumerator()) 
						/ videoCoder.getTimeBase().getDenominator()) // duration in seconds
						* videoCoder.getFrameRate().getDouble() 
						/ 2
						);
		System.out.println("Middle Frame: " + middleframe);	// should be correct
		
		// time-based approach
		long duration = videoStream.getDuration() 
				* videoCoder.getTimeBase().getNumerator() 
				/ videoCoder.getTimeBase().getDenominator();
		System.out.println("Video Duration: " + duration + " sec");
		
		if (videoCoder.open(null, null) < 0) throw new RuntimeException("OOPS! Could not initiate coder.");
		
		// set up a colorspace converter
		IVideoResampler resampler = IVideoResampler.make(
				videoCoder.getWidth(), videoCoder.getHeight(), IPixelFormat.Type.BGR24, 
				videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
		
		
		// now it's time to get the frame we want - we need to read it from a packet
		IPacket packet = IPacket.make(); // an encoded piece of data from one of the container streams
		
		// prepare a picture to read decoded stuff into
		IVideoPicture pic = IVideoPicture.make(
				videoCoder.getPixelType(), videoCoder.getWidth(), videoCoder.getHeight());
		
		// set the video cursor at the next best keyframe ( the first one resets to zero)
		// backward seeking is important when the video is very short (like the panda vid)
		container.seekKeyFrame(videoStreamID, -1, 0);
		container.seekKeyFrame(
				videoStreamID, 	// on which stream to look
				0, 				// min timestamp (for short videos)
				(long) (middleframe * videoCoder.getTimeBase().getDenominator() / (videoCoder.getFrameRate().getDouble()*videoCoder.getTimeBase().getNumerator())), // target
				container.getDuration(),	// max timestamp
				0		// seek flags
				);
		
		while(container.readNextPacket(packet) >= 0) {
			if (packet.getStreamIndex() != videoStreamID) continue; // if not part of video stream, ignore it
			if (pic.isComplete()) break; // break outer loop to avoid looping through the rest of the vid

			// debugging output
			long secs = packet.getTimeStamp() 
						* videoCoder.getTimeBase().getNumerator() 
						/ videoCoder.getTimeBase().getDenominator();
			
			System.out.print("Packet Timestamp: " + packet.getTimeStamp());
			System.out.print(" @second " + secs);
			System.out.println(" -- keyframe: " + packet.isKey());
			
			int progress = 0; // how many bytes are decoded
			while(progress < packet.getSize()) {
				progress += videoCoder.decodeVideo(pic, packet, progress); // write progress into pic
				if (pic.isComplete()) {
					
					IVideoPicture resampled = null;
					
					if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24) { // we must resample
						resampled = IVideoPicture.make(
								resampler.getOutputPixelFormat(), videoCoder.getWidth(), videoCoder.getHeight());
						if (resampler.resample(resampled, pic) <0) throw new RuntimeException("Oops! Resampling failed.");
						
						frame = new BufferedImage(videoCoder.getWidth(), videoCoder.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
						IConverter conv = ConverterFactory.createConverter(frame, IPixelFormat.Type.BGR24);
						frame = conv.toImage(resampled);
					} else {
						frame = new BufferedImage(videoCoder.getWidth(), videoCoder.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
						IConverter conv = ConverterFactory.createConverter(frame, IPixelFormat.Type.BGR24);
						frame = conv.toImage(pic);
					}
					break;
				}
			}

		}
		if (frame == null) throw new RuntimeException("OOPS! No frame could be extracted.");
		ImageIO.write(frame, "jpeg", outputFile);
		
		// resource cleanup
		videoCoder.close();
		container.close();
		
		return outputFile;

	}

	/**
	 * Main method. Parses the commandline parameters and prints usage
	 * information if required.
	 */
	public static void main(String[] args) throws Exception {

		// args = new String[] { "./media/video", "./test" };

		if (args.length < 2) {
			System.out.println("usage: java itm.video.VideoFrameGrabber <input-videoFile> <output-directory>");
			System.out.println("usage: java itm.video.VideoFrameGrabber <input-directory> <output-directory>");
			System.exit(1);
		}
		File fi = new File(args[0]);
		File fo = new File(args[1]);
		VideoFrameGrabber grabber = new VideoFrameGrabber();
		grabber.batchProcessVideoFiles(fi, fo);
	}

}
