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
 * A representation of the model object '<em><b>UNT Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.mylin.ecore.model.envelope.UNTType#getSegmentCount <em>Segment Count</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.UNTType#getMessageRefNum <em>Message Ref Num</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUNTType()
 * @model extendedMetaData="name='UNTType' kind='elementOnly'"
 * @generated
 */
public interface UNTType extends EObject {
	/**
	 * Returns the value of the '<em><b>Segment Count</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Segment Count</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Segment Count</em>' attribute.
	 * @see #isSetSegmentCount()
	 * @see #unsetSegmentCount()
	 * @see #setSegmentCount(long)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUNTType_SegmentCount()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Long" required="true"
	 *        extendedMetaData="kind='element' name='segmentCount' namespace='##targetNamespace'"
	 * @generated
	 */
	long getSegmentCount();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.UNTType#getSegmentCount <em>Segment Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Segment Count</em>' attribute.
	 * @see #isSetSegmentCount()
	 * @see #unsetSegmentCount()
	 * @see #getSegmentCount()
	 * @generated
	 */
	void setSegmentCount(long value);

	/**
	 * Unsets the value of the '{@link org.mylin.ecore.model.envelope.UNTType#getSegmentCount <em>Segment Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetSegmentCount()
	 * @see #getSegmentCount()
	 * @see #setSegmentCount(long)
	 * @generated
	 */
	void unsetSegmentCount();

	/**
	 * Returns whether the value of the '{@link org.mylin.ecore.model.envelope.UNTType#getSegmentCount <em>Segment Count</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Segment Count</em>' attribute is set.
	 * @see #unsetSegmentCount()
	 * @see #getSegmentCount()
	 * @see #setSegmentCount(long)
	 * @generated
	 */
	boolean isSetSegmentCount();

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
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUNTType_MessageRefNum()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='element' name='messageRefNum' namespace='##targetNamespace'"
	 * @generated
	 */
	String getMessageRefNum();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.UNTType#getMessageRefNum <em>Message Ref Num</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Message Ref Num</em>' attribute.
	 * @see #getMessageRefNum()
	 * @generated
	 */
	void setMessageRefNum(String value);

} // UNTType
