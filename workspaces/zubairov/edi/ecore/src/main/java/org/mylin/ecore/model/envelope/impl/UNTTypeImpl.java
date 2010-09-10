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
import org.mylin.ecore.model.envelope.UNTType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>UNT Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.UNTTypeImpl#getSegmentCount <em>Segment Count</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.UNTTypeImpl#getMessageRefNum <em>Message Ref Num</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class UNTTypeImpl extends EObjectImpl implements UNTType {
	/**
	 * The default value of the '{@link #getSegmentCount() <em>Segment Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSegmentCount()
	 * @generated
	 * @ordered
	 */
	protected static final long SEGMENT_COUNT_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getSegmentCount() <em>Segment Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSegmentCount()
	 * @generated
	 * @ordered
	 */
	protected long segmentCount = SEGMENT_COUNT_EDEFAULT;

	/**
	 * This is true if the Segment Count attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean segmentCountESet;

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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected UNTTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EnvelopePackage.Literals.UNT_TYPE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public long getSegmentCount() {
		return segmentCount;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSegmentCount(long newSegmentCount) {
		long oldSegmentCount = segmentCount;
		segmentCount = newSegmentCount;
		boolean oldSegmentCountESet = segmentCountESet;
		segmentCountESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.UNT_TYPE__SEGMENT_COUNT, oldSegmentCount, segmentCount, !oldSegmentCountESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetSegmentCount() {
		long oldSegmentCount = segmentCount;
		boolean oldSegmentCountESet = segmentCountESet;
		segmentCount = SEGMENT_COUNT_EDEFAULT;
		segmentCountESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, EnvelopePackage.UNT_TYPE__SEGMENT_COUNT, oldSegmentCount, SEGMENT_COUNT_EDEFAULT, oldSegmentCountESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetSegmentCount() {
		return segmentCountESet;
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
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.UNT_TYPE__MESSAGE_REF_NUM, oldMessageRefNum, messageRefNum));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case EnvelopePackage.UNT_TYPE__SEGMENT_COUNT:
				return getSegmentCount();
			case EnvelopePackage.UNT_TYPE__MESSAGE_REF_NUM:
				return getMessageRefNum();
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
			case EnvelopePackage.UNT_TYPE__SEGMENT_COUNT:
				setSegmentCount((Long)newValue);
				return;
			case EnvelopePackage.UNT_TYPE__MESSAGE_REF_NUM:
				setMessageRefNum((String)newValue);
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
			case EnvelopePackage.UNT_TYPE__SEGMENT_COUNT:
				unsetSegmentCount();
				return;
			case EnvelopePackage.UNT_TYPE__MESSAGE_REF_NUM:
				setMessageRefNum(MESSAGE_REF_NUM_EDEFAULT);
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
			case EnvelopePackage.UNT_TYPE__SEGMENT_COUNT:
				return isSetSegmentCount();
			case EnvelopePackage.UNT_TYPE__MESSAGE_REF_NUM:
				return MESSAGE_REF_NUM_EDEFAULT == null ? messageRefNum != null : !MESSAGE_REF_NUM_EDEFAULT.equals(messageRefNum);
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
		result.append(" (segmentCount: ");
		if (segmentCountESet) result.append(segmentCount); else result.append("<unset>");
		result.append(", messageRefNum: ");
		result.append(messageRefNum);
		result.append(')');
		return result.toString();
	}

} //UNTTypeImpl
