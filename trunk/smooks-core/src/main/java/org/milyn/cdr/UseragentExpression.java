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

package org.milyn.cdr;

import org.milyn.useragent.UAContext;
import org.milyn.useragent.UAContextUtil;
import org.milyn.profile.HttpAcceptHeaderProfile;
import org.milyn.profile.Profile;

/**
 * Represents a single parsed useragent attribute value expression.
 * <p/>
 * The <a>useragent</a> attribute can contain multiple comma seperated "useragent expressions" i.e.
 * a list of them.  See {@link org.milyn.cdr.SmooksResourceConfiguration} docs.  This class represents
 * a single expression within a list of useragent attribute expressions.
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
 * <p/>
 * See {@link UseragentExpression.ExpressionToken}.
 * @author tfennelly
 */
public class UseragentExpression {
	
	private String expression;
	private ExpressionToken[] expressionTokens;
	
	public UseragentExpression(String expression) {
		if(expression == null || expression.trim().equals("")) {
			throw new IllegalArgumentException("null or empty 'expression' arg.");
		}
		this.expression = expression = expression.toLowerCase();
		
		String[] tokens = expression.split(" and ");
		expressionTokens = new ExpressionToken[tokens.length];
		for(int i = 0; i < tokens.length; i++) {
			String useragentToken = tokens[i].trim();
			expressionTokens[i] = new ExpressionToken(useragentToken);
		}
	}
	
	/**
	 * Is the supplied device context a matching device for this useragent expression.
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
	 * Get the specificity of this useragent expression with respect to the supplied device.
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
	 * Get the useragent expression used to construct this instance.
	 * @return The useragent string for this instance.
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * Get the list of {@link ExpressionToken}s parsed out of the {@link #UseragentExpression(String) expression}
	 * used to construct this instance.
	 * @return The list of {@link ExpressionToken}s.
	 */
	public ExpressionToken[] getExpressionTokens() {
		return expressionTokens;
	}
	
	public String toString() {
		return expression;
	}
	
	/**
	 * useragent expression token.
	 * @author tfennelly
	 */
	public class ExpressionToken {
		
		private String expressionToken;
		private boolean negated;

		/**
		 * Private constructor.
		 * @param useragentToken 
		 */
		private ExpressionToken(String useragentToken) {
			negated = useragentToken.startsWith("not:");
			if(negated) {
				this.expressionToken = useragentToken.substring(4);
			} else {
				this.expressionToken = useragentToken;
			}
		}

		/**
		 * Get the useragent specified in this token.
		 * @return The useragent specified in this token.
		 */
		public String getUseragent() {
			return expressionToken;
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
			return expressionToken.equals("*");
		}
		
		/**
		 * Is the supplied device context a matching device for this useragent token.
		 * @param deviceContext Useragent device context.
		 * @return True if the device is a match, otherwise false.
		 */
		public boolean isMatchingDevice(UAContext deviceContext) {
			if(isWildcard()) {
				return true;
			} else if(negated) {
				return !UAContextUtil.isDeviceOrProfile(expressionToken, deviceContext);
			} else {
				return UAContextUtil.isDeviceOrProfile(expressionToken, deviceContext);
			}
		}
		
		/**
		 * Get the specificity of this token with respect to the supplied device.
		 * <p/>
		 * The following outlines the algorithm:
		 * <pre>
	if({@link #isNegated() isNegated()}) {
		if({@link UAContext deviceContext}.getCommonName().equals(expressionToken)) {
			return 0;
		} else if({@link UAContext deviceContext}.getProfileSet().isMember(expressionToken)) {
			return 0;
		} else if({@link #isWildcard() isWildcard()}) {
			return 0;
		}
		return 1;
	} else {
		// Is the "expressionToken" referencing the useragent name (common name), a profile,
		// or is it a wildcard token.
		Profile profile = deviceContext.getProfileSet().getProfile(expressionToken);
	
		if(deviceContext.getCommonName().equals(expressionToken)) {
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
				if(deviceContext.getCommonName().equals(expressionToken)) {
					return 0;
				} else if(deviceContext.getProfileSet().isMember(expressionToken)) {
					return 0;
				} else if(isWildcard()) {
					return 0;
				}
				return 1;
			} else {
				// Is the "expressionToken" referencing the useragent name (common name), a profile,
				// or is it a wildcard token.
				Profile profile = deviceContext.getProfileSet().getProfile(expressionToken);

				if(deviceContext.getCommonName().equalsIgnoreCase(expressionToken)) {
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
					return 5;
				}
				return 0;
			}
		}

		public String toString() {
			return expressionToken;
		}
	}
}
