/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.mylin.ecore.model.envelope;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Un Edifact Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.mylin.ecore.model.envelope.UnEdifactType#getUNB <em>UNB</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.UnEdifactType#getInterchangeMessage <em>Interchange Message</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.UnEdifactType#getUNZ <em>UNZ</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUnEdifactType()
 * @model extendedMetaData="name='unEdifactType' kind='elementOnly'"
 * @generated
 */
public interface UnEdifactType extends EObject {
	/**
	 * Returns the value of the '<em><b>UNB</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>UNB</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>UNB</em>' containment reference.
	 * @see #setUNB(UNBType)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUnEdifactType_UNB()
	 * @model containment="true" required="true"
	 *        extendedMetaData="kind='element' name='UNB' namespace='##targetNamespace'"
	 * @generated
	 */
	UNBType getUNB();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.UnEdifactType#getUNB <em>UNB</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>UNB</em>' containment reference.
	 * @see #getUNB()
	 * @generated
	 */
	void setUNB(UNBType value);

	/**
	 * Returns the value of the '<em><b>Interchange Message</b></em>' containment reference list.
	 * The list contents are of type {@link org.mylin.ecore.model.envelope.InterchangeMessageType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Interchange Message</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Interchange Message</em>' containment reference list.
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUnEdifactType_InterchangeMessage()
	 * @model containment="true" required="true"
	 *        extendedMetaData="kind='element' name='interchangeMessage' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<InterchangeMessageType> getInterchangeMessage();

	/**
	 * Returns the value of the '<em><b>UNZ</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>UNZ</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>UNZ</em>' containment reference.
	 * @see #setUNZ(UNZType)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUnEdifactType_UNZ()
	 * @model containment="true" required="true"
	 *        extendedMetaData="kind='element' name='UNZ' namespace='##targetNamespace'"
	 * @generated
	 */
	UNZType getUNZ();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.UnEdifactType#getUNZ <em>UNZ</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>UNZ</em>' containment reference.
	 * @see #getUNZ()
	 * @generated
	 */
	void setUNZ(UNZType value);

} // UnEdifactType
