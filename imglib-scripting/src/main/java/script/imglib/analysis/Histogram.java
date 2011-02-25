package script.imglib.analysis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.JFrame;

import mpicbg.imglib.algorithm.math.ComputeMinMax;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.img.ImgCursor;
import mpicbg.imglib.type.numeric.ARGBType;
import mpicbg.imglib.type.numeric.RealType;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

import script.imglib.algorithm.fn.AlgorithmUtil;

/** An histogram of the image (or an image computed from an IFunction)
 * between its minimum and maximum values,
 * with as many bins as desired (defaults to 256 bins).
 * 
 * The number of bins is then the {@link Histogram#size()},
 * and the value of each bin (each element in this {@link ArrayList})
 * is an integer, which is the count of voxels whose value falls within
 * the bin.
 */
public class Histogram<T extends RealType<T>> extends TreeMap<Double,Long>
{
	private static final long serialVersionUID = 1L;

	private final Img<T> img;
	private final double min, max, increment;

	public Histogram(final Object fn) throws Exception {
		this(fn, 256);
	}

	public Histogram(final Object fn, final Number nBins) throws Exception {
		this(fn, nBins.intValue());
	}

	@SuppressWarnings("unchecked")
	public Histogram(final Object fn, final int nBins) throws Exception {
		this.img = AlgorithmUtil.wrap(fn);
		ComputeMinMax<T> cmm = new ComputeMinMax<T>(this.img);
		cmm.process();
		this.min = cmm.getMin().getRealDouble();
		this.max = cmm.getMax().getRealDouble();
		this.increment = process(img, nBins, min, max);
	}

	@SuppressWarnings("unchecked")
	public Histogram(final Object fn, final int nBins, final double min, final double max) throws Exception {
		this.img = AlgorithmUtil.wrap(fn);
		this.min = min;
		this.max = max;
		this.increment = process(img, nBins, min, max);
	}

	private final double process(final Img<T> img, final int nBins, final double min, final double max) throws Exception {
		final double range = max - min;
		final double increment = range / nBins;
		final long[] bins = new long[nBins];
		//
		if (0.0 == range) {
			bins[0] = img.size();
		} else {
			final ImgCursor<T> c = img.cursor();
			// zero-based:
			final int N = nBins -1;
			// Analyze the image
			while (c.hasNext()) {
				c.fwd();
				int v = (int)(((c.get().getRealDouble() - min) / range) * N);
				if (v < 0) v = 0;
				else if (v > N) v = N;
				bins[v] += 1;
			}
		}
		// Put the contents of the bins into this ArrayList:
		for (int i=0; i<bins.length; i++) {
			this.put( min + i * increment, bins[i] );
		}
		
		return increment;
	}
	
	public double getMin() { return min; }
	public double getMax() { return max; }
	public double getIncrement() { return increment; }
	public Img<T> getImage() { return img; }

	public Img<ARGBType> asImage() {
		return ChartUtils.asImage(asChart(false));
	}

	public JFreeChart asChart() {
		return asChart(false);
	}

	/** Return the JFreeChart with this histogram, and as a side effect, show it in a JFrame
	 * that provides the means to edit the dimensions and also the plot properties via a popup menu. */
	public JFreeChart asChart(final boolean show) {
		double[] d = new double[this.size()];
		int i = 0;
		for (Number num : this.values()) d[i++] = num.doubleValue();
		HistogramDataset hd = new HistogramDataset();
		hd.setType(HistogramType.RELATIVE_FREQUENCY);
		String title = "Histogram";
		hd.addSeries(title, d, d.length);
		JFreeChart chart = ChartFactory.createHistogram(title, "", "", hd,
				PlotOrientation.VERTICAL, false, false, false);
		setTheme(chart);
		if (show) {
			JFrame frame = new JFrame(title);
			frame.getContentPane().add(new ChartPanel(chart));
			frame.pack();
			frame.setVisible(true);
		}
		return chart;
	}
	
	static private final void setTheme(final JFreeChart chart) {
		XYPlot plot = (XYPlot) chart.getPlot();
		XYBarRenderer r = (XYBarRenderer) plot.getRenderer();
		StandardXYBarPainter bp = new StandardXYBarPainter();
		r.setBarPainter(bp);
		r.setSeriesOutlinePaint(0, Color.lightGray);
		r.setShadowVisible(false);
		r.setDrawBarOutline(false);
		setBackgroundDefault(chart);
	}

	static private final void setBackgroundDefault(final JFreeChart chart) {
		BasicStroke gridStroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{2.0f, 1.0f}, 0.0f);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setRangeGridlineStroke(gridStroke);
		plot.setDomainGridlineStroke(gridStroke);
		plot.setBackgroundPaint(new Color(235, 235, 235));
		plot.setRangeGridlinePaint(Color.white);
		plot.setDomainGridlinePaint(Color.white);
		plot.setOutlineVisible(false);
		plot.getDomainAxis().setAxisLineVisible(false);
		plot.getRangeAxis().setAxisLineVisible(false);
		plot.getDomainAxis().setLabelPaint(Color.gray);
		plot.getRangeAxis().setLabelPaint(Color.gray);
		plot.getDomainAxis().setTickLabelPaint(Color.gray);
		plot.getRangeAxis().setTickLabelPaint(Color.gray);
		chart.getTitle().setPaint(Color.gray);
	}
}
