package org.lacrise.view;

import org.lacrise.R;
import org.lacrise.engine.game.Penalty;
import org.lacrise.engine.game.Player;
import org.lacrise.engine.game.Turn;

import android.content.Context;
import android.content.res.Resources;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TurnRowView extends LinearLayout {

	private TextView mFirstLine;
	private TextView mId;
	private TextView mSecondLine;
	private Resources mResources;

	public TurnRowView(Context context, Turn turn, int rank) {
		super(context);

		mResources = getResources();

		mId = new TextView(context);
		mId.setText(String.format(
				mResources.getString(R.string.turn_history_id), rank));
		addView(mId, R.layout.list_turn_history, R.id.turn_id);

		mFirstLine = new TextView(context);
		StringBuilder message = new StringBuilder(String.format(
				mResources.getString(R.string.turn_history_first_line),
				turn.getScore()));

		if (turn.getPenaltyList() != null && turn.getPenaltyList().size() > 0) {
			for (Penalty penalty : turn.getPenaltyList()) {
				Player victim = penalty.getVictim();
				if (penalty.getPlayer().getId().equals(victim.getId())) {
					message.append(mResources
							.getString(R.string.penalty_reason_own));
				} else {
					message.append(String.format(
							mResources.getString(R.string.penalty_reason_other),
							penalty.getPlayer().getName(), victim.getName()));
				}

			}
		}

		mFirstLine.setText(message);
		mFirstLine.setHorizontalScrollBarEnabled(true);
		
		addView(mFirstLine, R.layout.list_turn_history, R.id.firstLine);

		if (turn.isWhite()) {
			mSecondLine = new TextView(context);
			mSecondLine.setText(R.string.turn_history_second_line);

			addView(mSecondLine, R.layout.list_turn_history, R.id.secondLine);
		}
	}

}
