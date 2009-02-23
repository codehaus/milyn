package org.milyn.edisax;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.resource.URIResourceLocator;
import org.milyn.schema.edi_message_mapping_1_0.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EdifactModel contains all logic for unmarshalling and handling imports for the
 * edi-message-mapping model.
 * @author bardl
 */
public class EdifactModel {
    private static Log LOG = LogFactory.getLog(EdifactModel.class);

    private org.milyn.schema.edi_message_mapping_1_0.Edimap edimap;

    private static JAXBContext JAXB_EDIMAP;
    public static final String NEW_SEGMENT_SUFFIX = "[]";

    static {
        try {
            JAXB_EDIMAP = JAXBContext.newInstance(org.milyn.schema.edi_message_mapping_1_0.Edimap.class);
        } catch (JAXBException e) {
            LOG.error("Could not create new instance of JAXBContext.", e);
        }
    }

    /**
     * Returns the edimap containing the parser logic.
     * @return edi-message-mapping.
     */
    public Edimap getEdimap() {
        return edimap;
    }

    /**
     * Sets the edimap containing the parser logic.
     * @param edimap the edi-message-mapping
     */
    public void setEdimap(Edimap edimap) {
        this.edimap = edimap;
    }

    /**
     * Returns the delimiters used in edifact format.
     * @return delimiters.
     */
    public Delimiters getDelimiters() {
        return edimap.getDelimiters();
    }

    /**
     * Parse the edifact edimap specified in the edi-message-mapping.
     * @param inputStream the edi-message-mapping.
     * @throws EDIParseException is thrown when EdifactModel is unable to initialize edimap.
     */
    public void parseSequence(InputStream inputStream) throws EDIParseException {

        //To prevent circular dependency the name/url of all imported urls are stored in a dependency tree.
        //If a name/url already exists in a parent node, we have a circular dependency.
        DependencyTree<String> tree = new DependencyTree<String>();

        edimap = unmarshallEdimap(inputStream);
        importFiles(tree.getRoot(), edimap, tree);
        
    }

    /**
     * Insert value into {@link Edimap}.
     * @param ediPath The path to add the value. The path can contain either '.' or whitespace. 
     * @param value the value to insert.
     * @throws EDIParseException is thrown when trying to add max allowed occurrence of {@link Segment}.
     */
    public void insertValue(String ediPath, String value) throws EDIParseException {
        String[] ediPaths = ediPath.split("[\\. ]");

        int pathIndex = 0;
        int segIndex = 0;
        ValueNode valueNode = null;
        ValueNode prevNode = null;
        for (Segment segment : edimap.getSegments().getSegment()) {
            if (equals(segment.getSegcode(),(ediPaths[pathIndex]))) {
                if (ediPaths[pathIndex].contains(NEW_SEGMENT_SUFFIX)) {
                    insertNewSegmentIntoEdimap(edimap.getSegments().getSegment(), cloneSegment(segment), segIndex+1);
                }
                valueNode = findSegment(segment, ediPaths, pathIndex+1);
            }
            if  (valueNode != null) {
                prevNode = valueNode;
            }
            if (valueNode == null && prevNode != null) {
                break;
            }
            segIndex++;
        }

        if (valueNode == null) {            
            throw new EDIParseException(edimap, "Could not populate ediModel with invalid ediPath [" + ediPath + "].");
        }

        valueNode.setValue(value);
        
    }

