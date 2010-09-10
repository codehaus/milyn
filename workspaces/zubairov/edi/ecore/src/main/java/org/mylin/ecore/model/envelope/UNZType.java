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
 * A representation of the model object '<em><b>UNZ Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.mylin.ecore.model.envelope.UNZType#getControlCount <em>Control Count</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.UNZType#getControlRef <em>Control Ref</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUNZType()
 * @model extendedMetaData="name='UNZType' kind='elementOnly'"
 * @generated
 */
public interface UNZType extends EObject {
	/**
	 * Returns the value of the '<em><b>Control Count</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Control Count</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Control Count</em>' attribute.
	 * @see #isSetControlCount()
	 * @see #unsetControlCount()
	 * @see #setControlCount(long)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUNZType_ControlCount()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Long" required="true"
	 *        extendedMetaData="kind='element' name='controlCount' namespace='##targetNamespace'"
	 * @generated
	 */
	long getControlCount();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.UNZType#getControlCount <em>Control Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Control Count</em>' attribute.
	 * @see #isSetControlCount()
	 * @see #unsetControlCount()
	 * @see #getControlCount()
	 * @generated
	 */
	void setControlCount(long value);

	/**
	 * Unsets the value of the '{@link org.mylin.ecore.model.envelope.UNZType#getControlCount <em>Control Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetControlCount()
	 * @see #getControlCount()
	 * @see #setControlCount(long)
	 * @generated
	 */
	void unsetControlCount();

	/**
	 * Returns whether the value of the '{@link org.mylin.ecore.model.envelope.UNZType#getControlCount <em>Control Count</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Control Count</em>' attribute is set.
	 * @see #unsetControlCount()
	 * @see #getControlCount()
	 * @see #setControlCount(long)
	 * @generated
	 */
	boolean isSetControlCount();

	/**
	 * Returns the value of the '<em><b>Control Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Control Ref</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Control Ref</em>' attribute.
	 * @see #setControlRef(String)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUNZType_ControlRef()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='element' name='controlRef' namespace='##targetNamespace'"
	 * @generated
	 */
	String getControlRef();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.UNZType#getControlRef <em>Control Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Control Ref</em>' attribute.
	 * @see #getControlRef()
	 * @generated
	 */
	void setControlRef(String value);

} // UNZType
