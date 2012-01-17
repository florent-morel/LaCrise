package org.lacrise.activity.run;

import java.util.Map;

import org.lacrise.GameManager;
import org.lacrise.R;
import org.lacrise.engine.Constants;
import org.lacrise.engine.game.Player;
import org.lacrise.engine.game.Turn;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EnterTurnScore extends Activity implements OnClickListener {

	private static GameManager mGameManager;

	private Resources mResources;

	private EditText mTurnScoreField;

	private CheckBox mBoxWhite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mResources = getResources();
		mGameManager = GameManager.getSingletonObject();

		setContentView(R.layout.turn_score);

		setTitle(String.format(mResources.getString(R.string.turn_score_title),
				mGameManager.getGame().getRoundNumber(), mGameManager
						.getCurrentPlayer().getName()));

		createTurnTargetLayout();

    // Display keyboard directly
    mTurnScoreField.postDelayed(new Runnable() {
      @Override
      public void run() {
        InputMethodManager keyboard = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(mTurnScoreField, 0);
      }
    }, 200);

		Button button = (Button) findViewById(R.id.submit_turn);
		button.setOnClickListener(this);

	}

	private void createTurnTargetLayout() {

		Integer gap;
		Player currentPlayer = mGameManager.getCurrentPlayer();
		if (currentPlayer.getPlayerScore().getTotal() == null) {
			gap = mGameManager.getGame().getScoreToReach();
		} else {
			gap = mGameManager.getGame().getScoreToReach()
					- currentPlayer.getPlayerScore().getTotal();
		}

		TextView turnDialog = (TextView) findViewById(R.id.turn_score_player_text);
		turnDialog.setText(String.format(
				mResources.getString(R.string.turn_score_player),
				currentPlayer.getName(), gap.toString()));

		mTurnScoreField = (EditText) findViewById(R.id.turn_score_field);

		if (!currentPlayer.hasStarted()) {
			mTurnScoreField.setHint(mResources
					.getString(R.string.turn_score_hint_warmup));
		}

		TextView targetDialog = (TextView) findViewById(R.id.turn_score_target_text);

		mBoxWhite = (CheckBox) findViewById(R.id.turn_score_box_white);
		mBoxWhite.setOnClickListener(this);

		this.fillTargetView(currentPlayer, targetDialog);

		// Display warning in case player risks penalty
		TextView warningDialog = (TextView) findViewById(R.id.turn_score_warning);
		if (!currentPlayer.getPlayerScore().hasZero()) {
			// Remove the TextView in case no zero
			warningDialog.setVisibility(View.GONE);
		}
	}

  /**
   * Fill the gap target view.
   * @param currentPlayer
   * @param targetDialog
   */
  private void fillTargetView(Player currentPlayer, TextView targetDialog) {
		Map<Integer, Player> playersGap = mGameManager
				.getPlayersGap(currentPlayer);
		if (playersGap != null && !playersGap.isEmpty()) {
			StringBuilder targetString = new StringBuilder(
					mResources.getString(R.string.turn_target));
			targetString.append(Constants.NEW_LINE);

			for (Map.Entry<Integer, Player> e : playersGap.entrySet()) {
				Integer delta = e.getKey();
				Player player = e.getValue();

				if (player.getId().equals(currentPlayer.getId())) {
					String playerScoreText;
					if (currentPlayer.getTotalScore() != null) {
						playerScoreText = currentPlayer.getTotalScore()
								.toString();
					} else {
						playerScoreText = mResources
								.getString(R.string.no_score);
					}

					targetString.append(String.format(
							mResources.getString(R.string.target_your_score),
							playerScoreText));

				} else {
					// Append to the text
					targetString.append(String.format(
							mResources.getString(R.string.target_player),
							player.getName(), delta));
				}

				targetString.append(Constants.NEW_LINE);
			}
			targetDialog.setText(targetString.toString());
		}
  }

	private Turn submitTurnScore() {
		return mGameManager.endTurn(Integer.valueOf(mTurnScoreField.getText()
				.toString()));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.submit_turn:
			if (mGameManager.isTurnScoreValid(mTurnScoreField.getText()
					.toString())) {
				// Valid score, process
				Turn mPlayedTurn = this.submitTurnScore();
				if (mBoxWhite.isChecked()) {
					mPlayedTurn.setWhite(true);
				}
				this.setResult(mPlayedTurn.getTurnResultCode());
				finish();
			} else {
				// Wrong input, reject
				Toast toast = Toast.makeText(this,
						mResources.getString(R.string.turn_score_invalid),
						Toast.LENGTH_SHORT);
				toast.show();
			}
			break;
		case R.id.turn_score_field:
			mBoxWhite.setChecked(false);
			break;
		case R.id.turn_score_box_white:
			if (mBoxWhite.isChecked()) {
				// Player clicked on white
				mTurnScoreField.setText(Constants.ZERO_VALUE.toString());
			}
			break;
		}
	}

}
