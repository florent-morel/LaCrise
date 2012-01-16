package org.lacrise.activity.run;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.lacrise.GameManager;
import org.lacrise.R;
import org.lacrise.engine.Constants;
import org.lacrise.engine.game.Player;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageView;

public class ScoreChart extends Activity {

	private static GameManager mGameManager;

	private static Resources mResources;

	private static int[] colors = { Color.RED, Color.BLUE, Color.GREEN };

	// these_labela has elemnes[label,maxX,maxY]
	static int draw_only_this_idx = -1;
	static int[] drawSizes;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.score_chart);
		mResources = getResources();
		mGameManager = GameManager.getSingletonObject();

		ImageView image = (ImageView) findViewById(R.id.score_chart_img);

		Bitmap emptyBmap = Bitmap.createBitmap(250, 200, Config.ARGB_8888);

		int width = emptyBmap.getWidth();
		int height = emptyBmap.getHeight();
		Bitmap charty = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);

		charty = drawPlayerChart(emptyBmap);

		image.setImageBitmap(charty);
	}

	private Bitmap drawPlayerChart(Bitmap bitmap) {
		// code to get bitmap onto screen
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff0B0B61;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 12;

		// get the little rounded cornered outside
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		// ---- NOw just draw on this bitmap

		// Set the labels info manually
		String[] cur_elt_array = new String[4];
		cur_elt_array[0] = String.format(mResources
				.getString(R.string.score_board), mGameManager.getGame()
				.getRoundNumber(), mGameManager.getGame().getScoreToReach());
		cur_elt_array[1] = "volts";
		cur_elt_array[2] = mGameManager.getGame().getMaxScore().toString();
		cur_elt_array[3] = mGameManager.getGame().getMinScore().toString();

		List<String[]> labels = new ArrayList<String[]>();
		labels.add(cur_elt_array);

		this.drawGrid(canvas, labels);

		for (Player player : mGameManager.getGame().getPlayerList()) {

			// set the data to be plotted and we should be on our way
			List<Integer> playerScoreList = new ArrayList<Integer>();
			// Add zero value as starting score
			playerScoreList.add(Constants.ZERO_VALUE);

			Map<Integer, List<Integer>> playerScorePerRound = mGameManager.getGame().getPlayerScorePerRound(player.getId());

//			for (Integer roundScore : )) {
//				if (roundScore != null) {
//					playerScoreList.add(roundScore);
//				}
//			}

			drawPlayerScores(canvas, playerScorePerRound, labels, 0,
					colors[player.getId()]);
		}

		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	// these_labels is vector of [label,units,max.min]

	private void drawGrid(Canvas canvas, List<String[]> labels) {

		double rounded_max = 0.0;
		double rounded_min = 0.0;
		Object curElt;
		String[] cur_elt_array;
		int left_margin_d;

		if (draw_only_this_idx == -1)
			curElt = labels.get(0); // default it to 1st one if non set
		else
			curElt = labels.get(draw_only_this_idx); // now just the 1st elt

		cur_elt_array = (String[]) curElt;

		rounded_max = get_ceiling_or_floor(
		 Double.parseDouble(cur_elt_array[2]), true);
		rounded_min = get_ceiling_or_floor(
		 Double.parseDouble(cur_elt_array[3]), false);

		// ok so now we have the max value of the set just get a cool ceiling
		// and we go on
		final Paint paint = new Paint();
		paint.setTextSize(15);

		left_margin_d = getCurTextLengthInPixels(paint,
				Double.toString(rounded_max));
		// keep the position for later drawing -- leave space for the legend
		int p_height = 170;
		int p_width = 220;
		int nbCells = mGameManager.getGame().getRoundNumber() + 1;
		int[] tmp_draw_sizes = { 2 + left_margin_d, 25,
				p_width - 2 - left_margin_d, p_height - 25 - nbCells };
		drawSizes = tmp_draw_sizes; // keep it for later processing

		// with the margins worked out draw the plotting grid
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);

		// Android does by coords
		canvas.drawRect(drawSizes[0], drawSizes[1],
				drawSizes[0] + drawSizes[2], drawSizes[1] + drawSizes[3], paint);

		paint.setColor(Color.GRAY);

		// finally draw the grid

		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(drawSizes[0], drawSizes[1],
				drawSizes[0] + drawSizes[2], drawSizes[1] + drawSizes[3], paint);

		for (int i = 1; i < nbCells; i++) {

			// Test to display lines gray and white alternatively
//			if (i % 2 == 0) {
//				paint.setColor(Color.GRAY);
//			} else {
//				paint.setColor(Color.WHITE);
//			}

			canvas.drawLine(drawSizes[0],
					drawSizes[1] + (i * drawSizes[3] / nbCells), drawSizes[0]
							+ drawSizes[2], drawSizes[1]
							+ (i * drawSizes[3] / nbCells), paint);
			canvas.drawLine(drawSizes[0] + (i * drawSizes[2] / nbCells),
					drawSizes[1], drawSizes[0] + (i * drawSizes[2] / nbCells),
					drawSizes[1] + drawSizes[3], paint);

		}

		// good for one value
		printAxisLegend(canvas,
				Double.valueOf(rounded_max).intValue(),
				Double.valueOf(rounded_min).intValue(), cur_elt_array[0], 2, 0);

	}

	private void printAxisLegend(Canvas thisDrawingArea,
			Integer curMax, Integer curMin, String cur_label,
			int x_guide, int this_idx) {
		Integer lineLabel;
		int nbSectors = curMax / 50;

		if (nbSectors ==0 || nbSectors > 4) {
		  // Default value: 4
		  nbSectors = 4;
		}

		Integer delta = (curMax - curMin) / nbSectors;
		final Paint paint = new Paint();

		paint.setColor(Color.WHITE);
		paint.setTypeface(Typeface.SANS_SERIF);

		paint.setTextSize(8);

		// '<=' because we display from zero to curMax
		for (int i = 0; i <= nbSectors; i++) {
			// 'work our the values so is proper

			lineLabel = curMin + delta * i;

			if (i == nbSectors) {
				thisDrawingArea.drawText(lineLabel.toString(), x_guide - 2, drawSizes[1]
						+ drawSizes[3] - (i * drawSizes[3] / nbSectors), paint);
			} else {
				thisDrawingArea.drawText(lineLabel.toString(), x_guide - 2, drawSizes[1]
						+ drawSizes[3] - (i * drawSizes[3] / nbSectors) - 3, paint);
			}
		}

		paint.setTextSize(10);
		switch (this_idx) {
		case 0:
			thisDrawingArea.drawText(cur_label, x_guide - 2, drawSizes[1] - 15,
					paint);
			break;
		case 1:

			thisDrawingArea.drawText(cur_label, x_guide - 2 - 30,
					drawSizes[1] - 15, paint);
			break;
		}

	}

	private Point scalePoint(int this_x, double this_y,
			Point drawPoint, int scr_x, int scr_y, int scr_width,
			int src_height, double maxX, double minX, double maxY, double minY) {
		int temp_x, temp_y;
		Point temp = new Point();

		if (maxY == minY) // skip bad data
			return null;

		// don't touch it if is nothing
		try {
			temp_x = scr_x
					+ (int) (((double) this_x - minX) * ((double) scr_width / (maxX - minX)));
			temp_y = scr_y
					+ (int) ((maxY - this_y) * ((double) src_height / (maxY - minY)));

			temp.x = temp_x;
			temp.y = temp_y;
			drawPoint = temp;

		} catch (Exception e) {

			return (null);
		}

		return temp;

	}

	private boolean drawPlayerScores(Canvas canvas,
	    Map<Integer, List<Integer>> playerScoreMap, List<String[]> these_labels,
			int only_this_idx, int lineColor) {
		int lRow;
		int nParms;
		int prev_x = 0, prev_y = 0;
		int cur_x = 0, cur_y = 0;
		// Dim ShowMarker As Object
		Point point = new Point();
		point.set(0, 0);

		double cur_maxX, cur_minX = 0, cur_maxY = 20, cur_minY = 0;
		int cur_points_2_plot;

		int POINTS_TO_CHANGE = 30;
		double cur_OBD_val;

		// Object curElt;
		String curElt;
		Object curElt2;
		String[] cur_elt_array2;

		final Paint paint = new Paint();

		try // catch in this block for some thing
		{

			// 'Create the plot points for this series from the ChartPoints
			// array:
//			curElt = (String) playerScoreMap.get(0).toString();

			// the lines have to come out good
			paint.setStyle(Paint.Style.STROKE);
			//
			// for( nParms = 0 ; nParms < cur_elt_array.length ; nParms++ )
			nParms = only_this_idx;
			{

				// get cur item labels
				curElt2 = these_labels.get(nParms);
				cur_elt_array2 = (String[]) curElt2;

				cur_maxY = get_ceiling_or_floor(
						Double.parseDouble(cur_elt_array2[2]), true);
				cur_minY = get_ceiling_or_floor(
						Double.parseDouble(cur_elt_array2[3]), false);

				cur_points_2_plot = mGameManager.getGame().getRoundNumber() + 1;//playerScoreMap.size();
				cur_maxX = cur_points_2_plot;

//				curElt = (String) playerScoreList.get(0).toString();
//				cur_OBD_val = Double.parseDouble(curElt);

				// Draw origin point
				point = scalePoint(0, 0, point, drawSizes[0],
						drawSizes[1], drawSizes[2], drawSizes[3], cur_maxX,
						cur_minX, cur_maxY, cur_minY);

				cur_x = point.x;
				cur_y = point.y;

				paint.setColor(lineColor);

				// the point is only cool when samples are low
				if (cur_points_2_plot < POINTS_TO_CHANGE)
					canvas.drawRect(cur_x - 2, cur_y - 2, cur_x - 2 + 4,
							cur_y - 2 + 4, paint);

				prev_x = cur_x;
				prev_y = cur_y;

				// 'go and plot point for this parm -- pont after the 1st one
				// For each round, trace every scores player had

				Set<Entry<Integer, List<Integer>>> entrySet = playerScoreMap.entrySet();

				for (Entry<Integer, List<Integer>> entry : entrySet) {

				  Integer roundNumber = entry.getKey();
				  List<Integer> scores = entry.getValue();

				  for (lRow = 0; lRow < scores.size(); lRow++) {

				    curElt = (String) scores.get(lRow).toString();

				    cur_OBD_val = Double.parseDouble(curElt);

				    // 'work out an approx if cur Y values not avail(e.g.
				    // nothing)
				    if (cur_OBD_val == Double.NaN) {
				      continue; // skip bad one
				    } else {

				      point = scalePoint(roundNumber, cur_OBD_val, point,
				          drawSizes[0], drawSizes[1], drawSizes[2],
				          drawSizes[3], cur_maxX, cur_minX, cur_maxY,
				          cur_minY);

				      cur_x = point.x;
				      cur_y = point.y;

				      if (cur_points_2_plot < POINTS_TO_CHANGE) {
				        canvas.drawRect(cur_x - 2, cur_y - 2,
				            cur_x - 2 + 4, cur_y - 2 + 4, paint);
				      }

//				      if (roundNumber > 0) {
				        canvas.drawLine(prev_x, prev_y, cur_x, cur_y, paint);
//				      }
				      prev_x = cur_x;
				      prev_y = cur_y;

				    }
				  }

				}


			}
			return (true);
		} catch (Exception e) {
			return (false);
		}
	}

	// need the width of the labels
	private int getCurTextLengthInPixels(Paint this_paint,
			String this_text) {
		Rect rect = new Rect();
		this_paint.getTextBounds(this_text, 0, this_text.length(), rect);
		return rect.width();
	}

	private double get_ceiling_or_floor(double this_val, boolean is_max) {
		double this_min_tmp;
		int this_sign;
		int this_10_factor = 0;
		double this_rounded;

		if (this_val == 0.0) {
			this_rounded = 0.0;
			return this_rounded;
		}

		this_min_tmp = Math.abs(this_val);

		if (this_min_tmp >= 1.0 && this_min_tmp < 10.0)
			this_10_factor = 1;
		else if (this_min_tmp >= 10.0 && this_min_tmp < 100.0)
			this_10_factor = 10;
		else if (this_min_tmp >= 100.0 && this_min_tmp < 1000.0)
			this_10_factor = 100;
		else if (this_min_tmp >= 1000.0 && this_min_tmp < 10000.0)
			this_10_factor = 1000;
		else if (this_min_tmp >= 10000.0 && this_min_tmp < 100000.0)
			this_10_factor = 10000;

		// 'cover when min is pos and neg
		if (is_max) {
			if (this_val > 0.0)
				this_sign = 1;
			else
				this_sign = -1;

		} else {
			if (this_val > 0.0)
				this_sign = -1;
			else
				this_sign = 1;

		}

		if (this_min_tmp > 1)
			this_rounded = (double) (((int) (this_min_tmp / this_10_factor) + this_sign) * this_10_factor);
		else {
			this_rounded = (int) (this_min_tmp * 100.0);
			// ' cover same as above bfir number up to .001 less than tha it
			// will skip
			if (this_rounded >= 1 && this_rounded < 9)
				this_10_factor = 1;
			else if (this_rounded >= 10 && this_rounded < 99)
				this_10_factor = 10;
			else if (this_rounded >= 100 && this_rounded < 999)
				this_10_factor = 100;

			this_rounded = (double) (((int) ((this_rounded) / this_10_factor) + this_sign) * this_10_factor);
			this_rounded = (int) (this_rounded) / 100.0;

		}

		if (this_val < 0)
			this_rounded = -this_rounded;

		return this_rounded;

	}

}
