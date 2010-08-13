/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.milyn.smooks.camel.dataformat;

public class Customer {
	
	private String FirstName;
    private String LastName;
    private Gender Gender;
    
    @Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Age;
		result = prime * result + ((Country == null) ? 0 : Country.hashCode());
		result = prime * result
				+ ((FirstName == null) ? 0 : FirstName.hashCode());
		result = prime * result + ((Gender == null) ? 0 : Gender.hashCode());
		result = prime * result
				+ ((LastName == null) ? 0 : LastName.hashCode());
		return result;
	}
    
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		if (Age != other.Age)
			return false;
		if (Country == null)
		{
			if (other.Country != null)
				return false;
		} else if (!Country.equals(other.Country))
			return false;
		if (FirstName == null)
		{
			if (other.FirstName != null)
				return false;
		} else if (!FirstName.equals(other.FirstName))
			return false;
		if (Gender != other.Gender)
			return false;
		if (LastName == null)
		{
			if (other.LastName != null)
				return false;
		} else if (!LastName.equals(other.LastName))
			return false;
		return true;
	}

	private int Age;
	private String Country;
	
    public String getCountry() {
		return Country;
	}
	public void setCountry(String country) {
		Country = country;
	}
    public String getFirstName() {
		return FirstName;
	}
	public void setFirstName(String firstName) {
		FirstName = firstName;
	}
	public String getLastName() {
		return LastName;
	}
	public void setLastName(String lastName) {
		LastName = lastName;
	}
	public Gender getGender() {
		return Gender;
	}
	public void setGender(Gender gender) {
		Gender = gender;
	}
	public int getAge() {
		return Age;
	}
	public void setAge(int age) {
		Age = age;
	}

    public String toString() {
        return "[" + FirstName + ", " + LastName + ", " + Gender + ", " + Age + ", " + Country + "]";
    }
}

