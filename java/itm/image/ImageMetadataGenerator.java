package itm.image;

/*******************************************************************************
    This file is part of the ITM course 2016
    (c) University of Vienna 2009-2016
*******************************************************************************/

import itm.model.ImageMedia;
import itm.model.MediaFactory;
import itm.util.IOUtil;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
    This class reads images of various formats and stores some basic image meta data to text files.
    It can be called with 3 parameters, an input filename/directory, an output directory and an "overwrite" flag.
    It will read the input image(s), retrieve some meta data and write it to a text file in the output directory.
    The overwrite flag indicates whether the resulting output file should be overwritten or not.
    
    If the input file or the output directory do not exist, an exception is thrown.
*/
public class ImageMetadataGenerator 
{

    /**
        Constructor.
    */
    public ImageMetadataGenerator()
    {
    }
   

    /**
        Processes an image directory in a batch process.
        @param input a reference to the input image directory
        @param output a reference to the output directory
        @param overwrite indicates whether existing metadata files should be overwritten or not
        @return a list of the created media objects (images)
    */
    public ArrayList<ImageMedia> batchProcessImages( File input, File output, boolean overwrite ) throws IOException
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

        ArrayList<ImageMedia> ret = new ArrayList<ImageMedia>();

        if ( input.isDirectory() ) {
            File[] files = input.listFiles();
            for ( File f : files ) {
                try {
                    ImageMedia result = processImage( f, output, overwrite );
                    System.out.println( "converted " + f + " to " + output );
                    ret.add( result );
                } catch ( Exception e0 ) {
                    System.err.println( "Error converting " + input + " : " + e0.toString() );
                }
            }
        } else {
                try {
                    ImageMedia result = processImage( input, output, overwrite );
                    System.out.println( "converted " + input + " to " + output );
                    ret.add( result );
                } catch ( Exception e0 ) {
                    System.err.println( "Error converting " + input + " : " + e0.toString() );
                }
        }
        return ret;
    }    
    
    /**
        Processes the passed input image and stores the extracted metadata to a textfile in the output directory.
        @param input a reference to the input image
        @param output a reference to the output directory
        @param overwrite indicates whether existing metadata files should be overwritten or not
        @return the created image media object
    */
    protected ImageMedia processImage( File input, File output, boolean overwrite ) throws IOException, IllegalArgumentException
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

        // create outputfilename and check whether thumb already exists. All image 
        // metadata files have to start with "img_" -  this is used by the mediafactory!
        File outputFile = new File( output, "img_" + input.getName() + ".txt" );
        if ( outputFile.exists() ) {
            if ( ! overwrite ) {
                // load from file
                ImageMedia media = new ImageMedia();
                media.readFromFile( outputFile );
                return media;
                }
        }


        // get metadata and store it to media object
        ImageMedia media = (ImageMedia) MediaFactory.createMedia( input );

        // ***************************************************************
        //  Fill in your code here!
        // ***************************************************************
        
        // load the input image
        BufferedImage image = ImageIO.read(input);
        ColorModel colorModel = image.getColorModel();
        
        // set width and height of the image  
        media.setWidth(image.getWidth());
        media.setHeight(image.getHeight());
        // add a tag "image" to the media
        media.addTag("image");
        // add a tag corresponding to the filename extension of the file to the media 
        media.addTag(input.getName().substring(input.getName().lastIndexOf(".")+1));
        // set orientation
        if (image.getWidth() >= image.getHeight()) // both equal is landscape for me
        	media.setOrientation(ImageMedia.ORIENTATION_LANDSCAPE);
        else media.setOrientation(ImageMedia.ORIENTATION_PORTRAIT);
        
        // if there is a colormodel:
        // set color space type
        // set pixel size
        // set transparency
        // set number of (color) components
        if (colorModel != null) {
        	media.setColorSpaceType(colorModel.getColorSpace().getType());
        	media.setPixelSize(colorModel.getPixelSize());
        	media.setTransparency(colorModel.getTransparency());
        	media.setNumComponents(colorModel.getNumComponents());
        	media.setNumColorComponents(colorModel.getNumColorComponents());
        }

        // EXTENSION FOR ASSIGNMENT 3
        tagImageByColor(image,media);
        // END OF EXTENSION

        System.out.println(media.toString());

        // store meta data
        StringBuffer buf = media.serializeObject();
      
        // write the output to the file
        IOUtil.writeFile(buf, outputFile);
        
        // end of my code

        return media;
    }

    private void tagImageByColor(BufferedImage image, ImageMedia media) throws IllegalArgumentException {
        if (image == null || media == null) throw new IllegalArgumentException("Arguments cannot be null!");

        // hard coded values that set the accuracy
        int GREY_DIFFERENCE = 20;
        int COLOR_DIFFERENCE = 10;
        int EVAL_DIFFERENCE = 1000;

        int red =0, blue=0, green=0, grey =0;

        if (image.getColorModel().getColorSpace().getType() == ColorSpace.TYPE_RGB) { // visit every pixel
            for(int i = 0; i < image.getHeight(); i++) {
                for(int j = 0; j < image.getWidth(); j++) {
                    Color col = new Color(image.getRGB(j, i));

                    // ignore grey pixels (count them for debugging only)
                    if (Math.abs(col.getBlue() - col.getRed()) < GREY_DIFFERENCE &&
                            Math.abs(col.getBlue() - col.getGreen()) < GREY_DIFFERENCE) {
                        grey++;
                        continue;
                    }

                    // select the most dominant one - multiple ++ possible
                    int max = Math.max(col.getBlue(), Math.max(col.getGreen(),col.getRed()));
                    if ( (col.getBlue() >= (max-COLOR_DIFFERENCE)) && (col.getBlue() <= (max+COLOR_DIFFERENCE)) )
                        blue++;
                    if ( (col.getGreen() >= (max-COLOR_DIFFERENCE)) && (col.getGreen() <= (max+COLOR_DIFFERENCE)) )
                        green++;
                    if ( (col.getRed() >= (max-COLOR_DIFFERENCE)) && (col.getRed() <= (max+COLOR_DIFFERENCE)) )
                        red++;
                }
            }
        } // end loop over all pixels

        // we now have the color values for the most domintant pixels
        System.out.println("Blue: " + blue + "\nGreen: " + green + "\nRed: " + red + "\nGrey: " + grey);
        int max = Math.max(blue, Math.max(red, green));

        // tag the image
        if (blue == max) {
            media.addTag("blue");
            if (red >= blue-EVAL_DIFFERENCE) media.addTag("red");
            if (green >= blue-EVAL_DIFFERENCE) media.addTag("green");
        } else if (green == max) {
            media.addTag("green");
            if (red >= green-EVAL_DIFFERENCE) media.addTag("red");
            if (blue >= green-EVAL_DIFFERENCE) media.addTag("blue");
        } else if (red == max) {
            media.addTag("red");
            if (blue >= red-EVAL_DIFFERENCE) media.addTag("blue");
            if (green >= red-EVAL_DIFFERENCE) media.addTag("green");
        }
    }

    /**
        Main method. Parses the commandline parameters and prints usage information if required.
    */
    public static void main( String[] args ) throws Exception
    {
        if ( args.length < 2 ) {
            System.out.println( "usage: java itm.image.ImageMetadataGenerator <input-image> <output-directory>" );
            System.out.println( "usage: java itm.image.ImageMetadataGenerator <input-directory> <output-directory>" );
            System.exit( 1 );
        }
        File fi = new File( args[0] );
        File fo = new File( args[1] );
        ImageMetadataGenerator img = new ImageMetadataGenerator();
        img.batchProcessImages( fi, fo, true );        
    }    
}