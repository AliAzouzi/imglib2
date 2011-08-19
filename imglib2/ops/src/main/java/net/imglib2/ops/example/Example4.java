package net.imglib2.ops.example;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.Condition;
import net.imglib2.ops.DiscreteNeigh;
import net.imglib2.ops.Function;
import net.imglib2.ops.Neighborhood;
import net.imglib2.ops.Real;
import net.imglib2.ops.RegionIndexIterator;
import net.imglib2.ops.function.general.ConditionalFunction;
import net.imglib2.ops.function.real.ConstantRealFunction;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

// get values that are an average of the 5 values in a 3x3 cross

public class Example4 {

	private static final int XSIZE = 200;
	private static final int YSIZE = 300;
	
	private static boolean veryClose(double d1, double d2) {
		return Math.abs(d1-d2) < 0.00001;
	}

	private static double expectedValue(int x, int y) {
		double ctr = x+y;
		double ne = (x+1) + (y-1);
		double nw = (x-1) + (y-1);
		double se = (x+1) + (y+1);
		double sw = (x-1) + (y+1);
		return ctr * ne * nw * se * sw;
	}
	
	private static Img<DoubleType> allocateImage() {
		final ArrayImgFactory<DoubleType> imgFactory = new ArrayImgFactory<DoubleType>();
		return imgFactory.create(new long[]{XSIZE,YSIZE}, new DoubleType());
	}

	private static Img<? extends RealType<?>> makeInputImage() {
		Img<? extends RealType<?>> inputImg = allocateImage();
		RandomAccess<? extends RealType<?>> accessor = inputImg.randomAccess();
		long[] pos = new long[2];
		for (int x = 0; x < XSIZE; x++) {
			for (int y = 0; y < YSIZE; y++) {
				pos[0] = x;
				pos[1] = y;
				accessor.setPosition(pos);
				accessor.get().setReal(x+y);
			}			
		}
		return inputImg;
	}
	
	private class ProductFunction implements Function<long[],Real> {

		private Function<long[],Real> otherFunc;
		private Real variable;
		
		public ProductFunction(Function<long[],Real> otherFunc) {
			this.otherFunc = otherFunc;
			this.variable = createVariable();
		}
		
		@Override
		public void evaluate(Neighborhood<long[]> region, long[] point, Real output) {
			double product = 1;
			RegionIndexIterator iter = new RegionIndexIterator(region);
			while (iter.hasNext()) {
				iter.fwd();
				otherFunc.evaluate(region, iter.getPosition(), variable);
				product *= variable.getReal();
			}
			output.setReal(product);
		}

		@Override
		public Real createVariable() {
			return new Real();
		}
		
	}

	// Tricky part
	//   condition is only true for some values relative to outermost neighborhood keypoint.
	//   condition does not know outermost reference point since its nested in a neighborhood
	//   local to ProductFunction. maybe key point needs to get passed down in.

	// might need a somewhat large change
	//   a function is evaluated at a point inside a neighborhood and dumping into a variable
	//   func.eval(point,neigh,variable);
	// and Condition would be true at a point in a neigh
	//    cond.isTrue(point,neigh);
	
	private class OnTheXYCrossCondition implements Condition<long[]> {
		
		@Override
		public boolean isTrue(Neighborhood<long[]> neigh, long[] point) {
			long dx = point[0] - neigh.getKeyPoint()[0];
			long dy = point[1] - neigh.getKeyPoint()[1];
			if (Math.abs(dx) != Math.abs(dy))
				return false;
			return true;
		}
	}
	
	private boolean testCrossNeighborhoodProduct() {
		boolean success = true;
		
		Img<? extends RealType<?>> inputImg = makeInputImage();
		
		DiscreteNeigh neigh = new DiscreteNeigh(new long[2], new long[]{1,1}, new long[]{1,1});
		Condition<long[]> condition = new OnTheXYCrossCondition();
		Function<long[],Real> input = new RealImageFunction(inputImg);
		Function<long[],Real> one = new ConstantRealFunction<long[]>(1);
		Function<long[],Real> conditionalFunc = new ConditionalFunction<long[],Real>(condition, input, one);
		Function<long[],Real> prodFunc = new ProductFunction(conditionalFunc); 
		long[] index = new long[2];
		Real output = new Real();
		for (int x = 1; x < XSIZE-1; x++) {
			for (int y = 1; y < YSIZE-1; y++) {
				index[0] = x;
				index[1] = y;
				neigh.moveTo(index);
				prodFunc.evaluate(neigh, neigh.getKeyPoint(), output);
				if (!veryClose(output.getReal(), expectedValue(x,y))) {
					System.out.println(" FAILURE at ("+x+","+y+"): expected ("
						+expectedValue(x,y)+") actual ("+output.getReal()+")");
					success = false;
				}
			}
		}
		return success;
	}
	
	public static void main(String[] args) {
		System.out.println("Example4");
		if (new Example4().testCrossNeighborhoodProduct())
			System.out.println(" Successful test");
	}
}
