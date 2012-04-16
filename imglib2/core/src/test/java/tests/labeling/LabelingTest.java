/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package tests.labeling;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RealRandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.basictypeaccess.IntAccess;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.labeling.DefaultROIStrategyFactory;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingType;
import net.imglib2.labeling.NativeLabeling;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;


/**
 * TODO
 *
 * @author Lee Kamentsky
 */
public class LabelingTest {
	protected <T extends Comparable<T>> Labeling<T> makeLabeling(T exemplar, long [] dimensions) {
		NativeLabeling<T, IntAccess> labeling;
		labeling = new NativeImgLabeling<T>(dimensions, new ArrayImgFactory<LabelingType<T>>());
		LabelingType<T> type = new LabelingType<T>(labeling);
		labeling.setLinkedType(type);
		return labeling;
	}
	protected <T extends Comparable<T>> Labeling<T> makeLabeling(
			long [][] coordinates, T [] labels, long [] dimensions) {
		assertTrue(labels.length > 0);
		assertEquals(labels.length, coordinates.length);
		Labeling<T> labeling = makeLabeling(labels[0], dimensions);
		RandomAccess<LabelingType<T>> a = labeling.randomAccess();
		for (int i=0;i < coordinates.length; i++) {
			a.setPosition(coordinates[i]);
			List<T> currentLabels = new ArrayList<T>(a.get().getLabeling());
			if (! currentLabels.contains(labels[i]))
				currentLabels.add(labels[i]);
			a.get().setLabeling(currentLabels);
		}
		return labeling;
	}
	
	protected <T extends Comparable<T>> void labelSphere(
			Labeling<T> labeling, T label, double [] center, double radius) {
		Cursor<LabelingType<T>> c = labeling.localizingCursor();
		long [] position = new long [labeling.numDimensions()];
		while(c.hasNext()) {
			LabelingType<T> t = c.next();
			c.localize(position);
			double distance2 = 0;
			double distance = 0;
			for (int i=0; i<position.length; i++) {
				distance = ((double) position[i] - center[i]);
				distance2 += distance * distance;
			}
			distance = Math.sqrt(distance2);
			if (distance <= radius) {
				List<T> labels = new ArrayList<T>(t.getLabeling());
				if (! labels.contains(label))
					labels.add(label);
					t.setLabeling(labels);
			}
		}
	}
	@Test
	public void testDefaultConstructor() {
		long [] dimensions = { 5,6,7 };
		Labeling<String> labeling;
		labeling = new NativeImgLabeling<String>(
				dimensions, new ArrayImgFactory<LabelingType<String>>());
		assertEquals(3, labeling.numDimensions());
	}
	
	@Test
	public void testFactoryConstructor() {
		long [] dimensions = { 5,6,7 };
		Labeling<String> labeling;
		labeling = new NativeImgLabeling<String>(
				dimensions, new DefaultROIStrategyFactory<String>(), 
				new ArrayImgFactory<LabelingType<String>>());
		assertEquals(3, labeling.numDimensions());
	}
	
	@Test
	public void testEmptyImage() {
		Labeling<String> labeling = makeLabeling("Foo", new long [] { 5,6,7});
		assertTrue(labeling.getLabels().isEmpty());
		int iterations = 0;
		for (LabelingType<String> t:labeling){
			assertTrue(t.getLabeling().size() == 0);
			iterations++;
		}
		assertTrue(iterations == 5 * 6 * 7);
	}
	
