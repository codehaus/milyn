package org.mylin.ecore;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.milyn.edisax.EDIParser;
import org.mylin.ecore.model.envelope.DocumentRoot;
import org.mylin.ecore.model.envelope.UNEdifact;
import org.mylin.ecore.resource.EDIFactResourceFactoryImpl;
import org.mylin.ecore.resource.EDIPackageRegistry;

/**
 * Loader of UN/EDIFACT
 * 
 * @author zubairov
 * 
 */
public interface EDILoader {

	/**
	 * Loading EDIfact from stream
	 * 
	 * @param in
	 * @return
	 * @throws IOException 
	 */
	public UNEdifact load(InputStream in) throws IOException;

	public static final EDILoader INSTANCE = new EDILoader() {

		public UNEdifact load(InputStream in) throws IOException {
			EDIPackageRegistry registry = new EDIPackageRegistry();
			ResourceSetImpl rs = new ResourceSetImpl();
			rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
					.put("*", new EDIFactResourceFactoryImpl(registry));
			rs.setPackageRegistry(registry);
			Resource resource = rs.createResource(URI
					.createURI("http://whatever"));
			Map<String, Boolean> map = new HashMap<String, Boolean>();
			map.put(XMLResource.OPTION_EXTENDED_META_DATA, true);
			map.put(EDIParser.FEATURE_IGNORE_NEWLINES, true);
			map.put(EDIParser.FEATURE_VALIDATE, true);
			resource.load(in, map);
			DocumentRoot root = (DocumentRoot) resource.getContents().get(0);
			return root == null ? null : root.getUnEdifact();
		}
	};
}
