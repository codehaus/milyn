/*
	Milyn - Copyright (C) 2006 - 2010

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License (version 2.1) as published by the Free Software
	Foundation.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

	See the GNU Lesser General Public License for more details:
	http://www.gnu.org/licenses/lgpl.txt
*/

package org.milyn.edisax.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.archive.Archive;
import org.milyn.archive.ArchiveClassLoader;
import org.milyn.assertion.AssertArgument;
import org.milyn.edisax.EDIConfigurationException;
import org.milyn.edisax.EDIParser;
import org.milyn.edisax.model.EdifactModel;
import org.milyn.edisax.model.internal.DelimiterType;
import org.milyn.edisax.model.internal.Delimiters;
import org.milyn.edisax.model.internal.Description;
import org.milyn.io.StreamUtils;
import org.milyn.resource.URIResourceLocator;
import org.milyn.util.ClassUtil;
import org.xml.sax.SAXException;

/**
 * EDIUtils contain different helper-methods for handling edifact.
 *
 * @author bardl
 */
public class EDIUtils {

    private static Log logger = LogFactory.getLog(EDIUtils.class);
    
    public static final String EDI_MAPPING_MODEL_ZIP_LIST_FILE = "META-INF/services/org/smooks/edi/mapping-model.lst";
    public static final String EDI_MAPPING_MODEL_INTERCHANGE_PROPERTIES_FILE = "META-INF/services/org/smooks/edi/interchange.properties";
    public static final String EDI_MAPPING_MODEL_URN = "META-INF/services/org/smooks/edi/urn";
    /**
     * Most model sets contain a set of common definitions (common types).
     */
    public static final Description MODEL_SET_DEFINITIONS_DESCRIPTION = new Description().setName("__modelset_definitions").setVersion("local");

    /**
     * Splits a String by delimiter as long as delimiter does not follow an escape sequence.
     * The split method follows the same behavior as the method splitPreserveAllTokens(String, String)
     * in {@link org.apache.commons.lang.StringUtils}.
     *
     * @param value the string to split, may be null.
     * @param delimiter the delimiter sequence. A null delimiter splits on whitespace.
     * @param escape the escape sequence. A null escape is allowed,  and result will be consistent with the splitPreserveAllTokens method.
     * @return an array of split edi-sequences, null if null string input.
     */
    public static String[] split(String value, String delimiter, String escape) {

        // A null input string returns null
        if (value == null) {
            return null;
        }

        // Empty input string returns empty array
        if (value.length() == 0) {
            return new String[0];
        }

        // Empty delimiter splits on whitespace.
        if (delimiter == null) {
            delimiter = " ";
        }

        List<CharSequence> charSequences = new ArrayList<CharSequence>();
        readSequenceStructure(value, delimiter, escape, charSequences);

        return putCharacterSequenceIntoResult(charSequences);
    }
    
    public static void loadMappingModels(String mappingModelFiles, Map<Description, EdifactModel> mappingModels, URI baseURI) throws EDIConfigurationException, IOException, SAXException {
		AssertArgument.isNotNullAndNotEmpty(mappingModelFiles, "mappingModelFiles");
		AssertArgument.isNotNull(mappingModels, "mappingModels");
		AssertArgument.isNotNull(baseURI, "baseURI");

		String[] mappingModelFileTokens = mappingModelFiles.split(",");

        for(String mappingModelFile : mappingModelFileTokens) {
            mappingModelFile = mappingModelFile.trim();

            // First try processing based on the file extension
            if(mappingModelFile.endsWith(".xml")) {
                if(loadXMLMappingModel(mappingModelFile, mappingModels, baseURI)) {
                    // Loaded an XML config... on to next config in list...
                    continue;
                }
            } else if(mappingModelFile.endsWith(".zip") || mappingModelFile.endsWith(".jar")) {
                if(loadZippedMappingModels(mappingModelFile, mappingModels, baseURI)) {
                    // Loaded an zipped config... on to next config in list...
                    continue;
                }
            } else if(mappingModelFile.startsWith("urn:")) {
                String urn = mappingModelFile.substring(4);
                List<String> rootMappingModels = getMappingModelList(urn);

                loadMappingModels(mappingModels, baseURI, rootMappingModels);

                continue;
            }

            // The file extension didn't match up with what we expected, so perform a
            // brute force attempt to process the config...
            if(!loadXMLMappingModel(mappingModelFile, mappingModels, baseURI)) {
                if(!loadZippedMappingModels(mappingModelFile, mappingModels, baseURI)) {
                    throw new EDIConfigurationException("Failed to process EDI Mapping Model config file '" + mappingModelFile + "'.  Not a valid EDI Mapping Model configuration.");
                }
            }
        }
    }

