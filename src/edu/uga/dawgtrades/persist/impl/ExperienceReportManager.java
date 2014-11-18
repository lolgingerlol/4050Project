package edu.uga.dawgtrades.persist.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.sql.PreparedStatement;

import edu.uga.dawgtrades.model.DTException;
import edu.uga.dawgtrades.model.ExperienceReport;
import edu.uga.dawgtrades.model.ObjectModel;
import edu.uga.dawgtrades.model.RegisteredUser;

public class ExperienceReportManager {

	private ObjectModel objectModel = null;
	private Connection conn = null;
	
	public ExperienceReportManager(Connection conn, ObjectModel objectModel){
		this.conn = conn;
		this.objectModel = objectModel;
	}
	
	public void save(ExperienceReport experienceReport) throws DTException{
		String insertAttributeSql = "insert into experience_report (rating, report, reviewer_id, reviewed_id, date) values (?, ?, ?, ?, ?)";
		String updateAttributeSql = "update experience_report set rating = ?, report = ?, reviewer_id = ?, reviewed_id = ?, date = ? where id = ?";
		PreparedStatement stmt = null;
		int inscnt;
		long experienceReportId;
		
		if(experienceReport.getReviewer().getId() == -1)
			throw new DTException("ExperienceReportManager.save: Attempting to save an experienceReport without a reviewer");
		
		if(experienceReport.getReviewed().getId() == -1)
			throw new DTException("ExperienceReportManager.save: Attempting to save an experienceReport without a reviewed");
		
		
		try{
			
			if(!experienceReport.isPersistent())
				stmt = conn.prepareStatement(insertAttributeSql);
			else
				stmt = conn.prepareStatement(updateAttributeSql);
			
			
			stmt.setInt(1,experienceReport.getRating());

			if(experienceReport.getReport() != null)
				stmt.setString(2, experienceReport.getReport());
			else
				throw new DTException("ExperienceReportManager.save: can't save an ExperienceReport: report undefined");
		
			
			if(experienceReport.getReviewer().getId() != -1)
				stmt.setString(3, Long.toString(experienceReport.getReviewer().getId()));
			else
				throw new DTException("ExperienceReportManager.save: can't save an ExperienceReport: reviewer undefined");
		
			if(experienceReport.getReviewed().getId() != -1)
				stmt.setString(4, Long.toString(experienceReport.getReviewed().getId()));
			else
				throw new DTException("ExperienceReportManager.save: can't save an ExperienceReport: reviewer undefined");
		
			if(experienceReport.getDate() != null)
				stmt.setDate(5, (Date) experienceReport.getDate());
			else
				throw new DTException("ExperienceReportManager.save: can't save an ExperienceReport: date undefined");
		
			
			if(experienceReport.isPersistent())
				stmt.setLong(6, experienceReport.getId());
			
			inscnt = stmt.executeUpdate();
			
			if(!experienceReport.isPersistent()){
				if(inscnt >= 1){
					String sql = "select last_insert_id()";
					if(stmt.execute(sql)){
						ResultSet r = stmt.getResultSet();
						while(r.next()){
							experienceReportId = r.getLong(1);
							if(experienceReportId > 0)
								experienceReport.setId(experienceReportId);
						}
					}
				}
				else
					throw new DTException("ExperienceReportManager.save: failed to save an ExperienceReport");
			}
			else{
				if(inscnt < 1)
					throw new DTException("ExperienceReportManager.save: failed to save an ExperienceReport");
			}
		}
		catch(SQLException e){
			e.printStackTrace();
			throw new DTException("ExperienceReportManager.save: failed to save an ExperienceReport: " + e);
		}
	}

