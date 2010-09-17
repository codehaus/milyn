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
 * A representation of the model object '<em><b>UNB Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.mylin.ecore.model.envelope.UNBType#getSyntaxIdentifier <em>Syntax Identifier</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.UNBType#getSender <em>Sender</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.UNBType#getRecipient <em>Recipient</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.UNBType#getDateTime <em>Date Time</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.UNBType#getControlRef <em>Control Ref</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.UNBType#getRecipientRef <em>Recipient Ref</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.UNBType#getApplicationRef <em>Application Ref</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.UNBType#getProcessingPriorityCode <em>Processing Priority Code</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.UNBType#getAckRequest <em>Ack Request</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.UNBType#getAgreementId <em>Agreement Id</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.UNBType#getTestIndicator <em>Test Indicator</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUNBType()
 * @model extendedMetaData="name='UNBType' kind='elementOnly'"
 * @generated
 */
public interface UNBType extends EObject {
	/**
	 * Returns the value of the '<em><b>Syntax Identifier</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Syntax Identifier</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Syntax Identifier</em>' containment reference.
	 * @see #setSyntaxIdentifier(SyntaxIdentifierType)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUNBType_SyntaxIdentifier()
	 * @model containment="true" required="true"
	 *        annotation="smooks-mapping-data type='field'"
	 *        extendedMetaData="kind='element' name='syntaxIdentifier' namespace='##targetNamespace'"
	 * @generated
	 */
	SyntaxIdentifierType getSyntaxIdentifier();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.UNBType#getSyntaxIdentifier <em>Syntax Identifier</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Syntax Identifier</em>' containment reference.
	 * @see #getSyntaxIdentifier()
	 * @generated
	 */
	void setSyntaxIdentifier(SyntaxIdentifierType value);

	/**
	 * Returns the value of the '<em><b>Sender</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sender</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sender</em>' containment reference.
	 * @see #setSender(SenderType)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUNBType_Sender()
	 * @model containment="true" required="true"
	 *        annotation="smooks-mapping-data type='field'"
	 *        extendedMetaData="kind='element' name='sender' namespace='##targetNamespace'"
	 * @generated
	 */
	SenderType getSender();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.UNBType#getSender <em>Sender</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sender</em>' containment reference.
	 * @see #getSender()
	 * @generated
	 */
	void setSender(SenderType value);

	/**
	 * Returns the value of the '<em><b>Recipient</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Recipient</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Recipient</em>' containment reference.
	 * @see #setRecipient(RecipientType)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUNBType_Recipient()
	 * @model containment="true" required="true"
	 *        annotation="smooks-mapping-data type='field'"
	 *        extendedMetaData="kind='element' name='recipient' namespace='##targetNamespace'"
	 * @generated
	 */
	RecipientType getRecipient();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.UNBType#getRecipient <em>Recipient</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Recipient</em>' containment reference.
	 * @see #getRecipient()
	 * @generated
	 */
	void setRecipient(RecipientType value);

	/**
	 * Returns the value of the '<em><b>Date Time</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Date Time</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Date Time</em>' containment reference.
	 * @see #setDateTime(DateTimeType)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUNBType_DateTime()
	 * @model containment="true" required="true"
	 *        annotation="smooks-mapping-data type='field'"
	 *        extendedMetaData="kind='element' name='dateTime' namespace='##targetNamespace'"
	 * @generated
	 */
	DateTimeType getDateTime();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.UNBType#getDateTime <em>Date Time</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Date Time</em>' containment reference.
	 * @see #getDateTime()
	 * @generated
	 */
	void setDateTime(DateTimeType value);

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
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUNBType_ControlRef()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        annotation="smooks-mapping-data type='field'"
	 *        extendedMetaData="kind='element' name='controlRef' namespace='##targetNamespace'"
	 * @generated
	 */
	String getControlRef();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.UNBType#getControlRef <em>Control Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Control Ref</em>' attribute.
	 * @see #getControlRef()
	 * @generated
	 */
	void setControlRef(String value);

