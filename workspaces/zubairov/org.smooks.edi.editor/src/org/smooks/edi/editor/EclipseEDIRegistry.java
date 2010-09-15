package org.smooks.edi.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Descriptor;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.milyn.edisax.model.EdifactModel;
import org.milyn.edisax.unedifact.UNEdifactInterchangeParser.MappingRegistry;
import org.mylin.ecore.model.EdimapAdapter;
import org.xml.sax.SAXException;

/**
 * Eclipse-specific implementation of MappingRegistry.
 * 
 * This registry uses automatically registered list available inside Eclipse
 * using EMF dynamic extension polints to lookup EDI models
 * 
 * @author zubairov
 * 
 */
public class EclipseEDIRegistry implements MappingRegistry {

	private static final Pattern pattern = Pattern
			.compile("^([A-Z]+):D:([0-9]+[A-Z]):UN");

	private Map<String, EdifactModel> nameToModelMap = new HashMap<String, EdifactModel>();

	@Override
	public EdifactModel getModel(String lookupName) throws SAXException {
		EdifactModel result = nameToModelMap.get(lookupName);
		if (result == null) {
			// http://smooks.org/UNEDI/D99AUN/CUSCAR
			Matcher matcher = pattern.matcher(lookupName);
			if (matcher.matches()) {
				String URL = "http://smooks.org/UNEDI/D" + matcher.group(2)
						+ "UN/" + matcher.group(1);
				EPackage pkg = EPackage.Registry.INSTANCE.getEPackage(URL);
				if (pkg == null) {
					throw new SAXException("Can't find package for URL " + URL);
				}
				result = new EdifactModel(new EdimapAdapter(pkg));
				nameToModelMap.put(lookupName, result);
			} else {
				throw new SAXException("Can't parse lookupname " + lookupName);
			}
		}
		return result;
	}

}
