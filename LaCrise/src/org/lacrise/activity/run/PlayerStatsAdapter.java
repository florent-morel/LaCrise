package org.lacrise.activity.run;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lacrise.GameManager;
import org.lacrise.R;
import org.lacrise.engine.Constants;
import org.lacrise.engine.game.Player;
import org.lacrise.engine.game.Turn;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Display ongoing game options.
 * 
 * 
 * @author fmorel
 * 
 */
public class PlayerStatsAdapter extends PagerAdapter {

	private Context mContext;

	private static GameManager mGameManager;

	private Resources mResources;

	private TextView mNumberRounds;

	private TextView mCurrentRank;

	private TextView mZeroPenalty;

	private TextView mHit;

	private TextView mHitVictim;

	private TextView mBestRank;

	private TextView mWorstRank;

	private TextView mCurrentScore;

	private TextView mBestTurn;

	private TextView mAverageTurn;

	private Player mPlayer;

	private View mView;

	private Activity mActivity;

//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.player_stats);
//
//		mResources = getResources();
//		mGameManager = GameManager.getSingletonObject();
//
//		Integer playerId = this.getIntent().getIntExtra(Constants.PLAYER_ID, 0);
//		GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
//		View inflate = getLayoutInflater().inflate(R.layout.player_stats, null);
//		gestureOverlayView.addView(inflate);
//		gestureOverlayView.addOnGesturePerformedListener(this);
//		mGestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
//		if (!mGestureLib.load()) {
//			finish();
//		}
//		setContentView(gestureOverlayView);
//
//		mPlayer = mGameManager.getGame().getPlayerById(playerId);
//		setTitle(String.format(mResources.getString(R.string.statistics),
//				mPlayer.getName()));
//
//		fillStatsItems();
//
//	}

	public PlayerStatsAdapter(Activity activity) {
		super();
		mActivity = activity;
		mContext = activity.getBaseContext();
	    this.mResources = mContext.getResources();
		mGameManager = GameManager.getSingletonObject();


//		fillStatsItems();
	}

	private void fillStatsItems() {

		mNumberRounds = (TextView) mView.findViewById(R.id.number_of_rounds_text);
		mNumberRounds.setText(mPlayer.getLastPlayedTurnId().toString());

		mCurrentScore = (TextView) mView.findViewById(R.id.turn_score_current_text);
		mCurrentScore.setText(mPlayer.getTotalScore(false).toString());

		buildRankStat();
		buildZeroStat();
		buildHitStat(false);
		buildHitStat(true);

		mBestTurn = (TextView) mView.findViewById(R.id.turn_score_best_text);
		mBestTurn.setText(mGameManager.getGame().getBestRoundScore(mPlayer)
				.toString());

		mAverageTurn = (TextView) mView.findViewById(R.id.turn_score_average_text);
		mAverageTurn.setText(mGameManager.getGame().getAverageScore(mPlayer)
				.toString());
	}

	private void buildRankStat() {
		mCurrentRank = (TextView) mView.findViewById(R.id.rank_text);
		mCurrentRank.setText(mGameManager.getGame().getPlayerRank(mPlayer)
				.toString());

		Turn bestRank = mGameManager.getGame().getBestRank(mPlayer);
		if (bestRank != null) {
			mBestRank = (TextView) mView.findViewById(R.id.rank_best_text);
			mBestRank
					.setText(String.format(mResources
							.getString(R.string.rank_value), bestRank
							.getPlayerEndRank().get(mPlayer.getId()), bestRank
							.getId()));
		}

		Turn worstRank = mGameManager.getGame().getWorstRank(mPlayer);
		if (worstRank != null) {
			mWorstRank = (TextView) mView.findViewById(R.id.rank_worst_text);
			mWorstRank
					.setText(String.format(
							mResources.getString(R.string.rank_value),
							worstRank.getPlayerEndRank().get(mPlayer.getId()),
							worstRank.getId()));
		}
	}

	private void buildZeroStat() {
		Integer number = mGameManager.getGame().getNumberZeroPenalty(mPlayer);
		if (number > 0) {
			mZeroPenalty = (TextView) mView.findViewById(R.id.zero_penalty_text);
			mZeroPenalty.setText(number.toString());
		}
	}

