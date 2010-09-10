/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.mylin.ecore.model.envelope.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.mylin.ecore.model.envelope.EnvelopePackage;
import org.mylin.ecore.model.envelope.UNZType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>UNZ Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.UNZTypeImpl#getControlCount <em>Control Count</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.UNZTypeImpl#getControlRef <em>Control Ref</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class UNZTypeImpl extends EObjectImpl implements UNZType {
	/**
	 * The default value of the '{@link #getControlCount() <em>Control Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getControlCount()
	 * @generated
	 * @ordered
	 */
	protected static final long CONTROL_COUNT_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getControlCount() <em>Control Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getControlCount()
	 * @generated
	 * @ordered
	 */
	protected long controlCount = CONTROL_COUNT_EDEFAULT;

	/**
	 * This is true if the Control Count attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean controlCountESet;

	/**
	 * The default value of the '{@link #getControlRef() <em>Control Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getControlRef()
	 * @generated
	 * @ordered
	 */
	protected static final String CONTROL_REF_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getControlRef() <em>Control Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getControlRef()
	 * @generated
	 * @ordered
	 */
	protected String controlRef = CONTROL_REF_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected UNZTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EnvelopePackage.Literals.UNZ_TYPE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public long getControlCount() {
		return controlCount;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setControlCount(long newControlCount) {
		long oldControlCount = controlCount;
		controlCount = newControlCount;
		boolean oldControlCountESet = controlCountESet;
		controlCountESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.UNZ_TYPE__CONTROL_COUNT, oldControlCount, controlCount, !oldControlCountESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetControlCount() {
		long oldControlCount = controlCount;
		boolean oldControlCountESet = controlCountESet;
		controlCount = CONTROL_COUNT_EDEFAULT;
		controlCountESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EnvelopePackage.UNZ_TYPE__CONTROL_COUNT, oldControlCount, CONTROL_COUNT_EDEFAULT, oldControlCountESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetControlCount() {
		return controlCountESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getControlRef() {
		return controlRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setControlRef(String newControlRef) {
		String oldControlRef = controlRef;
		controlRef = newControlRef;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.UNZ_TYPE__CONTROL_REF, oldControlRef, controlRef));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case EnvelopePackage.UNZ_TYPE__CONTROL_COUNT:
				return getControlCount();
			case EnvelopePackage.UNZ_TYPE__CONTROL_REF:
				return getControlRef();
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
			case EnvelopePackage.UNZ_TYPE__CONTROL_COUNT:
				setControlCount((Long)newValue);
				return;
			case EnvelopePackage.UNZ_TYPE__CONTROL_REF:
				setControlRef((String)newValue);
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
			case EnvelopePackage.UNZ_TYPE__CONTROL_COUNT:
				unsetControlCount();
				return;
			case EnvelopePackage.UNZ_TYPE__CONTROL_REF:
				setControlRef(CONTROL_REF_EDEFAULT);
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
			case EnvelopePackage.UNZ_TYPE__CONTROL_COUNT:
				return isSetControlCount();
			case EnvelopePackage.UNZ_TYPE__CONTROL_REF:
				return CONTROL_REF_EDEFAULT == null ? controlRef != null : !CONTROL_REF_EDEFAULT.equals(controlRef);
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
		result.append(" (controlCount: ");
		if (controlCountESet) result.append(controlCount); else result.append("<unset>");
		result.append(", controlRef: ");
		result.append(controlRef);
		result.append(')');
		return result.toString();
	}

} //UNZTypeImpl
