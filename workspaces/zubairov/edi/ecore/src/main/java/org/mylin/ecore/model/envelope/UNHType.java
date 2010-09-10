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
 * A representation of the model object '<em><b>UNH Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.mylin.ecore.model.envelope.UNHType#getMixed <em>Mixed</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.UNHType#getMessageRefNum <em>Message Ref Num</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.UNHType#getMessageIdentifier <em>Message Identifier</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUNHType()
 * @model extendedMetaData="name='UNHType' kind='mixed'"
 * @generated
 */
public interface UNHType extends EObject {
	/**
	 * Returns the value of the '<em><b>Mixed</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mixed</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mixed</em>' attribute list.
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUNHType_Mixed()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='elementWildcard' name=':mixed'"
	 * @generated
	 */
	FeatureMap getMixed();

	/**
	 * Returns the value of the '<em><b>Message Ref Num</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Message Ref Num</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Message Ref Num</em>' attribute.
	 * @see #setMessageRefNum(String)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUNHType_MessageRefNum()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='messageRefNum' namespace='##targetNamespace'"
	 * @generated
	 */
	String getMessageRefNum();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.UNHType#getMessageRefNum <em>Message Ref Num</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Message Ref Num</em>' attribute.
	 * @see #getMessageRefNum()
	 * @generated
	 */
	void setMessageRefNum(String value);

	/**
	 * Returns the value of the '<em><b>Message Identifier</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Message Identifier</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Message Identifier</em>' containment reference.
	 * @see #setMessageIdentifier(MessageIdentifierType)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUNHType_MessageIdentifier()
	 * @model containment="true" required="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='messageIdentifier' namespace='##targetNamespace'"
	 * @generated
	 */
	MessageIdentifierType getMessageIdentifier();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.UNHType#getMessageIdentifier <em>Message Identifier</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Message Identifier</em>' containment reference.
	 * @see #getMessageIdentifier()
	 * @generated
	 */
	void setMessageIdentifier(MessageIdentifierType value);

} // UNHType