    private static boolean loadXMLMappingModel(String mappingModelFile, Map<Description, EdifactModel> mappingModels, URI baseURI) throws EDIConfigurationException {
		try {
			EdifactModel model = EDIParser.parseMappingModel(mappingModelFile, baseURI);
			mappingModels.put(model.getEdimap().getDescription(), model);
			return true;
		} catch (IOException e) {
			return false;
		} catch (SAXException e) {
			logger.debug("Configured mapping model file '" + mappingModelFile + "' is not a valid Mapping Model xml file.");
			return false;
		}
	}

    private static boolean loadZippedMappingModels(String mappingModelFile, Map<Description, EdifactModel> mappingModels, URI baseURI) throws IOException, SAXException, EDIConfigurationException {
		URIResourceLocator locator = new URIResourceLocator();

		locator.setBaseURI(baseURI);

		InputStream rawZipStream = locator.getResource(mappingModelFile);
		if(rawZipStream != null) {
            Archive archive = loadArchive(rawZipStream);

			if(archive != null) {
				List<String> rootMappingModels = getMappingModelList(archive);

				if(rootMappingModels.isEmpty()) {
					logger.debug("Configured mapping model file '" + mappingModelFile + "' is not a valid Mapping Model zip file.  Check that the zip has a valid '" + EDI_MAPPING_MODEL_ZIP_LIST_FILE + "' mapping list file.");
					return false;
				}

				ClassLoader threadCCL = Thread.currentThread().getContextClassLoader();

				try {
					ArchiveClassLoader archiveClassLoader = new ArchiveClassLoader(threadCCL, archive);

					Thread.currentThread().setContextClassLoader(archiveClassLoader);
                    loadMappingModels(mappingModels, baseURI, rootMappingModels);
                } finally {
					Thread.currentThread().setContextClassLoader(threadCCL);
				}

				return true;
			}
		}

		return false;
	}

    private static void loadMappingModels(Map<Description, EdifactModel> mappingModels, URI baseURI, List<String> rootMappingModels) throws IOException, SAXException, EDIConfigurationException {
        for (String rootMappingModel : rootMappingModels) {
            try {
                EdifactModel mappingModel = EDIParser.parseMappingModel(rootMappingModel, baseURI);

                mappingModel.setAssociateModels(mappingModels.values());
                mappingModels.put(mappingModel.getDescription(), mappingModel);
            } catch(Exception e) {
                throw new EDIConfigurationException("Error parsing EDI Mapping Model '" + rootMappingModel + "'.", e);
            }
        }
    }

    private static List<String> getMappingModelList(Archive archive) throws IOException {
		byte[] zipEntryBytes = archive.getEntries().get(EDI_MAPPING_MODEL_ZIP_LIST_FILE);

		if(zipEntryBytes != null) {
            return getMappingModelList(new ByteArrayInputStream(zipEntryBytes));
        }

		return Collections.EMPTY_LIST;
	}

    private static List<String> getMappingModelList(String urn) throws IOException, EDIConfigurationException {
        InputStream mappingModelListStream = getMappingModelConfigStream(urn, EDI_MAPPING_MODEL_ZIP_LIST_FILE);

        if(mappingModelListStream == null) {
            throw new EDIConfigurationException("Failed to locate jar file for EDI Mapping Model URN '" + urn + "'.  Jar must be available on classpath.");
        }

        return getMappingModelList(mappingModelListStream);
    }

