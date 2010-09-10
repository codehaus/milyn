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
import org.mylin.ecore.model.envelope.SyntaxIdentifierType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Syntax Identifier Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.SyntaxIdentifierTypeImpl#getMixed <em>Mixed</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.SyntaxIdentifierTypeImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.SyntaxIdentifierTypeImpl#getVersionNum <em>Version Num</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SyntaxIdentifierTypeImpl extends EObjectImpl implements SyntaxIdentifierType {
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
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SyntaxIdentifierTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EnvelopePackage.Literals.SYNTAX_IDENTIFIER_TYPE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getMixed() {
		if (mixed == null) {
			mixed = new BasicFeatureMap(this, EnvelopePackage.SYNTAX_IDENTIFIER_TYPE__MIXED);
		}
		return mixed;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getId() {
		return (String)getMixed().get(EnvelopePackage.Literals.SYNTAX_IDENTIFIER_TYPE__ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(String newId) {
		((FeatureMap.Internal)getMixed()).set(EnvelopePackage.Literals.SYNTAX_IDENTIFIER_TYPE__ID, newId);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getVersionNum() {
		return (String)getMixed().get(EnvelopePackage.Literals.SYNTAX_IDENTIFIER_TYPE__VERSION_NUM, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setVersionNum(String newVersionNum) {
		((FeatureMap.Internal)getMixed()).set(EnvelopePackage.Literals.SYNTAX_IDENTIFIER_TYPE__VERSION_NUM, newVersionNum);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EnvelopePackage.SYNTAX_IDENTIFIER_TYPE__MIXED:
				return ((InternalEList<?>)getMixed()).basicRemove(otherEnd, msgs);
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
			case EnvelopePackage.SYNTAX_IDENTIFIER_TYPE__MIXED:
				if (coreType) return getMixed();
				return ((FeatureMap.Internal)getMixed()).getWrapper();
			case EnvelopePackage.SYNTAX_IDENTIFIER_TYPE__ID:
				return getId();
			case EnvelopePackage.SYNTAX_IDENTIFIER_TYPE__VERSION_NUM:
				return getVersionNum();
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
			case EnvelopePackage.SYNTAX_IDENTIFIER_TYPE__MIXED:
				((FeatureMap.Internal)getMixed()).set(newValue);
				return;
			case EnvelopePackage.SYNTAX_IDENTIFIER_TYPE__ID:
				setId((String)newValue);
				return;
			case EnvelopePackage.SYNTAX_IDENTIFIER_TYPE__VERSION_NUM:
				setVersionNum((String)newValue);
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
			case EnvelopePackage.SYNTAX_IDENTIFIER_TYPE__MIXED:
				getMixed().clear();
				return;
			case EnvelopePackage.SYNTAX_IDENTIFIER_TYPE__ID:
				setId(ID_EDEFAULT);
				return;
			case EnvelopePackage.SYNTAX_IDENTIFIER_TYPE__VERSION_NUM:
				setVersionNum(VERSION_NUM_EDEFAULT);
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
			case EnvelopePackage.SYNTAX_IDENTIFIER_TYPE__MIXED:
				return mixed != null && !mixed.isEmpty();
			case EnvelopePackage.SYNTAX_IDENTIFIER_TYPE__ID:
				return ID_EDEFAULT == null ? getId() != null : !ID_EDEFAULT.equals(getId());
			case EnvelopePackage.SYNTAX_IDENTIFIER_TYPE__VERSION_NUM:
				return VERSION_NUM_EDEFAULT == null ? getVersionNum() != null : !VERSION_NUM_EDEFAULT.equals(getVersionNum());
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

} //SyntaxIdentifierTypeImpl