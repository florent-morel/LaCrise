package org.lacrise.activity.create;

import org.lacrise.GameManager;
import org.lacrise.R;
import org.lacrise.engine.Constants;
import org.lacrise.engine.game.Player;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity adding a new player to current game.
 * 
 * @author florent
 * 
 */
public class AddPlayer extends Activity implements OnClickListener {

	private static GameManager mGameManager;

	private Resources mResources;

	private EditText mName;

	private EditText mScore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mResources = getResources();

		mGameManager = GameManager.getSingletonObject();

		setContentView(R.layout.add_player);
		setTitle(R.string.menu_add_player);

		mName = (EditText) findViewById(R.id.newPlayerName);

		mScore = (EditText) findViewById(R.id.newScore);
		if (!mGameManager.getGame().allPlayerStarted()) {
			TextView scoreText = (TextView) findViewById(R.id.newScoreText);
			scoreText.setText(R.string.add_player_no_score_text);
			mScore.setVisibility(View.GONE);
		}

		Button button = (Button) findViewById(R.id.create);
		button.setOnClickListener(this);

	}

	/**
	 * Add player to the game after validation.
	 */
	private void addPlayerToGame() {

		String playerName = mName.getText().toString();

		boolean validName = processPlayerName(playerName);

		String playerScore = mScore.getText().toString();

		boolean validScore = processPlayerScore(playerScore);

		if (validName && validScore) {
			Player newPlayer = new Player(mGameManager.getGame()
					.getPlayerList().size());
			newPlayer.setName(playerName);

			if (mGameManager.getGame().allPlayerStarted()) {
				// Set given score to player
				newPlayer.getPlayerScore().setTotal(
						Integer.valueOf(playerScore));
				// No Warm-up in case all player already started
				newPlayer.setHasStarted();
			}

			mGameManager.getGame().addPlayerToList(newPlayer);

			Toast toast = Toast.makeText(this, String.format(
					mResources.getString(R.string.add_player_confirm),
					playerName), Toast.LENGTH_SHORT);
			toast.show();

			this.setResult(Constants.ACTIVITY_SUCCESS);
			finish();
		}

	}

	private boolean processPlayerName(String string) {
		boolean isValid = false;
		if (this.validateNewPlayerName(string)) {
			// New player name is valid, process
			isValid = true;
		} else {
			// Wrong input, reject
			Toast toast = Toast.makeText(this,
					mResources.getString(R.string.add_player_name_invalid),
					Toast.LENGTH_SHORT);
			toast.show();
		}
		return isValid;
	}

	private boolean processPlayerScore(String string) {
		boolean isValid = false;
		if (this.validateNewPlayerScore(string)) {
			// New player score is valid, process
			isValid = true;
		} else {
			// Wrong input, reject
			Toast toast = Toast.makeText(this,
					mResources.getString(R.string.add_player_score_invalid),
					Toast.LENGTH_SHORT);
			toast.show();
		}
		return isValid;
	}

	private boolean validateNewPlayerName(String string) {
		boolean isValid = false;

		if (!this.isExistingName(string)) {
			isValid = true;
		}

		return isValid;
	}

	private boolean validateNewPlayerScore(String score) {
		boolean isValid = false;

		if (mGameManager.isTurnScoreValid(score)) {
			Integer newPlayerScore = Integer.valueOf(score);
			if (!this.isExistingScore(newPlayerScore)) {
				isValid = true;
			}
		}

		return isValid;
	}

	/**
	 * Check that no other player has the same score.
	 * 
	 * @param playerScore
	 * @return
	 */
	private boolean isExistingScore(Integer playerScore) {
		boolean exist = false;
		for (Player player : mGameManager.getGame().getPlayerList()) {
			if (playerScore.equals(player.getTotalScore())) {
				exist = true;
				break;
			}
		}
		return exist;
	}

	/**
	 * Check that no other player has the same name.
	 * 
	 * @param playerName
	 * @return
	 */
	private boolean isExistingName(String playerName) {
		boolean exist = false;
		for (Player player : mGameManager.getGame().getPlayerList()) {
			if (playerName.equalsIgnoreCase((player.getName()))) {
				exist = true;
				break;
			}
		}
		return exist;
	}

	@Override
	public void onClick(View v) {
		this.addPlayerToGame();
	}

}