	private void buildHitStat(boolean isVictim) {
		Map<Integer, List<Player>> playerPenaltyMap = mGameManager.getGame()
				.getMaxHit(mPlayer, isVictim);
		if (playerPenaltyMap != null && !playerPenaltyMap.isEmpty()) {
			StringBuilder message = new StringBuilder();
			Integer nbHits = Constants.ZERO_VALUE;
			for (Entry<Integer, List<Player>> entry : playerPenaltyMap
					.entrySet()) {
				nbHits = entry.getKey();
				if (nbHits > Constants.ZERO_VALUE) {
					for (Player player : entry.getValue()) {
						message.append(player.getName());
						message.append(Constants.SPACE);
					}
				}
			}

			if (nbHits > Constants.ZERO_VALUE) {
				if (isVictim) {
					mHitVictim = (TextView) mView.findViewById(R.id.penalty_victim_text);
					mHitVictim.setText(String.format(
							mResources.getString(R.string.penalty_hit_value),
							message.toString(), nbHits));
				} else {
					mHit = (TextView) mView.findViewById(R.id.penalty_hit_text);
					mHit.setText(String.format(
							mResources.getString(R.string.penalty_hit_value),
							message.toString(), nbHits));
				}
			}
		}
	}

//	@Override
//	public void onGesturePerformed(GestureOverlayView arg0, Gesture gesture) {
//		ArrayList<Prediction> predictions = mGestureLib.recognize(gesture);
//		for (Prediction prediction : predictions) {
//			if (prediction.score > 1.0) {
//				if (Constants.RIGHT.equalsIgnoreCase(prediction.name)) {
//					// Get previous player id
//					displayStats(mGameManager.getNextPlayer(mPlayer, false,
//							true, false).getId());
//				} else if (Constants.LEFT.equalsIgnoreCase(prediction.name)) {
//					// Get next player id
//					displayStats(mGameManager.getNextPlayer(mPlayer, false,
//							true, true).getId());
//				}
//			}
//		}
//	}

//	private void displayStats(Integer playerId) {
//		Intent intent = new Intent(this, PlayerStatsAdapter.class);
//		intent.putExtra(Constants.PLAYER_ID, playerId);
//		startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
//		finish();
//	}

	@Override
	public int getCount() {
		return mGameManager.getGame().getPlayerList().size();
	}

    /**
     * Create the page for the given position.  The adapter is responsible
     * for adding the view to the container given here, although it only
     * must ensure this is done by the time it returns from
     * {@link #finishUpdate()}.
     *
     * @param container The containing View in which the page will be shown.
     * @param position The page position to be instantiated.
     * @return Returns an Object representing the new page.  This does not
     * need to be a View, but can be some other container of the page.
     */
        @Override
	public Object instantiateItem(ViewGroup group, int position) {
		LayoutInflater vi = (LayoutInflater) (mContext)
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = vi.inflate(R.layout.player_stats, null);
		mPlayer = mGameManager.getGame().getPlayerById(position);

		group.addView(mView, 0);

		// switch to page 2 (index 1) if we're out of bounds
		// if (viewPager.getCurrentItem() < 0 || viewPager.getCurrentItem() >=
		// viewPagerAdapter.getCount()) {
		// viewPager.setCurrentItem(1, false);
		// }

		fillStatsItems();
		return mView;
	}

    /**
     * Remove a page for the given position.  The adapter is responsible
     * for removing the view from its container, although it only must ensure
     * this is done by the time it returns from {@link #finishUpdate()}.
     *
     * @param container The containing View from which the page will be removed.
     * @param position The page position to be removed.
     * @param object The same object that was returned by
     * {@link #instantiateItem(View, int)}.
     */
        @Override
        public void destroyItem(View collection, int position, Object view) {
                ((ViewPager) collection).removeView((ScrollView) view);
        }

        
        
        @Override
        public boolean isViewFromObject(View view, Object object) {
                return view==((ScrollView)object);
        }


//        @Override
//        public CharSequence getPageTitle (int position) {
        	// TODO this doesn't work, check why
//        	return String.format(mResources.getString(R.string.statistics),
//    				mGameManager.getGame().getPlayerById(position).getName());
//        }
        
    /**
     * Called when the a change in the shown pages has been completed.  At this
     * point you must ensure that all of the pages have actually been added or
     * removed from the container as appropriate.
     * @param container The containing View which is displaying this adapter's
     * page views.
     */
        @Override
        public void setPrimaryItem (ViewGroup container, int position, Object object) {
    		mActivity.setTitle(String.format(mResources.getString(R.string.statistics),
    				mGameManager.getGame().getPlayerById(position).getName()));
    		}
        

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {}

        @Override
        public Parcelable saveState() {
                return null;
        }

        @Override
        public void startUpdate(View arg0) {}

}
