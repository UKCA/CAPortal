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
package uk.ac.ngs.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ngs.dao.JdbcRaopListDao;
import uk.ac.ngs.domain.RaopListRow;

/**
 * Service class for managing CRRs including deletion and approval.link 
 * Public service methods are transactional and are limited to specific roles. 
 *
 * @author Josh Hadley
 */
@Service
public class RaopManagerService {

    private JdbcRaopListDao jdbcRaopDao;
 
    /**
     * A class to store the RA Contact Details in a correct format.
     */
    public static class RaContact {
       private final long cert_key;
       private final String loc; 
       private final String ou; 
       private final String name;
       private final String email;
       private final String phone;
       private final String street;
       private final String city;
       private final String postcode;
       private final String cn;
       private final boolean manager;
       private final boolean operator;
       private final Date trainingDate;
       private final String title;
       private final String coneemail;
       private final String location;
       private final long ra_id;
       private final String department_hp;
       private final String institute_hp;
       private final boolean active;
       private final long ra_id2;
       
       public RaContact(long cert_key, String loc, String ou, String name, String email,
                        String phone, String street, String city, String postcode, String cn,
                        boolean manager, boolean operator, Date trainingDate, String title, 
                        String coneemail, String location, long ra_id, String department_hp,
                        String institute_hp, boolean active, long ra_id2){
          
            this.cert_key = cert_key;
            this.loc = loc; 
            this.ou = ou; 
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.street = street;
            this.city = city;
            this.postcode = postcode;
            this.cn = cn;
            this.manager = manager;
            this.operator = operator;
            this.trainingDate = trainingDate;
            this.title = title;
            this.coneemail = coneemail;
            this.location = location;
            this.ra_id = ra_id;
            this.department_hp = department_hp;
            this.institute_hp = institute_hp;
            this.active = active;
            this.ra_id2 = ra_id2;
        }
       
        /**
         * @return the cert_key
         */
        public long getCert_key() {
            return cert_key;
        }
       
        /**
         * @return the loc
         */
        public String getLoc() {
            return loc;
        }

        /**
         * @return the ou
         */
        public String getOu() {
            return ou;
        }
        
        /**
        * @return the name
        */
        public String getName() {
            return name;
        }
        
        /**
        * @return the email
        */
        public String getEmail() {
            return email;
        }
        
        /**
        * @return the phone
        */
        public String getPhone() {
            return phone;
        }
        
        /**
        * @return the street
        */
        public String getStreet() {
            return street;
        }
        
        /**
        * @return the city
        */
        public String getCity() {
            return city;
        }
       
        /**
        * @return the postcode
        */
        public String getPostcode() {
            return postcode;
        }
        
        /**
        * @return the cn
        */
        public String getCn() {
            return cn;
        }
        
        /**
        * @return the manager
        */
        public boolean getManager() {
            return manager;
        }
        
        /**
        * @return the operator
        */
        public boolean getOperator() {
            return operator;
        }
        
        /**
        * @return the trainingDate
        */
        public Date getTrainingDate() {
            return trainingDate;
        }
        
        /**
        * @return the title
        */
        public String getTitle() {
            return title;
        }
        
        /**
        * @return the coneemail
        */
        public String getConeemail() {
            return coneemail;
        }
        
        /**
        * @return the location
        */  
        public String getLocation() {
            return location;
        }
        
        /**
        * @return the ra_id
        */
        public long getRa_id() {
            return ra_id;
        }
        
        /**
        * @return the department_hp
        */
        public String getDepartment_hp() {
            return department_hp;
        }
        
        /**
        * @return the institute_hp
        */
        public String getInstitute_hp() {
            return institute_hp;
        }
        
        /**
        * @return the active
        */
        public boolean getActive() {
            return active;
        }
        
        /**
        * @return the ra_id2
        */
        public long getRa_id2() {
            return ra_id2;
        }
    }
    
