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
import org.mylin.ecore.model.envelope.MessageIdentifierType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Message Identifier Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.MessageIdentifierTypeImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.MessageIdentifierTypeImpl#getVersionNum <em>Version Num</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.MessageIdentifierTypeImpl#getReleaseNum <em>Release Num</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.MessageIdentifierTypeImpl#getControllingAgencyCode <em>Controlling Agency Code</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.MessageIdentifierTypeImpl#getAssociationAssignedCode <em>Association Assigned Code</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MessageIdentifierTypeImpl extends EObjectImpl implements MessageIdentifierType {
	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getVersionNum() <em>Version Num</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVersionNum()
	 * @generated
	 * @ordered
	 */
	protected static final String VERSION_NUM_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getVersionNum() <em>Version Num</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVersionNum()
	 * @generated
	 * @ordered
	 */
	protected String versionNum = VERSION_NUM_EDEFAULT;

	/**
	 * The default value of the '{@link #getReleaseNum() <em>Release Num</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReleaseNum()
	 * @generated
	 * @ordered
	 */
	protected static final String RELEASE_NUM_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getReleaseNum() <em>Release Num</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReleaseNum()
	 * @generated
	 * @ordered
	 */
	protected String releaseNum = RELEASE_NUM_EDEFAULT;

	/**
	 * The default value of the '{@link #getControllingAgencyCode() <em>Controlling Agency Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getControllingAgencyCode()
	 * @generated
	 * @ordered
	 */
	protected static final String CONTROLLING_AGENCY_CODE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getControllingAgencyCode() <em>Controlling Agency Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getControllingAgencyCode()
	 * @generated
	 * @ordered
	 */
	protected String controllingAgencyCode = CONTROLLING_AGENCY_CODE_EDEFAULT;

	/**
	 * The default value of the '{@link #getAssociationAssignedCode() <em>Association Assigned Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAssociationAssignedCode()
	 * @generated
	 * @ordered
	 */
	protected static final String ASSOCIATION_ASSIGNED_CODE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAssociationAssignedCode() <em>Association Assigned Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAssociationAssignedCode()
	 * @generated
	 * @ordered
	 */
	protected String associationAssignedCode = ASSOCIATION_ASSIGNED_CODE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MessageIdentifierTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EnvelopePackage.Literals.MESSAGE_IDENTIFIER_TYPE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getVersionNum() {
		return versionNum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setVersionNum(String newVersionNum) {
		String oldVersionNum = versionNum;
		versionNum = newVersionNum;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__VERSION_NUM, oldVersionNum, versionNum));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getReleaseNum() {
		return releaseNum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setReleaseNum(String newReleaseNum) {
		String oldReleaseNum = releaseNum;
		releaseNum = newReleaseNum;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__RELEASE_NUM, oldReleaseNum, releaseNum));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getControllingAgencyCode() {
		return controllingAgencyCode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setControllingAgencyCode(String newControllingAgencyCode) {
		String oldControllingAgencyCode = controllingAgencyCode;
		controllingAgencyCode = newControllingAgencyCode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__CONTROLLING_AGENCY_CODE, oldControllingAgencyCode, controllingAgencyCode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getAssociationAssignedCode() {
		return associationAssignedCode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAssociationAssignedCode(String newAssociationAssignedCode) {
		String oldAssociationAssignedCode = associationAssignedCode;
		associationAssignedCode = newAssociationAssignedCode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__ASSOCIATION_ASSIGNED_CODE, oldAssociationAssignedCode, associationAssignedCode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__ID:
				return getId();
			case EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__VERSION_NUM:
				return getVersionNum();
			case EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__RELEASE_NUM:
				return getReleaseNum();
			case EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__CONTROLLING_AGENCY_CODE:
				return getControllingAgencyCode();
			case EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__ASSOCIATION_ASSIGNED_CODE:
				return getAssociationAssignedCode();
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
			case EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__ID:
				setId((String)newValue);
				return;
			case EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__VERSION_NUM:
				setVersionNum((String)newValue);
				return;
			case EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__RELEASE_NUM:
				setReleaseNum((String)newValue);
				return;
			case EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__CONTROLLING_AGENCY_CODE:
				setControllingAgencyCode((String)newValue);
				return;
			case EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__ASSOCIATION_ASSIGNED_CODE:
				setAssociationAssignedCode((String)newValue);
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
			case EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__ID:
				setId(ID_EDEFAULT);
				return;
			case EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__VERSION_NUM:
				setVersionNum(VERSION_NUM_EDEFAULT);
				return;
			case EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__RELEASE_NUM:
				setReleaseNum(RELEASE_NUM_EDEFAULT);
				return;
			case EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__CONTROLLING_AGENCY_CODE:
				setControllingAgencyCode(CONTROLLING_AGENCY_CODE_EDEFAULT);
				return;
			case EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__ASSOCIATION_ASSIGNED_CODE:
				setAssociationAssignedCode(ASSOCIATION_ASSIGNED_CODE_EDEFAULT);
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
			case EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__VERSION_NUM:
				return VERSION_NUM_EDEFAULT == null ? versionNum != null : !VERSION_NUM_EDEFAULT.equals(versionNum);
			case EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__RELEASE_NUM:
				return RELEASE_NUM_EDEFAULT == null ? releaseNum != null : !RELEASE_NUM_EDEFAULT.equals(releaseNum);
			case EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__CONTROLLING_AGENCY_CODE:
				return CONTROLLING_AGENCY_CODE_EDEFAULT == null ? controllingAgencyCode != null : !CONTROLLING_AGENCY_CODE_EDEFAULT.equals(controllingAgencyCode);
			case EnvelopePackage.MESSAGE_IDENTIFIER_TYPE__ASSOCIATION_ASSIGNED_CODE:
				return ASSOCIATION_ASSIGNED_CODE_EDEFAULT == null ? associationAssignedCode != null : !ASSOCIATION_ASSIGNED_CODE_EDEFAULT.equals(associationAssignedCode);
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
		result.append(" (id: ");
		result.append(id);
		result.append(", versionNum: ");
		result.append(versionNum);
		result.append(", releaseNum: ");
		result.append(releaseNum);
		result.append(", controllingAgencyCode: ");
		result.append(controllingAgencyCode);
		result.append(", associationAssignedCode: ");
		result.append(associationAssignedCode);
		result.append(')');
		return result.toString();
	}

} //MessageIdentifierTypeImpl