    public static Properties getInterchangeProperties(String ediMappingModel) throws IOException {
        InputStream interchangePropertiesStream = null;

        if(ediMappingModel.startsWith("urn:")) {
            interchangePropertiesStream = getMappingModelConfigStream(ediMappingModel, EDI_MAPPING_MODEL_INTERCHANGE_PROPERTIES_FILE);

            if(interchangePropertiesStream == null) {
                throw new EDIConfigurationException("Failed to locate jar file for EDI Mapping Model URN '" + ediMappingModel + "'.  Jar must be available on classpath.");
            }
        } else if(ediMappingModel.endsWith(".jar") || ediMappingModel.endsWith(".zip")) {
            URIResourceLocator locator = new URIResourceLocator();

            InputStream rawZipStream = locator.getResource(ediMappingModel);
            if(rawZipStream != null) {
                Archive archive = loadArchive(rawZipStream);
                if(archive != null) {
                    byte[] bytes = archive.getEntries().get(EDI_MAPPING_MODEL_INTERCHANGE_PROPERTIES_FILE);
                    if(bytes != null) {
                        interchangePropertiesStream = new ByteArrayInputStream(bytes);
                    }
                }
            }
        }

        if(interchangePropertiesStream != null) {
            Properties properties = new Properties();
            try {
                properties.load(interchangePropertiesStream);
                return properties;
            } finally {
                interchangePropertiesStream.close();
            }
        }

        return null;
    }

