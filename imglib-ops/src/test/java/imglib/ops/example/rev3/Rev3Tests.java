package imglib.ops.example.rev3;

import static org.junit.Assert.assertEquals;
import imglib.ops.example.rev3.function.BinaryFunction;
import imglib.ops.example.rev3.function.ConstantFunction;
import imglib.ops.example.rev3.function.ConvolutionFunction;
import imglib.ops.example.rev3.function.ImageFunction;
import imglib.ops.example.rev3.function.UnaryFunction;
import imglib.ops.example.rev3.operator.BinaryOperator;
import imglib.ops.example.rev3.operator.UnaryOperator;
import imglib.ops.example.rev3.operator.binary.AddOperator;
import imglib.ops.example.rev3.operator.binary.MultiplyOperator;
import imglib.ops.example.rev3.operator.unary.HalfOperator;

import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;

import org.junit.Test;

public class Rev3Tests
{

	// ************  private interface ********************************************************
	
	private static Image<UnsignedByteType> createImage(int width, int height)
	{
		ImageFactory<UnsignedByteType> factory = new ImageFactory<UnsignedByteType>(new UnsignedByteType(), new ArrayContainerFactory());
		
		return factory.createImage(new int[]{width,height});
	}

	private static Image<UnsignedByteType> createPopulatedImage(int width, int height, int[] values)
	{
		Image<UnsignedByteType> image = createImage(width, height);
		
		LocalizableByDimCursor<UnsignedByteType> cursor = image.createLocalizableByDimCursor();
		
		int[] position = new int[2];
		
		int i = 0;
		
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				position[0] = x;
				position[1] = y;
				cursor.setPosition(position);
				cursor.getType().setInteger(values[i++]);
			}
		}

		return image;
	}
	
	
	private static void assertImageValsEqual(int width, int height, int[] values, Image<UnsignedByteType> image)
	{
		LocalizableByDimCursor<UnsignedByteType> cursor = image.createLocalizableByDimCursor();

		int[] position = new int[2];
		
		int i = 0;
		
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				position[0] = x;
				position[1] = y;
				cursor.setPosition(position);
				assertEquals(values[i++], cursor.getType().getInteger());
			}
		}
	}

	// ************  Tests ********************************************************
	
	@Test
	public void testConstantFill()
	{
		Image<UnsignedByteType> outputImage = createPopulatedImage(3,3,new int[9]);
		
		ConstantFunction function = new ConstantFunction(43);
		
		Operation op = new Operation(outputImage, new int[3], new int[]{3,3}, function);
		
		op.execute();
		
		assertImageValsEqual(3,3,new int[]{43,43,43,43,43,43,43,43,43}, outputImage);
	}

	@Test
	public void testCopyOtherImage()
	{
		Image<UnsignedByteType> inputImage = createPopulatedImage(3,3,new int[]{1,2,3,4,5,6,7,8,9});
		
		Image<UnsignedByteType> outputImage = createPopulatedImage(3,3,new int[9]);
		
		assertImageValsEqual(3,3,new int[9], outputImage);

		ImageFunction function = new ImageFunction(inputImage);
		
		Operation op = new Operation(outputImage, new int[3], new int[]{3,3}, function);
		
		op.execute();
		
		assertImageValsEqual(3,3,new int[]{1,2,3,4,5,6,7,8,9}, inputImage);
		assertImageValsEqual(3,3,new int[]{1,2,3,4,5,6,7,8,9}, outputImage);
	}
	
	@Test
	public void testConvolve()
	{
		double[] kernel = new double[]{1,1,1,1,1,1,1,1,1};

		Image<UnsignedByteType> inputImage = createPopulatedImage(3,3,new int[]{1,2,3,4,5,6,7,8,9});
		
		Image<UnsignedByteType> outputImage = createPopulatedImage(3,3,new int[9]);
		
		assertImageValsEqual(3,3,new int[9], outputImage);

		ImageFunction imageFunction = new ImageFunction(inputImage);

		ConvolutionFunction convolver = new ConvolutionFunction(new int[]{3,3}, kernel, imageFunction);
		
		Operation op = new Operation(outputImage, new int[]{1,1}, new int[]{1,1}, convolver);
		
		op.execute();
		
		assertImageValsEqual(3,3,new int[]{0,0,0,0,45,0,0,0,0}, outputImage);
	}
	
	@Test
	public void testBinaryFunction()
	{
		Image<UnsignedByteType> leftImage = createPopulatedImage(3,3,new int[]{1,2,3,4,5,6,7,8,9});
		
		Image<UnsignedByteType> rightImage = createPopulatedImage(3,3,new int[]{10,20,30,40,50,60,70,80,90});

		Image<UnsignedByteType> outputImage = createPopulatedImage(3,3,new int[9]);

		assertImageValsEqual(3,3,new int[9], outputImage);

		ImageFunction leftImageFunction = new ImageFunction(leftImage);
		
		ImageFunction rightImageFunction = new ImageFunction(rightImage);

		BinaryOperator addOp = new AddOperator();
		
		BinaryFunction addFunc = new BinaryFunction(addOp, leftImageFunction, rightImageFunction);
		
		Operation op = new Operation(outputImage, new int[2], new int[]{3,3}, addFunc);
		
		op.execute();
		
		assertImageValsEqual(3,3,new int[]{11,22,33,44,55,66,77,88,99}, outputImage);
	}
	
	@Test
	public void testUnaryFunction()
	{
		Image<UnsignedByteType> inputImage = createPopulatedImage(3,3,new int[]{10,20,30,40,50,60,70,80,90});

		Image<UnsignedByteType> outputImage = createPopulatedImage(3,3,new int[9]);

		assertImageValsEqual(3,3,new int[9], outputImage);

		ImageFunction inputImageFunction = new ImageFunction(inputImage);
		
		UnaryOperator halfOp = new HalfOperator();
		
		UnaryFunction halfFunc = new UnaryFunction(halfOp, inputImageFunction);
		
		Operation op = new Operation(outputImage, new int[2], new int[]{3,3}, halfFunc);
		
		op.execute();
		
		assertImageValsEqual(3,3,new int[]{5,10,15,20,25,30,35,40,45}, outputImage);
	}

	@Test
	public void testComposedFunction()
	{
		// lets set an Image's values to half(2*Image1 + 3*Image2 + 4)
		
		Image<UnsignedByteType> inputImage1 = createPopulatedImage(3,3,new int[]{1,2,3,4,5,6,7,8,9});
		Image<UnsignedByteType> inputImage2 = createPopulatedImage(3,3,new int[]{5,10,15,20,25,30,35,40,45});
		Image<UnsignedByteType> outputImage = createPopulatedImage(3,3,new int[9]);

		MultiplyOperator multOp = new MultiplyOperator();
		AddOperator addOp = new AddOperator();
		HalfOperator halfOp = new HalfOperator();
		
		ImageFunction image1Func = new ImageFunction(inputImage1);
		ImageFunction image2Func = new ImageFunction(inputImage2);

		ConstantFunction two = new ConstantFunction(2);
		ConstantFunction three = new ConstantFunction(3);
		ConstantFunction four = new ConstantFunction(4);

		BinaryFunction term1 = new BinaryFunction(multOp, two, image1Func);
		
		BinaryFunction term2 = new BinaryFunction(multOp, three, image2Func);
		
		BinaryFunction twoTerms = new BinaryFunction(addOp, term1, term2);
		
		BinaryFunction threeTerms = new BinaryFunction(addOp, twoTerms, four);

		UnaryFunction totalFunc = new UnaryFunction(halfOp, threeTerms);
		
		Operation op = new Operation(outputImage, new int[2], new int[]{3,3}, totalFunc);
		
		op.execute();
		
		assertImageValsEqual(3,3,new int[]{11,19,28,36,45,53,62,70,79}, outputImage);  // NOTICE IT ROUNDS 0.5 UP ...
	}
	
	// TODO
	// test Conditions
	// recreate all rev2 tests from NewFunctionlIdeas.java
}
