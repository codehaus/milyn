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
 * A representation of the model object '<em><b>Sender Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.mylin.ecore.model.envelope.SenderType#getId <em>Id</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.SenderType#getCodeQualifier <em>Code Qualifier</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.SenderType#getInternalId <em>Internal Id</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.SenderType#getInternalSubId <em>Internal Sub Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getSenderType()
 * @model extendedMetaData="name='SenderType' kind='elementOnly'"
 * @generated
 */
public interface SenderType extends EObject {
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
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getSenderType_Id()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        annotation="smooks-mapping-data type='component'"
	 *        extendedMetaData="kind='element' name='id' namespace='##targetNamespace'"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.SenderType#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Code Qualifier</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Code Qualifier</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Code Qualifier</em>' attribute.
	 * @see #setCodeQualifier(String)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getSenderType_CodeQualifier()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        annotation="smooks-mapping-data type='component'"
	 *        extendedMetaData="kind='element' name='codeQualifier' namespace='##targetNamespace'"
	 * @generated
	 */
	String getCodeQualifier();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.SenderType#getCodeQualifier <em>Code Qualifier</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Code Qualifier</em>' attribute.
	 * @see #getCodeQualifier()
	 * @generated
	 */
	void setCodeQualifier(String value);

	/**
	 * Returns the value of the '<em><b>Internal Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Internal Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Internal Id</em>' attribute.
	 * @see #setInternalId(String)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getSenderType_InternalId()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        annotation="smooks-mapping-data type='component'"
	 *        extendedMetaData="kind='element' name='internalId' namespace='##targetNamespace'"
	 * @generated
	 */
	String getInternalId();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.SenderType#getInternalId <em>Internal Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Internal Id</em>' attribute.
	 * @see #getInternalId()
	 * @generated
	 */
	void setInternalId(String value);

	/**
	 * Returns the value of the '<em><b>Internal Sub Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Internal Sub Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Internal Sub Id</em>' attribute.
	 * @see #setInternalSubId(String)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getSenderType_InternalSubId()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        annotation="smooks-mapping-data type='component'"
	 *        extendedMetaData="kind='element' name='internalSubId' namespace='##targetNamespace'"
	 * @generated
	 */
	String getInternalSubId();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.SenderType#getInternalSubId <em>Internal Sub Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Internal Sub Id</em>' attribute.
	 * @see #getInternalSubId()
	 * @generated
	 */
	void setInternalSubId(String value);

} // SenderType