	@Test
	public void testLabelOne() {
		long [][] coordinates = {{ 1,1,1}};
		String [] labels = { "Foo" };
		long [] dimensions = new long [] { 5,6,7};
		Labeling<String> labeling = makeLabeling(coordinates, labels, dimensions);
		assertEquals(labeling.getLabels().size(), 1);
		assertTrue(labeling.getLabels().contains("Foo"));
	}
	@Test
	public void testGetAreaOne() {
		long [][] coordinates = {{ 1,1,1}};
		String [] labels = { "Foo" };
		long [] dimensions = new long [] { 5,6,7};
		Labeling<String> labeling = makeLabeling(coordinates, labels, dimensions);
		assertEquals(labeling.getArea("Foo"), 1);
	}
	@Test
	public void testExtentsOne() {
		long [][] coordinates = {{ 1,3,5}};
		String [] labels = { "Foo" };
		long [] dimensions = new long [] { 5,6,7};
		Labeling<String> labeling = makeLabeling(coordinates, labels, dimensions);
		long [] minExtents = new long[3];
		long [] maxExtents = new long[3];
		assertFalse(labeling.getExtents("Bar", minExtents, maxExtents));
		assertTrue(labeling.getExtents("Foo", minExtents, maxExtents));
		assertArrayEquals(coordinates[0], minExtents);
		assertArrayEquals(coordinates[0], maxExtents);
	}
	@Test
	public void testExtentsMany() {
		long [][] coordinates = {{ 1,4,5}, {2,3,6} };
		long [] expectedMinExtents = { 1,3,5 };
		long [] expectedMaxExtents = { 2,4,6 };
		String [] labels = { "Foo", "Foo" };
		long [] dimensions = new long [] { 5,6,7};
		Labeling<String> labeling = makeLabeling(coordinates, labels, dimensions);
		long [] minExtents = new long[3];
		long [] maxExtents = new long[3];
		assertFalse(labeling.getExtents("Bar", minExtents, maxExtents));
		assertTrue(labeling.getExtents("Foo", minExtents, maxExtents));
		assertArrayEquals(expectedMinExtents, minExtents);
		assertArrayEquals(expectedMaxExtents, maxExtents);
	}
	@Test
	public void testRasterStartOne() {
		long [][] coordinates = {{ 1,3,5}};
		String [] labels = { "Foo" };
		long [] dimensions = new long [] { 5,6,7};
		Labeling<String> labeling = makeLabeling(coordinates, labels, dimensions);
		long [] rasterStart = new long[3];
		assertFalse(labeling.getRasterStart("Bar", rasterStart));
		assertTrue(labeling.getRasterStart("Foo", rasterStart));
		assertArrayEquals(coordinates[0], rasterStart);
	}
	@Test
	public void testRandomAccessible() {
		long [][] coordinates = {{ 1,3,5}};
		String [] labels = { "Foo" };
		long [] dimensions = new long [] { 5,6,7};
		Labeling<String> labeling = makeLabeling(coordinates, labels, dimensions);
		RandomAccess<LabelingType<String>> a = labeling.randomAccess();
		a.setPosition(coordinates[0]);
		List<String> list = a.get().getLabeling();
		assertEquals(list.size(), 1);
		assertEquals(list.get(0), "Foo");
		a.setPosition(new long [] { 1,2,3});
		list = a.get().getLabeling();
		assertEquals(list.size(), 0);
	}
	@Test
	public void testROIRandomAccess() {
		long [][] coordinates = {{ 1,3,5}};
		String [] labels = { "Foo" };
		long [] dimensions = new long [] { 5,6,7};
		Labeling<String> labeling = makeLabeling(coordinates, labels, dimensions);
		RealRandomAccess<BitType> a = labeling.getRegionOfInterest("Foo").realRandomAccess();
		a.setPosition(coordinates[0]);
		assertTrue(a.get().get());
		a.setPosition(new long [] { 1,2,3});
		assertFalse(a.get().get());
	}
	
