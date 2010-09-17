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

import org.mylin.ecore.model.envelope.DateTimeType;
import org.mylin.ecore.model.envelope.EnvelopePackage;
import org.mylin.ecore.model.envelope.RecipientRefType;
import org.mylin.ecore.model.envelope.RecipientType;
import org.mylin.ecore.model.envelope.SenderType;
import org.mylin.ecore.model.envelope.SyntaxIdentifierType;
import org.mylin.ecore.model.envelope.UNBType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>UNB Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.UNBTypeImpl#getSyntaxIdentifier <em>Syntax Identifier</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.UNBTypeImpl#getSender <em>Sender</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.UNBTypeImpl#getRecipient <em>Recipient</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.UNBTypeImpl#getDateTime <em>Date Time</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.UNBTypeImpl#getControlRef <em>Control Ref</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.UNBTypeImpl#getRecipientRef <em>Recipient Ref</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.UNBTypeImpl#getApplicationRef <em>Application Ref</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.UNBTypeImpl#getProcessingPriorityCode <em>Processing Priority Code</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.UNBTypeImpl#getAckRequest <em>Ack Request</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.UNBTypeImpl#getAgreementId <em>Agreement Id</em>}</li>
 *   <li>{@link org.mylin.ecore.model.envelope.impl.UNBTypeImpl#getTestIndicator <em>Test Indicator</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class UNBTypeImpl extends EObjectImpl implements UNBType {
	/**
	 * The cached value of the '{@link #getSyntaxIdentifier() <em>Syntax Identifier</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSyntaxIdentifier()
	 * @generated
	 * @ordered
	 */
	protected SyntaxIdentifierType syntaxIdentifier;

	/**
	 * The cached value of the '{@link #getSender() <em>Sender</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSender()
	 * @generated
	 * @ordered
	 */
	protected SenderType sender;

	/**
	 * The cached value of the '{@link #getRecipient() <em>Recipient</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRecipient()
	 * @generated
	 * @ordered
	 */
	protected RecipientType recipient;

	/**
	 * The cached value of the '{@link #getDateTime() <em>Date Time</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDateTime()
	 * @generated
	 * @ordered
	 */
	protected DateTimeType dateTime;

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
	 * The cached value of the '{@link #getRecipientRef() <em>Recipient Ref</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRecipientRef()
	 * @generated
	 * @ordered
	 */
	protected RecipientRefType recipientRef;

	/**
	 * The default value of the '{@link #getApplicationRef() <em>Application Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getApplicationRef()
	 * @generated
	 * @ordered
	 */
	protected static final String APPLICATION_REF_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getApplicationRef() <em>Application Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getApplicationRef()
	 * @generated
	 * @ordered
	 */
	protected String applicationRef = APPLICATION_REF_EDEFAULT;

	/**
	 * The default value of the '{@link #getProcessingPriorityCode() <em>Processing Priority Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProcessingPriorityCode()
	 * @generated
	 * @ordered
	 */
	protected static final String PROCESSING_PRIORITY_CODE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getProcessingPriorityCode() <em>Processing Priority Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProcessingPriorityCode()
	 * @generated
	 * @ordered
	 */
	protected String processingPriorityCode = PROCESSING_PRIORITY_CODE_EDEFAULT;

	/**
	 * The default value of the '{@link #getAckRequest() <em>Ack Request</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAckRequest()
	 * @generated
	 * @ordered
	 */
	protected static final String ACK_REQUEST_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAckRequest() <em>Ack Request</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAckRequest()
	 * @generated
	 * @ordered
	 */
	protected String ackRequest = ACK_REQUEST_EDEFAULT;

	/**
	 * The default value of the '{@link #getAgreementId() <em>Agreement Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAgreementId()
	 * @generated
	 * @ordered
	 */
	protected static final String AGREEMENT_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAgreementId() <em>Agreement Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAgreementId()
	 * @generated
	 * @ordered
	 */
	protected String agreementId = AGREEMENT_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getTestIndicator() <em>Test Indicator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTestIndicator()
	 * @generated
	 * @ordered
	 */
	protected static final String TEST_INDICATOR_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTestIndicator() <em>Test Indicator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTestIndicator()
	 * @generated
	 * @ordered
	 */
	protected String testIndicator = TEST_INDICATOR_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected UNBTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EnvelopePackage.Literals.UNB_TYPE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SyntaxIdentifierType getSyntaxIdentifier() {
		return syntaxIdentifier;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSyntaxIdentifier(SyntaxIdentifierType newSyntaxIdentifier, NotificationChain msgs) {
		SyntaxIdentifierType oldSyntaxIdentifier = syntaxIdentifier;
		syntaxIdentifier = newSyntaxIdentifier;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EnvelopePackage.UNB_TYPE__SYNTAX_IDENTIFIER, oldSyntaxIdentifier, newSyntaxIdentifier);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSyntaxIdentifier(SyntaxIdentifierType newSyntaxIdentifier) {
		if (newSyntaxIdentifier != syntaxIdentifier) {
			NotificationChain msgs = null;
			if (syntaxIdentifier != null)
				msgs = ((InternalEObject)syntaxIdentifier).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EnvelopePackage.UNB_TYPE__SYNTAX_IDENTIFIER, null, msgs);
			if (newSyntaxIdentifier != null)
				msgs = ((InternalEObject)newSyntaxIdentifier).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EnvelopePackage.UNB_TYPE__SYNTAX_IDENTIFIER, null, msgs);
			msgs = basicSetSyntaxIdentifier(newSyntaxIdentifier, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.UNB_TYPE__SYNTAX_IDENTIFIER, newSyntaxIdentifier, newSyntaxIdentifier));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SenderType getSender() {
		return sender;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSender(SenderType newSender, NotificationChain msgs) {
		SenderType oldSender = sender;
		sender = newSender;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EnvelopePackage.UNB_TYPE__SENDER, oldSender, newSender);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSender(SenderType newSender) {
		if (newSender != sender) {
			NotificationChain msgs = null;
			if (sender != null)
				msgs = ((InternalEObject)sender).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EnvelopePackage.UNB_TYPE__SENDER, null, msgs);
			if (newSender != null)
				msgs = ((InternalEObject)newSender).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EnvelopePackage.UNB_TYPE__SENDER, null, msgs);
			msgs = basicSetSender(newSender, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.UNB_TYPE__SENDER, newSender, newSender));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RecipientType getRecipient() {
		return recipient;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetRecipient(RecipientType newRecipient, NotificationChain msgs) {
		RecipientType oldRecipient = recipient;
		recipient = newRecipient;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EnvelopePackage.UNB_TYPE__RECIPIENT, oldRecipient, newRecipient);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRecipient(RecipientType newRecipient) {
		if (newRecipient != recipient) {
			NotificationChain msgs = null;
			if (recipient != null)
				msgs = ((InternalEObject)recipient).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EnvelopePackage.UNB_TYPE__RECIPIENT, null, msgs);
			if (newRecipient != null)
				msgs = ((InternalEObject)newRecipient).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EnvelopePackage.UNB_TYPE__RECIPIENT, null, msgs);
			msgs = basicSetRecipient(newRecipient, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.UNB_TYPE__RECIPIENT, newRecipient, newRecipient));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DateTimeType getDateTime() {
		return dateTime;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetDateTime(DateTimeType newDateTime, NotificationChain msgs) {
		DateTimeType oldDateTime = dateTime;
		dateTime = newDateTime;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EnvelopePackage.UNB_TYPE__DATE_TIME, oldDateTime, newDateTime);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDateTime(DateTimeType newDateTime) {
		if (newDateTime != dateTime) {
			NotificationChain msgs = null;
			if (dateTime != null)
				msgs = ((InternalEObject)dateTime).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EnvelopePackage.UNB_TYPE__DATE_TIME, null, msgs);
			if (newDateTime != null)
				msgs = ((InternalEObject)newDateTime).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EnvelopePackage.UNB_TYPE__DATE_TIME, null, msgs);
			msgs = basicSetDateTime(newDateTime, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.UNB_TYPE__DATE_TIME, newDateTime, newDateTime));
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
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.UNB_TYPE__CONTROL_REF, oldControlRef, controlRef));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RecipientRefType getRecipientRef() {
		return recipientRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetRecipientRef(RecipientRefType newRecipientRef, NotificationChain msgs) {
		RecipientRefType oldRecipientRef = recipientRef;
		recipientRef = newRecipientRef;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, EnvelopePackage.UNB_TYPE__RECIPIENT_REF, oldRecipientRef, newRecipientRef);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRecipientRef(RecipientRefType newRecipientRef) {
		if (newRecipientRef != recipientRef) {
			NotificationChain msgs = null;
			if (recipientRef != null)
				msgs = ((InternalEObject)recipientRef).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - EnvelopePackage.UNB_TYPE__RECIPIENT_REF, null, msgs);
			if (newRecipientRef != null)
				msgs = ((InternalEObject)newRecipientRef).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - EnvelopePackage.UNB_TYPE__RECIPIENT_REF, null, msgs);
			msgs = basicSetRecipientRef(newRecipientRef, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.UNB_TYPE__RECIPIENT_REF, newRecipientRef, newRecipientRef));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getApplicationRef() {
		return applicationRef;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setApplicationRef(String newApplicationRef) {
		String oldApplicationRef = applicationRef;
		applicationRef = newApplicationRef;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.UNB_TYPE__APPLICATION_REF, oldApplicationRef, applicationRef));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getProcessingPriorityCode() {
		return processingPriorityCode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProcessingPriorityCode(String newProcessingPriorityCode) {
		String oldProcessingPriorityCode = processingPriorityCode;
		processingPriorityCode = newProcessingPriorityCode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.UNB_TYPE__PROCESSING_PRIORITY_CODE, oldProcessingPriorityCode, processingPriorityCode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getAckRequest() {
		return ackRequest;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAckRequest(String newAckRequest) {
		String oldAckRequest = ackRequest;
		ackRequest = newAckRequest;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.UNB_TYPE__ACK_REQUEST, oldAckRequest, ackRequest));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getAgreementId() {
		return agreementId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAgreementId(String newAgreementId) {
		String oldAgreementId = agreementId;
		agreementId = newAgreementId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.UNB_TYPE__AGREEMENT_ID, oldAgreementId, agreementId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTestIndicator() {
		return testIndicator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTestIndicator(String newTestIndicator) {
		String oldTestIndicator = testIndicator;
		testIndicator = newTestIndicator;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EnvelopePackage.UNB_TYPE__TEST_INDICATOR, oldTestIndicator, testIndicator));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EnvelopePackage.UNB_TYPE__SYNTAX_IDENTIFIER:
				return basicSetSyntaxIdentifier(null, msgs);
			case EnvelopePackage.UNB_TYPE__SENDER:
				return basicSetSender(null, msgs);
			case EnvelopePackage.UNB_TYPE__RECIPIENT:
				return basicSetRecipient(null, msgs);
			case EnvelopePackage.UNB_TYPE__DATE_TIME:
				return basicSetDateTime(null, msgs);
			case EnvelopePackage.UNB_TYPE__RECIPIENT_REF:
				return basicSetRecipientRef(null, msgs);
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
			case EnvelopePackage.UNB_TYPE__SYNTAX_IDENTIFIER:
				return getSyntaxIdentifier();
			case EnvelopePackage.UNB_TYPE__SENDER:
				return getSender();
			case EnvelopePackage.UNB_TYPE__RECIPIENT:
				return getRecipient();
			case EnvelopePackage.UNB_TYPE__DATE_TIME:
				return getDateTime();
			case EnvelopePackage.UNB_TYPE__CONTROL_REF:
				return getControlRef();
			case EnvelopePackage.UNB_TYPE__RECIPIENT_REF:
				return getRecipientRef();
			case EnvelopePackage.UNB_TYPE__APPLICATION_REF:
				return getApplicationRef();
			case EnvelopePackage.UNB_TYPE__PROCESSING_PRIORITY_CODE:
				return getProcessingPriorityCode();
			case EnvelopePackage.UNB_TYPE__ACK_REQUEST:
				return getAckRequest();
			case EnvelopePackage.UNB_TYPE__AGREEMENT_ID:
				return getAgreementId();
			case EnvelopePackage.UNB_TYPE__TEST_INDICATOR:
				return getTestIndicator();
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
			case EnvelopePackage.UNB_TYPE__SYNTAX_IDENTIFIER:
				setSyntaxIdentifier((SyntaxIdentifierType)newValue);
				return;
			case EnvelopePackage.UNB_TYPE__SENDER:
				setSender((SenderType)newValue);
				return;
			case EnvelopePackage.UNB_TYPE__RECIPIENT:
				setRecipient((RecipientType)newValue);
				return;
			case EnvelopePackage.UNB_TYPE__DATE_TIME:
				setDateTime((DateTimeType)newValue);
				return;
			case EnvelopePackage.UNB_TYPE__CONTROL_REF:
				setControlRef((String)newValue);
				return;
			case EnvelopePackage.UNB_TYPE__RECIPIENT_REF:
				setRecipientRef((RecipientRefType)newValue);
				return;
			case EnvelopePackage.UNB_TYPE__APPLICATION_REF:
				setApplicationRef((String)newValue);
				return;
			case EnvelopePackage.UNB_TYPE__PROCESSING_PRIORITY_CODE:
				setProcessingPriorityCode((String)newValue);
				return;
			case EnvelopePackage.UNB_TYPE__ACK_REQUEST:
				setAckRequest((String)newValue);
				return;
			case EnvelopePackage.UNB_TYPE__AGREEMENT_ID:
				setAgreementId((String)newValue);
				return;
			case EnvelopePackage.UNB_TYPE__TEST_INDICATOR:
				setTestIndicator((String)newValue);
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
			case EnvelopePackage.UNB_TYPE__SYNTAX_IDENTIFIER:
				setSyntaxIdentifier((SyntaxIdentifierType)null);
				return;
			case EnvelopePackage.UNB_TYPE__SENDER:
				setSender((SenderType)null);
				return;
			case EnvelopePackage.UNB_TYPE__RECIPIENT:
				setRecipient((RecipientType)null);
				return;
			case EnvelopePackage.UNB_TYPE__DATE_TIME:
				setDateTime((DateTimeType)null);
				return;
			case EnvelopePackage.UNB_TYPE__CONTROL_REF:
				setControlRef(CONTROL_REF_EDEFAULT);
				return;
			case EnvelopePackage.UNB_TYPE__RECIPIENT_REF:
				setRecipientRef((RecipientRefType)null);
				return;
			case EnvelopePackage.UNB_TYPE__APPLICATION_REF:
				setApplicationRef(APPLICATION_REF_EDEFAULT);
				return;
			case EnvelopePackage.UNB_TYPE__PROCESSING_PRIORITY_CODE:
				setProcessingPriorityCode(PROCESSING_PRIORITY_CODE_EDEFAULT);
				return;
			case EnvelopePackage.UNB_TYPE__ACK_REQUEST:
				setAckRequest(ACK_REQUEST_EDEFAULT);
				return;
			case EnvelopePackage.UNB_TYPE__AGREEMENT_ID:
				setAgreementId(AGREEMENT_ID_EDEFAULT);
				return;
			case EnvelopePackage.UNB_TYPE__TEST_INDICATOR:
				setTestIndicator(TEST_INDICATOR_EDEFAULT);
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
			case EnvelopePackage.UNB_TYPE__SYNTAX_IDENTIFIER:
				return syntaxIdentifier != null;
			case EnvelopePackage.UNB_TYPE__SENDER:
				return sender != null;
			case EnvelopePackage.UNB_TYPE__RECIPIENT:
				return recipient != null;
			case EnvelopePackage.UNB_TYPE__DATE_TIME:
				return dateTime != null;
			case EnvelopePackage.UNB_TYPE__CONTROL_REF:
				return CONTROL_REF_EDEFAULT == null ? controlRef != null : !CONTROL_REF_EDEFAULT.equals(controlRef);
			case EnvelopePackage.UNB_TYPE__RECIPIENT_REF:
				return recipientRef != null;
			case EnvelopePackage.UNB_TYPE__APPLICATION_REF:
				return APPLICATION_REF_EDEFAULT == null ? applicationRef != null : !APPLICATION_REF_EDEFAULT.equals(applicationRef);
			case EnvelopePackage.UNB_TYPE__PROCESSING_PRIORITY_CODE:
				return PROCESSING_PRIORITY_CODE_EDEFAULT == null ? processingPriorityCode != null : !PROCESSING_PRIORITY_CODE_EDEFAULT.equals(processingPriorityCode);
			case EnvelopePackage.UNB_TYPE__ACK_REQUEST:
				return ACK_REQUEST_EDEFAULT == null ? ackRequest != null : !ACK_REQUEST_EDEFAULT.equals(ackRequest);
			case EnvelopePackage.UNB_TYPE__AGREEMENT_ID:
				return AGREEMENT_ID_EDEFAULT == null ? agreementId != null : !AGREEMENT_ID_EDEFAULT.equals(agreementId);
			case EnvelopePackage.UNB_TYPE__TEST_INDICATOR:
				return TEST_INDICATOR_EDEFAULT == null ? testIndicator != null : !TEST_INDICATOR_EDEFAULT.equals(testIndicator);
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
		result.append(" (controlRef: ");
		result.append(controlRef);
		result.append(", applicationRef: ");
		result.append(applicationRef);
		result.append(", processingPriorityCode: ");
		result.append(processingPriorityCode);
		result.append(", ackRequest: ");
		result.append(ackRequest);
		result.append(", agreementId: ");
		result.append(agreementId);
		result.append(", testIndicator: ");
		result.append(testIndicator);
		result.append(')');
		return result.toString();
	}

} //UNBTypeImpl
