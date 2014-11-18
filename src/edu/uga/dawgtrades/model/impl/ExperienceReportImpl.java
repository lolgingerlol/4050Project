package edu.uga.dawgtrades.model.impl;

import java.util.Date;

import edu.uga.dawgtrades.model.DTException;
import edu.uga.dawgtrades.model.ExperienceReport;
import edu.uga.dawgtrades.model.RegisteredUser;

public class ExperienceReportImpl extends Persistent implements
		ExperienceReport {

	private int rating;
	private String report;
	private Date date;
	private RegisteredUser reviewer;
	private RegisteredUser reviewed;
	public ExperienceReportImpl(RegisteredUser reviewer,
			RegisteredUser reviewed, int rating, String report, Date date) {
		this.reviewer = reviewer;
		this.reviewed = reviewed;
		this.rating = rating;
		this.report = report;
		this.date = date;
	}

	@Override
	public int getRating() {
		return this.rating;
	}

	@Override
	public void setRating(int rating) throws DTException {
		this.rating = rating;

	}

	@Override
	public String getReport() {
		return this.report;
	}

	@Override
	public void setReport(String report) {
		this.report = report;
	}

	@Override
	public Date getDate() {
		return this.date;
	}

	@Override
	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public RegisteredUser getReviewer() {
		return this.reviewer;
	}

	@Override
	public void setReviewer(RegisteredUser reviewer) {
		this.reviewer = reviewer;
	}

	@Override
	public RegisteredUser getReviewed() {
		return this.reviewed;
	}

	@Override
	public void setReviewed(RegisteredUser reviewed) {
		this.reviewed = reviewed;
	}

}