    /**
     * Add the new Raop Contact Row to the Raoplist Table.
     * After successful completion, the Raop will be informed their details
     * are now present within the table
     * 
     * @param loc L of Certificate
     * @param ou OU of Certificate
     * @param name name of RAOP
     * @param email Email of Certificate
     * @param phone Phone of RAOP
     * @param street Street of RA
     * @param city City of RA
     * @param postcode Postcode of RA
     * @param cn CN of Certificate
     * @param manager If they have the RA Manager, True/False
     * @param operator If they have the RA operator, True/False
     * @param trainingDate The training date of the RA-OP
     * @param title The title of the RA-OP
     * @param coneemail The company email of the RA-OP
     * @param location The location of the company
     * @param ra_id The id of the RA in the RA list table
     * @param department_hp The url of the Department of the RA-OP
     * @param institute_hp The url of the Institute of the RA-OP
     * @param ra_id2 Legacy RA_ID
     * @return The ID/PK of the newly inserted db 'crr' row.  
     */    
    @RolesAllowed("ROLE_CAOP")
    public RaopListRow addRaopContact(String loc, String ou, String name,
                            String email, String phone, String street, String city,
                            String postcode, String cn, boolean manager, boolean operator,
                            Date trainingDate, String title, String coneemail, 
                            String location, int ra_id, String department_hp,
                            String institute_hp, int ra_id2){

        // create a new crr row 
        RaopListRow raopRow = this.buildRaopRow(loc, ou, name,
                            email, phone, street, city,
                            postcode, cn, manager, operator,
                            trainingDate, title, coneemail, 
                            location, ra_id, department_hp,
                            institute_hp, ra_id2);
        if (this.jdbcRaopDao.insertRaopListRow(raopRow) != 1) {
           throw new RuntimeException("Row Insert Failed"); 
        }
        return raopRow;  
    }

    /**
     * Update an existing Raop Row with a modified RaopListRow.   
     * After success, the individual will be notified about the modifications
     * 
     * @param raop Updated Raop Record
     */
    @RolesAllowed({"ROLE_RAOP", "ROLE_CAOP"}) 
    public void updateRaopContact(RaopListRow raop, RaopListRow updatedRaop){
       // throw early  
       if(raop == null ){throw new IllegalArgumentException("Invalid Raop Row"); } 
      
       if(this.jdbcRaopDao.updateRaopRow(raop, updatedRaop) != 1){
          throw new RuntimeException("Multiple raop rows attempted for update");     
       }
    }
     

    /** Sample RAOP Data colunn
        OU              =   Organisation Unit
        L               =   Location
        Name            =   Test User
        Email           =   Certificate Email
        Phone           =   XXXXX-XXXXXX
        Street          =   Department Location
        City            =   Nearest Town/City
        Postcode        =   XXX-XXX/X
        CN              =   Certificate CN
        Manager         =   True/False (If User is a RA Manager)
        Operator        =   True/False (If User is a RA Operator)
        Training Date   =   DD/MM/YYYY
        Title           =   Mr/Mrs/Miss/Dr
        coneemail       =   example-user@example.com
        location        =   Company Location
        ra_id           =   ID of the RA
        department_hp   =   Internet URL for a Department Homepage
        institute_hp    =   Internet URL for a Institute Homepage
        active          =   True/False
        ra_id2          =   Legacy Purposes
     */  
    private RaopListRow buildRaopRow(String loc, String ou, String name,
                            String email, String phone, String street, String city,
                            String postcode, String cn, boolean manager, boolean operator,
                            Date trainingDate, String title, String coneemail, 
                            String location, int ra_id, String department_hp,
                            String institute_hp, int ra_id2){
        
        RaopListRow raopRow = new RaopListRow(); 
        raopRow.setOu(ou);
        raopRow.setL(loc);
        raopRow.setName(name);
        raopRow.setEmail(email);
        raopRow.setPhone(phone);
        raopRow.setStreet(street);
        raopRow.setCity(city);
        raopRow.setPostcode(postcode);
        raopRow.setCn(cn);
        raopRow.setManager(manager);
        raopRow.setOperator(operator);
        raopRow.setTrainingDate(trainingDate);
        raopRow.setTitle(title);
        raopRow.setConeemail(coneemail);
        raopRow.setLocation(location);
        raopRow.setRa_id(ra_id);
        raopRow.setDepartment_hp(department_hp);
        raopRow.setInstitute_hp(institute_hp);
        raopRow.setActive(true);
        raopRow.setRa_id2(ra_id2);
        
        return raopRow; 
    }    

    
    /**
     * @param jdbcRaopDao the jdbcRaopDao to set
     */
    @Inject
    public void setJdbcRaopListDao(JdbcRaopListDao jdbcRaopDao) {
        this.jdbcRaopDao = jdbcRaopDao;
    }
}