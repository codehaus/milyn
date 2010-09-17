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
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.mylin.ecore.model.envelope.EnvelopePackage;
import org.mylin.ecore.model.envelope.SenderType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Sender Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.SenderTypeImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.SenderTypeImpl#getCodeQualifier <em>Code Qualifier</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.SenderTypeImpl#getInternalId <em>Internal Id</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.SenderTypeImpl#getInternalSubId <em>Internal Sub Id</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SenderTypeImpl extends EObjectImpl implements SenderType {
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
	 * The default value of the '{@link #getCodeQualifier() <em>Code Qualifier</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCodeQualifier()
	 * @generated
	 * @ordered
	 */
	protected static final String CODE_QUALIFIER_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCodeQualifier() <em>Code Qualifier</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCodeQualifier()
	 * @generated
	 * @ordered
	 */
	protected String codeQualifier = CODE_QUALIFIER_EDEFAULT;

	/**
	 * The default value of the '{@link #getInternalId() <em>Internal Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInternalId()
	 * @generated
	 * @ordered
	 */
	protected static final String INTERNAL_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getInternalId() <em>Internal Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInternalId()
	 * @generated
	 * @ordered
	 */
	protected String internalId = INTERNAL_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getInternalSubId() <em>Internal Sub Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInternalSubId()
	 * @generated
	 * @ordered
	 */
	protected static final String INTERNAL_SUB_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getInternalSubId() <em>Internal Sub Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInternalSubId()
	 * @generated
	 * @ordered
	 */
	protected String internalSubId = INTERNAL_SUB_ID_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SenderTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EnvelopePackage.Literals.SENDER_TYPE;
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
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.SENDER_TYPE__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getCodeQualifier() {
		return codeQualifier;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCodeQualifier(String newCodeQualifier) {
		String oldCodeQualifier = codeQualifier;
		codeQualifier = newCodeQualifier;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.SENDER_TYPE__CODE_QUALIFIER, oldCodeQualifier, codeQualifier));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getInternalId() {
		return internalId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setInternalId(String newInternalId) {
		String oldInternalId = internalId;
		internalId = newInternalId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.SENDER_TYPE__INTERNAL_ID, oldInternalId, internalId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getInternalSubId() {
		return internalSubId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setInternalSubId(String newInternalSubId) {
		String oldInternalSubId = internalSubId;
		internalSubId = newInternalSubId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.SENDER_TYPE__INTERNAL_SUB_ID, oldInternalSubId, internalSubId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case EnvelopePackage.SENDER_TYPE__ID:
				return getId();
			case EnvelopePackage.SENDER_TYPE__CODE_QUALIFIER:
				return getCodeQualifier();
			case EnvelopePackage.SENDER_TYPE__INTERNAL_ID:
				return getInternalId();
			case EnvelopePackage.SENDER_TYPE__INTERNAL_SUB_ID:
				return getInternalSubId();
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
			case EnvelopePackage.SENDER_TYPE__ID:
				setId((String)newValue);
				return;
			case EnvelopePackage.SENDER_TYPE__CODE_QUALIFIER:
				setCodeQualifier((String)newValue);
				return;
			case EnvelopePackage.SENDER_TYPE__INTERNAL_ID:
				setInternalId((String)newValue);
				return;
			case EnvelopePackage.SENDER_TYPE__INTERNAL_SUB_ID:
				setInternalSubId((String)newValue);
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
			case EnvelopePackage.SENDER_TYPE__ID:
				setId(ID_EDEFAULT);
				return;
			case EnvelopePackage.SENDER_TYPE__CODE_QUALIFIER:
				setCodeQualifier(CODE_QUALIFIER_EDEFAULT);
				return;
			case EnvelopePackage.SENDER_TYPE__INTERNAL_ID:
				setInternalId(INTERNAL_ID_EDEFAULT);
				return;
			case EnvelopePackage.SENDER_TYPE__INTERNAL_SUB_ID:
				setInternalSubId(INTERNAL_SUB_ID_EDEFAULT);
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
			case EnvelopePackage.SENDER_TYPE__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case EnvelopePackage.SENDER_TYPE__CODE_QUALIFIER:
				return CODE_QUALIFIER_EDEFAULT == null ? codeQualifier != null : !CODE_QUALIFIER_EDEFAULT.equals(codeQualifier);
			case EnvelopePackage.SENDER_TYPE__INTERNAL_ID:
				return INTERNAL_ID_EDEFAULT == null ? internalId != null : !INTERNAL_ID_EDEFAULT.equals(internalId);
			case EnvelopePackage.SENDER_TYPE__INTERNAL_SUB_ID:
				return INTERNAL_SUB_ID_EDEFAULT == null ? internalSubId != null : !INTERNAL_SUB_ID_EDEFAULT.equals(internalSubId);
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
		result.append(", codeQualifier: ");
		result.append(codeQualifier);
		result.append(", internalId: ");
		result.append(internalId);
		result.append(", internalSubId: ");
		result.append(internalSubId);
		result.append(')');
		return result.toString();
	}

} //SenderTypeImpl
