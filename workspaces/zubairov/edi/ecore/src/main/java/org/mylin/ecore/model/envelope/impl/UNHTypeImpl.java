/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.mylin.ecore.model.envelope.impl;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

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
 *   <li>{@link org.mylin.ecore.model.envelope.impl.UNHTypeImpl#getMixed <em>Mixed</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.UNHTypeImpl#getMessageRefNum <em>Message Ref Num</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.UNHTypeImpl#getMessageIdentifier <em>Message Identifier</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class UNHTypeImpl extends EObjectImpl implements UNHType {
	/**
	 * The cached value of the '{@link #getMixed() <em>Mixed</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMixed()
	 * @generated
	 * @ordered
	 */
	protected FeatureMap mixed;

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
	public FeatureMap getMixed() {
		if (mixed == null) {
			mixed = new BasicFeatureMap(this, EnvelopePackage.UNH_TYPE__MIXED);
		}
		return mixed;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getMessageRefNum() {
		return (String)getMixed().get(EnvelopePackage.Literals.UNH_TYPE__MESSAGE_REF_NUM, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMessageRefNum(String newMessageRefNum) {
		((FeatureMap.Internal)getMixed()).set(EnvelopePackage.Literals.UNH_TYPE__MESSAGE_REF_NUM, newMessageRefNum);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MessageIdentifierType getMessageIdentifier() {
		return (MessageIdentifierType)getMixed().get(EnvelopePackage.Literals.UNH_TYPE__MESSAGE_IDENTIFIER, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetMessageIdentifier(MessageIdentifierType newMessageIdentifier, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(EnvelopePackage.Literals.UNH_TYPE__MESSAGE_IDENTIFIER, newMessageIdentifier, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMessageIdentifier(MessageIdentifierType newMessageIdentifier) {
		((FeatureMap.Internal)getMixed()).set(EnvelopePackage.Literals.UNH_TYPE__MESSAGE_IDENTIFIER, newMessageIdentifier);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EnvelopePackage.UNH_TYPE__MIXED:
				return ((InternalEList<?>)getMixed()).basicRemove(otherEnd, msgs);
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
			case EnvelopePackage.UNH_TYPE__MIXED:
				if (coreType) return getMixed();
				return ((FeatureMap.Internal)getMixed()).getWrapper();
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
			case EnvelopePackage.UNH_TYPE__MIXED:
				((FeatureMap.Internal)getMixed()).set(newValue);
				return;
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
			case EnvelopePackage.UNH_TYPE__MIXED:
				getMixed().clear();
				return;
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
			case EnvelopePackage.UNH_TYPE__MIXED:
				return mixed != null && !mixed.isEmpty();
			case EnvelopePackage.UNH_TYPE__MESSAGE_REF_NUM:
				return MESSAGE_REF_NUM_EDEFAULT == null ? getMessageRefNum() != null : !MESSAGE_REF_NUM_EDEFAULT.equals(getMessageRefNum());
			case EnvelopePackage.UNH_TYPE__MESSAGE_IDENTIFIER:
				return getMessageIdentifier() != null;
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
		result.append(" (mixed: ");
		result.append(mixed);
		result.append(')');
		return result.toString();
	}

} //UNHTypeImpl
