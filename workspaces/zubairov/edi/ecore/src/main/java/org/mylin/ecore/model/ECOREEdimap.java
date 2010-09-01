package org.mylin.ecore.model;

import java.net.URI;
import java.util.List;

import org.eclipse.emf.ecore.EPackage;
import org.milyn.edisax.model.internal.Delimiters;
import org.milyn.edisax.model.internal.Description;
import org.milyn.edisax.model.internal.IEdimap;
import org.milyn.edisax.model.internal.ISegmentGroup;
import org.milyn.edisax.model.internal.Import;

public class ECOREEdimap implements IEdimap {

	private EPackage pkg;

	public ECOREEdimap(EPackage pkg) {
		this.pkg = pkg;
	}

	public URI getSrc() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Import> getImports() {
		// TODO Auto-generated method stub
		return null;
	}

	public Description getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public Delimiters getDelimiters() {
		// TODO Auto-generated method stub
		return null;
	}

	public ISegmentGroup getSegments() {
		// TODO Auto-generated method stub
		return null;
	}

}
