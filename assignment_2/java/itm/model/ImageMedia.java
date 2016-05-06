package itm.model;

/*******************************************************************************
    This file is part of the ITM course 2016
    (c) University of Vienna 2009-2016
*******************************************************************************/

import java.awt.color.ColorSpace;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

/**
    This class describes an image. 
*/
public class ImageMedia extends AbstractMedia 
{

    public final static int ORIENTATION_LANDSCAPE = 0;
    public final static int ORIENTATION_PORTRAIT = 1;

    // ***************************************************************
    //  Fill in your code here!
    // ***************************************************************

    // add required properties (scope: protected!)
    // -> I'll add the class vars, then...
    
    /**
     * Width of the image (in pixels).
     */
    protected Integer width;
    /**
     * Height of the image (in pixels).
     */
    protected Integer height;
    /**
     * Number of image components.
     */
    protected Integer numComponents;
    /**
     * Number of image color components.
     */
    protected Integer numColorComponents;
    /**
     * Transparency.
     */
    protected Integer transparency;
    /**
     * number of bits per pixel described by [corresponding] ColorModel (source: Java SE 8 API)
     */
    protected Integer pixelSize;
    /**
     * ColorSpace Integer representation of the color space. 
     * Can be translated into human-readable string by the method {@link itm.model.ImageMedia#serializeCSType serializeCSType}.
     */
    protected Integer colorSpaceType;
    /**
     * Orientation of the image, either ImageMedia.ORIENTATION_LANDSCAPE (0) or ImageMedia.ORIENTATION_PORTRAIT (1)
     */
    protected Integer orientation;
    
    
    // add get/set methods for the properties
    
    // classic Getters and Setters!

    public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getNumComponents() {
		return numComponents;
	}

	public void setNumComponents(Integer numComponents) {
		this.numComponents = numComponents;
	}

	public Integer getNumColorComponents() {
		return numColorComponents;
	}

	public void setNumColorComponents(Integer numColorComponents) {
		this.numColorComponents = numColorComponents;
	}

	public Integer getTransparency() {
		return transparency;
	}

	public void setTransparency(Integer transparency) {
		this.transparency = transparency;
	}

	public Integer getPixelSize() {
		return pixelSize;
	}

	public void setPixelSize(Integer pixelSize) {
		this.pixelSize = pixelSize;
	}

	public Integer getColorSpaceType() {
		return colorSpaceType;
	}

	public void setColorSpaceType(Integer colorSpaceType) {
		this.colorSpaceType = colorSpaceType;
	}

	public Integer getOrientation() {
		return orientation;
	}

	public void setOrientation(Integer orientation) {
		this.orientation = orientation;
	}
	

	/**
        Constructor.
    */
    public ImageMedia()
    {
        super();
    }

    /**
        Constructor.
    */
    public ImageMedia( File instance )
    {
        super( instance );
    }


    /**
        Converts a color space type to a human readable string
        @return a string describing the passed colorspace
    */
    protected String serializeCSType( int cstype )
    {
        switch ( cstype ) {
            case ColorSpace.CS_CIEXYZ: return "CS_CIEXYZ"; 
            case ColorSpace.CS_GRAY: return "CS_GRAY"; 
            case ColorSpace.CS_LINEAR_RGB: return "CS_LINEAR_RGB"; 
            case ColorSpace.CS_PYCC: return "CS_PYCC"; 
            case ColorSpace.CS_sRGB: return "CS_sRGB"; 
            case ColorSpace.TYPE_CMY: return "TYPE_CMY"; 
            case ColorSpace.TYPE_CMYK: return "TYPE_CMYK"; 
            case ColorSpace.TYPE_GRAY: return "TYPE_GRAY"; 
            case ColorSpace.TYPE_RGB: return "TYPE_RGB"; 
            case ColorSpace.TYPE_HLS: return "TYPE_HLS"; 
            default: return ""+cstype; 
        }
    }

