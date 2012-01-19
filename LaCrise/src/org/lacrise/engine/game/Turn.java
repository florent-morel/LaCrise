package org.lacrise.engine.game;

import java.util.ArrayList;
import java.util.List;

import org.lacrise.engine.Constants;

public class Turn {
	
	private Integer mTurnResultCode = Integer.valueOf(Constants.RESULT_OK);
	
	private Integer mId;

	private Integer mScore = Constants.ZERO_VALUE;

	private boolean mIsWarmup;

	private boolean mIsWhite;
	
	private List<Penalty> mPenaltyList = new ArrayList<Penalty>();
	
	public Turn(Integer id, boolean isWarmup) {
		super();
		this.mId = id;
		mPenaltyList = new ArrayList<Penalty>();
		this.mIsWarmup = isWarmup;
	}

	public Integer getTurnResultCode() {
		return mTurnResultCode;
	}

	public void setTurnResultCode(Integer turnResultCode) {
		this.mTurnResultCode = turnResultCode;
	}

	public void addPenalty(Penalty penalty) {
		mPenaltyList.add(penalty);
	}

	public List<Penalty> getPenaltyList() {
		return mPenaltyList;
	}

	public Integer getId() {
		return mId;
	}

	public Integer getScore() {
		return mScore;
	}

	public void setScore(Integer score) {
		this.mScore = score;
	}

	public boolean isWhite() {
		return mIsWhite;
	}

	public void setWhite(boolean isWhite) {
		this.mIsWhite = isWhite;
	}

	public boolean isWarmup() {
		return mIsWarmup;
	}

}
