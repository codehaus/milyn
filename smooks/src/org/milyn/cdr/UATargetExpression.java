/*
	Milyn - Copyright (C) 2003

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

package org.milyn.cdr;

import org.milyn.device.UAContext;
import org.milyn.device.UAContextUtil;
import org.milyn.device.profile.HttpAcceptHeaderProfile;
import org.milyn.device.profile.Profile;

/**
 * Represents a single parsed uatarget attribute value expression.
 * <p/>
 * uatarget attribute values can be contain multiple "uatarget expressions" i.e.
 * a list of them.  See {@link org.milyn.cdr.CDRDef} docs.  This class represents
 * a single expression within a list of uatarget attribute value expressions.
 * <p/>
 * So, an expression is composed of 1 or more "expression tokens" seperated by 
 * "AND".  The expression arg to the constructor will be in one of 
 * the following forms:
 * <ol>
 * 	<li>"deviceX" (or "profileX") i.e. a single entity.</li>
 * 	<li>"deviceX AND profileY" i.e. a compound entity.</li>
 * 	<li>"profileX AND profileY" i.e. a compound entity.</li>
 * 	<li>"profileX AND not:profileY" i.e. a compound entity.</li>
 * </ol>
 * Note, we only supports "AND" operations between the tokens, but a token can be
 * negated by prefixing it with "not:".
 * @author tfennelly
 */
public class UATargetExpression {
	
	private String expression;
	private ExpressionToken[] expressionTokens;
	
	public UATargetExpression(String expression) {
		if(expression == null || expression.trim().equals("")) {
			throw new IllegalArgumentException("null or empty 'uaTargetExpression' arg.");
		}
		this.expression = expression = expression.toLowerCase();
		
		String[] tokens = expression.split(" and ");
		expressionTokens = new ExpressionToken[tokens.length];
		for(int i = 0; i < tokens.length; i++) {
			String uatargetToken = tokens[i].trim();
			expressionTokens[i] = new ExpressionToken(uatargetToken);
		}
	}
	
	/**
	 * Is the supplied device context a matching device for this uatarget expression.
	 * @param deviceContext Useragent device context.
	 * @return True if the device is a match, otherwise false.
	 */
	public boolean isMatchingDevice(UAContext deviceContext) {
		for (int i = 0; i < expressionTokens.length; i++) {
			if(!expressionTokens[i].isMatchingDevice(deviceContext)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Get the specificity of this uatarget expression with respect to the supplied device.
	 * <p/>
	 * Iterates over this expressions list of {@link ExpressionToken}s calling
	 * {@link ExpressionToken#getSpecificity(UAContext)} and adds up their specificities.
	 * @param deviceContext Device context.
	 * @return Specificity value of the expression.
	 */
	public double getSpecificity(UAContext deviceContext) {
		double specificity = 0;

		// Only if the expression matches the device.
		if(isMatchingDevice(deviceContext)) {
			for (int i = 0; i < expressionTokens.length; i++) {
				if(expressionTokens[i].isMatchingDevice(deviceContext)) {
					specificity += expressionTokens[i].getSpecificity(deviceContext);
				}
			}
		}
		
		return specificity;
	}

	/**
	 * Get the uatarget expression used to construct this instance.
	 * @return The uatarget string for this instance.
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * Get the list of {@link ExpressionToken}s parsed out of the {@link #UATargetExpression(String) expression}
	 * used to construct this instance.
	 * @return The list of {@link ExpressionToken}s.
	 */
	public ExpressionToken[] getExpressionTokens() {
		return expressionTokens;
	}
	
	/**
	 * uatarget expression token.
	 * @author tfennelly
	 */
	public class ExpressionToken {
		
		private String uatarget;
		private boolean negated;

		/**
		 * Private constructor.
		 * @param uatargetToken 
		 */
		private ExpressionToken(String uatargetToken) {
			negated = uatargetToken.startsWith("not:");
			if(negated) {
				this.uatarget = uatargetToken.substring(4);
			} else {
				this.uatarget = uatargetToken;
			}
		}

		/**
		 * Get the uatarget specified in this token.
		 * @return The uatarget specified in this token.
		 */
		public String getUatarget() {
			return uatarget;
		}

		/**
		 * Is this token negated.
		 * <p/>
		 * Is the token prefixed with "not:".
		 * @return True if the token is negated, otherwise false.
		 */
		public boolean isNegated() {
			return negated;
		}
		
		/**
		 * Is the token a wildcard token.
		 * <p/>
		 * Is the token equal to "*".
		 * @return True if the token is a wildcard token, otherwise false. 
		 */
		public boolean isWildcard() {
			return uatarget.equals("*");
		}
		
		/**
		 * Is the supplied device context a matching device for this uatarget token.
		 * @param deviceContext Useragent device context.
		 * @return True if the device is a match, otherwise false.
		 */
		public boolean isMatchingDevice(UAContext deviceContext) {
			if(isWildcard()) {
				return true;
			} else if(negated) {
				return !UAContextUtil.isDeviceOrProfile(uatarget, deviceContext);
			} else {
				return UAContextUtil.isDeviceOrProfile(uatarget, deviceContext);
			}
		}
		
		/**
		 * Get the specificity of this token with respect to the supplied device.
		 * <p/>
		 * The following outlines the algorithm:
		 * <pre>
			if({@link #isNegated() isNegated()}) {
				if({@link UAContext deviceContext}.getCommonName().equals(uatarget)) {
					return 0;
				} else if({@link UAContext deviceContext}.getProfileSet().isMember(uatarget)) {
					return 0;
				} else if({@link #isWildcard() isWildcard()}) {
					return 0;
				}
				return 1;
			} else {
				Profile profile = deviceContext.getProfileSet().getProfile(uatarget);

				if(deviceContext.getCommonName().equals(uatarget)) {
					return 100;
				} else if(profile != null) {
					// If it's a HTTP "Accept" header media profile, multiply
					// the specificity by the media qvalue.  See the
					// {@link HttpAcceptHeaderProfile HttpAcceptHeaderProfile javadocs}.
					if(profile instanceof HttpAcceptHeaderProfile) {
						return (10 * ((HttpAcceptHeaderProfile)profile).getParamNumeric("q", 1));
					} else {
						return 10;
					}
				} else if(isWildcard()) {
					return 1;
				}
				return 0;
			}
		 * </pre>
		 * @param deviceContext Device context.
		 * @return Specificity value for the token.
		 */
		public double getSpecificity(UAContext deviceContext) {
			if(isNegated()) {
				if(deviceContext.getCommonName().equals(uatarget)) {
					return 0;
				} else if(deviceContext.getProfileSet().isMember(uatarget)) {
					return 0;
				} else if(isWildcard()) {
					return 0;
				}
				return 1;
			} else {
				Profile profile = deviceContext.getProfileSet().getProfile(uatarget);

				if(deviceContext.getCommonName().equals(uatarget)) {
					return 100;
				} else if(profile != null) {
					// If it's a HTTP "Accept" header media profile, multiple
					// the specificity by the media qvalue.  See the
					// HttpAcceptHeaderProfile javadocs.
					if(profile instanceof HttpAcceptHeaderProfile) {
						return (10 * ((HttpAcceptHeaderProfile)profile).getParamNumeric("q", 1));
					} else {
						return 10;
					}
				} else if(isWildcard()) {
					return 1;
				}
				return 0;
			}
		}
	}
}