    /**
        Converts a human readable string string to a color space type
        @return the colorspace corresponding to the passed string
    */
    protected int deserializeCSType( String cstype )
    {
        if ( cstype.equals( "CS_CIEXYZ" ) ) {
            return ColorSpace.CS_CIEXYZ;
        }
        if ( cstype.equals( "CS_GRAY" ) ) {
            return ColorSpace.CS_GRAY;
        }
        if ( cstype.equals( "CS_LINEAR_RGB" ) ) {
            return ColorSpace.CS_LINEAR_RGB;
        }
        if ( cstype.equals( "CS_PYCC" ) ) {
            return ColorSpace.CS_PYCC;
        }
        if ( cstype.equals( "CS_sRGB" ) ) {
            return ColorSpace.CS_sRGB;
        }
        if ( cstype.equals( "TYPE_CMY" ) ) { 
            return ColorSpace.TYPE_CMY;
        }
        if ( cstype.equals( "TYPE_CMYK" ) ) {
            return ColorSpace.TYPE_CMYK;
        }
        if ( cstype.equals( "TYPE_GRAY" ) ) {
            return ColorSpace.TYPE_GRAY;
        }
        if ( cstype.equals( "TYPE_RGB" ) ) {
            return ColorSpace.TYPE_RGB;
        }
        if ( cstype.equals( "TYPE_HLS" ) ) {
            return ColorSpace.TYPE_HLS;
        }

        return Integer.parseInt( cstype );
    }
        
        
    /**
        Serializes this object to a string buffer.
        @return a StringBuffer containing a serialized version of this object.
    */
    @Override
    public StringBuffer serializeObject() throws IOException
    {
        StringWriter data = new StringWriter();
        // print writer for creating the output
        PrintWriter out = new PrintWriter( data );
        // print type
        out.println( "type: image" );
        StringBuffer sup = super.serializeObject();
        // print the serialization of the superclass (AbstractMedia)
        out.print( sup );

        // ***************************************************************
        //  Fill in your code here!
        // ***************************************************************

        // print properties
        	// AbstactMedia handles file, name, size and tags. I do the image-specific rest!
        out.println("width: " + this.getWidth());
        out.println("height: " + this.getHeight());
        out.println("numComponents: " + this.getNumComponents());
        out.println("numColorComponents: " + this.getNumColorComponents());
        out.println("transparacency: " + this.getTransparency());
        out.println("pixelSize: " + this.getPixelSize());
        out.println("orientation: " + this.getOrientation());
        out.println("colorSpaceType: " + this.serializeCSType(this.getColorSpaceType()));
        out.println();
        
        return data.getBuffer();
    }



    /**
        Deserializes this object from the passed string buffer.
    */
    @Override
    public void deserializeObject( String data ) throws IOException
    {
        super.deserializeObject( data );
        
        StringReader sr = new StringReader( data );
        BufferedReader br = new BufferedReader( sr );
        String line = null;
        while ( ( line = br.readLine() ) != null ) {

            // ***************************************************************
            //  Fill in your code here!
            // ***************************************************************
            
            // read and set properties
        		// the super-method has already filled in some gaps in our object -> let's add the rest!
        	if (line.startsWith("width: ")) {
        		this.setWidth(Integer.parseInt(line.substring("width: ".length())));
        	} else
        	if (line.startsWith("height: ")) {
        		this.setHeight(Integer.parseInt(line.substring("height: ".length())));
        	} else 
        	if (line.startsWith("numComponents: ")) {
        		this.setNumComponents(Integer.parseInt(line.substring("numComponents: ".length())));
        	} else
        	if (line.startsWith("numColorComponents: ")) {
        		this.setNumColorComponents(Integer.parseInt(line.substring("numColorComponents: ".length())));
        	} else
        	if (line.startsWith("transparacency: ")) {
        		this.setTransparency(Integer.parseInt(line.substring("transparacency: ".length())));
        	} else 
        	if (line.startsWith("pixelSize: ")) {
        		this.setPixelSize(Integer.parseInt(line.substring("pixelSize: ".length())));
        	} else
        	if (line.startsWith("orientation: ")) {
        		this.setOrientation(Integer.parseInt(line.substring("orientation: ".length())));
        	} else 
        	if (line.startsWith("colorSpaceType: ")) {
        		this.setColorSpaceType(this.deserializeCSType(line.substring("colorSpaceType: ".length())));
        	} else {
        		// it was a different parameter altogether! Didn't happen. Debug exception removed.
        		//throw new IllegalArgumentException("An unknown value occurred! \n CAUSE is this line: " + line);
        	}
        	
        } // while
        
     // and it's stored in THIS!
    }
}