	@Test
	public void testLocalizableCursorOne() {
		long [][] coordinates = {{ 1,3,2}};
		int expected = 132;
		String [] labels = { "Foo" };
		long [] dimensions = new long [] { 5,6,7};
		Labeling<String> labeling = makeLabeling(coordinates, labels, dimensions);
		Img<IntType> img = new ArrayImgFactory<IntType>().create(dimensions, new IntType());
		RandomAccess<IntType> a = img.randomAccess();
		for (int i = 0; i<dimensions[0]; i++) {
			a.setPosition(i, 0);
			for (int j = 0; j < dimensions[1]; j++) {
				a.setPosition(j, 1);
				for (int k = 0; k < dimensions[2]; k++) {
					a.setPosition(k, 2);
					a.get().set(i * 100 + j*10 + k);
				}
			}
		}
		Cursor<IntType> c = labeling.getIterableRegionOfInterest("Foo").getIterableIntervalOverROI(img).cursor();
		int iterations = 0;
		while(c.hasNext()) {
			IntType t = c.next();
			iterations++;
			assertEquals(c.getLongPosition(0), 1);
			assertEquals(c.getLongPosition(1), 3);
			assertEquals(c.getLongPosition(2), 2);
			assertEquals(expected, t.get());
		}
		assertEquals(1, iterations);
	}
	