    /**
     * Find {@link ValueNode} in {@link Segment}.
     * @param segment the {@link Segment} to search in.
     * @param ediPaths the path.
     * @param pathIndex current location in ediPaths.
     * @return The {@link ValueNode} found by ediPath.
     * @throws EDIParseException is thrown when trying to add max allowed occurrence of {@link Segment}.
     */
    private ValueNode findSegment(Segment segment, String[] ediPaths, int pathIndex) throws EDIParseException {
        ValueNode result;

        result = findFields(segment, ediPaths, pathIndex);
        if (result != null) {
            return result;
        }

        int segIndex = 0;
        ValueNode prevNode = null;
        for (Segment s : segment.getSegment()) {
            if ( equals(s.getSegcode(),(ediPaths[pathIndex])) ) {
                if (ediPaths[pathIndex].contains(NEW_SEGMENT_SUFFIX)) {
                    insertNewSegmentIntoEdimap(segment.getSegment(), cloneSegment(s), segIndex+1);
                    return new Field(); //Return a dummy-node.
                }
                result = findSegment(s, ediPaths, pathIndex+1);
                if  (result != null) {
                    prevNode = result;
                }
                if (result == null && prevNode != null) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Insert a new {@link Segment} into ediMap, as long as max number of allowed segments are not exceeded. The new
     * {@link Segment} are inserted after last macthing {@link Segment}. 
     * @param segments the list to add the new {@link Segment}.
     * @param segment the new {@link Segment} to add.
     * @param segIndex the index of first matching {@link Segment}.
     * @throws EDIParseException is thrown when trying to add a new {@link Segment} exceeding max number of allowed Segments.
     */
    private void insertNewSegmentIntoEdimap(List<Segment> segments, Segment segment, int segIndex) throws EDIParseException {
        int nrOfSegments = 1;
        int maxOccurs = segment.getMaxOccurs() == -1 ? Integer.MAX_VALUE : segment.getMaxOccurs();
        for (int i = segIndex; i < segments.size(); i++) {
            if (i+1 == segments.size() || !segments.get(i+1).getSegcode().equalsIgnoreCase(segment.getSegcode())) {

                if (nrOfSegments+1 >  maxOccurs) {
                    throw new EDIParseException("Could not create segment [" + segment.getSegcode() + "]. Number of segments [" + (nrOfSegments+1) + "] exceed max number of allowed segments [" + segment.getMaxOccurs() + "]");
                }

                segments.add(i+1, segment);
                return;
            }
            nrOfSegments++;
        }
    }

    /**
     * Clones a {@link Segment}.
     * @param segment to clone.
     * @return a new {@link Segment}.
     */
    private Segment cloneSegment(Segment segment) {
        Segment newSegment = new Segment();
        newSegment.setMaxOccurs(segment.getMaxOccurs());
        newSegment.setMinOccurs(segment.getMinOccurs());
        newSegment.setSegcode(segment.getSegcode());
        newSegment.setSegref(segment.getSegref());
        newSegment.setTruncatable(segment.isTruncatable());

        for (Field field : segment.getField()) {
            newSegment.getField().add(cloneField(field));
        }

        for (Segment s : segment.getSegment()) {
            newSegment.getSegment().add(cloneSegment(s));
        }

        return newSegment;
    }

    /**
     * Clones a {@link Field}.
     * @param field to clone.
     * @return a new {@link Field}.
     */
    private Field cloneField(Field field) {
        Field newField = new Field();
        newField.setRequired(field.isRequired());
        newField.setTruncatable(field.isTruncatable());
        newField.setXmltag(field.getXmltag());

        for (Component component : field.getComponent()) {
            newField.getComponent().add(cloneComponent(component));
        }
        return newField;
    }

    /**
     * Clones a {@link Component}.
     * @param component to clone.
     * @return a new {@link Component}.
     */
    private Component cloneComponent(Component component) {
        Component newComponent = new Component();
        newComponent.setRequired(component.isRequired());
        newComponent.setTruncatable(component.isTruncatable());
        newComponent.setXmltag(component.getXmltag());

        for (SubComponent subcomponent : component.getSubComponent()) {
            newComponent.getSubComponent().add(cloneSubComponent(subcomponent));
        }
        return newComponent;
    }

    /**
     * Clones a {@link SubComponent}.
     * @param subcomponent to clone.
     * @return a new {@link SubComponent}.
     */
    private SubComponent cloneSubComponent(SubComponent subcomponent) {
        SubComponent newSubComponent = new SubComponent();
        newSubComponent.setRequired(subcomponent.isRequired());
        newSubComponent.setXmltag(subcomponent.getXmltag());
        return newSubComponent;
    }

    /**
     * Find {@link ValueNode} in {@link Field} list.
     * @param segment the {@link Segment} containing list of {@link Field}s.
     * @param ediPaths the path.
     * @param pathIndex current location in ediPaths.
     * @return The {@link ValueNode} found by ediPath.
     */
    private ValueNode findFields(Segment segment, String[] ediPaths, int pathIndex) {
        ValueNode result;

        if (ediPaths[pathIndex].contains(NEW_SEGMENT_SUFFIX)) {
            return null;
        }

        for (Field field : segment.getField()) {
            if (field.getXmltag().equalsIgnoreCase(ediPaths[pathIndex])) {
                result = findComponents(field, ediPaths, pathIndex+1);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * Find {@link ValueNode} in {@link Field}.
     * @param field the {@link Field} containing list of {@link Component}s.
     * @param ediPaths the path.
     * @param pathIndex current location in ediPaths.
     * @return The {@link ValueNode} found by ediPath.
     */
    private ValueNode findComponents(Field field, String[] ediPaths, int pathIndex) {
        ValueNode result;

        if (pathIndex == ediPaths.length) {
            return field;
        }

        for (Component component : field.getComponent()) {
            if (component.getXmltag().equalsIgnoreCase(ediPaths[pathIndex])) {
                result = findSubComponents(component, ediPaths, pathIndex+1);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * Find {@link ValueNode} in {@link Component}.
     * @param component the {@link Component} containing list of {@link SubComponent}s.
     * @param ediPaths the path.
     * @param pathIndex current location in ediPaths.
     * @return The {@link ValueNode} found by ediPath.
     */
    private ValueNode findSubComponents(Component component, String[] ediPaths, int pathIndex) {

        if (pathIndex == ediPaths.length) {
            return component;
        }

        for (SubComponent subcomponent : component.getSubComponent()) {
            if (subcomponent.getXmltag().equalsIgnoreCase(ediPaths[pathIndex])) {
                pathIndex++;
                if (pathIndex != ediPaths.length) {
                    //throw new EDIParseException(edimap, "Could not populate ediModel with ediPath [" + ediPaths + "]. ");
                    return null;
                } else {
                    return subcomponent;
                }
            }
        }
        return null;
    }

    /**
     * Compare two segCodes, taking into account the possibility of [] to occurr in segCode.
     * @param segCode1 a segCode to compare
     * @param segCode2 a segCode to compare
     * @return true if segCode are equal, otherwise false.
     */
    private boolean equals(String segCode1, String segCode2) {
        segCode1 = segCode1.replace(NEW_SEGMENT_SUFFIX, "");
        segCode2 = segCode2.replace(NEW_SEGMENT_SUFFIX, "");
        return segCode1.equalsIgnoreCase(segCode2);
    }

    /**
     * Write Edimap to {@link Result}.
     * @param encoding the encoding.
     * @param result the {@link Result} to write to.
     * @throws IOException is thrown when Result is not a StreamResult.   
     * @throws EDIParseException is thrown when required Segments, Fields, Components or SubComponents are missing in {@link Edimap}.
     */
    public void write(String encoding, Result result) throws IOException, EDIParseException {
        EDIModelWriter.writeEDIModel(edimap, result, encoding);
    }

    /**
     * Handle all imports for the specified edimap. The parent Node is used by the
     * DependencyTree tree to keep track of previous imports for preventing cyclic dependency.
     * @param parent The node representing the importing file.
     * @param edimap The importing edimap.
     * @param tree The DependencyTree for preventing cyclic dependency in import.
     * @throws EDIParseException Thrown when a cyclic dependency is detected.
     */
    private void importFiles(Node<String> parent, Edimap edimap, DependencyTree<String> tree) throws EDIParseException {
        Edimap importedEdimap;
        Node<String> child, conflictNode;
        for (Import imp : edimap.getImport()) {
            child = new Node<String>(imp.getName());
            conflictNode = tree.add(parent, child);
            if ( conflictNode != null ) {
                throw new EDIParseException(edimap, "Circular dependency encountered in edi-message-mapping with imported files [" + imp.getName() + "] and [" + conflictNode.getValue() + "]");
            }
            importedEdimap = unmarshallEdimap(findUrl(imp.getName()));
            importFiles(child, importedEdimap, tree);
            Map<String, Segment> importedSegments = createImportMap(importedEdimap);

            for (Segment segment : edimap.getSegments().getSegment()) {
                applyImportOnSegment(segment, imp, importedSegments);
            }
        }
    }

    /**
     * Inserts data from imported segment into the importing segment. Continues through all
     * the child segments of the importing segment.
     * @param segment the importing segment.
     * @param imp import information like url and namespace.
     * @param importedSegments the imported segment.
     * @throws EDIParseException Thrown when a segref attribute in importing segment contains
     * a value not located in the imported segment but with the namespace referencing the imported file.
     */
    private void applyImportOnSegment(Segment segment, Import imp, Map<String, Segment> importedSegments) throws EDIParseException {
        if (segment.getSegref() != null && segment.getSegref().startsWith(imp.getNamespace()+":")) {
            String key = segment.getSegref().substring(segment.getSegref().indexOf(':') + 1);
            Segment importedSegment = importedSegments.get(key);

            if (importedSegment == null) {
                throw new EDIParseException(edimap, "Referenced segment [" + key + "] does not exist in imported edi-message-mapping [" + imp.getName() + "]");
            }
            insertImportedSegmentInfo(segment, importedSegment, imp.getTruncatableFields(), imp.getTruncatableComponents());
        }

        for (Segment seg : segment.getSegment()) {
            applyImportOnSegment(seg, imp, importedSegments);
        }
    }

    /**
     * Inserts fields and segments from the imported segment into the importing segment. Also
     * overrides the truncatable attributes in Fields and Components of the imported file if
     * values are set to true or false in truncatableFields or truncatableComponents.
     * @param segment the importing segment.
     * @param importedSegment the imported segment.
     * @param truncatableFields a global attribute for overriding the truncatable attribute in imported segment.
     * @param truncatableComponents a global attribute for overriding the truncatable attribute in imported segment.
     */
    private void insertImportedSegmentInfo(Segment segment, Segment importedSegment, String truncatableFields, String truncatableComponents) {
        //Overwrite all existing fields in segment, but add additional segments to existing segments.
        segment.getField().clear();
        segment.getField().addAll(importedSegment.getField());                
        segment.getSegment().addAll(0, segment.getSegment());

        //If global truncatable attributes are set in importing mapping, then
        //override the attributes in the imported files.
        if (truncatableFields != null || truncatableComponents != null) {
            for ( Field field : segment.getField()) {
                field.setTruncatable(isTruncatable(truncatableFields, field.isTruncatable()));
                if ( truncatableComponents != null ) {
                    for (Component component : field.getComponent()) {
                        component.setTruncatable(isTruncatable(truncatableComponents, component.isTruncatable()));
                    }
                }
            }
        }        
    }

    /**
     * Creates a Map given an Edimap. All segments in edimap are stored as values in the Map
     * with the corresponding segcode as key.
     * @param edimap the edimap containing segments to be inserted into Map.
     * @return Map containing all segment in edimap.
     */
    private Map<String, Segment> createImportMap(Edimap edimap) {
        HashMap<String, Segment> result = new HashMap<String, Segment>();
        for (Segment segment : edimap.getSegments().getSegment()) {
            result.put(segment.getSegcode(), segment);
        }
        return result;
    }

    /**
     * Unmarshalls an Edimap in the form of an inputStream.
     * @param inputStream the edimap.
     * @return the unmarshalled edimap.
     * @throws EDIParseException Thrown when jaxb is unable to unmarshall the InputStream into Edimap.
     */
    private Edimap unmarshallEdimap(InputStream inputStream) throws EDIParseException {
        Edimap edimap;

        try {
            Unmarshaller _unmarshaller = JAXB_EDIMAP.createUnmarshaller();
            edimap = (Edimap)_unmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            throw new EDIParseException( e.getMessage(), e);
        }

        return edimap;
    }

    /**
     * Returns the InputStream of the specified url.
     * @param url the url to locate.
     * @return InputStream of the specified url.
     * @throws EDIParseException Thrown when unable to locate the specified url.
     */
    private InputStream findUrl(String url) throws EDIParseException {
        InputStream inputStream;

        if (url == null || url.equals("")) {
            return null;
        }

        //Try to locate definition from URIResourceLocator.
        try {
            inputStream = new URIResourceLocator().getResource(url);
        } catch (IOException e) {
            throw new EDIParseException(edimap, "Unable to locate resource [" + url + "]");
        }

        return inputStream;
    }

    /**
     * Returns truncatable attributes specified in import element in the importing edi-message-mapping
     * if it exists. Otherwise it sets value of the truncatable attribute found the imported segment.
     * @param truncatableImporting truncatable value found in import element in importing edi-message-mapping.
     * @param truncatableImported truncatable value found in imported segment.
     * @return truncatable from importing edi-message-mapping if it exists, otherwise return value from imported segment.
     */
    private Boolean isTruncatable(String truncatableImporting, boolean truncatableImported) {
        Boolean result = truncatableImported;
        if (truncatableImporting != null) {
            result = Boolean.parseBoolean( truncatableImporting );
        }
        return result;
    }

    
    /************************************************************************
     * Private classes  used for locating and preventing cyclic dependency. *
     ************************************************************************/

    private class DependencyTree<T> {
        Node<T> root;

        public DependencyTree() {
            root = new Node<T>(null);
        }

        public Node<T> getRoot() {
            return root;
        }

        /**
         * Add child to parent Node if value does not exist in direct path from child to root
         * node, i.e. in any ancestralnode.
         * @param parent parent node
         * @param child the child node to add.
         * @return null if the value in child is not in confilct with value in any ancestor Node, otherwise return the conflicting ancestor Node. 
         */
        public Node<T> add(Node<T> parent, Node<T> child){
            Node<T> node = parent;
            while (node != null ) {
                if (node != root && node.getValue().equals(child.getValue())) {
                    return node;
                }
                node = node.getParent();
            }
            child.setParent(parent);
            parent.getChildren().add(child);
            return null;
        }

        public List<T> getUniqueValues() {
            List<T> result = new ArrayList<T>();
            return getUniqueValuesForNode(root, result);
        }

        private List<T> getUniqueValuesForNode(Node<T> node, List<T> list) {
            if ( node.getValue() != null && !list.contains( node.getValue() ) ) {
                list.add(node.getValue());
            }
            return list;
        }
    }

    private class Node<T> {
        private T value;
        private Node<T> parent;
        private List<Node<T>> children;

        public Node(T value) {
            children = new ArrayList<Node<T>>();
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        public Node<T> getParent() {
            return parent;
        }

        public void setParent(Node<T> parent) {
            this.parent = parent;
        }

        public List<Node<T>> getChildren() {
            return children;
        }
    }
}