    public static String concatAndTruncate(List<String> nodeTokens, DelimiterType outerDelimiterType, Delimiters delimiters) {
        if(nodeTokens.isEmpty()) {
            return "";
        }

        for(int i = nodeTokens.size() - 1; i >= 0; i--) {
            if(!delimiters.removeableNodeToken(nodeTokens.get(i), outerDelimiterType)) {
                break;
            }
            nodeTokens.remove(i);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for(String nodeToken : nodeTokens) {
            stringBuilder.append(nodeToken);
        }
        return stringBuilder.toString();
    }

    private static InputStream getMappingModelConfigStream(String urn, String fileName) throws IOException, EDIConfigurationException {
        List<URL> urnFiles = ClassUtil.getResources(EDI_MAPPING_MODEL_URN, EDIUtils.class);

        if(urn.startsWith("urn:")) {
            urn = urn.substring(4);
        }

        for(URL urnFile : urnFiles) {
            InputStream urnStream = urnFile.openStream();
            try {
                String archiveURN = StreamUtils.readStreamAsString(urnStream);
                if(archiveURN.equals(urn)) {
                    String urnFileString = urnFile.toString();
                    String modelConfigFile = urnFileString.substring(0, urnFileString.length() - EDI_MAPPING_MODEL_URN.length()) + fileName;

                    List<URL> urlList = ClassUtil.getResources(fileName, EDIUtils.class);

                    for(URL url : urlList) {
                        if(url.toString().equals(modelConfigFile)) {
                            return url.openStream();
                        }
                    }
                }
            } finally {
                urnStream.close();
            }
        }

        throw new EDIConfigurationException("Failed to locate jar file for EDI Mapping Model URN '" + urn + "'.  Jar must be available on classpath.");
    }

    private static List<String> getMappingModelList(InputStream modelListStream) throws IOException {
        List<String> rootMappingModels = new ArrayList<String>();

        try {
            BufferedReader lineReader = new BufferedReader(new InputStreamReader(modelListStream, "UTF-8"));

            String line = lineReader.readLine();
            while(line != null) {
                line = line.trim();
                if(line.length() > 0 && !line.startsWith("#")) {
                    rootMappingModels.add(line);
                }
                line = lineReader.readLine();
            }

        } finally {
            modelListStream.close();
        }

        return rootMappingModels;
    }

    private static Archive loadArchive(InputStream rawStream) {
        try {
            return new Archive(new ZipInputStream(rawStream));
		} catch(Exception e) {
			// Assume it's not a Zip file.  Just return null...
			return null;
		}
	}

    /**
     * Loops through all CharSequences and decides whether to write out value or split.
     * @param charSequences a list of CharSequence
     * @return a String[] containing the split values.
     */
    private static String[] putCharacterSequenceIntoResult(List<CharSequence> charSequences) {
        List<String> result = new ArrayList<String>();
        StringBuilder stringBuilder = new StringBuilder();
        boolean escapeNextSequence = false;
        boolean delimiterLastSequence = false;
        CharSequence previousSequence = null;
        for (CharSequence sequence : charSequences) {
            delimiterLastSequence = false;

            if (previousSequence != null && (sequence.getType() != CharSequenceTypeEnum.DELIMITER) && escapeNextSequence) {
                stringBuilder.append(previousSequence.getValue());
            }
            previousSequence = sequence;

            if (sequence.getType() == CharSequenceTypeEnum.PLAIN) {
                stringBuilder.append(sequence.getValue());
            } else if (sequence.getType() == CharSequenceTypeEnum.DELIMITER) {
                if (escapeNextSequence) {
                    stringBuilder.append(sequence.getValue());
                } else {
                    result.add(stringBuilder.toString());
                    stringBuilder = new StringBuilder();
                    delimiterLastSequence = true;
                }
            } else if (sequence.getType() == CharSequenceTypeEnum.ESCAPE) {
                if (escapeNextSequence) {
                    stringBuilder.append(sequence.getValue());
                } else {
                    escapeNextSequence = true;
                    continue;
                }
            }

            escapeNextSequence = false;
        }

        if (stringBuilder.length() > 0 || delimiterLastSequence) {
            result.add(stringBuilder.toString());
        }

        return result.toArray(new String[result.size()]);
    }

    /**
     * Reads value and put the different parts into a list of CharSequence for easier handling of escape- and
     * delimiter-sequences when splitting value.
     * @param value the string to split
     * @param delimiter the characters defining the delimiter
     * @param escape the characters defining the escape
     * @param result a lis CharSequence.
     */
    private static void readSequenceStructure(String value, String delimiter, String escape, List<CharSequence> result) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int j = 0; j < value.length(); j++) {
            char theChar = value.charAt(j);
            stringBuilder.append(theChar);

            int escapeLength = escape == null ? 0 : escape.length();
            int delimiterLength = delimiter == null ? 0 : delimiter.length();
            int readLength = stringBuilder.length();

            if (readLength >= delimiterLength) {
                if (stringBuilder.substring(readLength-delimiterLength, readLength).equals(delimiter)) {
                    stringBuilder.replace(readLength-delimiterLength, readLength, "");
                    if (stringBuilder.length() > 0) {
                        result.add(new CharSequence(stringBuilder.toString(), CharSequenceTypeEnum.PLAIN));
                        stringBuilder = new StringBuilder();
                    }
                    result.add(new CharSequence(delimiter, CharSequenceTypeEnum.DELIMITER));
                    continue;
                }
            }

            if (readLength >= escapeLength) {
                if (stringBuilder.substring(readLength-escapeLength, readLength).equals(escape)) {
                    stringBuilder.replace(readLength-escapeLength, readLength, "");
                    if (stringBuilder.length() > 0) {
                        result.add(new CharSequence(stringBuilder.toString(), CharSequenceTypeEnum.PLAIN));
                        stringBuilder = new StringBuilder();
                    }
                    result.add(new CharSequence(escape, CharSequenceTypeEnum.ESCAPE));
                    continue;
                }
            }
        }

        if (stringBuilder.length() > 0) {
            result.add(new CharSequence(stringBuilder.toString(), CharSequenceTypeEnum.PLAIN));
        }
    }

    private static class CharSequence {
        String value;
        CharSequenceTypeEnum type;

        public CharSequence(String value, CharSequenceTypeEnum type) {
            this.value = value;
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public CharSequenceTypeEnum getType() {
            return type;
        }
    }

    private enum CharSequenceTypeEnum {
        PLAIN,
        ESCAPE,
        DELIMITER
    }
}
