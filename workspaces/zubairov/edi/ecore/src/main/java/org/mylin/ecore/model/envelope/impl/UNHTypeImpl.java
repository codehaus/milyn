/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.mylin.ecore.model.envelope.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.mylin.ecore.model.envelope.EnvelopePackage;
import org.mylin.ecore.model.envelope.MessageIdentifierType;
import org.mylin.ecore.model.envelope.UNHType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>UNH Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.UNHTypeImpl#getMessageRefNum <em>Message Ref Num</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.UNHTypeImpl#getMessageIdentifier <em>Message Identifier</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class UNHTypeImpl extends EObjectImpl implements UNHType {
	/**
	 * The default value of the '{@link #getMessageRefNum() <em>Message Ref Num</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMessageRefNum()
	 * @generated
	 * @ordered
	 */
	protected static final String MESSAGE_REF_NUM_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMessageRefNum() <em>Message Ref Num</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMessageRefNum()
	 * @generated
	 * @ordered
	 */
	protected String messageRefNum = MESSAGE_REF_NUM_EDEFAULT;

	/**
	 * The cached value of the '{@link #getMessageIdentifier() <em>Message Identifier</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMessageIdentifier()
	 * @generated
	 * @ordered
	 */
	protected MessageIdentifierType messageIdentifier;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected UNHTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EnvelopePackage.Literals.UNH_TYPE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getMessageRefNum() {
		return messageRefNum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMessageRefNum(String newMessageRefNum) {
		String oldMessageRefNum = messageRefNum;
		messageRefNum = newMessageRefNum;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.UNH_TYPE__MESSAGE_REF_NUM, oldMessageRefNum, messageRefNum));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MessageIdentifierType getMessageIdentifier() {
		return messageIdentifier;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetMessageIdentifier(MessageIdentifierType newMessageIdentifier, NotificationChain msgs) {
		MessageIdentifierType oldMessageIdentifier = messageIdentifier;
		messageIdentifier = newMessageIdentifier;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EnvelopePackage.UNH_TYPE__MESSAGE_IDENTIFIER, oldMessageIdentifier, newMessageIdentifier);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMessageIdentifier(MessageIdentifierType newMessageIdentifier) {
		if (newMessageIdentifier != messageIdentifier) {
			NotificationChain msgs = null;
			if (messageIdentifier != null)
				msgs = ((InternalEObject)messageIdentifier).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EnvelopePackage.UNH_TYPE__MESSAGE_IDENTIFIER, null, msgs);
			if (newMessageIdentifier != null)
				msgs = ((InternalEObject)newMessageIdentifier).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EnvelopePackage.UNH_TYPE__MESSAGE_IDENTIFIER, null, msgs);
			msgs = basicSetMessageIdentifier(newMessageIdentifier, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.UNH_TYPE__MESSAGE_IDENTIFIER, newMessageIdentifier, newMessageIdentifier));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EnvelopePackage.UNH_TYPE__MESSAGE_IDENTIFIER:
				return basicSetMessageIdentifier(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case EnvelopePackage.UNH_TYPE__MESSAGE_REF_NUM:
				return getMessageRefNum();
			case EnvelopePackage.UNH_TYPE__MESSAGE_IDENTIFIER:
				return getMessageIdentifier();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case EnvelopePackage.UNH_TYPE__MESSAGE_REF_NUM:
				setMessageRefNum((String)newValue);
				return;
			case EnvelopePackage.UNH_TYPE__MESSAGE_IDENTIFIER:
				setMessageIdentifier((MessageIdentifierType)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case EnvelopePackage.UNH_TYPE__MESSAGE_REF_NUM:
				setMessageRefNum(MESSAGE_REF_NUM_EDEFAULT);
				return;
			case EnvelopePackage.UNH_TYPE__MESSAGE_IDENTIFIER:
				setMessageIdentifier((MessageIdentifierType)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case EnvelopePackage.UNH_TYPE__MESSAGE_REF_NUM:
				return MESSAGE_REF_NUM_EDEFAULT == null ? messageRefNum != null : !MESSAGE_REF_NUM_EDEFAULT.equals(messageRefNum);
			case EnvelopePackage.UNH_TYPE__MESSAGE_IDENTIFIER:
				return messageIdentifier != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (messageRefNum: ");
		result.append(messageRefNum);
		result.append(')');
		return result.toString();
	}

} //UNHTypeImpl
