package edu.uga.dawgtrades.persist.impl;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Date;

import edu.uga.dawgtrades.model.ExperienceReport;
import edu.uga.dawgtrades.model.DTException;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.RegisteredUser;




public class ExperienceReportIterator
		implements Iterator<ExperienceReport>{
		
		private ResultSet rs = null;
		private ObjectModel objectModel = null;
		private boolean more = false;
		
		public ExperienceReportIterator(ResultSet rs, ObjectModel objectModel) throws DTException{
			this.rs = rs;
			this.objectModel = objectModel;
			try{
				more = rs.next();
			}
			catch(Exception e){
				throw new DTException("ExperienceReportIterator: Cannot create Attribute iterator; Root cause: " + e);
			}
		}
		
		public boolean hasNext(){
			return more;
		}
		
		public ExperienceReport next(){
			long id;
			long reviewerId;
			long reviewedId;
			int rating;
			String report;
			Date date;
			
			if(more){
				try{
					id = rs.getLong(1);
					reviewerId = rs.getLong(2);
					reviewedId = rs.getLong(3);
					 rating = rs.getInt(4);
					 report = rs.getString(5);
					date = rs.getDate(6);
					more = rs.next();
				}
				catch(Exception e){
					throw new NoSuchElementException("ExperienceReportIterator: No next ExperienceReport object; Root cause " + e);
				}
				
				ExperienceReport experienceReport = objectModel.createExperienceReport();
				experienceReport.setId(id);
				
				RegisteredUser reviewer = objectModel.createRegisteredUser();
				reviewer.setId(reviewerId);
				RegisteredUser reviewed = objectModel.createRegisteredUser();
				reviewer.setId(reviewedId);
				experienceReport.setReviewer(reviewer);
				experienceReport.setReviewed(reviewed);
				try {
					experienceReport.setRating(rating);
				} catch (DTException e) {
					e.printStackTrace();
				}
				experienceReport.setReport(report);
				experienceReport.setDate(date);
				
				return experienceReport;
			}
			else
				throw new NoSuchElementException("ExperienceReportIterator: No next ExperienceReport object");
		}
		
		public void remove(){
			throw new UnsupportedOperationException();
		}
		
}
