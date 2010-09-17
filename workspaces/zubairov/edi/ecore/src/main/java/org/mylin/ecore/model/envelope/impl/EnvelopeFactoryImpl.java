/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.mylin.ecore.model.envelope.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.mylin.ecore.model.envelope.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class EnvelopeFactoryImpl extends EFactoryImpl implements EnvelopeFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static EnvelopeFactory init() {
		try {
			EnvelopeFactory theEnvelopeFactory = (EnvelopeFactory)EPackage.Registry.INSTANCE.getEFactory("http://smooks.org/EDIFACT/41/Envelope"); 
			if (theEnvelopeFactory != null) {
				return theEnvelopeFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new EnvelopeFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EnvelopeFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case EnvelopePackage.DATE_TIME_TYPE: return createDateTimeType();
			case EnvelopePackage.DOCUMENT_ROOT: return createDocumentRoot();
			case EnvelopePackage.INTERCHANGE_MESSAGE_TYPE: return createInterchangeMessageType();
			case EnvelopePackage.MESSAGE_IDENTIFIER_TYPE: return createMessageIdentifierType();
			case EnvelopePackage.RECIPIENT_REF_TYPE: return createRecipientRefType();
			case EnvelopePackage.RECIPIENT_TYPE: return createRecipientType();
			case EnvelopePackage.SENDER_TYPE: return createSenderType();
			case EnvelopePackage.SYNTAX_IDENTIFIER_TYPE: return createSyntaxIdentifierType();
			case EnvelopePackage.UNB_TYPE: return createUNBType();
			case EnvelopePackage.UN_EDIFACT: return createUNEdifact();
			case EnvelopePackage.UNH_TYPE: return createUNHType();
			case EnvelopePackage.UNT_TYPE: return createUNTType();
			case EnvelopePackage.UNZ_TYPE: return createUNZType();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DateTimeType createDateTimeType() {
		DateTimeTypeImpl dateTimeType = new DateTimeTypeImpl();
		return dateTimeType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DocumentRoot createDocumentRoot() {
		DocumentRootImpl documentRoot = new DocumentRootImpl();
		return documentRoot;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InterchangeMessageType createInterchangeMessageType() {
		InterchangeMessageTypeImpl interchangeMessageType = new InterchangeMessageTypeImpl();
		return interchangeMessageType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MessageIdentifierType createMessageIdentifierType() {
		MessageIdentifierTypeImpl messageIdentifierType = new MessageIdentifierTypeImpl();
		return messageIdentifierType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RecipientRefType createRecipientRefType() {
		RecipientRefTypeImpl recipientRefType = new RecipientRefTypeImpl();
		return recipientRefType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RecipientType createRecipientType() {
		RecipientTypeImpl recipientType = new RecipientTypeImpl();
		return recipientType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SenderType createSenderType() {
		SenderTypeImpl senderType = new SenderTypeImpl();
		return senderType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SyntaxIdentifierType createSyntaxIdentifierType() {
		SyntaxIdentifierTypeImpl syntaxIdentifierType = new SyntaxIdentifierTypeImpl();
		return syntaxIdentifierType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public UNBType createUNBType() {
		UNBTypeImpl unbType = new UNBTypeImpl();
		return unbType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public UNEdifact createUNEdifact() {
		UNEdifactImpl unEdifact = new UNEdifactImpl();
		return unEdifact;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public UNHType createUNHType() {
		UNHTypeImpl unhType = new UNHTypeImpl();
		return unhType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public UNTType createUNTType() {
		UNTTypeImpl untType = new UNTTypeImpl();
		return untType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public UNZType createUNZType() {
		UNZTypeImpl unzType = new UNZTypeImpl();
		return unzType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EnvelopePackage getEnvelopePackage() {
		return (EnvelopePackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static EnvelopePackage getPackage() {
		return EnvelopePackage.eINSTANCE;
	}

} //EnvelopeFactoryImpl
