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

