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
import org.mylin.ecore.model.envelope.InterchangeMessageType;
import org.mylin.ecore.model.envelope.UNHType;
import org.mylin.ecore.model.envelope.UNTType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Interchange Message Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.InterchangeMessageTypeImpl#getUNH <em>UNH</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.InterchangeMessageTypeImpl#getMessage <em>Message</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.InterchangeMessageTypeImpl#getUNT <em>UNT</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class InterchangeMessageTypeImpl extends EObjectImpl implements InterchangeMessageType {
	/**
	 * The cached value of the '{@link #getUNH() <em>UNH</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUNH()
	 * @generated
	 * @ordered
	 */
	protected UNHType uNH;

	/**
	 * The cached value of the '{@link #getMessage() <em>Message</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMessage()
	 * @generated
	 * @ordered
	 */
	protected FeatureMap message;

	/**
	 * The cached value of the '{@link #getUNT() <em>UNT</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUNT()
	 * @generated
	 * @ordered
	 */
	protected UNTType uNT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected InterchangeMessageTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EnvelopePackage.Literals.INTERCHANGE_MESSAGE_TYPE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public UNHType getUNH() {
		return uNH;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetUNH(UNHType newUNH, NotificationChain msgs) {
		UNHType oldUNH = uNH;
		uNH = newUNH;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EnvelopePackage.INTERCHANGE_MESSAGE_TYPE__UNH, oldUNH, newUNH);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUNH(UNHType newUNH) {
		if (newUNH != uNH) {
			NotificationChain msgs = null;
			if (uNH != null)
				msgs = ((InternalEObject)uNH).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EnvelopePackage.INTERCHANGE_MESSAGE_TYPE__UNH, null, msgs);
			if (newUNH != null)
				msgs = ((InternalEObject)newUNH).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EnvelopePackage.INTERCHANGE_MESSAGE_TYPE__UNH, null, msgs);
			msgs = basicSetUNH(newUNH, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.INTERCHANGE_MESSAGE_TYPE__UNH, newUNH, newUNH));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getMessage() {
		if (message == null) {
			message = new BasicFeatureMap(this, EnvelopePackage.INTERCHANGE_MESSAGE_TYPE__MESSAGE);
		}
		return message;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public UNTType getUNT() {
		return uNT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetUNT(UNTType newUNT, NotificationChain msgs) {
		UNTType oldUNT = uNT;
		uNT = newUNT;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EnvelopePackage.INTERCHANGE_MESSAGE_TYPE__UNT, oldUNT, newUNT);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUNT(UNTType newUNT) {
		if (newUNT != uNT) {
			NotificationChain msgs = null;
			if (uNT != null)
				msgs = ((InternalEObject)uNT).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EnvelopePackage.INTERCHANGE_MESSAGE_TYPE__UNT, null, msgs);
			if (newUNT != null)
				msgs = ((InternalEObject)newUNT).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EnvelopePackage.INTERCHANGE_MESSAGE_TYPE__UNT, null, msgs);
			msgs = basicSetUNT(newUNT, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.INTERCHANGE_MESSAGE_TYPE__UNT, newUNT, newUNT));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EnvelopePackage.INTERCHANGE_MESSAGE_TYPE__UNH:
				return basicSetUNH(null, msgs);
			case EnvelopePackage.INTERCHANGE_MESSAGE_TYPE__MESSAGE:
				return ((InternalEList<?>)getMessage()).basicRemove(otherEnd, msgs);
			case EnvelopePackage.INTERCHANGE_MESSAGE_TYPE__UNT:
				return basicSetUNT(null, msgs);
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
			case EnvelopePackage.INTERCHANGE_MESSAGE_TYPE__UNH:
				return getUNH();
			case EnvelopePackage.INTERCHANGE_MESSAGE_TYPE__MESSAGE:
				if (coreType) return getMessage();
				return ((FeatureMap.Internal)getMessage()).getWrapper();
			case EnvelopePackage.INTERCHANGE_MESSAGE_TYPE__UNT:
				return getUNT();
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
			case EnvelopePackage.INTERCHANGE_MESSAGE_TYPE__UNH:
				setUNH((UNHType)newValue);
				return;
			case EnvelopePackage.INTERCHANGE_MESSAGE_TYPE__MESSAGE:
				((FeatureMap.Internal)getMessage()).set(newValue);
				return;
			case EnvelopePackage.INTERCHANGE_MESSAGE_TYPE__UNT:
				setUNT((UNTType)newValue);
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
			case EnvelopePackage.INTERCHANGE_MESSAGE_TYPE__UNH:
				setUNH((UNHType)null);
				return;
			case EnvelopePackage.INTERCHANGE_MESSAGE_TYPE__MESSAGE:
				getMessage().clear();
				return;
			case EnvelopePackage.INTERCHANGE_MESSAGE_TYPE__UNT:
				setUNT((UNTType)null);
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
			case EnvelopePackage.INTERCHANGE_MESSAGE_TYPE__UNH:
				return uNH != null;
			case EnvelopePackage.INTERCHANGE_MESSAGE_TYPE__MESSAGE:
				return message != null && !message.isEmpty();
			case EnvelopePackage.INTERCHANGE_MESSAGE_TYPE__UNT:
				return uNT != null;
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
		result.append(" (message: ");
		result.append(message);
		result.append(')');
		return result.toString();
	}

} //InterchangeMessageTypeImpl
