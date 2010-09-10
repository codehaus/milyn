package org.mylin.ecore.resource;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.milyn.edisax.model.EdifactModel;
import org.milyn.edisax.model.internal.IEdimap;
import org.milyn.edisax.unedifact.UNEdifactInterchangeParser.MappingRegistry;
import org.mylin.ecore.model.EdimapAdapter;
import org.xml.sax.SAXException;

/**
 * A ultimate registry that serves to EMF loading framework as well as
 * to UN EDIFACT parser.
 * 
 * Loading models on demand
 * 
 * @author zubairov
 * 
 */
public class EDIPackageRegistry extends EPackageRegistryImpl implements
		Registry, MappingRegistry {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -400421356940005210L;

	public EDIPackageRegistry() {
		super(EPackage.Registry.INSTANCE);
	}

	public EdifactModel getModel(String lookupName) throws SAXException {
		EPackage pkg;
		try {
			pkg = loadCUSCARModel();
			IEdimap edimap = new EdimapAdapter(pkg);
			return new EdifactModel(edimap);
		} catch (IOException e) {
			throw new SAXException("Can't load model", e);
		}
	}

	/**
	 * Loading CUSCAR ecore model
	 * 
	 * @return
	 * @throws IOException
	 */
	private EPackage loadCUSCARModel() throws IOException {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry()
				.getExtensionToFactoryMap()
				.put(Resource.Factory.Registry.DEFAULT_EXTENSION,
						new EcoreResourceFactoryImpl());
		Resource resource = rs
				.createResource(URI.createFileURI("cuscar.ecore"));
		resource.load(null);
		EPackage pkg = (EPackage) resource.getAllContents().next();
		return pkg;
	}
	
	@Override
	public EPackage getEPackage(String nsURI) {
		return super.getEPackage(nsURI);
	}
	
	@Override
	public EFactory getEFactory(String nsURI) {
		return super.getEFactory(nsURI);
	}

}
