package message;

import transfer.Message;

public class SpecifyFeature extends Message {

	private String projectName;
	private String featureName;
	private String featureDescription;
	
	public SpecifyFeature(String projectName, String featureName,
			String featureDescription) {
		super();
		this.projectName = projectName;
		this.featureName = featureName;
		this.featureDescription = featureDescription;
	}

	public String getFeatureName() {
		return featureName;
	}

	public String getFeatureDescription() {
		return featureDescription;
	}
	public String getProjectName() {
		return projectName;
	}

}
