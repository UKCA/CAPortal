/*
 * Copyright (C) 2015 STFC
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ngs.forms;

import java.io.Serializable;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 *
 * @author Josh Hadley
 */
public class RaContactBean implements Serializable{
    
    private long cert_key;
    
    private String title;
    
    @Pattern(message="Invalid name (at least 2 names min of 2 chars each)", regexp="^\\w{2,30}( \\w{2,30})+$") 
    private String name; 
    
    @Pattern(message="Invalid chars \" ' ; `", regexp="^[^\"';`]*$")
    private String ra;
   
    private Boolean raOperator;
    
    private Boolean raManager;
    
    @Pattern(message="Invalid email", regexp="^(([0-9a-zA-Z]+[-._])*[0-9a-zA-Z]+@([-0-9a-zA-Z]+[.])+[a-zA-Z]{2,6}[,;]?)+$")
    private String emailAddress; 
    
    @Pattern(message="Invalid Phone Number (10 chars min)", regexp="^[0-9a-zA-z ]{10,20}$")
    private String phone;
    
    private String street;
    
    private String city;
    
    //@Size 
    //@Pattern(message="Invalid Postcode (6 chars min)", regexp="^[0-9a-zA-z ]{10,20}$")
    private String postcode;
    
    //@Pattern(message="Invalid Training Date (DD-MM-YYYY)", regexp="^[0-3]?[0-9]/[0-3]?[0-9]/(?:[0-9]{2})?[0-9]{2}$")
    private String training;
 
    /**
     * @return the title
     */
    public long getCert_key() {
        return cert_key;
    }

    /**
     * @param cert_key the title to set
     */
    public void setCert_key(long cert_key) {
        this.cert_key = cert_key;
    }
    
    
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the raOperator
     */
    public Boolean getRaOperator() {
        return raOperator;
    }
    
    /**
     * @param raOperator the raOperator to set
     */
    public void setRaOperator(Boolean raOperator) {
        this.raOperator = raOperator;
    }
    
    /**
     * @return the raManager
     */
    public Boolean getRaManager() {
        return raManager;
    }
    
    /**
     * @param raManager the raManager to set
     */
    public void setRaManager(Boolean raManager) {
        this.raManager = raManager;
    }
   
    /**
     * @return the ra
     */
    public String getRa() {
        return ra;
    }
    
    /**
     * @param ra the ra to set
     */
    public void setRa(String ra) {
        this.ra = ra;
    }
    
    /**
     * @return the emailAddress
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * @param emailAddress the emailAddress to set
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    /**
     * @return the street
     */
    public String getStreet() {
        return street;
    }

    /**
     * @param street the street to set
     */
    public void setStreet(String street) {
        this.street = street;
    }
    
    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }
    
    /**
     * @return the postcode
     */
    public String getPostcode() {
        return postcode;
    }

    /**
     * @param postcode the postcode to set
     */
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
    
    /**
     * @return the training
     */
    public String getTraining() {
        return training;
    }

    /**
     * @param training the training to set
     */
    public void setTraining(String training) {
        this.training = training;
    }
}