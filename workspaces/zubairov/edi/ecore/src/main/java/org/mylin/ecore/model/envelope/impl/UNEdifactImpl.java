/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.mylin.ecore.model.envelope.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.mylin.ecore.model.envelope.EnvelopePackage;
import org.mylin.ecore.model.envelope.InterchangeMessageType;
import org.mylin.ecore.model.envelope.UNBType;
import org.mylin.ecore.model.envelope.UNEdifact;
import org.mylin.ecore.model.envelope.UNZType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>UN Edifact</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.UNEdifactImpl#getUNB <em>UNB</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.UNEdifactImpl#getMessages <em>Messages</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.UNEdifactImpl#getUNZ <em>UNZ</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class UNEdifactImpl extends EObjectImpl implements UNEdifact {
	/**
	 * The cached value of the '{@link #getUNB() <em>UNB</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUNB()
	 * @generated
	 * @ordered
	 */
	protected UNBType uNB;

	/**
	 * The cached value of the '{@link #getMessages() <em>Messages</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMessages()
	 * @generated
	 * @ordered
	 */
	protected EList<InterchangeMessageType> messages;

	/**
	 * The cached value of the '{@link #getUNZ() <em>UNZ</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUNZ()
	 * @generated
	 * @ordered
	 */
	protected UNZType uNZ;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected UNEdifactImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EnvelopePackage.Literals.UN_EDIFACT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public UNBType getUNB() {
		return uNB;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetUNB(UNBType newUNB, NotificationChain msgs) {
		UNBType oldUNB = uNB;
		uNB = newUNB;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EnvelopePackage.UN_EDIFACT__UNB, oldUNB, newUNB);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUNB(UNBType newUNB) {
		if (newUNB != uNB) {
			NotificationChain msgs = null;
			if (uNB != null)
				msgs = ((InternalEObject)uNB).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EnvelopePackage.UN_EDIFACT__UNB, null, msgs);
			if (newUNB != null)
				msgs = ((InternalEObject)newUNB).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EnvelopePackage.UN_EDIFACT__UNB, null, msgs);
			msgs = basicSetUNB(newUNB, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.UN_EDIFACT__UNB, newUNB, newUNB));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<InterchangeMessageType> getMessages() {
		if (messages == null) {
			messages = new EObjectContainmentEList<InterchangeMessageType>(InterchangeMessageType.class, this, EnvelopePackage.UN_EDIFACT__MESSAGES);
		}
		return messages;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public UNZType getUNZ() {
		return uNZ;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetUNZ(UNZType newUNZ, NotificationChain msgs) {
		UNZType oldUNZ = uNZ;
		uNZ = newUNZ;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EnvelopePackage.UN_EDIFACT__UNZ, oldUNZ, newUNZ);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUNZ(UNZType newUNZ) {
		if (newUNZ != uNZ) {
			NotificationChain msgs = null;
			if (uNZ != null)
				msgs = ((InternalEObject)uNZ).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EnvelopePackage.UN_EDIFACT__UNZ, null, msgs);
			if (newUNZ != null)
				msgs = ((InternalEObject)newUNZ).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EnvelopePackage.UN_EDIFACT__UNZ, null, msgs);
			msgs = basicSetUNZ(newUNZ, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.UN_EDIFACT__UNZ, newUNZ, newUNZ));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EnvelopePackage.UN_EDIFACT__UNB:
				return basicSetUNB(null, msgs);
			case EnvelopePackage.UN_EDIFACT__MESSAGES:
				return ((InternalEList<?>)getMessages()).basicRemove(otherEnd, msgs);
			case EnvelopePackage.UN_EDIFACT__UNZ:
				return basicSetUNZ(null, msgs);
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
			case EnvelopePackage.UN_EDIFACT__UNB:
				return getUNB();
			case EnvelopePackage.UN_EDIFACT__MESSAGES:
				return getMessages();
			case EnvelopePackage.UN_EDIFACT__UNZ:
				return getUNZ();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case EnvelopePackage.UN_EDIFACT__UNB:
				setUNB((UNBType)newValue);
				return;
			case EnvelopePackage.UN_EDIFACT__MESSAGES:
				getMessages().clear();
				getMessages().addAll((Collection<? extends InterchangeMessageType>)newValue);
				return;
			case EnvelopePackage.UN_EDIFACT__UNZ:
				setUNZ((UNZType)newValue);
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
			case EnvelopePackage.UN_EDIFACT__UNB:
				setUNB((UNBType)null);
				return;
			case EnvelopePackage.UN_EDIFACT__MESSAGES:
				getMessages().clear();
				return;
			case EnvelopePackage.UN_EDIFACT__UNZ:
				setUNZ((UNZType)null);
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
			case EnvelopePackage.UN_EDIFACT__UNB:
				return uNB != null;
			case EnvelopePackage.UN_EDIFACT__MESSAGES:
				return messages != null && !messages.isEmpty();
			case EnvelopePackage.UN_EDIFACT__UNZ:
				return uNZ != null;
		}
		return super.eIsSet(featureID);
	}

} //UNEdifactImpl
