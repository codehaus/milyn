/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.mylin.ecore.model.envelope.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

import org.mylin.ecore.model.envelope.DateTimeType;
import org.mylin.ecore.model.envelope.DocumentRoot;
import org.mylin.ecore.model.envelope.EnvelopeFactory;
import org.mylin.ecore.model.envelope.EnvelopePackage;
import org.mylin.ecore.model.envelope.InterchangeMessageType;
import org.mylin.ecore.model.envelope.MessageIdentifierType;
import org.mylin.ecore.model.envelope.RecipientType;
import org.mylin.ecore.model.envelope.SenderType;
import org.mylin.ecore.model.envelope.SyntaxIdentifierType;
import org.mylin.ecore.model.envelope.UNBType;
import org.mylin.ecore.model.envelope.UNHType;
import org.mylin.ecore.model.envelope.UNTType;
import org.mylin.ecore.model.envelope.UNZType;
import org.mylin.ecore.model.envelope.UnEdifactType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class EnvelopePackageImpl extends EPackageImpl implements EnvelopePackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dateTimeTypeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass documentRootEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass interchangeMessageTypeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass messageIdentifierTypeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass recipientTypeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass senderTypeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass syntaxIdentifierTypeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass unbTypeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass unEdifactTypeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass unhTypeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass untTypeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass unzTypeEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.mylin.ecore.model.envelope.EnvelopePackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private EnvelopePackageImpl() {
		super(eNS_URI, EnvelopeFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link EnvelopePackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static EnvelopePackage init() {
		if (isInited) return (EnvelopePackage)EPackage.Registry.INSTANCE.getEPackage(EnvelopePackage.eNS_URI);

		// Obtain or create and register package
		EnvelopePackageImpl theEnvelopePackage = (EnvelopePackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof EnvelopePackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new EnvelopePackageImpl());

		isInited = true;

		// Initialize simple dependencies
		XMLTypePackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theEnvelopePackage.createPackageContents();

		// Initialize created meta-data
		theEnvelopePackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theEnvelopePackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(EnvelopePackage.eNS_URI, theEnvelopePackage);
		return theEnvelopePackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDateTimeType() {
		return dateTimeTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDateTimeType_Mixed() {
		return (EAttribute)dateTimeTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDateTimeType_Date() {
		return (EAttribute)dateTimeTypeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDateTimeType_Time() {
		return (EAttribute)dateTimeTypeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDocumentRoot() {
		return documentRootEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDocumentRoot_Mixed() {
		return (EAttribute)documentRootEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDocumentRoot_XMLNSPrefixMap() {
		return (EReference)documentRootEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDocumentRoot_XSISchemaLocation() {
		return (EReference)documentRootEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDocumentRoot_UnEdifact() {
		return (EReference)documentRootEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getInterchangeMessageType() {
		return interchangeMessageTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getInterchangeMessageType_UNH() {
		return (EReference)interchangeMessageTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getInterchangeMessageType_Any() {
		return (EAttribute)interchangeMessageTypeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getInterchangeMessageType_UNT() {
		return (EReference)interchangeMessageTypeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMessageIdentifierType() {
		return messageIdentifierTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMessageIdentifierType_Id() {
		return (EAttribute)messageIdentifierTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMessageIdentifierType_VersionNum() {
		return (EAttribute)messageIdentifierTypeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMessageIdentifierType_ReleaseNum() {
		return (EAttribute)messageIdentifierTypeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMessageIdentifierType_ControllingAgencyCode() {
		return (EAttribute)messageIdentifierTypeEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMessageIdentifierType_AssociationAssignedCode() {
		return (EAttribute)messageIdentifierTypeEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRecipientType() {
		return recipientTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRecipientType_Mixed() {
		return (EAttribute)recipientTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRecipientType_Id() {
		return (EAttribute)recipientTypeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSenderType() {
		return senderTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSenderType_Mixed() {
		return (EAttribute)senderTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSenderType_Id() {
		return (EAttribute)senderTypeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSyntaxIdentifierType() {
		return syntaxIdentifierTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSyntaxIdentifierType_Mixed() {
		return (EAttribute)syntaxIdentifierTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSyntaxIdentifierType_Id() {
		return (EAttribute)syntaxIdentifierTypeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSyntaxIdentifierType_VersionNum() {
		return (EAttribute)syntaxIdentifierTypeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getUNBType() {
		return unbTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getUNBType_SyntaxIdentifier() {
		return (EReference)unbTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getUNBType_Sender() {
		return (EReference)unbTypeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getUNBType_Recipient() {
		return (EReference)unbTypeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getUNBType_DateTime() {
		return (EReference)unbTypeEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getUNBType_ControlRef() {
		return (EAttribute)unbTypeEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getUnEdifactType() {
		return unEdifactTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getUnEdifactType_UNB() {
		return (EReference)unEdifactTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getUnEdifactType_InterchangeMessage() {
		return (EReference)unEdifactTypeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getUnEdifactType_UNZ() {
		return (EReference)unEdifactTypeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getUNHType() {
		return unhTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getUNHType_Mixed() {
		return (EAttribute)unhTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getUNHType_MessageRefNum() {
		return (EAttribute)unhTypeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getUNHType_MessageIdentifier() {
		return (EReference)unhTypeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getUNTType() {
		return untTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getUNTType_SegmentCount() {
		return (EAttribute)untTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getUNTType_MessageRefNum() {
		return (EAttribute)untTypeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getUNZType() {
		return unzTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getUNZType_ControlCount() {
		return (EAttribute)unzTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getUNZType_ControlRef() {
		return (EAttribute)unzTypeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EnvelopeFactory getEnvelopeFactory() {
		return (EnvelopeFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		dateTimeTypeEClass = createEClass(DATE_TIME_TYPE);
		createEAttribute(dateTimeTypeEClass, DATE_TIME_TYPE__MIXED);
		createEAttribute(dateTimeTypeEClass, DATE_TIME_TYPE__DATE);
		createEAttribute(dateTimeTypeEClass, DATE_TIME_TYPE__TIME);

		documentRootEClass = createEClass(DOCUMENT_ROOT);
		createEAttribute(documentRootEClass, DOCUMENT_ROOT__MIXED);
		createEReference(documentRootEClass, DOCUMENT_ROOT__XMLNS_PREFIX_MAP);
		createEReference(documentRootEClass, DOCUMENT_ROOT__XSI_SCHEMA_LOCATION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__UN_EDIFACT);

		interchangeMessageTypeEClass = createEClass(INTERCHANGE_MESSAGE_TYPE);
		createEReference(interchangeMessageTypeEClass, INTERCHANGE_MESSAGE_TYPE__UNH);
		createEAttribute(interchangeMessageTypeEClass, INTERCHANGE_MESSAGE_TYPE__ANY);
		createEReference(interchangeMessageTypeEClass, INTERCHANGE_MESSAGE_TYPE__UNT);

		messageIdentifierTypeEClass = createEClass(MESSAGE_IDENTIFIER_TYPE);
		createEAttribute(messageIdentifierTypeEClass, MESSAGE_IDENTIFIER_TYPE__ID);
		createEAttribute(messageIdentifierTypeEClass, MESSAGE_IDENTIFIER_TYPE__VERSION_NUM);
		createEAttribute(messageIdentifierTypeEClass, MESSAGE_IDENTIFIER_TYPE__RELEASE_NUM);
		createEAttribute(messageIdentifierTypeEClass, MESSAGE_IDENTIFIER_TYPE__CONTROLLING_AGENCY_CODE);
		createEAttribute(messageIdentifierTypeEClass, MESSAGE_IDENTIFIER_TYPE__ASSOCIATION_ASSIGNED_CODE);

		recipientTypeEClass = createEClass(RECIPIENT_TYPE);
		createEAttribute(recipientTypeEClass, RECIPIENT_TYPE__MIXED);
		createEAttribute(recipientTypeEClass, RECIPIENT_TYPE__ID);

		senderTypeEClass = createEClass(SENDER_TYPE);
		createEAttribute(senderTypeEClass, SENDER_TYPE__MIXED);
		createEAttribute(senderTypeEClass, SENDER_TYPE__ID);

		syntaxIdentifierTypeEClass = createEClass(SYNTAX_IDENTIFIER_TYPE);
		createEAttribute(syntaxIdentifierTypeEClass, SYNTAX_IDENTIFIER_TYPE__MIXED);
		createEAttribute(syntaxIdentifierTypeEClass, SYNTAX_IDENTIFIER_TYPE__ID);
		createEAttribute(syntaxIdentifierTypeEClass, SYNTAX_IDENTIFIER_TYPE__VERSION_NUM);

		unbTypeEClass = createEClass(UNB_TYPE);
		createEReference(unbTypeEClass, UNB_TYPE__SYNTAX_IDENTIFIER);
		createEReference(unbTypeEClass, UNB_TYPE__SENDER);
		createEReference(unbTypeEClass, UNB_TYPE__RECIPIENT);
		createEReference(unbTypeEClass, UNB_TYPE__DATE_TIME);
		createEAttribute(unbTypeEClass, UNB_TYPE__CONTROL_REF);

		unEdifactTypeEClass = createEClass(UN_EDIFACT_TYPE);
		createEReference(unEdifactTypeEClass, UN_EDIFACT_TYPE__UNB);
		createEReference(unEdifactTypeEClass, UN_EDIFACT_TYPE__INTERCHANGE_MESSAGE);
		createEReference(unEdifactTypeEClass, UN_EDIFACT_TYPE__UNZ);

		unhTypeEClass = createEClass(UNH_TYPE);
		createEAttribute(unhTypeEClass, UNH_TYPE__MIXED);
		createEAttribute(unhTypeEClass, UNH_TYPE__MESSAGE_REF_NUM);
		createEReference(unhTypeEClass, UNH_TYPE__MESSAGE_IDENTIFIER);

		untTypeEClass = createEClass(UNT_TYPE);
		createEAttribute(untTypeEClass, UNT_TYPE__SEGMENT_COUNT);
		createEAttribute(untTypeEClass, UNT_TYPE__MESSAGE_REF_NUM);

		unzTypeEClass = createEClass(UNZ_TYPE);
		createEAttribute(unzTypeEClass, UNZ_TYPE__CONTROL_COUNT);
		createEAttribute(unzTypeEClass, UNZ_TYPE__CONTROL_REF);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		XMLTypePackage theXMLTypePackage = (XMLTypePackage)EPackage.Registry.INSTANCE.getEPackage(XMLTypePackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes and features; add operations and parameters
		initEClass(dateTimeTypeEClass, DateTimeType.class, "DateTimeType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDateTimeType_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, DateTimeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDateTimeType_Date(), theXMLTypePackage.getString(), "date", null, 1, 1, DateTimeType.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getDateTimeType_Time(), theXMLTypePackage.getString(), "time", null, 1, 1, DateTimeType.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(documentRootEClass, DocumentRoot.class, "DocumentRoot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDocumentRoot_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_XMLNSPrefixMap(), ecorePackage.getEStringToStringMapEntry(), null, "xMLNSPrefixMap", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_XSISchemaLocation(), ecorePackage.getEStringToStringMapEntry(), null, "xSISchemaLocation", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_UnEdifact(), this.getUnEdifactType(), null, "unEdifact", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(interchangeMessageTypeEClass, InterchangeMessageType.class, "InterchangeMessageType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getInterchangeMessageType_UNH(), this.getUNHType(), null, "uNH", null, 1, 1, InterchangeMessageType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getInterchangeMessageType_Any(), ecorePackage.getEFeatureMapEntry(), "any", null, 1, 1, InterchangeMessageType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getInterchangeMessageType_UNT(), this.getUNTType(), null, "uNT", null, 1, 1, InterchangeMessageType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(messageIdentifierTypeEClass, MessageIdentifierType.class, "MessageIdentifierType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getMessageIdentifierType_Id(), theXMLTypePackage.getString(), "id", null, 1, 1, MessageIdentifierType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMessageIdentifierType_VersionNum(), theXMLTypePackage.getString(), "versionNum", null, 1, 1, MessageIdentifierType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMessageIdentifierType_ReleaseNum(), theXMLTypePackage.getString(), "releaseNum", null, 1, 1, MessageIdentifierType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMessageIdentifierType_ControllingAgencyCode(), theXMLTypePackage.getString(), "controllingAgencyCode", null, 1, 1, MessageIdentifierType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMessageIdentifierType_AssociationAssignedCode(), theXMLTypePackage.getString(), "associationAssignedCode", null, 1, 1, MessageIdentifierType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(recipientTypeEClass, RecipientType.class, "RecipientType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getRecipientType_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, RecipientType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRecipientType_Id(), theXMLTypePackage.getString(), "id", null, 1, 1, RecipientType.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(senderTypeEClass, SenderType.class, "SenderType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSenderType_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, SenderType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSenderType_Id(), theXMLTypePackage.getString(), "id", null, 1, 1, SenderType.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(syntaxIdentifierTypeEClass, SyntaxIdentifierType.class, "SyntaxIdentifierType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSyntaxIdentifierType_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, SyntaxIdentifierType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSyntaxIdentifierType_Id(), theXMLTypePackage.getString(), "id", null, 1, 1, SyntaxIdentifierType.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getSyntaxIdentifierType_VersionNum(), theXMLTypePackage.getString(), "versionNum", null, 1, 1, SyntaxIdentifierType.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(unbTypeEClass, UNBType.class, "UNBType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getUNBType_SyntaxIdentifier(), this.getSyntaxIdentifierType(), null, "syntaxIdentifier", null, 1, 1, UNBType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getUNBType_Sender(), this.getSenderType(), null, "sender", null, 1, 1, UNBType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getUNBType_Recipient(), this.getRecipientType(), null, "recipient", null, 1, 1, UNBType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getUNBType_DateTime(), this.getDateTimeType(), null, "dateTime", null, 1, 1, UNBType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getUNBType_ControlRef(), theXMLTypePackage.getString(), "controlRef", null, 1, 1, UNBType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(unEdifactTypeEClass, UnEdifactType.class, "UnEdifactType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getUnEdifactType_UNB(), this.getUNBType(), null, "uNB", null, 1, 1, UnEdifactType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getUnEdifactType_InterchangeMessage(), this.getInterchangeMessageType(), null, "interchangeMessage", null, 1, -1, UnEdifactType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getUnEdifactType_UNZ(), this.getUNZType(), null, "uNZ", null, 1, 1, UnEdifactType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(unhTypeEClass, UNHType.class, "UNHType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getUNHType_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, UNHType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getUNHType_MessageRefNum(), theXMLTypePackage.getString(), "messageRefNum", null, 1, 1, UNHType.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getUNHType_MessageIdentifier(), this.getMessageIdentifierType(), null, "messageIdentifier", null, 1, 1, UNHType.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(untTypeEClass, UNTType.class, "UNTType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getUNTType_SegmentCount(), theXMLTypePackage.getLong(), "segmentCount", null, 1, 1, UNTType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getUNTType_MessageRefNum(), theXMLTypePackage.getString(), "messageRefNum", null, 1, 1, UNTType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(unzTypeEClass, UNZType.class, "UNZType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getUNZType_ControlCount(), theXMLTypePackage.getLong(), "controlCount", null, 1, 1, UNZType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getUNZType_ControlRef(), theXMLTypePackage.getString(), "controlRef", null, 1, 1, UNZType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http:///org/eclipse/emf/ecore/util/ExtendedMetaData
		createExtendedMetaDataAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createExtendedMetaDataAnnotations() {
		String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";		
		addAnnotation
		  (dateTimeTypeEClass, 
		   source, 
		   new String[] {
			 "name", "DateTimeType",
			 "kind", "mixed"
		   });		
		addAnnotation
		  (getDateTimeType_Mixed(), 
		   source, 
		   new String[] {
			 "kind", "elementWildcard",
			 "name", ":mixed"
		   });		
		addAnnotation
		  (getDateTimeType_Date(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "date",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getDateTimeType_Time(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "time",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (documentRootEClass, 
		   source, 
		   new String[] {
			 "name", "",
			 "kind", "mixed"
		   });		
		addAnnotation
		  (getDocumentRoot_Mixed(), 
		   source, 
		   new String[] {
			 "kind", "elementWildcard",
			 "name", ":mixed"
		   });		
		addAnnotation
		  (getDocumentRoot_XMLNSPrefixMap(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "xmlns:prefix"
		   });		
		addAnnotation
		  (getDocumentRoot_XSISchemaLocation(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "xsi:schemaLocation"
		   });		
		addAnnotation
		  (getDocumentRoot_UnEdifact(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "unEdifact",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (interchangeMessageTypeEClass, 
		   source, 
		   new String[] {
			 "name", "interchangeMessageType",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getInterchangeMessageType_UNH(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "UNH",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getInterchangeMessageType_Any(), 
		   source, 
		   new String[] {
			 "kind", "elementWildcard",
			 "wildcards", "##any",
			 "name", ":1",
			 "processing", "lax"
		   });		
		addAnnotation
		  (getInterchangeMessageType_UNT(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "UNT",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (messageIdentifierTypeEClass, 
		   source, 
		   new String[] {
			 "name", "MessageIdentifierType",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getMessageIdentifierType_Id(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "id",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getMessageIdentifierType_VersionNum(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "versionNum",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getMessageIdentifierType_ReleaseNum(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "releaseNum",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getMessageIdentifierType_ControllingAgencyCode(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "controllingAgencyCode",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getMessageIdentifierType_AssociationAssignedCode(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "associationAssignedCode",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (recipientTypeEClass, 
		   source, 
		   new String[] {
			 "name", "RecipientType",
			 "kind", "mixed"
		   });		
		addAnnotation
		  (getRecipientType_Mixed(), 
		   source, 
		   new String[] {
			 "kind", "elementWildcard",
			 "name", ":mixed"
		   });		
		addAnnotation
		  (getRecipientType_Id(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "id",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (senderTypeEClass, 
		   source, 
		   new String[] {
			 "name", "SenderType",
			 "kind", "mixed"
		   });		
		addAnnotation
		  (getSenderType_Mixed(), 
		   source, 
		   new String[] {
			 "kind", "elementWildcard",
			 "name", ":mixed"
		   });		
		addAnnotation
		  (getSenderType_Id(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "id",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (syntaxIdentifierTypeEClass, 
		   source, 
		   new String[] {
			 "name", "SyntaxIdentifierType",
			 "kind", "mixed"
		   });		
		addAnnotation
		  (getSyntaxIdentifierType_Mixed(), 
		   source, 
		   new String[] {
			 "kind", "elementWildcard",
			 "name", ":mixed"
		   });		
		addAnnotation
		  (getSyntaxIdentifierType_Id(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "id",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getSyntaxIdentifierType_VersionNum(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "versionNum",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (unbTypeEClass, 
		   source, 
		   new String[] {
			 "name", "UNBType",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getUNBType_SyntaxIdentifier(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "syntaxIdentifier",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getUNBType_Sender(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "sender",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getUNBType_Recipient(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "recipient",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getUNBType_DateTime(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "dateTime",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getUNBType_ControlRef(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "controlRef",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (unEdifactTypeEClass, 
		   source, 
		   new String[] {
			 "name", "unEdifactType",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getUnEdifactType_UNB(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "UNB",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getUnEdifactType_InterchangeMessage(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "interchangeMessage",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getUnEdifactType_UNZ(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "UNZ",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (unhTypeEClass, 
		   source, 
		   new String[] {
			 "name", "UNHType",
			 "kind", "mixed"
		   });		
		addAnnotation
		  (getUNHType_Mixed(), 
		   source, 
		   new String[] {
			 "kind", "elementWildcard",
			 "name", ":mixed"
		   });		
		addAnnotation
		  (getUNHType_MessageRefNum(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "messageRefNum",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getUNHType_MessageIdentifier(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "messageIdentifier",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (untTypeEClass, 
		   source, 
		   new String[] {
			 "name", "UNTType",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getUNTType_SegmentCount(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "segmentCount",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getUNTType_MessageRefNum(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "messageRefNum",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (unzTypeEClass, 
		   source, 
		   new String[] {
			 "name", "UNZType",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getUNZType_ControlCount(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "controlCount",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getUNZType_ControlRef(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "controlRef",
			 "namespace", "##targetNamespace"
		   });
	}

} //EnvelopePackageImpl