	@Test
	public void testSphere() {
		long [] dimensions = new long [] { 20,20,20 };
		Labeling<String> labeling = makeLabeling("MyLabels", dimensions);
		labelSphere(labeling, "Foo", new double [] { 10,9,8 }, 5);
		/*
		 * Test the extents
		 */
		long [] minExtents = new long[3];
		long [] maxExtents = new long[3];
		assertTrue(labeling.getExtents("Foo", minExtents, maxExtents));
		assertArrayEquals(new long [] { 5,4,3}, minExtents);
		assertArrayEquals(new long [] { 15, 14, 13 }, maxExtents);
		/*
		 * Test the raster start which should be 5, 9, 8
		 */
		long [] start = new long[3];
		assertTrue(labeling.getRasterStart("Foo", start));
		assertArrayEquals(new long [] { 5, 9, 8}, start);
		double expectedVolumeLow = 4./ 3. * Math.PI * Math.pow(4.5, 3);
		double expectedVolumeHigh = 4./ 3. * Math.PI * Math.pow(5.5, 3);
		assertTrue(labeling.getArea("Foo") > expectedVolumeLow);
		assertTrue(labeling.getArea("Foo") < expectedVolumeHigh);
		RandomAccess<LabelingType<String>> a = labeling.randomAccess();
		Img<DoubleType> img = new ArrayImgFactory<DoubleType>().create(dimensions, new DoubleType());
		RandomAccess<DoubleType> img_a = img.randomAccess();
		for (int i = 0; i<dimensions[0]; i++) {
			img_a.setPosition(i, 0);
			for (int j = 0; j < dimensions[1]; j++) {
				img_a.setPosition(j, 1);
				for (int k = 0; k < dimensions[2]; k++) {
					img_a.setPosition(k, 2);
					img_a.get().set(Math.sqrt((i-10)*(i-10) + (j-9)*(j-9) + (k-8)*(k-8)));
				}
			}
		}
		Cursor<DoubleType> c = labeling.getIterableRegionOfInterest("Foo").getIterableIntervalOverROI(img).cursor();
		int iterations = 0;
		long [] position = new long[3];
		while(c.hasNext()) {
			iterations++;
			DoubleType t = c.next();
			assertTrue(t.get() <= 5);
			c.localize(position);
			a.setPosition(position);
			assertEquals(a.get().getLabeling().size(), 1);
		}
		assertEquals(iterations, labeling.getArea("Foo"));
	}
	@Test
	public void testTwoLabels() {
		long [] dimensions = new long [] { 20,20,40 };
		Labeling<String> labeling = makeLabeling("MyLabels", dimensions);
		String [] labels = { "Foo", "Bar" };
		double [][] centers = {{10,9,8}, { 8, 9, 30 }};
		for (int i=0; i<2; i++) {
			labelSphere(labeling, labels[i], centers[i], 5);
		}
		long [] temp = new long[3];
		for (int i=0; i<2; i++ ) {
			double [] coords = new double[3];
			Arrays.fill(coords, 0);
			// oooooo 
			// it's a cursor iterating over the labels themselves for one label
			// oooooo
			
			Cursor<LabelingType<String>> c = labeling.getIterableRegionOfInterest(labels[i]).getIterableIntervalOverROI(labeling).cursor();
			while(c.hasNext()){
				LabelingType<String> t = c.next();
				c.localize(temp);
				for (int j=0;j<temp.length; j++) {
					coords[j] += temp[j];
				}
				assertEquals(t.getLabeling().size(), 1);
				assertEquals(t.getLabeling().get(0), labels[i]);
			}
			for (int j=0;j<coords.length; j++) {
				coords[j] /= labeling.getArea(labels[i]);
				assertTrue(Math.abs(coords[j] - centers[i][j]) < .5);
			}
		}
	}
	@Test
	public void testOverlappingLabels() {
		long [] dimensions = new long [] { 20,20,30 };
		Labeling<String> labeling = makeLabeling("MyLabels", dimensions);
		String [] labels = { "Foo", "Bar" };
		double [][] centers = {{10,9,8}, { 8, 9, 12 }};
		for (int i=0; i<2; i++) {
			labelSphere(labeling, labels[i], centers[i], 5);
		}
		Collection<String> foundLabels = labeling.getLabels();
		assertEquals(foundLabels.size(),2);
		for (int i=0; i<2; i++) {
			assertTrue(foundLabels.contains(labels[i]));
		}
		long [] temp = new long[3];
		for (int i=0; i<2; i++ ) {
			double [] coords = new double[3];
			Arrays.fill(coords, 0);
			Cursor<LabelingType<String>> c = labeling.getIterableRegionOfInterest(labels[i]).getIterableIntervalOverROI(labeling).cursor();
			while(c.hasNext()){
				LabelingType<String> t = c.next();
				c.localize(temp);
				long [] d = new long[] { 0, 0 };
				for (int j=0;j<temp.length; j++) {
					coords[j] += temp[j];
					for (int k=0; k<d.length; k++) {
						d[k] += (temp[j] - centers[k][j]) * (temp[j] - centers[k][j]);
					}
				}
				boolean in_both = ((d[0] <= 25) & (d[1] <= 25));
				assertEquals(t.getLabeling().size(), in_both?2:1);
				if (in_both) {
					// Canonical order is alphabetical, but order of placement
					// would reverse.
					assertEquals(t.getLabeling().get(0), "Bar");
					assertEquals(t.getLabeling().get(1), "Foo");
				}
				assertTrue(t.getLabeling().contains(labels[i]));
			}
			for (int j=0;j<coords.length; j++) {
				coords[j] /= labeling.getArea(labels[i]);
				assertTrue(Math.abs(coords[j] - centers[i][j]) < .5);
			}
		}
	}
	@Test
	public void TestCopy() {
		long [] dimensions = new long [] { 20,30 };
		Labeling<Integer> labeling = makeLabeling(1, dimensions);
		Random r = new Random(202030);
		for (LabelingType<Integer> t:labeling) {
			t.setLabel(r.nextInt(10)+1);
		}
		Img<LabelingType<Integer>> copy = labeling.copy();
		Cursor<LabelingType<Integer>> c = copy.cursor();
		RandomAccess<LabelingType<Integer>> ra = labeling.randomAccess();
		while(c.hasNext()) {
			LabelingType<Integer> t = c.next();
			List<Integer> y = t.getLabeling();
			ra.setPosition(c);
			List<Integer> x = ra.get().getLabeling();
			assertEquals(y.size(), 1);
			assertEquals(x.get(0), y.get(0));
		}
	}
	
	@Test
	public void testPerformance()
	{
		final int rounds = 10;
		final long[] dimensions = new long[] { 1000, 1000, 40};
		final Labeling< Integer > labeling = makeLabeling( 1, dimensions );
				
		for(int r = 0; r  < rounds; r++)
		{
			final Cursor< LabelingType< Integer >> c = labeling.cursor();
			while( c.hasNext() )
			{
				c.fwd();		
				c.get().getLabeling();
			}
		}
	}
}