	public Iterator<ExperienceReport> restore(ExperienceReport experienceReport) throws DTException{
		String selectAttributeSql = "select id, reviewer_id, reviewed_id, rating, report, date from experience_report";
		Statement stmt = null;
		StringBuffer query = new StringBuffer(100);
		StringBuffer condition = new StringBuffer(100);
		
		condition.setLength(0);
		
		query.append(selectAttributeSql);
		
		if(experienceReport != null){
			if(experienceReport.getId() >= 0)
				query.append(" where id = " + experienceReport.getId());
			else{
				if(experienceReport.getReviewer().getId() != -1)
					condition.append("reviewer_id = '" + experienceReport.getReviewer().getId() + "'");
				if(experienceReport.getReviewed().getId() != -1){
	                    if( condition.length() > 0 )
	                    	condition.append(" and ");
					condition.append("reviewed_id = '" + experienceReport.getReviewed().getId() + "'");
				}
				if(experienceReport.getRating() != 0){
					 if( condition.length() > 0 )
	                    	condition.append(" and ");
					condition.append("rating = '" + experienceReport.getRating() + "'");
				}
				if(experienceReport.getDate() != null ) {
					 if( condition.length() > 0 )
	                    	condition.append(" and ");
					condition.append("date = '" + experienceReport.getDate() + "'");
				}
				if(condition.length() > 0){
					query.append(" where ");
					query.append(condition);
				}
			}
		}
		
		try{
			stmt = conn.createStatement();
			if(stmt.execute(query.toString())){
				ResultSet r = stmt.getResultSet();
				return new ExperienceReportIterator(r, objectModel);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			throw new DTException("ExperienceReportManager.restore: Could not restore persistent ExperienceReport object; Root cause: " + e);
		}
		
		throw new DTException("ExperienceReportManager.restore: Could not restore persistent ExperienceReport object");
	}
	
	
	
	public void delete(ExperienceReport experienceReport) throws DTException{
		String deleteAttributeSql = "delete from experience_report where id = ?";
		PreparedStatement stmt = null;
		int inscnt;
		
		if(!experienceReport.isPersistent())
			return;
		try{
			stmt = conn.prepareStatement(deleteAttributeSql);
			stmt.setLong(1, experienceReport.getId());
			inscnt = stmt.executeUpdate();
			if(inscnt == 1){
				return;
			}
			else
				throw new DTException("ExperienceReportManager.delete: failed to delete an ExperienceReport");
		}
		catch(SQLException e){
			e.printStackTrace();
			throw new DTException("ExperienceReportManager.delete: failed to delete an ExperienceReport" + e);
		}
	}

	public RegisteredUser restoreReviewer(ExperienceReport experienceReport) throws DTException{
		String selectItemSql = "select r.id, r.name, r.first_name, r.last_name, r.password, r.email, r.phone, r.isAdmin, r.canText from registered_user r, experience_report er where er.reviewer_id = r.id";
		Statement stmt = null;
		StringBuffer query = new StringBuffer(100);
		StringBuffer condition = new StringBuffer(100);
		
		condition.setLength(0);
		
		query.append(selectItemSql);
		
		if(experienceReport != null){
			if(experienceReport.getId() >= 0)
				query.append(" and er.id = " + experienceReport.getId());
			else{
				 if(experienceReport.getReviewed().getId() != -1)
					 condition.append(" and er.reviewed_id = '" + experienceReport.getReviewed().getId() + "'");
				if(experienceReport.getRating() != 0)
					condition.append(" and er.rating = '" + experienceReport.getRating() + "'");
				if(experienceReport.getReport() != null)
					condition.append(" and er.report = '" + experienceReport.getReport() + "'");
				 if(experienceReport.getDate() != null)
					condition.append(" and er.date = '" + experienceReport.getDate() + "'");
			
				if(condition.length() > 0){
					query.append(condition);
				}
			}
		}
		try{
			stmt = conn.createStatement();
			
			if(stmt.execute(query.toString())){
				ResultSet r = stmt.getResultSet();
				Iterator<RegisteredUser> itemIter = new RegisteredUserIterator(r, objectModel);
				if(itemIter != null && itemIter.hasNext()){
					return itemIter.next();
				}
				else
					return null;
			}
		}
		catch(Exception e){
			throw new DTException("ExperienceReportManager.restoreReviewer: Could not restore persistent RegisteredUser object; Root cause: " + e);
		}
		
		throw new DTException("ExperienceReportManager.restoreReviewer: Could not restore persistent RegisteredUser object");
	}
	
	public RegisteredUser restoreReviewed(ExperienceReport experienceReport) throws DTException{
		String selectItemSql = "select r.id, r.name, r.first_name, r.last_name, r.password, r.email, r.phone, r.isAdmin, r.canText from registered_user r, experience_report er where er.reviewed_id = r.id";
		Statement stmt = null;
		StringBuffer query = new StringBuffer(100);
		StringBuffer condition = new StringBuffer(100);
		
		condition.setLength(0);
		
		query.append(selectItemSql);
		
		if(experienceReport != null){
			if(experienceReport.getId() >= 0)
				query.append("er.id = " + experienceReport.getId());
			else{
				 if(experienceReport.getReviewer().getId() != -1)
					 condition.append(" and er.reviewer_id = '" + experienceReport.getReviewer().getId() + "'");
				if(experienceReport.getRating() != 0)
					condition.append(" and er.rating = '" + experienceReport.getRating() + "'");
				if(experienceReport.getReport() != null)
					condition.append(" and er.report = '" + experienceReport.getReport() + "'");
				 if(experienceReport.getDate() != null)
					condition.append(" and er.date = '" + experienceReport.getDate() + "'");
			
				if(condition.length() > 0){
					query.append(condition);
				}
			}
		}
		try{
			stmt = conn.createStatement();
			
			if(stmt.execute(query.toString())){
				ResultSet r = stmt.getResultSet();
				Iterator<RegisteredUser> itemIter = new RegisteredUserIterator(r, objectModel);
				if(itemIter != null && itemIter.hasNext()){
					return itemIter.next();
				}
				else
					return null;
			}
		}
		catch(Exception e){
			throw new DTException("ExperienceReportManager.restoreReviewed: Could not restore persistent RegisteredUser object; Root cause: " + e);
		}
		
		throw new DTException("ExperienceReportManager.restoreReviewed: Could not restore persistent RegisteredUser object");
	}

}
