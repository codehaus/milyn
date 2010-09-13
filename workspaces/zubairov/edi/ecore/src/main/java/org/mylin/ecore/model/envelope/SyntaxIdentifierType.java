/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.mylin.ecore.model.envelope;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Syntax Identifier Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.mylin.ecore.model.envelope.SyntaxIdentifierType#getId <em>Id</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.SyntaxIdentifierType#getVersionNum <em>Version Num</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getSyntaxIdentifierType()
 * @model extendedMetaData="name='SyntaxIdentifierType' kind='elementOnly'"
 * @generated
 */
public interface SyntaxIdentifierType extends EObject {
	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getSyntaxIdentifierType_Id()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        annotation="smooks-mapping-data type='component'"
	 *        extendedMetaData="kind='element' name='id' namespace='##targetNamespace'"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.SyntaxIdentifierType#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Version Num</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Version Num</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Version Num</em>' attribute.
	 * @see #setVersionNum(String)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getSyntaxIdentifierType_VersionNum()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        annotation="smooks-mapping-data type='component'"
	 *        extendedMetaData="kind='element' name='versionNum' namespace='##targetNamespace'"
	 * @generated
	 */
	String getVersionNum();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.SyntaxIdentifierType#getVersionNum <em>Version Num</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Version Num</em>' attribute.
	 * @see #getVersionNum()
	 * @generated
	 */
	void setVersionNum(String value);

} // SyntaxIdentifierType
