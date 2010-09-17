/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.mylin.ecore.model.envelope.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.mylin.ecore.model.envelope.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.mylin.ecore.model.envelope.EnvelopePackage
 * @generated
 */
public class EnvelopeAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static EnvelopePackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EnvelopeAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = EnvelopePackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EnvelopeSwitch<Adapter> modelSwitch =
		new EnvelopeSwitch<Adapter>() {
			@Override
			public Adapter caseDateTimeType(DateTimeType object) {
				return createDateTimeTypeAdapter();
			}
			@Override
			public Adapter caseDocumentRoot(DocumentRoot object) {
				return createDocumentRootAdapter();
			}
			@Override
			public Adapter caseInterchangeMessageType(InterchangeMessageType object) {
				return createInterchangeMessageTypeAdapter();
			}
			@Override
			public Adapter caseMessageIdentifierType(MessageIdentifierType object) {
				return createMessageIdentifierTypeAdapter();
			}
			@Override
			public Adapter caseRecipientRefType(RecipientRefType object) {
				return createRecipientRefTypeAdapter();
			}
			@Override
			public Adapter caseRecipientType(RecipientType object) {
				return createRecipientTypeAdapter();
			}
			@Override
			public Adapter caseSenderType(SenderType object) {
				return createSenderTypeAdapter();
			}
			@Override
			public Adapter caseSyntaxIdentifierType(SyntaxIdentifierType object) {
				return createSyntaxIdentifierTypeAdapter();
			}
			@Override
			public Adapter caseUNBType(UNBType object) {
				return createUNBTypeAdapter();
			}
			@Override
			public Adapter caseUNEdifact(UNEdifact object) {
				return createUNEdifactAdapter();
			}
			@Override
			public Adapter caseUNHType(UNHType object) {
				return createUNHTypeAdapter();
			}
			@Override
			public Adapter caseUNTType(UNTType object) {
				return createUNTTypeAdapter();
			}
			@Override
			public Adapter caseUNZType(UNZType object) {
				return createUNZTypeAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link org.mylin.ecore.model.envelope.DateTimeType <em>Date Time Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.mylin.ecore.model.envelope.DateTimeType
	 * @generated
	 */
	public Adapter createDateTimeTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.mylin.ecore.model.envelope.DocumentRoot <em>Document Root</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.mylin.ecore.model.envelope.DocumentRoot
	 * @generated
	 */
	public Adapter createDocumentRootAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.mylin.ecore.model.envelope.InterchangeMessageType <em>Interchange Message Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.mylin.ecore.model.envelope.InterchangeMessageType
	 * @generated
	 */
	public Adapter createInterchangeMessageTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.mylin.ecore.model.envelope.MessageIdentifierType <em>Message Identifier Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.mylin.ecore.model.envelope.MessageIdentifierType
	 * @generated
	 */
	public Adapter createMessageIdentifierTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.mylin.ecore.model.envelope.RecipientRefType <em>Recipient Ref Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.mylin.ecore.model.envelope.RecipientRefType
	 * @generated
	 */
	public Adapter createRecipientRefTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.mylin.ecore.model.envelope.RecipientType <em>Recipient Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.mylin.ecore.model.envelope.RecipientType
	 * @generated
	 */
	public Adapter createRecipientTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.mylin.ecore.model.envelope.SenderType <em>Sender Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.mylin.ecore.model.envelope.SenderType
	 * @generated
	 */
	public Adapter createSenderTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.mylin.ecore.model.envelope.SyntaxIdentifierType <em>Syntax Identifier Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.mylin.ecore.model.envelope.SyntaxIdentifierType
	 * @generated
	 */
	public Adapter createSyntaxIdentifierTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.mylin.ecore.model.envelope.UNBType <em>UNB Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.mylin.ecore.model.envelope.UNBType
	 * @generated
	 */
	public Adapter createUNBTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.mylin.ecore.model.envelope.UNEdifact <em>UN Edifact</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.mylin.ecore.model.envelope.UNEdifact
	 * @generated
	 */
	public Adapter createUNEdifactAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.mylin.ecore.model.envelope.UNHType <em>UNH Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.mylin.ecore.model.envelope.UNHType
	 * @generated
	 */
	public Adapter createUNHTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.mylin.ecore.model.envelope.UNTType <em>UNT Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.mylin.ecore.model.envelope.UNTType
	 * @generated
	 */
	public Adapter createUNTTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.mylin.ecore.model.envelope.UNZType <em>UNZ Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.mylin.ecore.model.envelope.UNZType
	 * @generated
	 */
	public Adapter createUNZTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //EnvelopeAdapterFactory
