package org.milyn.javabean;

import java.util.Date;

/**
 * @author
 */
public class Header {
    private Date date;
    private Long customerNumber;
    private String customerName;
    private boolean privatePerson;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(Long customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

	public boolean getPrivatePerson() {
		return privatePerson;
	}

	public void setPrivatePerson(boolean privatePerson ) {
		this.privatePerson = privatePerson;
	}
}
