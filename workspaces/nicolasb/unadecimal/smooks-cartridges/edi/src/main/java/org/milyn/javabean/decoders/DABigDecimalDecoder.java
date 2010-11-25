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
package org.milyn.javabean.decoders;

import org.milyn.edisax.model.internal.Delimiters;
import org.milyn.edisax.unedifact.UNEdifactInterchangeParser;
import org.milyn.javabean.DataDecodeException;
import org.milyn.javabean.DecodeType;
import org.milyn.delivery.Filter;
import org.milyn.container.ExecutionContext;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.DecimalFormatSymbols;

/**
 * {@link BigDecimal} Decoder, which is EDI delimiters aware for parsing decimal.
 *
 * @author <a href="mailto:sinfomicien@gmail.com">sinfomicien@gmail.com</a>
 */
@DecodeType(BigDecimal.class)
public class DABigDecimalDecoder extends org.milyn.javabean.decoders.BigDecimalDecoder {
    private DecimalFormat decimalFormat = null;
    private Delimiters interchangeDelimiters = null;

    public DABigDecimalDecoder setDelimiters(Delimiters interchangeDelimiters){
	    this.interchangeDelimiters = interchangeDelimiters;
	    return this;
    }

    private BigDecimalDecoder initDecimalFormat() {
        //Check to see if we can use the parent default format
        NumberFormat parentNumberFormat = getNumberFormat();
        if (parentNumberFormat != null && parentNumberFormat instanceof DecimalFormat) {
                decimalFormat = (DecimalFormat) parentNumberFormat;
        }else {
                decimalFormat = new DecimalFormat();
        }
	//EDI Format only include ',' or '.' for decimal separation, no grouping separator
	decimalFormat.applyPattern("#0.0#");
        // Retrieve the current delimiter if exist, default one otherwise
	if (interchangeDelimiters == null){
		ExecutionContext ec = Filter.getCurrentExecutionContext();
		if (ec != null) {
        	        interchangeDelimiters = (Delimiters) ec.getBeanContext().getBean("interchangeDelimiters");
		}else {
			interchangeDelimiters = UNEdifactInterchangeParser.defaultUNEdifactDelimiters;
		}
	}
        //Setting decimal separator from delimiters
        DecimalFormatSymbols dfs = decimalFormat.getDecimalFormatSymbols();
        dfs.setDecimalSeparator(interchangeDelimiters.getDecimalSeparator().charAt(0));
        decimalFormat.setDecimalFormatSymbols(dfs);
	return this;
    }

    public Object decode(String data) throws DataDecodeException {
        if (decimalFormat == null) {
		initDecimalFormat();
        }
        try {
           Number number = decimalFormat.parse(data.trim());

           if(number instanceof BigDecimal) {
                    return number;
           } else if(number instanceof BigInteger) {
                    return new BigDecimal((BigInteger) number);
           }

           return new BigDecimal(number.doubleValue());
       } catch (ParseException e) {
            throw new DataDecodeException("Failed to decode BigDecimal value '" + data + "' using NumberFormat instance " + decimalFormat + ".", e);
       }
    }

    public String encode(Object object) throws DataDecodeException {
        if(decimalFormat == null) {
		initDecimalFormat();
	}
        return decimalFormat.format(object);
    }
}
