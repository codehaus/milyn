package org.milyn.edisax;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.schema.edi_message_mapping_1_0.*;
import org.milyn.schema.edi_message_mapping_1_0.Edimap;
import org.milyn.resource.URIResourceLocator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.util.*;
import java.io.InputStream;
import java.io.IOException;

/**
 * EdifactModel contains all logic for unmarshalling and handling imports for the
 * edi-message-mapping model.
 */
public class EdifactModel {
    private static Log LOG = LogFactory.getLog(EdifactModel.class);

    private org.milyn.schema.edi_message_mapping_1_0.Edimap edimap;

    private static JAXBContext JAXB_EDIMAP;

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


