package org.lacrise.activity.run;

import org.lacrise.GameManager;
import org.lacrise.R;
import org.lacrise.engine.Constants;
import org.lacrise.engine.game.Player;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Display ongoing game options.
 * 
 * 
 * @author fmorel
 * 
 */
public class Statistics extends Activity {

	private static GameManager mGameManager;

	private Resources mResources;

	private TextView mNumberRounds;

	private TextView mCurrentRank;

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

		mPlayer = mGameManager.getGame().getPlayerById(playerId);
		setTitle(String.format(mResources.getString(R.string.statistics),
				mPlayer.getName()));

		mCurrentRank = (TextView) findViewById(R.id.rank_text);
		mCurrentRank.setText(mGameManager.getGame().getPlayerRank(mPlayer)
				.toString());

		mNumberRounds = (TextView) findViewById(R.id.number_of_rounds_text);
		mNumberRounds.setText(mPlayer.getLastPlayedTurnId().toString());

		mCurrentScore = (TextView) findViewById(R.id.turn_score_current_text);
		mCurrentScore.setText(mPlayer.getTotalScore(false).toString());

		mBestTurn = (TextView) findViewById(R.id.turn_score_best_text);
		mBestTurn.setText(mGameManager.getGame().getBestRoundScore(mPlayer)
				.toString());

		mAverageTurn = (TextView) findViewById(R.id.turn_score_average_text);
		mAverageTurn.setText(mGameManager.getGame().getAverageScore(mPlayer)
				.toString());

	}

}
