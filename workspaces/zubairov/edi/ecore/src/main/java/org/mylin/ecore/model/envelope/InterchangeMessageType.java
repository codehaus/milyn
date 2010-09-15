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
 * A representation of the model object '<em><b>Interchange Message Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.mylin.ecore.model.envelope.InterchangeMessageType#getUNH <em>UNH</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.InterchangeMessageType#getMessage <em>Message</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.InterchangeMessageType#getUNT <em>UNT</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getInterchangeMessageType()
 * @model extendedMetaData="name='interchangeMessageType' kind='elementOnly'"
 * @generated
 */
public interface InterchangeMessageType extends EObject {
	/**
	 * Returns the value of the '<em><b>UNH</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>UNH</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>UNH</em>' containment reference.
	 * @see #setUNH(UNHType)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getInterchangeMessageType_UNH()
	 * @model containment="true" required="true"
	 *        annotation="smooks-mapping-data segcode='UNH' type='segment'"
	 *        extendedMetaData="kind='element' name='UNH' namespace='##targetNamespace'"
	 * @generated
	 */
	UNHType getUNH();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.InterchangeMessageType#getUNH <em>UNH</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>UNH</em>' containment reference.
	 * @see #getUNH()
	 * @generated
	 */
	void setUNH(UNHType value);

	/**
	 * Returns the value of the '<em><b>Message</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Message</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Message</em>' attribute list.
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getInterchangeMessageType_Message()
	 * @model dataType="org.eclipse.emf.ecore.EFeatureMapEntry" required="true" many="false"
	 *        annotation="smooks-mapping-data type='group'"
	 *        extendedMetaData="kind='elementWildcard' wildcards='##any' name=':1' processing='lax'"
	 * @generated
	 */
	FeatureMap getMessage();

	/**
	 * Returns the value of the '<em><b>UNT</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>UNT</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>UNT</em>' containment reference.
	 * @see #setUNT(UNTType)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getInterchangeMessageType_UNT()
	 * @model containment="true" required="true"
	 *        annotation="smooks-mapping-data segcode='UNT' type='segment'"
	 *        extendedMetaData="kind='element' name='UNT' namespace='##targetNamespace'"
	 * @generated
	 */
	UNTType getUNT();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.InterchangeMessageType#getUNT <em>UNT</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>UNT</em>' containment reference.
	 * @see #getUNT()
	 * @generated
	 */
	void setUNT(UNTType value);

} // InterchangeMessageType
