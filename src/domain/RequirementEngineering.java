package domain;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import message.IsValidatedProjectAsw;
import message.IsValidatedProjectMsg;
import message.StartProjectMsg;
import message.ValidateAndPersistProjectMsg;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import requirementEngineeringLanguage.Project;
import requirementEngineeringLanguage.RequirementEngineeringLanguageFactory;
import transfer.Service;
import errors.UnknownProjectException;

/*
 * This class represent the domain knowledge of the Sensor Deployment domain
 * It implements the exposed operations with EMF stack
 */

public class RequirementEngineering extends Service{
	
	static private Map<String,Project> currents = new HashMap<String,Project>();
	static private boolean validated = false;
	
	static private Project getProject(String name) throws UnknownProjectException{
		if(!currents.containsKey(name)){
			Project preexisting = loadModel(name);
			if (preexisting==null){
				throw new UnknownProjectException("[ERROR] : Catalog " + name + " does not exist"+ "\t\t (" + System.currentTimeMillis() + " )");
			}else{
				currents.put(name, preexisting); 
				return preexisting;
			}
		}else
			return currents.get(name);
	}
	
	static private void updateProject(String name, Project p){
		if(!currents.containsKey(name))
			currents.put(name, p);
		else
			currents.replace(name, currents.get(name), p);
	}

	
	public static void startProject (StartProjectMsg msg){
		String name = msg.getProjectName();
		try{
			getProject(name);
			System.out.println("--> [Warning] : Catalog " + name + " already exists" + "\t\t (" + System.currentTimeMillis() + " )");
		}
		catch(UnknownProjectException e){
			Project p = RequirementEngineeringLanguageFactory.eINSTANCE.createProject();
			p.setName(name);
			updateProject(name, p);;
			System.out.println("Project " + name + " created" + "\t\t (" + System.currentTimeMillis() + " )");
		}
		validated = false;
	}

	
	static { // register the language
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> packageRegistry = reg.getExtensionToFactoryMap();
		packageRegistry.put(requirementEngineeringLanguage.RequirementEngineeringLanguagePackage.eNS_URI,
				requirementEngineeringLanguage.RequirementEngineeringLanguagePackage.eINSTANCE);

	}

	public static Project loadModel(String name) {
		// load the xmi file
		XMIResource resource = new XMIResourceImpl(URI.createURI("resources/" + name + ".xmi"));
		try {
			resource.load(null);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}

		// get the root of the model
		Project pro = (Project) resource.getContents().get(0);

		return pro;
	}

	public static void validateAndPersist(ValidateAndPersistProjectMsg msg) throws IOException {
		String fileName = "resources/" + msg.getModelName() + ".xmi";
		File file = new File(fileName);
		Files.deleteIfExists(file.toPath());

		ResourceSet resSet = new ResourceSetImpl();
		resSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		Resource res = resSet.createResource(URI.createFileURI(fileName));
		res.getContents().add(currents.get(msg.getModelName()));

		try {
			res.save(Collections.EMPTY_MAP);
		} catch (Exception e) {
			System.err.println("ERREUR sauvegarde du modèle : " + e);
			e.printStackTrace();
		}
		validated = true;

	}
	
	public static IsValidatedProjectAsw isValidated(IsValidatedProjectMsg msg){
		return new IsValidatedProjectAsw(validated);
	}

}
