package org.lacrise.activity.run;

import org.lacrise.GameManager;
import org.lacrise.R;
import org.lacrise.activity.create.AddPlayer;
import org.lacrise.engine.Constants;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Display ongoing game options.
 * 
 * 
 * @author fmorel
 * 
 */
public class GameOptions extends Activity implements OnClickListener {

	private static GameManager mGameManager;

	private Resources mResources;

	private EditText mScoreToReachText;

	private Button mButton;

	private Button mTextAddPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_options);
		mResources = getResources();
		mGameManager = GameManager.getSingletonObject();

		mScoreToReachText = (EditText) findViewById(R.id.options_score_field);
		mScoreToReachText.setText(mGameManager.getGame().getScoreToReach()
				.toString());
		mButton = (Button) findViewById(R.id.submit_options);

		mTextAddPlayer = (Button) findViewById(R.id.buttonAddPlayer);
		mTextAddPlayer.setOnClickListener(this);

		// Display new score to reach only if not already reached
		if (mGameManager.getGame().isTotalReached()) {
			mScoreToReachText.setEnabled(false);
			mButton.setVisibility(View.GONE);
		} else {
			mButton.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == mTextAddPlayer.getId()) {
			launchAddPlayer();
		} else if (v.getId() == mButton.getId()) {
			Integer newTotal = processNewScore(mScoreToReachText.getText()
					.toString());
			if (newTotal != null) {
				mGameManager.getGame().setScoreToReach(newTotal);
				Toast toast = Toast.makeText(this, String.format(
						mResources.getString(R.string.new_total_confirm),
						newTotal.toString()), Toast.LENGTH_SHORT);
				toast.show();

				this.setResult(Constants.ACTIVITY_SUCCESS);
				finish();
			}

		}
	}

	private void launchAddPlayer() {
		Intent intent = new Intent(this, AddPlayer.class);
		startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
	}

	/**
	 * Check if new score is valid (superior to current maximum player score).
	 * 
	 * @param string
	 * @return
	 */
	private Integer processNewScore(String string) {
		Integer toReturn = null;

		Integer newTotal = Integer.valueOf(string);
		Integer maxScore = mGameManager.getFirstRankedPlayer().getTotalScore(
				true);
		if (newTotal != null) {
			if (maxScore == null) {
				// No player scored yet, new total to reach has to be > 0
				if (newTotal > 0) {
					// New player score is valid, process
					toReturn = newTotal;
				}
			} else if (newTotal.compareTo(maxScore) > 0) {
				// New player score is valid, process
				toReturn = newTotal;
			}
		}

		if (toReturn == null) {
			// Wrong input, reject
			Toast toast = Toast.makeText(this,
					String.format(mResources
							.getString(R.string.dialog_options_score_invalid),
							maxScore), Toast.LENGTH_SHORT);
			toast.show();
		}

		return toReturn;
	}

}
