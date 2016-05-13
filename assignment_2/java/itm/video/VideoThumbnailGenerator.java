package itm.video;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

import itm.util.IOUtil;

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
public class VideoThumbnailGenerator {

	/**
	 * Constructor.
	 */
	public VideoThumbnailGenerator() {
	}

	/**
	 * Processes a video file directory in a batch process.
	 * 
	 * @param input
	 *            a reference to the video file directory
	 * @param output
	 *            a reference to the output directory
	 * @param overwrite
	 *            indicates whether existing output files should be overwritten
	 *            or not
	 * @return a list of the created media objects (videos)
	 */
	public ArrayList<File> batchProcessVideoFiles(File input, File output, boolean overwrite, int timespan) throws IOException {
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

				String ext = f.getName().substring(f.getName().lastIndexOf(".") + 1).toLowerCase();
				if (ext.equals("avi") || ext.equals("swf") || ext.equals("asf") || ext.equals("flv")
						|| ext.equals("mp4"))
					try {
						File result = processVideo(f, output, overwrite, timespan);
						System.out.println("processed file " + f + " to " + output);
						ret.add(result);
					} catch (Exception e0) {
						System.err.println("Error processing file " + input + " : " + e0.toString());
					}
			}
		} else {

			String ext = input.getName().substring(input.getName().lastIndexOf(".") + 1).toLowerCase();
			if (ext.equals("avi") || ext.equals("swf") || ext.equals("asf") || ext.equals("flv") || ext.equals("mp4"))
				try {
					File result = processVideo(input, output, overwrite, timespan);
					System.out.println("processed " + input + " to " + result);
					ret.add(result);
				} catch (Exception e0) {
					System.err.println("Error when creating processing file " + input + " : " + e0.toString());
				}

		}
		return ret;
	}

	/**
	 * Processes the passed input video file and stores a thumbnail of it to the
	 * output directory.
	 * 
	 * @param input
	 *            a reference to the input video file
	 * @param output
	 *            a reference to the output directory
	 * @param overwrite
	 *            indicates whether existing files should be overwritten or not
	 * @return the created video media object
	 */
	protected File processVideo(File input, File output, boolean overwrite, int timespan) throws Exception {
		if (!input.exists())
			throw new IOException("Input file " + input + " was not found!");
		if (input.isDirectory())
			throw new IOException("Input file " + input + " is a directory!");
		if (!output.exists())
			throw new IOException("Output directory " + output + " not found!");
		if (!output.isDirectory())
			throw new IOException(output + " is not a directory!");

		// create output file and check whether it already exists.
		File outputFile = new File(output, input.getName() + "_thumb.avi");

		// ***************************************************************
		// Fill in your code here!
		// ***************************************************************

		// extract frames from input video
		IContainer container = IContainer.make();
		if (container.open(input.getAbsolutePath(), IContainer.Type.READ, null) < 0)
			throw new RuntimeException("Oops! Could not open file.");
		IStream videoStream = null;
		IStreamCoder videoCoder = null;
		
		int videoStreamID = -1;
		for (int i = 0; i < container.getNumStreams(); i++) {
			if (container.getStream(i).getStreamCoder().getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
				videoStreamID = i;
				videoStream = container.getStream(i);
				videoCoder = videoStream.getStreamCoder();
				break;
			}
		}
		if (videoStream == null || videoCoder == null)
			throw new RuntimeException("OOPS! Could not extract video stream.");
		if (videoCoder.open(null, null) < 0) throw new RuntimeException("OOPS! Could not initiate coder.");
		
		System.out.println("Video Stream: " + videoStream.toString());
		
		// now comes a lot of duplicate code from the frame grabber
		// I really don't like this, but don't dare to create a seperate class for that stuff
		ArrayList<BufferedImage> frames = new ArrayList<>();
		IVideoResampler resampler = IVideoResampler.make(
				videoCoder.getWidth(), videoCoder.getHeight(), IPixelFormat.Type.BGR24, 
				videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
		IPacket packet = IPacket.make();
		IVideoPicture pic = IVideoPicture.make(
				videoCoder.getPixelType(), videoCoder.getWidth(), videoCoder.getHeight());
		BufferedImage frame = null;
		
		ArrayList<Long> secsProcessed = new ArrayList<>();
		
		while(container.readNextPacket(packet) >= 0) {
			if (packet.getStreamIndex() != videoStreamID) continue;
			long secs = packet.getTimeStamp() 
					* videoCoder.getTimeBase().getNumerator() 
					/ videoCoder.getTimeBase().getDenominator();
			
			int progress = 0;
			while(progress < packet.getSize()) {
				progress += videoCoder.decodeVideo(pic, packet, progress);
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
					
					if (timespan == 0) {
						frames.add(frame);
					} else if ((secs % timespan == 0) && !(secsProcessed.contains(secs))) {
						secsProcessed.add(secs);
						frames.add(frame);
					}
					break;
				}
			}
		}
		
		System.out.println("Number of frames we got: " + frames.size());
		for (int i = 0; i < frames.size(); i++) {
			ImageIO.write(frames.get(i), "png", new File(output, input.getName() + "_" + i + ".png"));
		}
		
		// add a watermark of your choice and paste it to the image
        // e.g. text or a graphic

		// create a video writer

		// add a stream with the proper width, height and frame rate
		
		// if timespan is set to zero, compare the frames to use and add 
		// only frames with significant changes to the final video

		// loop: get the frame image, encode the image to the video stream
		
		// Close the writer
		videoCoder.close();
		container.close();

		return outputFile;
	}

	/**
	 * Main method. Parses the commandline parameters and prints usage
	 * information if required.
	 */
	public static void main(String[] args) throws Exception {

		if (args.length < 3) {
            System.out.println("usage: java itm.video.VideoThumbnailGenerator <input-video> <output-directory> <timespan>");
            System.out.println("usage: java itm.video.VideoThumbnailGenerator <input-directory> <output-directory> <timespan>");
            System.exit(1);
        }
        File fi = new File(args[0]);
        File fo = new File(args[1]);
        int timespan = 5;
        if(args.length == 3)
            timespan = Integer.parseInt(args[2]);
        
        VideoThumbnailGenerator videoMd = new VideoThumbnailGenerator();
        videoMd.batchProcessVideoFiles(fi, fo, true, timespan);
	}
}