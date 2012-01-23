package org.lacrise.activity.run;

import java.util.ArrayList;
import java.util.List;

import org.lacrise.GameManager;
import org.lacrise.R;
import org.lacrise.engine.Constants;
import org.lacrise.engine.game.Player;

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
import android.widget.Toast;

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

  private TextView mHit;

  private TextView mHitVictim;

//	private TextView mBestRank;
//
//	private TextView mWorstRank;

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
    setTitle(String.format(mResources.getString(R.string.statistics), mPlayer.getName()));

    fillStatsItems();

  }

	private void fillStatsItems() {
		mCurrentRank = (TextView) findViewById(R.id.rank_text);
		mCurrentRank.setText(mGameManager.getGame().getPlayerRank(mPlayer)
				.toString());

		mNumberRounds = (TextView) findViewById(R.id.number_of_rounds_text);
		mNumberRounds.setText(mPlayer.getLastPlayedTurnId().toString());

    mCurrentScore = (TextView) findViewById(R.id.turn_score_current_text);
    mCurrentScore.setText(mPlayer.getTotalScore(false).toString());

    mGameManager.getGame().getMaxHit(mPlayer);

    buildVictimStat();

    mCurrentScore = (TextView) findViewById(R.id.turn_score_current_text);
    mCurrentScore.setText(mPlayer.getTotalScore(false).toString());

		mBestTurn = (TextView) findViewById(R.id.turn_score_best_text);
		mBestTurn.setText(mGameManager.getGame().getBestRoundScore(mPlayer)
				.toString());

		mAverageTurn = (TextView) findViewById(R.id.turn_score_average_text);
		mAverageTurn.setText(mGameManager.getGame().getAverageScore(mPlayer)
				.toString());
	}

  /**
   *
   */
  private void buildVictimStat() {
    List<Player> hitList = mGameManager.getGame().getMaxHitVictim(mPlayer);
    if (hitList != null && !hitList.isEmpty()) {
      StringBuilder message = new StringBuilder();
      for (Player player : hitList) {
        message.append(player.getName());
        message.append(Constants.SPACE);
      }

      mHitVictim = (TextView) findViewById(R.id.penalty_victim_text);
      mHitVictim.setText(String.format(mResources
          .getString(R.string.penalty_hit_value), message.toString(), 1));
    }
  }

	@Override
	public void onGesturePerformed(GestureOverlayView arg0, Gesture gesture) {
		ArrayList<Prediction> predictions = mGestureLib.recognize(gesture);
		for (Prediction prediction : predictions) {
			if (prediction.score > 1.0) {
				if (Constants.RIGHT.equalsIgnoreCase(prediction.name)) {
					// Get previous player id
					displayStats(mGameManager.getNextPlayer(mPlayer, false, false).getId());
				} else if (Constants.LEFT.equalsIgnoreCase(prediction.name)) {
				  // Get next player id
				  displayStats(mGameManager.getNextPlayer(mPlayer, false, true).getId());
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
