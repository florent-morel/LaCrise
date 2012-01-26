package org.lacrise.activity.run;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lacrise.GameManager;
import org.lacrise.R;
import org.lacrise.engine.Constants;
import org.lacrise.engine.game.Player;
import org.lacrise.engine.game.Turn;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Display ongoing game options.
 *
 *
 * @author fmorel
 *
 */
public class Statistics extends Activity implements OnGesturePerformedListener {

	private GestureLibrary mGestureLib;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_stats);

		mResources = getResources();
		mGameManager = GameManager.getSingletonObject();

		Integer playerId = this.getIntent().getIntExtra(Constants.PLAYER_ID, 0);
		GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
		View inflate = getLayoutInflater().inflate(R.layout.player_stats, null);
		gestureOverlayView.addView(inflate);
		gestureOverlayView.addOnGesturePerformedListener(this);
		mGestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
		if (!mGestureLib.load()) {
			finish();
		}
		setContentView(gestureOverlayView);

		mPlayer = mGameManager.getGame().getPlayerById(playerId);
		setTitle(String.format(mResources.getString(R.string.statistics),
				mPlayer.getName()));

		fillStatsItems();

	}

	private void fillStatsItems() {

		mNumberRounds = (TextView) findViewById(R.id.number_of_rounds_text);
		mNumberRounds.setText(mPlayer.getLastPlayedTurnId().toString());

		mCurrentScore = (TextView) findViewById(R.id.turn_score_current_text);
		mCurrentScore.setText(mPlayer.getTotalScore(false).toString());

		buildRankStat();
		buildZeroStat();
		buildHitStat(false);
		buildHitStat(true);

		mBestTurn = (TextView) findViewById(R.id.turn_score_best_text);
		mBestTurn.setText(mGameManager.getGame().getBestRoundScore(mPlayer)
				.toString());

		mAverageTurn = (TextView) findViewById(R.id.turn_score_average_text);
		mAverageTurn.setText(mGameManager.getGame().getAverageScore(mPlayer)
				.toString());
	}

	private void buildRankStat() {
		mCurrentRank = (TextView) findViewById(R.id.rank_text);
		mCurrentRank.setText(mGameManager.getGame().getPlayerRank(mPlayer)
				.toString());

		Turn bestRank = mGameManager.getGame().getBestRank(mPlayer);
		mBestRank = (TextView) findViewById(R.id.rank_best_text);
		mBestRank.setText(String.format(
				mResources.getString(R.string.rank_value),
				bestRank.getPlayerEndRank().get(mPlayer.getId()), bestRank.getId()));

		Turn worstRank = mGameManager.getGame().getWorstRank(mPlayer);
		mWorstRank = (TextView) findViewById(R.id.rank_worst_text);
		mWorstRank.setText(String.format(
				mResources.getString(R.string.rank_value),
				worstRank.getPlayerEndRank().get(mPlayer.getId()), worstRank.getId()));
	}

	private void buildZeroStat() {
		Integer number = mGameManager.getGame().getNumberZeroPenalty(mPlayer);
		if (number > 0) {
			mZeroPenalty = (TextView) findViewById(R.id.zero_penalty_text);
			mZeroPenalty.setText(number.toString());
		}
	}

  private void buildHitStat(boolean isVictim) {
    Map<Integer, List<Player>> playerPenaltyMap = mGameManager.getGame().getMaxHit(mPlayer, isVictim);
    if (playerPenaltyMap != null && !playerPenaltyMap.isEmpty()) {
      StringBuilder message = new StringBuilder();
      Integer nbHits = Constants.ZERO_VALUE;
      for (Entry<Integer, List<Player>> entry : playerPenaltyMap.entrySet()) {
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
          mHitVictim = (TextView)findViewById(R.id.penalty_victim_text);
          mHitVictim
              .setText(String.format(mResources.getString(R.string.penalty_hit_value), message.toString(), nbHits));
        }
        else {
          mHit = (TextView)findViewById(R.id.penalty_hit_text);
          mHit.setText(String.format(mResources.getString(R.string.penalty_hit_value), message.toString(), nbHits));
        }
      }
    }
  }

	@Override
	public void onGesturePerformed(GestureOverlayView arg0, Gesture gesture) {
		ArrayList<Prediction> predictions = mGestureLib.recognize(gesture);
		for (Prediction prediction : predictions) {
			if (prediction.score > 1.0) {
				if (Constants.RIGHT.equalsIgnoreCase(prediction.name)) {
					// Get previous player id
					displayStats(mGameManager.getNextPlayer(mPlayer, true,
							false).getId());
				} else if (Constants.LEFT.equalsIgnoreCase(prediction.name)) {
					// Get next player id
					displayStats(mGameManager.getNextPlayer(mPlayer, true,
							true).getId());
				}
			}
		}
	}

	private void displayStats(Integer playerId) {
		Intent intent = new Intent(this, Statistics.class);
		intent.putExtra(Constants.PLAYER_ID, playerId);
		startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
		finish();
	}

}
