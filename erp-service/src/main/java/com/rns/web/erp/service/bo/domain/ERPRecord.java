package com.rns.web.erp.service.bo.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ERPRecord {

	private Integer id;
	private List<ERPField> values;
	private Date recordDate;
	private String status;
	private Date createdDate;
	private ERPUser createdUser;
	private ERPField titleField;
	private String recordDateString;
	private ERPFile file;
	private List<ERPFile> files;
	private ERPComment comment;
	private List<ERPComment> comments;
	private List<ERPLog> logs;
	private ERPUser assignedUser;
	private Integer fileCount;
	private Integer commentCount;
	private String color;
	private String projectName;
	private Integer projectId;
	private boolean followUp;
	private ERPAccessRights rights;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public List<ERPField> getValues() {
		if(values == null) {
			values = new ArrayList<ERPField>();
		}
		return values;
	}
	public void setValues(List<ERPField> values) {
		this.values = values;
	}
	public Date getRecordDate() {
		return recordDate;
	}
	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public ERPUser getCreatedUser() {
		return createdUser;
	}
	public void setCreatedUser(ERPUser createdUser) {
		this.createdUser = createdUser;
	}
	public ERPField getTitleField() {
		return titleField;
	}
	public void setTitleField(ERPField titleField) {
		this.titleField = titleField;
	}
	public String getRecordDateString() {
		return recordDateString;
	}
	public void setRecordDateString(String recordDateString) {
		this.recordDateString = recordDateString;
	}
	public ERPFile getFile() {
		return file;
	}
	public void setFile(ERPFile file) {
		this.file = file;
	}
	public List<ERPFile> getFiles() {
		if(files == null) {
			files = new ArrayList<ERPFile>();
		}
		return files;
	}
	public void setFiles(List<ERPFile> files) {
		this.files = files;
	}
	public List<ERPComment> getComments() {
		if(comments == null) {
			comments = new ArrayList<ERPComment>();
		}
		return comments;
	}
	public void setComments(List<ERPComment> comments) {
		this.comments = comments;
	}
	public ERPComment getComment() {
		return comment;
	}
	public void setComment(ERPComment comment) {
		this.comment = comment;
	}
	public List<ERPLog> getLogs() {
		if(logs == null) {
			logs = new ArrayList<ERPLog>();
		}
		return logs;
	}
	public void setLogs(List<ERPLog> logs) {
		this.logs = logs;
	}
	public ERPUser getAssignedUser() {
		return assignedUser;
	}
	public void setAssignedUser(ERPUser assignedUser) {
		this.assignedUser = assignedUser;
	}
	public Integer getFileCount() {
		return fileCount;
	}
	public void setFileCount(Integer fileCount) {
		this.fileCount = fileCount;
	}
	public Integer getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public boolean isFollowUp() {
		return followUp;
	}
	public void setFollowUp(boolean followUp) {
		this.followUp = followUp;
	}
	public Integer getProjectId() {
		return projectId;
	}
	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}
	public ERPAccessRights getRights() {
		return rights;
	}
	public void setRights(ERPAccessRights rights) {
		this.rights = rights;
	}
	
}