	/**
	 * Returns the value of the '<em><b>Recipient Ref</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Recipient Ref</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Recipient Ref</em>' containment reference.
	 * @see #setRecipientRef(RecipientRefType)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUNBType_RecipientRef()
	 * @model containment="true"
	 *        annotation="smooks-mapping-data type='field'"
	 *        extendedMetaData="kind='element' name='recipientRef' namespace='##targetNamespace'"
	 * @generated
	 */
	RecipientRefType getRecipientRef();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.UNBType#getRecipientRef <em>Recipient Ref</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Recipient Ref</em>' containment reference.
	 * @see #getRecipientRef()
	 * @generated
	 */
	void setRecipientRef(RecipientRefType value);

	/**
	 * Returns the value of the '<em><b>Application Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Application Ref</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Application Ref</em>' attribute.
	 * @see #setApplicationRef(String)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUNBType_ApplicationRef()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        annotation="smooks-mapping-data type='field'"
	 *        extendedMetaData="kind='element' name='applicationRef' namespace='##targetNamespace'"
	 * @generated
	 */
	String getApplicationRef();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.UNBType#getApplicationRef <em>Application Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Application Ref</em>' attribute.
	 * @see #getApplicationRef()
	 * @generated
	 */
	void setApplicationRef(String value);

	/**
	 * Returns the value of the '<em><b>Processing Priority Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Processing Priority Code</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Processing Priority Code</em>' attribute.
	 * @see #setProcessingPriorityCode(String)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUNBType_ProcessingPriorityCode()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        annotation="smooks-mapping-data type='field'"
	 *        extendedMetaData="kind='element' name='processingPriorityCode' namespace='##targetNamespace'"
	 * @generated
	 */
	String getProcessingPriorityCode();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.UNBType#getProcessingPriorityCode <em>Processing Priority Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Processing Priority Code</em>' attribute.
	 * @see #getProcessingPriorityCode()
	 * @generated
	 */
	void setProcessingPriorityCode(String value);

	/**
	 * Returns the value of the '<em><b>Ack Request</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ack Request</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ack Request</em>' attribute.
	 * @see #setAckRequest(String)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUNBType_AckRequest()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        annotation="smooks-mapping-data type='field'"
	 *        extendedMetaData="kind='element' name='ackRequest' namespace='##targetNamespace'"
	 * @generated
	 */
	String getAckRequest();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.UNBType#getAckRequest <em>Ack Request</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ack Request</em>' attribute.
	 * @see #getAckRequest()
	 * @generated
	 */
	void setAckRequest(String value);

	/**
	 * Returns the value of the '<em><b>Agreement Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Agreement Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Agreement Id</em>' attribute.
	 * @see #setAgreementId(String)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUNBType_AgreementId()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        annotation="smooks-mapping-data type='field'"
	 *        extendedMetaData="kind='element' name='agreementId' namespace='##targetNamespace'"
	 * @generated
	 */
	String getAgreementId();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.UNBType#getAgreementId <em>Agreement Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Agreement Id</em>' attribute.
	 * @see #getAgreementId()
	 * @generated
	 */
	void setAgreementId(String value);

	/**
	 * Returns the value of the '<em><b>Test Indicator</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Test Indicator</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Test Indicator</em>' attribute.
	 * @see #setTestIndicator(String)
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#getUNBType_TestIndicator()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        annotation="smooks-mapping-data type='field'"
	 *        extendedMetaData="kind='element' name='testIndicator' namespace='##targetNamespace'"
	 * @generated
	 */
	String getTestIndicator();

	/**
	 * Sets the value of the '{@link org.mylin.ecore.model.envelope.UNBType#getTestIndicator <em>Test Indicator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Test Indicator</em>' attribute.
	 * @see #getTestIndicator()
	 * @generated
	 */
	void setTestIndicator(String value);

} // UNBType
