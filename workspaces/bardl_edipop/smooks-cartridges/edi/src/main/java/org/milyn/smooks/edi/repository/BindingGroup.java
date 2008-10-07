package org.milyn.smooks.edi.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * BindingGroup is used to group segments together. A BindingGroup is defined by the resource-config
 * in the configuration file. All bindings within the same resource-config is viewed as a BindingGroup.
 * @see org.milyn.smooks.edi.repository.BindingGroupRepository
 * @author bardl
 */
public class BindingGroup {
    private int nrOfCreatedBatches;
    private List<Map.Entry<String, String>> mappingValues;

    public BindingGroup() {
        this.nrOfCreatedBatches = 0;
        this.mappingValues = new ArrayList<Map.Entry<String,String>>();
    }

    public void addMappingValue(String ediPath, String ediValue) {
        Map.Entry<String,String> entry = new EDIEntry<String,String>(ediPath, ediValue);
        mappingValues.add(entry);
    }

    public List<Map.Entry<String, String>> getMappingValues() {
        return mappingValues;
    }

    public int getNrOfCreatedBatches() {
        return nrOfCreatedBatches;
    }

    public void setNrOfCreatedBatches(int nrOfCreatedBatches) {
        this.nrOfCreatedBatches = nrOfCreatedBatches;
    }

    private class EDIEntry<K,V> implements Map.Entry<K,V> {
        private K key;
        private V value;

        public EDIEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            this.value = value;
            return value;
        }
    }
}
