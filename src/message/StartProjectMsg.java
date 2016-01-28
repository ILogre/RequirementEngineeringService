package message;

import java.util.Map;

import transfer.Message;

public class StartProjectMsg extends Message {

	private String projectName;
	private Map<String,String> views;
	
	public StartProjectMsg(String projectName, Map<String, String> views) {
		super();
		this.projectName = projectName;
		this.views = views;
	}

	public String getProjectName() {
		return projectName;
	}

	public Map<String, String> getViews() {
		return views;
	}
}
