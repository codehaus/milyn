/*
 Milyn - Copyright (C) 2006

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

package org.milyn.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;

import org.milyn.util.ClassUtil;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * DTD resolver for local DTD's.
 * 
 * @author tfennelly
 */
public class LocalEntityResolver implements EntityResolver {

	/**
	 * Local DTD folder.
	 */
	private File localDTDFolder = null;

	/**
	 * DTD package for locating DTDs in the classpath.
	 */
	private static final String DTD_CP_PACKAGE = "/org/milyn/dtd/";

	/**
	 * DTD entity lookup table. <p/> Contains preread DTD entity byte arrays.
	 */
	private static Hashtable dtdEntities = new Hashtable();

    /**
     * Document DTD.  This is a bit of a hack.  There's a way of getting the DOM
     * parser to populate the DocumentType.
     */
    private String docDTD;

    /**
	 * Public default Constructor
	 */
	public LocalEntityResolver() {
	}

	/**
	 * Public default Constructor <p/> This constructor allows specification of
	 * a local file system folder from which DTDs can be loaded.
	 * 
	 * @param localDTDFolder
	 *            Local DTD folder.
	 */
	public LocalEntityResolver(File localDTDFolder) {
		if (localDTDFolder == null) {
			throw new IllegalStateException(
					"Cannot resolve local DTD entities.  Local DTD folder arg 'null'.");
		}
		if (!localDTDFolder.exists()) {
			throw new IllegalStateException(
					"Cannot resolve local DTD entities.  Local DTD folder not present: ["
							+ localDTDFolder.getAbsolutePath() + "].");
		}
		this.localDTDFolder = localDTDFolder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String,
	 *      java.lang.String)
	 */
	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		byte cachedBytes[] = (byte[]) LocalEntityResolver.dtdEntities
				.get(systemId);
		InputSource entityInputSource = null;

        if(systemId.endsWith(".dtd")) {
            docDTD = systemId;
        }

        if (cachedBytes == null) {
			URL systemIdUrl = new URL(systemId);
			String entityPath = systemIdUrl.getHost() + systemIdUrl.getFile();
			String entityName = (new File(entityPath)).getName();
			File fileSysEntity = null;
			InputStream entityStream = null;

			// First try locate the file in the DTD folder based on the files
			// full path.
			// If this fails, try locate it in the root of the DTD folder
			// directly. If
			// this too fails try the classpath - specifically the org.milyn.dtd
			// package.
			if (localDTDFolder != null) {
				fileSysEntity = new File(localDTDFolder, entityPath);
				if (!fileSysEntity.exists()) {
					fileSysEntity = new File(localDTDFolder, entityName);
				}
			}
			if (localDTDFolder != null && fileSysEntity.exists()) {
				entityStream = new FileInputStream(fileSysEntity);
			} else {
				entityStream = ClassUtil.getResourceAsStream(
						LocalEntityResolver.DTD_CP_PACKAGE + entityName,
						getClass());
				if (entityStream == null) {
					return null;
				}
			}

			// Read the entity stream and store it in the cache.
			cachedBytes = readEntity(entityStream);
            LocalEntityResolver.dtdEntities.put(systemId, cachedBytes);
		}

		entityInputSource = new InputSource(new ByteArrayInputStream(
				cachedBytes));
		entityInputSource.setPublicId(publicId);
		entityInputSource.setSystemId(systemId);

		return entityInputSource;
	}

	/**
	 * Read a DTD entity from the InputStream.
	 * 
	 * @param stream
	 *            Entity stream to be read.
	 * @return DTD entity bytes.
	 */
	private byte[] readEntity(InputStream stream) {
		ByteArrayOutputStream entityBytes = new ByteArrayOutputStream();
		byte[] bytes = new byte[512];
		int readCount = 0;

		try {
			while ((readCount = stream.read(bytes)) != -1) {
				entityBytes.write(bytes, 0, readCount);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return entityBytes.toByteArray();
	}

	/**
	 * Clear the entity cache.
	 */
	public static void clearEntityCache() {
		dtdEntities.clear();
	}

    /**
     * Get the document DTD.
     * <p/>
     * This is a bit of a hack.  There's a way of getting the DOM
     * parser to populate the DocumentType.
     *
     * @return The Document DTD (systemId).
     */
    public String getDocDTD() {
        return docDTD;
    }
}
