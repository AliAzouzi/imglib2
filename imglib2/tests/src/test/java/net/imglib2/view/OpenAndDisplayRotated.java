package net.imglib2.view;

import ij.ImageJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.display.XYProjector;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.real.FloatType;

public class OpenAndDisplayRotated
{	
	final static public void main( final String[] args )
	throws ImgIOException
{
	new ImageJ();
	
	final ImgOpener io = new ImgOpener();
	RandomAccessibleInterval< FloatType > img = Views.rotatedView(
	        io.openImg( "/home/tobias/workspace/data/73_float.tif", new ArrayImgFactory<FloatType>(), new FloatType()),
			0, 1 );
	
	final ARGBScreenImage screenImage = new ARGBScreenImage( ( int )img.dimension( 0 ), ( int )img.dimension( 1 ) );
	final XYProjector< FloatType, ARGBType > projector = new XYProjector< FloatType, ARGBType >( img, screenImage, new RealARGBConverter< FloatType >( 0, 127 ) );
	
	final ColorProcessor cp = new ColorProcessor( screenImage.image() );
	final ImagePlus imp = new ImagePlus( "argbScreenProjection", cp );
	imp.show();

	// ImageJFunctions.show( img );
	
	for ( int k = 0; k < 3; ++k ) 
		for ( int i = 0; i < img.dimension( 2 ); ++i )
		{
			projector.setPosition( i, 2 );
			projector.map();
			final ColorProcessor cpa = new ColorProcessor( screenImage.image() );
			imp.setProcessor( cpa );
			imp.updateAndDraw();
		}
	
	projector.map();
	
	projector.setPosition( 40, 2 );
	projector.map();
}
}
