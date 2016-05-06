package itm.image;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/*******************************************************************************
    This file is part of the ITM course 2016
    (c) University of Vienna 2009-2016
*******************************************************************************/

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
    This class converts images of various formats to PNG thumbnails files.
    It can be called with 3 parameters, an input filename/directory, an output directory and a compression quality parameter.
    It will read the input image(s), grayscale and scale it/them and convert it/them to a PNG file(s) that is/are written to the output directory.

    If the input file or the output directory do not exist, an exception is thrown.
*/
public class ImageThumbnailGenerator 
{

    /**
        Constructor.
    */
    public ImageThumbnailGenerator()
    {
    }

    /**
        Processes an image directory in a batch process.
        @param input a reference to the input image file
        @param output a reference to the output directory
        @param rotation
        @param overwrite indicates whether existing thumbnails should be overwritten or not
        @return a list of the created files
    */
    public ArrayList<File> batchProcessImages( File input, File output, double rotation, boolean overwrite ) throws IOException
    {
        if ( ! input.exists() ) {
            throw new IOException( "Input file " + input + " was not found!" );
        }
        if ( ! output.exists() ) {
            throw new IOException( "Output directory " + output + " not found!" );
        }
        if ( ! output.isDirectory() ) {
            throw new IOException( output + " is not a directory!" );
        }

        ArrayList<File> ret = new ArrayList<File>();

        if ( input.isDirectory() ) {
            File[] files = input.listFiles();
            for ( File f : files ) {
                try {
                    File result = processImage( f, output, rotation, overwrite );
                    System.out.println( "converted " + f + " to " + result );
                    ret.add( result );
                } catch ( Exception e0 ) {
                    System.err.println( "Error converting " + input + " : " + e0.toString() );
                }
            }
        } else {
            try {
                File result = processImage( input, output, rotation, overwrite );
                System.out.println( "converted " + input + " to " + result );
                ret.add( result );
            } catch ( Exception e0 ) {
                System.err.println( "Error converting " + input + " : " + e0.toString() );
            }
        } 
        return ret;
    }  

    /**
        Processes the passed input image and stores it to the output directory.
        This function should not do anything if the outputfile already exists and if the overwrite flag is set to false.
        @param input a reference to the input image file
        @param output a reference to the output directory
        @param dimx the width of the resulting thumbnail
        @param dimy the height of the resulting thumbnail
        @param overwrite indicates whether existing thumbnails should be overwritten or not
    */
    protected File processImage( File input, File output, double rotation, boolean overwrite ) throws IOException, IllegalArgumentException
    {
        if ( ! input.exists() ) {
            throw new IOException( "Input file " + input + " was not found!" );
        }
        if ( input.isDirectory() ) {
            throw new IOException( "Input file " + input + " is a directory!" );
        }
        if ( ! output.exists() ) {
            throw new IOException( "Output directory " + output + " not found!" );
        }
        if ( ! output.isDirectory() ) {
            throw new IOException( output + " is not a directory!" );
        }

        // create outputfilename and check whether thumb already exists
        File outputFile = new File( output, input.getName() + ".thumb.png" );
        if ( outputFile.exists() ) {
            if ( ! overwrite ) {
                return outputFile;
            }
        }

        // ***************************************************************
        //  Fill in your code here!
        // ***************************************************************

        // load the input image
        BufferedImage original = ImageIO.read(input);
        
        BufferedImage image = null;	// the rescaled, rotated thumbnail
        Graphics2D image2d = null;	// the thing we can work with
        
        if (original.getHeight() <= 200 && original.getWidth() <= 100) {
        	image = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        	image2d = image.createGraphics();
        	
        	image2d.drawImage(original, 200/2 - original.getWidth()/2, 100/2 - original.getHeight()/2, null);
        	
        } else if (original.getHeight() > original.getWidth()) { // orientation = portrait
        	int rescale_height = (int) Math.round((200.0 / original.getHeight()) * original.getWidth());
        	image = new BufferedImage(200, rescale_height, BufferedImage.TYPE_INT_RGB);
        	image2d = image.createGraphics();
        	
        	AffineTransform aft = AffineTransform.getQuadrantRotateInstance(1,200/2,200/2);
        	image2d.drawImage(original.getScaledInstance(rescale_height, 200, Image.SCALE_SMOOTH), aft, null);
        } else {
        	int rescale_height = (int) Math.round((200.0 / original.getWidth()) * original.getHeight());
        	image = new BufferedImage(200, rescale_height, BufferedImage.TYPE_INT_RGB);
        	image2d = image.createGraphics();
        	
        	image2d.drawImage(original.getScaledInstance(200, rescale_height, Image.SCALE_SMOOTH), null, null);
        }
        
        
        
        // add a watermark of your choice and paste it to the image
        // e.g. text or a graphic
        BufferedImage watermark = ImageIO.read(new URL("http://wwwlab.cs.univie.ac.at/~a1306497/ITM/nekoDoodle.png"));
        AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5F); // transparency
        image2d.setComposite(alpha);
        image2d.drawImage(watermark,null,null);
        
        ImageIO.write(image, "PNG", outputFile);
        
        // rotate by the given parameter the image - do not crop image parts!
        // scale the image to a maximum of [ 200 w X 100 h ] pixels - do not distort!
        // if the image is smaller than [ 200 w X 100 h ] - print it on a [ dim X dim ] canvas!
        	// --> I already did that! The watermark is always same size, same position.

        // rotate you image by the given rotation parameter
        // save as extra file - say: don't return as output file
        // encode and save the image  
        File outputRotated = new File(output, input.getName() + ".thumb.rotated.png");
        BufferedImage thumbnail = ImageIO.read(outputFile);	// now work with the newly created thumbnail
        BufferedImage thumbnail_rot = new BufferedImage(thumbnail.getWidth(),thumbnail.getHeight(),BufferedImage.TYPE_INT_RGB);
        AffineTransform axform = AffineTransform.getRotateInstance(Math.toRadians(rotation),thumbnail.getWidth()/2,thumbnail.getHeight()/2);
        
        Graphics2D rotated2d = thumbnail_rot.createGraphics();
        rotated2d.drawImage(thumbnail, axform, null);
        ImageIO.write(thumbnail_rot, "PNG", outputRotated);
        

        return outputFile;

        /**
            ./ant.sh ImageThumbnailGenerator -Dinput=media/img/ -Doutput=test/ -Drotation=90
        */
    }

    /**
        Main method. Parses the commandline parameters and prints usage information if required.
    */
    public static void main( String[] args ) throws Exception
    {
        if ( args.length < 3 ) {
            System.out.println( "usage: java itm.image.ImageThumbnailGenerator <input-image> <output-directory> <rotation degree>" );
            System.out.println( "usage: java itm.image.ImageThumbnailGenerator <input-directory> <output-directory> <rotation degree>" );
            System.exit( 1 );
        }
        File fi = new File( args[0] );
        File fo = new File( args[1] );
        double rotation = Double.parseDouble( args[2] );

        ImageThumbnailGenerator itg = new ImageThumbnailGenerator();
        itg.batchProcessImages( fi, fo, rotation, true );
    }    
}