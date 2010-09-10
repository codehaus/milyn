/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.mylin.ecore.model.envelope;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.mylin.ecore.model.envelope.EnvelopeFactory
 * @model kind="package"
 * @generated
 */
public interface EnvelopePackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "envelope";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://smooks.org/EDIFACT/41/Envelope";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "env";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	EnvelopePackage eINSTANCE = org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl.init();

	/**
	 * The meta object id for the '{@link org.mylin.ecore.model.envelope.impl.DateTimeTypeImpl <em>Date Time Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.mylin.ecore.model.envelope.impl.DateTimeTypeImpl
	 * @see org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl#getDateTimeType()
	 * @generated
	 */
	int DATE_TIME_TYPE = 0;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_TIME_TYPE__MIXED = 0;

	/**
	 * The feature id for the '<em><b>Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_TIME_TYPE__DATE = 1;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_TIME_TYPE__TIME = 2;

	/**
	 * The number of structural features of the '<em>Date Time Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_TIME_TYPE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.mylin.ecore.model.envelope.impl.DocumentRootImpl <em>Document Root</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.mylin.ecore.model.envelope.impl.DocumentRootImpl
	 * @see org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl#getDocumentRoot()
	 * @generated
	 */
	int DOCUMENT_ROOT = 1;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__MIXED = 0;

	/**
	 * The feature id for the '<em><b>XMLNS Prefix Map</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__XMLNS_PREFIX_MAP = 1;

	/**
	 * The feature id for the '<em><b>XSI Schema Location</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__XSI_SCHEMA_LOCATION = 2;

	/**
	 * The feature id for the '<em><b>Un Edifact</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__UN_EDIFACT = 3;

	/**
	 * The number of structural features of the '<em>Document Root</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.mylin.ecore.model.envelope.impl.InterchangeMessageTypeImpl <em>Interchange Message Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.mylin.ecore.model.envelope.impl.InterchangeMessageTypeImpl
	 * @see org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl#getInterchangeMessageType()
	 * @generated
	 */
	int INTERCHANGE_MESSAGE_TYPE = 2;

	/**
	 * The feature id for the '<em><b>UNH</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERCHANGE_MESSAGE_TYPE__UNH = 0;

	/**
	 * The feature id for the '<em><b>Message</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERCHANGE_MESSAGE_TYPE__MESSAGE = 1;

	/**
	 * The feature id for the '<em><b>UNT</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERCHANGE_MESSAGE_TYPE__UNT = 2;

	/**
	 * The number of structural features of the '<em>Interchange Message Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERCHANGE_MESSAGE_TYPE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.mylin.ecore.model.envelope.impl.MessageIdentifierTypeImpl <em>Message Identifier Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.mylin.ecore.model.envelope.impl.MessageIdentifierTypeImpl
	 * @see org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl#getMessageIdentifierType()
	 * @generated
	 */
	int MESSAGE_IDENTIFIER_TYPE = 3;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_IDENTIFIER_TYPE__ID = 0;

	/**
	 * The feature id for the '<em><b>Version Num</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_IDENTIFIER_TYPE__VERSION_NUM = 1;

	/**
	 * The feature id for the '<em><b>Release Num</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_IDENTIFIER_TYPE__RELEASE_NUM = 2;

	/**
	 * The feature id for the '<em><b>Controlling Agency Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_IDENTIFIER_TYPE__CONTROLLING_AGENCY_CODE = 3;

	/**
	 * The feature id for the '<em><b>Association Assigned Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_IDENTIFIER_TYPE__ASSOCIATION_ASSIGNED_CODE = 4;

	/**
	 * The number of structural features of the '<em>Message Identifier Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MESSAGE_IDENTIFIER_TYPE_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link org.mylin.ecore.model.envelope.impl.RecipientTypeImpl <em>Recipient Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.mylin.ecore.model.envelope.impl.RecipientTypeImpl
	 * @see org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl#getRecipientType()
	 * @generated
	 */
	int RECIPIENT_TYPE = 4;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RECIPIENT_TYPE__MIXED = 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RECIPIENT_TYPE__ID = 1;

	/**
	 * The number of structural features of the '<em>Recipient Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RECIPIENT_TYPE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.mylin.ecore.model.envelope.impl.SenderTypeImpl <em>Sender Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.mylin.ecore.model.envelope.impl.SenderTypeImpl
	 * @see org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl#getSenderType()
	 * @generated
	 */
	int SENDER_TYPE = 5;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SENDER_TYPE__MIXED = 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SENDER_TYPE__ID = 1;

	/**
	 * The number of structural features of the '<em>Sender Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SENDER_TYPE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.mylin.ecore.model.envelope.impl.SyntaxIdentifierTypeImpl <em>Syntax Identifier Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.mylin.ecore.model.envelope.impl.SyntaxIdentifierTypeImpl
	 * @see org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl#getSyntaxIdentifierType()
	 * @generated
	 */
	int SYNTAX_IDENTIFIER_TYPE = 6;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SYNTAX_IDENTIFIER_TYPE__MIXED = 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SYNTAX_IDENTIFIER_TYPE__ID = 1;

	/**
	 * The feature id for the '<em><b>Version Num</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SYNTAX_IDENTIFIER_TYPE__VERSION_NUM = 2;

	/**
	 * The number of structural features of the '<em>Syntax Identifier Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SYNTAX_IDENTIFIER_TYPE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.mylin.ecore.model.envelope.impl.UNBTypeImpl <em>UNB Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.mylin.ecore.model.envelope.impl.UNBTypeImpl
	 * @see org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl#getUNBType()
	 * @generated
	 */
	int UNB_TYPE = 7;

	/**
	 * The feature id for the '<em><b>Syntax Identifier</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNB_TYPE__SYNTAX_IDENTIFIER = 0;

	/**
	 * The feature id for the '<em><b>Sender</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNB_TYPE__SENDER = 1;

	/**
	 * The feature id for the '<em><b>Recipient</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNB_TYPE__RECIPIENT = 2;

	/**
	 * The feature id for the '<em><b>Date Time</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNB_TYPE__DATE_TIME = 3;

	/**
	 * The feature id for the '<em><b>Control Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNB_TYPE__CONTROL_REF = 4;

	/**
	 * The number of structural features of the '<em>UNB Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNB_TYPE_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link org.mylin.ecore.model.envelope.impl.UnEdifactTypeImpl <em>Un Edifact Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.mylin.ecore.model.envelope.impl.UnEdifactTypeImpl
	 * @see org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl#getUnEdifactType()
	 * @generated
	 */
	int UN_EDIFACT_TYPE = 8;

	/**
	 * The feature id for the '<em><b>UNB</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UN_EDIFACT_TYPE__UNB = 0;

	/**
	 * The feature id for the '<em><b>Messages</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UN_EDIFACT_TYPE__MESSAGES = 1;

	/**
	 * The feature id for the '<em><b>UNZ</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UN_EDIFACT_TYPE__UNZ = 2;

	/**
	 * The number of structural features of the '<em>Un Edifact Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UN_EDIFACT_TYPE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.mylin.ecore.model.envelope.impl.UNHTypeImpl <em>UNH Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.mylin.ecore.model.envelope.impl.UNHTypeImpl
	 * @see org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl#getUNHType()
	 * @generated
	 */
	int UNH_TYPE = 9;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNH_TYPE__MIXED = 0;

	/**
	 * The feature id for the '<em><b>Message Ref Num</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNH_TYPE__MESSAGE_REF_NUM = 1;

	/**
	 * The feature id for the '<em><b>Message Identifier</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNH_TYPE__MESSAGE_IDENTIFIER = 2;

	/**
	 * The number of structural features of the '<em>UNH Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNH_TYPE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.mylin.ecore.model.envelope.impl.UNTTypeImpl <em>UNT Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.mylin.ecore.model.envelope.impl.UNTTypeImpl
	 * @see org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl#getUNTType()
	 * @generated
	 */
	int UNT_TYPE = 10;

	/**
	 * The feature id for the '<em><b>Segment Count</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNT_TYPE__SEGMENT_COUNT = 0;

	/**
	 * The feature id for the '<em><b>Message Ref Num</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNT_TYPE__MESSAGE_REF_NUM = 1;

	/**
	 * The number of structural features of the '<em>UNT Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNT_TYPE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.mylin.ecore.model.envelope.impl.UNZTypeImpl <em>UNZ Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.mylin.ecore.model.envelope.impl.UNZTypeImpl
	 * @see org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl#getUNZType()
	 * @generated
	 */
	int UNZ_TYPE = 11;

	/**
	 * The feature id for the '<em><b>Control Count</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNZ_TYPE__CONTROL_COUNT = 0;

	/**
	 * The feature id for the '<em><b>Control Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNZ_TYPE__CONTROL_REF = 1;

	/**
	 * The number of structural features of the '<em>UNZ Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNZ_TYPE_FEATURE_COUNT = 2;


	/**
	 * Returns the meta object for class '{@link org.mylin.ecore.model.envelope.DateTimeType <em>Date Time Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Date Time Type</em>'.
	 * @see org.mylin.ecore.model.envelope.DateTimeType
	 * @generated
	 */
	EClass getDateTimeType();

	/**
	 * Returns the meta object for the attribute list '{@link org.mylin.ecore.model.envelope.DateTimeType#getMixed <em>Mixed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mixed</em>'.
	 * @see org.mylin.ecore.model.envelope.DateTimeType#getMixed()
	 * @see #getDateTimeType()
	 * @generated
	 */
	EAttribute getDateTimeType_Mixed();

	/**
	 * Returns the meta object for the attribute '{@link org.mylin.ecore.model.envelope.DateTimeType#getDate <em>Date</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Date</em>'.
	 * @see org.mylin.ecore.model.envelope.DateTimeType#getDate()
	 * @see #getDateTimeType()
	 * @generated
	 */
	EAttribute getDateTimeType_Date();

	/**
	 * Returns the meta object for the attribute '{@link org.mylin.ecore.model.envelope.DateTimeType#getTime <em>Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Time</em>'.
	 * @see org.mylin.ecore.model.envelope.DateTimeType#getTime()
	 * @see #getDateTimeType()
	 * @generated
	 */
	EAttribute getDateTimeType_Time();

	/**
	 * Returns the meta object for class '{@link org.mylin.ecore.model.envelope.DocumentRoot <em>Document Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Document Root</em>'.
	 * @see org.mylin.ecore.model.envelope.DocumentRoot
	 * @generated
	 */
	EClass getDocumentRoot();

	/**
	 * Returns the meta object for the attribute list '{@link org.mylin.ecore.model.envelope.DocumentRoot#getMixed <em>Mixed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mixed</em>'.
	 * @see org.mylin.ecore.model.envelope.DocumentRoot#getMixed()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EAttribute getDocumentRoot_Mixed();

	/**
	 * Returns the meta object for the map '{@link org.mylin.ecore.model.envelope.DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>XMLNS Prefix Map</em>'.
	 * @see org.mylin.ecore.model.envelope.DocumentRoot#getXMLNSPrefixMap()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_XMLNSPrefixMap();

	/**
	 * Returns the meta object for the map '{@link org.mylin.ecore.model.envelope.DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>XSI Schema Location</em>'.
	 * @see org.mylin.ecore.model.envelope.DocumentRoot#getXSISchemaLocation()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_XSISchemaLocation();

	/**
	 * Returns the meta object for the containment reference '{@link org.mylin.ecore.model.envelope.DocumentRoot#getUnEdifact <em>Un Edifact</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Un Edifact</em>'.
	 * @see org.mylin.ecore.model.envelope.DocumentRoot#getUnEdifact()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_UnEdifact();

	/**
	 * Returns the meta object for class '{@link org.mylin.ecore.model.envelope.InterchangeMessageType <em>Interchange Message Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Interchange Message Type</em>'.
	 * @see org.mylin.ecore.model.envelope.InterchangeMessageType
	 * @generated
	 */
	EClass getInterchangeMessageType();

	/**
	 * Returns the meta object for the containment reference '{@link org.mylin.ecore.model.envelope.InterchangeMessageType#getUNH <em>UNH</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>UNH</em>'.
	 * @see org.mylin.ecore.model.envelope.InterchangeMessageType#getUNH()
	 * @see #getInterchangeMessageType()
	 * @generated
	 */
	EReference getInterchangeMessageType_UNH();

	/**
	 * Returns the meta object for the attribute list '{@link org.mylin.ecore.model.envelope.InterchangeMessageType#getMessage <em>Message</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Message</em>'.
	 * @see org.mylin.ecore.model.envelope.InterchangeMessageType#getMessage()
	 * @see #getInterchangeMessageType()
	 * @generated
	 */
	EAttribute getInterchangeMessageType_Message();

	/**
	 * Returns the meta object for the containment reference '{@link org.mylin.ecore.model.envelope.InterchangeMessageType#getUNT <em>UNT</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>UNT</em>'.
	 * @see org.mylin.ecore.model.envelope.InterchangeMessageType#getUNT()
	 * @see #getInterchangeMessageType()
	 * @generated
	 */
	EReference getInterchangeMessageType_UNT();

	/**
	 * Returns the meta object for class '{@link org.mylin.ecore.model.envelope.MessageIdentifierType <em>Message Identifier Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Message Identifier Type</em>'.
	 * @see org.mylin.ecore.model.envelope.MessageIdentifierType
	 * @generated
	 */
	EClass getMessageIdentifierType();

	/**
	 * Returns the meta object for the attribute '{@link org.mylin.ecore.model.envelope.MessageIdentifierType#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.mylin.ecore.model.envelope.MessageIdentifierType#getId()
	 * @see #getMessageIdentifierType()
	 * @generated
	 */
	EAttribute getMessageIdentifierType_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.mylin.ecore.model.envelope.MessageIdentifierType#getVersionNum <em>Version Num</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version Num</em>'.
	 * @see org.mylin.ecore.model.envelope.MessageIdentifierType#getVersionNum()
	 * @see #getMessageIdentifierType()
	 * @generated
	 */
	EAttribute getMessageIdentifierType_VersionNum();

	/**
	 * Returns the meta object for the attribute '{@link org.mylin.ecore.model.envelope.MessageIdentifierType#getReleaseNum <em>Release Num</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Release Num</em>'.
	 * @see org.mylin.ecore.model.envelope.MessageIdentifierType#getReleaseNum()
	 * @see #getMessageIdentifierType()
	 * @generated
	 */
	EAttribute getMessageIdentifierType_ReleaseNum();

	/**
	 * Returns the meta object for the attribute '{@link org.mylin.ecore.model.envelope.MessageIdentifierType#getControllingAgencyCode <em>Controlling Agency Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Controlling Agency Code</em>'.
	 * @see org.mylin.ecore.model.envelope.MessageIdentifierType#getControllingAgencyCode()
	 * @see #getMessageIdentifierType()
	 * @generated
	 */
	EAttribute getMessageIdentifierType_ControllingAgencyCode();

	/**
	 * Returns the meta object for the attribute '{@link org.mylin.ecore.model.envelope.MessageIdentifierType#getAssociationAssignedCode <em>Association Assigned Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Association Assigned Code</em>'.
	 * @see org.mylin.ecore.model.envelope.MessageIdentifierType#getAssociationAssignedCode()
	 * @see #getMessageIdentifierType()
	 * @generated
	 */
	EAttribute getMessageIdentifierType_AssociationAssignedCode();

	/**
	 * Returns the meta object for class '{@link org.mylin.ecore.model.envelope.RecipientType <em>Recipient Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Recipient Type</em>'.
	 * @see org.mylin.ecore.model.envelope.RecipientType
	 * @generated
	 */
	EClass getRecipientType();

	/**
	 * Returns the meta object for the attribute list '{@link org.mylin.ecore.model.envelope.RecipientType#getMixed <em>Mixed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mixed</em>'.
	 * @see org.mylin.ecore.model.envelope.RecipientType#getMixed()
	 * @see #getRecipientType()
	 * @generated
	 */
	EAttribute getRecipientType_Mixed();

	/**
	 * Returns the meta object for the attribute '{@link org.mylin.ecore.model.envelope.RecipientType#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.mylin.ecore.model.envelope.RecipientType#getId()
	 * @see #getRecipientType()
	 * @generated
	 */
	EAttribute getRecipientType_Id();

	/**
	 * Returns the meta object for class '{@link org.mylin.ecore.model.envelope.SenderType <em>Sender Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Sender Type</em>'.
	 * @see org.mylin.ecore.model.envelope.SenderType
	 * @generated
	 */
	EClass getSenderType();

	/**
	 * Returns the meta object for the attribute list '{@link org.mylin.ecore.model.envelope.SenderType#getMixed <em>Mixed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mixed</em>'.
	 * @see org.mylin.ecore.model.envelope.SenderType#getMixed()
	 * @see #getSenderType()
	 * @generated
	 */
	EAttribute getSenderType_Mixed();

	/**
	 * Returns the meta object for the attribute '{@link org.mylin.ecore.model.envelope.SenderType#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.mylin.ecore.model.envelope.SenderType#getId()
	 * @see #getSenderType()
	 * @generated
	 */
	EAttribute getSenderType_Id();

	/**
	 * Returns the meta object for class '{@link org.mylin.ecore.model.envelope.SyntaxIdentifierType <em>Syntax Identifier Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Syntax Identifier Type</em>'.
	 * @see org.mylin.ecore.model.envelope.SyntaxIdentifierType
	 * @generated
	 */
	EClass getSyntaxIdentifierType();

	/**
	 * Returns the meta object for the attribute list '{@link org.mylin.ecore.model.envelope.SyntaxIdentifierType#getMixed <em>Mixed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mixed</em>'.
	 * @see org.mylin.ecore.model.envelope.SyntaxIdentifierType#getMixed()
	 * @see #getSyntaxIdentifierType()
	 * @generated
	 */
	EAttribute getSyntaxIdentifierType_Mixed();

	/**
	 * Returns the meta object for the attribute '{@link org.mylin.ecore.model.envelope.SyntaxIdentifierType#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.mylin.ecore.model.envelope.SyntaxIdentifierType#getId()
	 * @see #getSyntaxIdentifierType()
	 * @generated
	 */
	EAttribute getSyntaxIdentifierType_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.mylin.ecore.model.envelope.SyntaxIdentifierType#getVersionNum <em>Version Num</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version Num</em>'.
	 * @see org.mylin.ecore.model.envelope.SyntaxIdentifierType#getVersionNum()
	 * @see #getSyntaxIdentifierType()
	 * @generated
	 */
	EAttribute getSyntaxIdentifierType_VersionNum();

	/**
	 * Returns the meta object for class '{@link org.mylin.ecore.model.envelope.UNBType <em>UNB Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>UNB Type</em>'.
	 * @see org.mylin.ecore.model.envelope.UNBType
	 * @generated
	 */
	EClass getUNBType();

	/**
	 * Returns the meta object for the containment reference '{@link org.mylin.ecore.model.envelope.UNBType#getSyntaxIdentifier <em>Syntax Identifier</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Syntax Identifier</em>'.
	 * @see org.mylin.ecore.model.envelope.UNBType#getSyntaxIdentifier()
	 * @see #getUNBType()
	 * @generated
	 */
	EReference getUNBType_SyntaxIdentifier();

	/**
	 * Returns the meta object for the containment reference '{@link org.mylin.ecore.model.envelope.UNBType#getSender <em>Sender</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Sender</em>'.
	 * @see org.mylin.ecore.model.envelope.UNBType#getSender()
	 * @see #getUNBType()
	 * @generated
	 */
	EReference getUNBType_Sender();

	/**
	 * Returns the meta object for the containment reference '{@link org.mylin.ecore.model.envelope.UNBType#getRecipient <em>Recipient</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Recipient</em>'.
	 * @see org.mylin.ecore.model.envelope.UNBType#getRecipient()
	 * @see #getUNBType()
	 * @generated
	 */
	EReference getUNBType_Recipient();

	/**
	 * Returns the meta object for the containment reference '{@link org.mylin.ecore.model.envelope.UNBType#getDateTime <em>Date Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Date Time</em>'.
	 * @see org.mylin.ecore.model.envelope.UNBType#getDateTime()
	 * @see #getUNBType()
	 * @generated
	 */
	EReference getUNBType_DateTime();

	/**
	 * Returns the meta object for the attribute '{@link org.mylin.ecore.model.envelope.UNBType#getControlRef <em>Control Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Control Ref</em>'.
	 * @see org.mylin.ecore.model.envelope.UNBType#getControlRef()
	 * @see #getUNBType()
	 * @generated
	 */
	EAttribute getUNBType_ControlRef();

	/**
	 * Returns the meta object for class '{@link org.mylin.ecore.model.envelope.UnEdifactType <em>Un Edifact Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Un Edifact Type</em>'.
	 * @see org.mylin.ecore.model.envelope.UnEdifactType
	 * @generated
	 */
	EClass getUnEdifactType();

	/**
	 * Returns the meta object for the containment reference '{@link org.mylin.ecore.model.envelope.UnEdifactType#getUNB <em>UNB</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>UNB</em>'.
	 * @see org.mylin.ecore.model.envelope.UnEdifactType#getUNB()
	 * @see #getUnEdifactType()
	 * @generated
	 */
	EReference getUnEdifactType_UNB();

	/**
	 * Returns the meta object for the containment reference list '{@link org.mylin.ecore.model.envelope.UnEdifactType#getMessages <em>Messages</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Messages</em>'.
	 * @see org.mylin.ecore.model.envelope.UnEdifactType#getMessages()
	 * @see #getUnEdifactType()
	 * @generated
	 */
	EReference getUnEdifactType_Messages();

	/**
	 * Returns the meta object for the containment reference '{@link org.mylin.ecore.model.envelope.UnEdifactType#getUNZ <em>UNZ</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>UNZ</em>'.
	 * @see org.mylin.ecore.model.envelope.UnEdifactType#getUNZ()
	 * @see #getUnEdifactType()
	 * @generated
	 */
	EReference getUnEdifactType_UNZ();

	/**
	 * Returns the meta object for class '{@link org.mylin.ecore.model.envelope.UNHType <em>UNH Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>UNH Type</em>'.
	 * @see org.mylin.ecore.model.envelope.UNHType
	 * @generated
	 */
	EClass getUNHType();

	/**
	 * Returns the meta object for the attribute list '{@link org.mylin.ecore.model.envelope.UNHType#getMixed <em>Mixed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mixed</em>'.
	 * @see org.mylin.ecore.model.envelope.UNHType#getMixed()
	 * @see #getUNHType()
	 * @generated
	 */
	EAttribute getUNHType_Mixed();

	/**
	 * Returns the meta object for the attribute '{@link org.mylin.ecore.model.envelope.UNHType#getMessageRefNum <em>Message Ref Num</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Message Ref Num</em>'.
	 * @see org.mylin.ecore.model.envelope.UNHType#getMessageRefNum()
	 * @see #getUNHType()
	 * @generated
	 */
	EAttribute getUNHType_MessageRefNum();

	/**
	 * Returns the meta object for the containment reference '{@link org.mylin.ecore.model.envelope.UNHType#getMessageIdentifier <em>Message Identifier</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Message Identifier</em>'.
	 * @see org.mylin.ecore.model.envelope.UNHType#getMessageIdentifier()
	 * @see #getUNHType()
	 * @generated
	 */
	EReference getUNHType_MessageIdentifier();

	/**
	 * Returns the meta object for class '{@link org.mylin.ecore.model.envelope.UNTType <em>UNT Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>UNT Type</em>'.
	 * @see org.mylin.ecore.model.envelope.UNTType
	 * @generated
	 */
	EClass getUNTType();

	/**
	 * Returns the meta object for the attribute '{@link org.mylin.ecore.model.envelope.UNTType#getSegmentCount <em>Segment Count</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Segment Count</em>'.
	 * @see org.mylin.ecore.model.envelope.UNTType#getSegmentCount()
	 * @see #getUNTType()
	 * @generated
	 */
	EAttribute getUNTType_SegmentCount();

	/**
	 * Returns the meta object for the attribute '{@link org.mylin.ecore.model.envelope.UNTType#getMessageRefNum <em>Message Ref Num</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Message Ref Num</em>'.
	 * @see org.mylin.ecore.model.envelope.UNTType#getMessageRefNum()
	 * @see #getUNTType()
	 * @generated
	 */
	EAttribute getUNTType_MessageRefNum();

	/**
	 * Returns the meta object for class '{@link org.mylin.ecore.model.envelope.UNZType <em>UNZ Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>UNZ Type</em>'.
	 * @see org.mylin.ecore.model.envelope.UNZType
	 * @generated
	 */
	EClass getUNZType();

	/**
	 * Returns the meta object for the attribute '{@link org.mylin.ecore.model.envelope.UNZType#getControlCount <em>Control Count</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Control Count</em>'.
	 * @see org.mylin.ecore.model.envelope.UNZType#getControlCount()
	 * @see #getUNZType()
	 * @generated
	 */
	EAttribute getUNZType_ControlCount();

	/**
	 * Returns the meta object for the attribute '{@link org.mylin.ecore.model.envelope.UNZType#getControlRef <em>Control Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Control Ref</em>'.
	 * @see org.mylin.ecore.model.envelope.UNZType#getControlRef()
	 * @see #getUNZType()
	 * @generated
	 */
	EAttribute getUNZType_ControlRef();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	EnvelopeFactory getEnvelopeFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.mylin.ecore.model.envelope.impl.DateTimeTypeImpl <em>Date Time Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.mylin.ecore.model.envelope.impl.DateTimeTypeImpl
		 * @see org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl#getDateTimeType()
		 * @generated
		 */
		EClass DATE_TIME_TYPE = eINSTANCE.getDateTimeType();

		/**
		 * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATE_TIME_TYPE__MIXED = eINSTANCE.getDateTimeType_Mixed();

		/**
		 * The meta object literal for the '<em><b>Date</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATE_TIME_TYPE__DATE = eINSTANCE.getDateTimeType_Date();

		/**
		 * The meta object literal for the '<em><b>Time</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATE_TIME_TYPE__TIME = eINSTANCE.getDateTimeType_Time();

		/**
		 * The meta object literal for the '{@link org.mylin.ecore.model.envelope.impl.DocumentRootImpl <em>Document Root</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.mylin.ecore.model.envelope.impl.DocumentRootImpl
		 * @see org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl#getDocumentRoot()
		 * @generated
		 */
		EClass DOCUMENT_ROOT = eINSTANCE.getDocumentRoot();

		/**
		 * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DOCUMENT_ROOT__MIXED = eINSTANCE.getDocumentRoot_Mixed();

		/**
		 * The meta object literal for the '<em><b>XMLNS Prefix Map</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__XMLNS_PREFIX_MAP = eINSTANCE.getDocumentRoot_XMLNSPrefixMap();

		/**
		 * The meta object literal for the '<em><b>XSI Schema Location</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__XSI_SCHEMA_LOCATION = eINSTANCE.getDocumentRoot_XSISchemaLocation();

		/**
		 * The meta object literal for the '<em><b>Un Edifact</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__UN_EDIFACT = eINSTANCE.getDocumentRoot_UnEdifact();

		/**
		 * The meta object literal for the '{@link org.mylin.ecore.model.envelope.impl.InterchangeMessageTypeImpl <em>Interchange Message Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.mylin.ecore.model.envelope.impl.InterchangeMessageTypeImpl
		 * @see org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl#getInterchangeMessageType()
		 * @generated
		 */
		EClass INTERCHANGE_MESSAGE_TYPE = eINSTANCE.getInterchangeMessageType();

		/**
		 * The meta object literal for the '<em><b>UNH</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INTERCHANGE_MESSAGE_TYPE__UNH = eINSTANCE.getInterchangeMessageType_UNH();

		/**
		 * The meta object literal for the '<em><b>Message</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INTERCHANGE_MESSAGE_TYPE__MESSAGE = eINSTANCE.getInterchangeMessageType_Message();

		/**
		 * The meta object literal for the '<em><b>UNT</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INTERCHANGE_MESSAGE_TYPE__UNT = eINSTANCE.getInterchangeMessageType_UNT();

		/**
		 * The meta object literal for the '{@link org.mylin.ecore.model.envelope.impl.MessageIdentifierTypeImpl <em>Message Identifier Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.mylin.ecore.model.envelope.impl.MessageIdentifierTypeImpl
		 * @see org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl#getMessageIdentifierType()
		 * @generated
		 */
		EClass MESSAGE_IDENTIFIER_TYPE = eINSTANCE.getMessageIdentifierType();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MESSAGE_IDENTIFIER_TYPE__ID = eINSTANCE.getMessageIdentifierType_Id();

		/**
		 * The meta object literal for the '<em><b>Version Num</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MESSAGE_IDENTIFIER_TYPE__VERSION_NUM = eINSTANCE.getMessageIdentifierType_VersionNum();

		/**
		 * The meta object literal for the '<em><b>Release Num</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MESSAGE_IDENTIFIER_TYPE__RELEASE_NUM = eINSTANCE.getMessageIdentifierType_ReleaseNum();

		/**
		 * The meta object literal for the '<em><b>Controlling Agency Code</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MESSAGE_IDENTIFIER_TYPE__CONTROLLING_AGENCY_CODE = eINSTANCE.getMessageIdentifierType_ControllingAgencyCode();

		/**
		 * The meta object literal for the '<em><b>Association Assigned Code</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MESSAGE_IDENTIFIER_TYPE__ASSOCIATION_ASSIGNED_CODE = eINSTANCE.getMessageIdentifierType_AssociationAssignedCode();

		/**
		 * The meta object literal for the '{@link org.mylin.ecore.model.envelope.impl.RecipientTypeImpl <em>Recipient Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.mylin.ecore.model.envelope.impl.RecipientTypeImpl
		 * @see org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl#getRecipientType()
		 * @generated
		 */
		EClass RECIPIENT_TYPE = eINSTANCE.getRecipientType();

		/**
		 * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RECIPIENT_TYPE__MIXED = eINSTANCE.getRecipientType_Mixed();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RECIPIENT_TYPE__ID = eINSTANCE.getRecipientType_Id();

		/**
		 * The meta object literal for the '{@link org.mylin.ecore.model.envelope.impl.SenderTypeImpl <em>Sender Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.mylin.ecore.model.envelope.impl.SenderTypeImpl
		 * @see org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl#getSenderType()
		 * @generated
		 */
		EClass SENDER_TYPE = eINSTANCE.getSenderType();

		/**
		 * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SENDER_TYPE__MIXED = eINSTANCE.getSenderType_Mixed();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SENDER_TYPE__ID = eINSTANCE.getSenderType_Id();

		/**
		 * The meta object literal for the '{@link org.mylin.ecore.model.envelope.impl.SyntaxIdentifierTypeImpl <em>Syntax Identifier Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.mylin.ecore.model.envelope.impl.SyntaxIdentifierTypeImpl
		 * @see org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl#getSyntaxIdentifierType()
		 * @generated
		 */
		EClass SYNTAX_IDENTIFIER_TYPE = eINSTANCE.getSyntaxIdentifierType();

		/**
		 * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SYNTAX_IDENTIFIER_TYPE__MIXED = eINSTANCE.getSyntaxIdentifierType_Mixed();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SYNTAX_IDENTIFIER_TYPE__ID = eINSTANCE.getSyntaxIdentifierType_Id();

		/**
		 * The meta object literal for the '<em><b>Version Num</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SYNTAX_IDENTIFIER_TYPE__VERSION_NUM = eINSTANCE.getSyntaxIdentifierType_VersionNum();

		/**
		 * The meta object literal for the '{@link org.mylin.ecore.model.envelope.impl.UNBTypeImpl <em>UNB Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.mylin.ecore.model.envelope.impl.UNBTypeImpl
		 * @see org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl#getUNBType()
		 * @generated
		 */
		EClass UNB_TYPE = eINSTANCE.getUNBType();

		/**
		 * The meta object literal for the '<em><b>Syntax Identifier</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference UNB_TYPE__SYNTAX_IDENTIFIER = eINSTANCE.getUNBType_SyntaxIdentifier();

		/**
		 * The meta object literal for the '<em><b>Sender</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference UNB_TYPE__SENDER = eINSTANCE.getUNBType_Sender();

		/**
		 * The meta object literal for the '<em><b>Recipient</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference UNB_TYPE__RECIPIENT = eINSTANCE.getUNBType_Recipient();

		/**
		 * The meta object literal for the '<em><b>Date Time</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference UNB_TYPE__DATE_TIME = eINSTANCE.getUNBType_DateTime();

		/**
		 * The meta object literal for the '<em><b>Control Ref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UNB_TYPE__CONTROL_REF = eINSTANCE.getUNBType_ControlRef();

		/**
		 * The meta object literal for the '{@link org.mylin.ecore.model.envelope.impl.UnEdifactTypeImpl <em>Un Edifact Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.mylin.ecore.model.envelope.impl.UnEdifactTypeImpl
		 * @see org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl#getUnEdifactType()
		 * @generated
		 */
		EClass UN_EDIFACT_TYPE = eINSTANCE.getUnEdifactType();

		/**
		 * The meta object literal for the '<em><b>UNB</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference UN_EDIFACT_TYPE__UNB = eINSTANCE.getUnEdifactType_UNB();

		/**
		 * The meta object literal for the '<em><b>Messages</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference UN_EDIFACT_TYPE__MESSAGES = eINSTANCE.getUnEdifactType_Messages();

		/**
		 * The meta object literal for the '<em><b>UNZ</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference UN_EDIFACT_TYPE__UNZ = eINSTANCE.getUnEdifactType_UNZ();

		/**
		 * The meta object literal for the '{@link org.mylin.ecore.model.envelope.impl.UNHTypeImpl <em>UNH Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.mylin.ecore.model.envelope.impl.UNHTypeImpl
		 * @see org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl#getUNHType()
		 * @generated
		 */
		EClass UNH_TYPE = eINSTANCE.getUNHType();

		/**
		 * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UNH_TYPE__MIXED = eINSTANCE.getUNHType_Mixed();

		/**
		 * The meta object literal for the '<em><b>Message Ref Num</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UNH_TYPE__MESSAGE_REF_NUM = eINSTANCE.getUNHType_MessageRefNum();

		/**
		 * The meta object literal for the '<em><b>Message Identifier</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference UNH_TYPE__MESSAGE_IDENTIFIER = eINSTANCE.getUNHType_MessageIdentifier();

		/**
		 * The meta object literal for the '{@link org.mylin.ecore.model.envelope.impl.UNTTypeImpl <em>UNT Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.mylin.ecore.model.envelope.impl.UNTTypeImpl
		 * @see org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl#getUNTType()
		 * @generated
		 */
		EClass UNT_TYPE = eINSTANCE.getUNTType();

		/**
		 * The meta object literal for the '<em><b>Segment Count</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UNT_TYPE__SEGMENT_COUNT = eINSTANCE.getUNTType_SegmentCount();

		/**
		 * The meta object literal for the '<em><b>Message Ref Num</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UNT_TYPE__MESSAGE_REF_NUM = eINSTANCE.getUNTType_MessageRefNum();

		/**
		 * The meta object literal for the '{@link org.mylin.ecore.model.envelope.impl.UNZTypeImpl <em>UNZ Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.mylin.ecore.model.envelope.impl.UNZTypeImpl
		 * @see org.mylin.ecore.model.envelope.impl.EnvelopePackageImpl#getUNZType()
		 * @generated
		 */
		EClass UNZ_TYPE = eINSTANCE.getUNZType();

		/**
		 * The meta object literal for the '<em><b>Control Count</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UNZ_TYPE__CONTROL_COUNT = eINSTANCE.getUNZType_ControlCount();

		/**
		 * The meta object literal for the '<em><b>Control Ref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UNZ_TYPE__CONTROL_REF = eINSTANCE.getUNZType_ControlRef();

	}

} //EnvelopePackage
