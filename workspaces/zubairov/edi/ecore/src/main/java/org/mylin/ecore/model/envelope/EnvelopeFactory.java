/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.mylin.ecore.model.envelope;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.mylin.ecore.model.envelope.EnvelopePackage
 * @generated
 */
public interface EnvelopeFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	EnvelopeFactory eINSTANCE = org.mylin.ecore.model.envelope.impl.EnvelopeFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Date Time Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Date Time Type</em>'.
	 * @generated
	 */
	DateTimeType createDateTimeType();

	/**
	 * Returns a new object of class '<em>Document Root</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Document Root</em>'.
	 * @generated
	 */
	DocumentRoot createDocumentRoot();

	/**
	 * Returns a new object of class '<em>Interchange Message Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Interchange Message Type</em>'.
	 * @generated
	 */
	InterchangeMessageType createInterchangeMessageType();

	/**
	 * Returns a new object of class '<em>Message Identifier Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Message Identifier Type</em>'.
	 * @generated
	 */
	MessageIdentifierType createMessageIdentifierType();

	/**
	 * Returns a new object of class '<em>Recipient Ref Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Recipient Ref Type</em>'.
	 * @generated
	 */
	RecipientRefType createRecipientRefType();

	/**
	 * Returns a new object of class '<em>Recipient Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Recipient Type</em>'.
	 * @generated
	 */
	RecipientType createRecipientType();

	/**
	 * Returns a new object of class '<em>Sender Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Sender Type</em>'.
	 * @generated
	 */
	SenderType createSenderType();

	/**
	 * Returns a new object of class '<em>Syntax Identifier Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Syntax Identifier Type</em>'.
	 * @generated
	 */
	SyntaxIdentifierType createSyntaxIdentifierType();

	/**
	 * Returns a new object of class '<em>UNB Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>UNB Type</em>'.
	 * @generated
	 */
	UNBType createUNBType();

	/**
	 * Returns a new object of class '<em>UN Edifact</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>UN Edifact</em>'.
	 * @generated
	 */
	UNEdifact createUNEdifact();

	/**
	 * Returns a new object of class '<em>UNH Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>UNH Type</em>'.
	 * @generated
	 */
	UNHType createUNHType();

	/**
	 * Returns a new object of class '<em>UNT Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>UNT Type</em>'.
	 * @generated
	 */
	UNTType createUNTType();

	/**
	 * Returns a new object of class '<em>UNZ Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>UNZ Type</em>'.
	 * @generated
	 */
	UNZType createUNZType();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	EnvelopePackage getEnvelopePackage();

} //EnvelopeFactory
