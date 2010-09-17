/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.mylin.ecore.model.envelope;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Recipient Ref Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.mylin.ecore.model.envelope.RecipientRefType#getRef <em>Ref</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.RecipientRefType#getRefQualifier <em>Ref Qualifier</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getRecipientRefType()
 * @model extendedMetaData="name='RecipientRefType' kind='elementOnly'"
 * @generated
 */
public interface RecipientRefType extends EObject {
	/**
	 * Returns the value of the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ref</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ref</em>' attribute.
	 * @see #setRef(String)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getRecipientRefType_Ref()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        annotation="smooks-mapping-data type='field'"
	 *        extendedMetaData="kind='element' name='ref' namespace='##targetNamespace'"
	 * @generated
	 */
	String getRef();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.RecipientRefType#getRef <em>Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ref</em>' attribute.
	 * @see #getRef()
	 * @generated
	 */
	void setRef(String value);

	/**
	 * Returns the value of the '<em><b>Ref Qualifier</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ref Qualifier</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ref Qualifier</em>' attribute.
	 * @see #setRefQualifier(String)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getRecipientRefType_RefQualifier()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        annotation="smooks-mapping-data type='field'"
	 *        extendedMetaData="kind='element' name='refQualifier' namespace='##targetNamespace'"
	 * @generated
	 */
	String getRefQualifier();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.RecipientRefType#getRefQualifier <em>Ref Qualifier</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ref Qualifier</em>' attribute.
	 * @see #getRefQualifier()
	 * @generated
	 */
	void setRefQualifier(String value);

} // RecipientRefType
