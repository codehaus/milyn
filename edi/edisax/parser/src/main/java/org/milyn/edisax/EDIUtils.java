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

package org.milyn.edisax;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.milyn.archive.Archive;
import org.milyn.archive.ArchiveClassLoader;
import org.milyn.assertion.AssertArgument;
import org.milyn.edisax.model.EdifactModel;
import org.milyn.edisax.model.internal.Description;
import org.milyn.resource.URIResourceLocator;
import org.xml.sax.SAXException;

/**
 * EDIUtils contain different helper-methods for handling edifact.
 *
 * @author bardl
 */
public class EDIUtils {

    private static Log logger = LogFactory.getLog(EDIUtils.class);
    
    public static final String EDI_MAPPING_MODEL_ZIP_LIST_FILE = "META-INF/services/org/smooks/edi/mapping-models.lst";

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

        List<String> tokens = new ArrayList<String>();

        int escapeIndex = 0;
        int delimiterIndex = 0;
        boolean foundEscape = false;
        StringBuilder escapeContent = new StringBuilder();
        StringBuilder delimiterContent = new StringBuilder();
        StringBuilder token = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char tmp = value.charAt(i);

            // If character equals current escape character or start of a new escape sequence.
            if (escape != null && ( tmp == escape.charAt(0) || tmp == escape.charAt(escapeIndex) )) {

                // If starting from the beginning of a new escape sequence.
                if (tmp == escape.charAt(0)) {
                    token.append(escapeContent);
                    escapeIndex = 0;
                    escapeContent = new StringBuilder();
                }

                // If we haven't found the whole escape seguence.
                if ( escapeIndex < escape.length() -1 ) {
                    escapeIndex++;
                    if (foundEscape) {
                        token.append(escapeContent);
                        escapeContent = new StringBuilder();
                    }
                    foundEscape = false;

                    // If we have found the whole escape seguence.
                } else {
                    if (foundEscape) {
                        token.append(escapeContent);
                    }
                    foundEscape = true;
                    escapeIndex = 0;
                }

                escapeContent.append(tmp);

                // If character equals current delimiter or start of a new delimiter sequence.
            } else if (tmp == delimiter.charAt(delimiterIndex) || tmp == delimiter.charAt(0)) {

                // If starting from the beginning of a new delimiter sequence.
                if ( tmp == delimiter.charAt(0) ) {
                    token.append(delimiterContent);
                    delimiterIndex = 0;
                    delimiterContent = new StringBuilder();
                }

                delimiterContent.append(tmp);
                // If we haven't found the whole delimiter sequence.
                if ( delimiterIndex < delimiter.length() -1 ) {
                    delimiterIndex++;
                    // If we have found the whole delimiter sequence.
                } else {
                    if (foundEscape) {
                        token.append(delimiterContent);
                        escapeContent = new StringBuilder();
                    } else {
                        tokens.add(token.toString());
                        token = new StringBuilder();
                    }
                    delimiterIndex = 0;
                    delimiterContent = new StringBuilder();
                }
                // If Character doesn't match current delimiter or escape character.
            } else {
                // Append and reset escape sequence if it exists.
                token.append(escapeContent);
                foundEscape = false;
                escapeContent = new StringBuilder();

                // Append and reset delimiter sequence if it exists.
                token.append(delimiterContent);
                delimiterContent = new StringBuilder();

                // Append the current character.
                token.append(value.charAt(i));
            }
        }

        tokens.add(token.toString());

        return tokens.toArray(new String[tokens.size()]);
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
					for (String rootMappingModel : rootMappingModels) {
						EdifactModel mappingModel = EDIParser.parseMappingModel(rootMappingModel, baseURI);
						mappingModels.put(mappingModel.getEdimap().getDescription(), mappingModel);
					}
				} finally {
					Thread.currentThread().setContextClassLoader(threadCCL);
				}
				
				return true;
			}		
		}
		
		return false;
	}

	private static List<String> getMappingModelList(Archive archive) throws IOException {
		List<String> rootMappingModels = new ArrayList<String>();
		byte[] zipEntryBytes = archive.getEntries().get(EDI_MAPPING_MODEL_ZIP_LIST_FILE);
		
		if(zipEntryBytes != null) {
			ByteArrayInputStream entryStream = new ByteArrayInputStream(zipEntryBytes);
			
			try {
				BufferedReader lineReader = new BufferedReader(new InputStreamReader(entryStream, "UTF-8"));
				
				String line = lineReader.readLine();
				while(line != null) {
					line = line.trim();
					if(line.length() > 0 && !line.startsWith("#")) {
						rootMappingModels.add(line);
					}					
					line = lineReader.readLine();
				}
			} finally {
				entryStream.close();
			}
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
}
