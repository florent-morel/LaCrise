package org.lacrise.adapter;

import java.util.ArrayList;
import java.util.List;

import org.lacrise.GameManager;
import org.lacrise.R;
import org.lacrise.engine.Constants;
import org.lacrise.engine.game.Player;
import org.lacrise.engine.game.Turn;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PlayerScoreAdapter extends ArrayAdapter<Player> {

	private Context mContext;

	private List<Player> mItems = new ArrayList<Player>();

	private Resources mResources;

	public LayoutInflater mInflater;

	private Player mNextPlayer;

	private GameManager mGameManager;

	public PlayerScoreAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		this.mContext = context;
		this.mResources = mContext.getResources();
		mGameManager = GameManager.getSingletonObject();

		if (!mGameManager.getGame().isGameOver()) {
			mNextPlayer = mGameManager.getNextPlayer();
		}

		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addItem(Player player) {
		mItems.add(player);
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Player getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, final View convertView,
			ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) ((Activity) getContext())
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.player_row, null);
		}

		Player player = mItems.get(position);

		TextView rank = (TextView) v.findViewById(R.id.rank);
		rank.setText(String.format(mResources.getString(R.string.player_rank),
				position + 1));

		buildFirstLine(v, player);

		buildSecondLine(v, player);

		return v;

	}

	private void buildFirstLine(View v, Player player) {
		TextView firstLine = (TextView) v.findViewById(R.id.firstLine);
		StringBuilder firstLineBuilder = new StringBuilder();
		if (mNextPlayer != null && mNextPlayer.getId().equals(player.getId())) {
			firstLineBuilder.append(Constants.GTH);
		}
		firstLineBuilder.append(player.getName());
		firstLineBuilder.append(Constants.SPACE);

		if (player.getPlayerScore().getTotal() == null) {
			firstLineBuilder.append(mResources.getString(R.string.no_score));
		} else {
			firstLineBuilder.append(player.getPlayerScore().getTotal()
					.toString());
		}

		firstLine.setText(firstLineBuilder);
	}

	private void buildSecondLine(View v, Player player) {
		TextView secondLine = (TextView) v.findViewById(R.id.secondLine);
		if (!player.hasStarted()
				&& player.getLastPlayedTurnId().compareTo(
						mGameManager.getGame().getWarmUpRounds()) < 0) {
			secondLine
					.setText(String.format(mResources.getString(
							R.string.second_line_player_warmup,
							mGameManager.getGame().getWarmUpRounds()
									- player.getLastPlayedTurnId())));
		} else {
			Turn currentTurn = player.getCurrentTurn();
			Integer score = null;
			boolean isWarmup = true;
			if (currentTurn != null) {
				score = currentTurn.getScore();
				isWarmup = currentTurn.isWarmup();
			}

			StringBuilder secondLineBuilder = new StringBuilder();

			if (!player.hasStarted() && isWarmup) {
				// Player is entering the game and last turn was last warm-up
				secondLineBuilder.append(mResources.getString(
						R.string.second_line_player_entering));
			} else {
				secondLineBuilder.append(String.format(mResources.getString(
						R.string.second_line_player_last_turn, score,
						player.getLastPlayedTurnId())));
			}

			if (player.getPlayerScore().hasZero()) {
				secondLineBuilder.append(Constants.SPACE);
				secondLineBuilder.append(mResources
						.getString(R.string.second_line_player_zero));
			}
			secondLine.setText(secondLineBuilder);
		}
	}

}
