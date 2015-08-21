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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import uk.ac.ngs.dao.JdbcCertificateDao;
import uk.ac.ngs.dao.JdbcRaopListDao;
import uk.ac.ngs.domain.CertificateRow;
import uk.ac.ngs.domain.RaopListRow;
import uk.ac.ngs.forms.RaContactBean;
//import uk.ac.ngs.security.SecurityContextService;
//import uk.ac.ngs.service.email.EmailService;

/**
 * Service Class for processing new RA Operators or updates to existing RA-OPs 
 * Intended to be called from higher level layers, e.g. from Web controllers.
 * 
 * @author Josh Hadley
 */
@Service
public class RaContactService {

    private static final Log log = LogFactory.getLog(RaContactService.class);
    private RaopManagerService raopService;
    private JdbcCertificateDao certDao;
    private JdbcRaopListDao raopDao;
    
    /**
     * Immutable transfer object that defines the result (success or fail) of a 
     * service layer revocation operation. 
     */
    public static class RaContactServiceResult {

        private final Errors errors;
        private final boolean success;
        private final Long certKey;

        /**
         * Construct an instance to signify a <b>success</b>. 
         * @param certKey 
         */
        public RaContactServiceResult(Long certKey) {
            this.success = true;
            this.errors = new MapBindingResult(new HashMap<String, String>(), "raContact");
            this.certKey = certKey;
        }
        
        /**
         * Construct an instance to signify a <b>fail</b>. 
         * @param errors 
         */
        public RaContactServiceResult(Errors errors) {
            this.errors = errors;
            this.success = false;
            this.certKey = null; 
        }

        public Errors getErrors() {
            return this.errors;
        }

        public boolean getSuccess() {
            return this.success;
        }

        public Long getCertKey() {
            return this.certKey;
        }
    }

    /**
     * Perform a insert operation into the raoplist table (requires a CAOP).
     * If successful, a new <tt>raop</tt> row is created with status Active. 
     * 
     * @param raOperatorBean Insert data 
     * @return 
     */
    /*@Transactional
    public RaContactServiceResult addRaContact(RaContactBean raOperatorBean)
        throws IOException {
        return this.addRaContactHelper(raOperatorBean);
    }
    
    public RaContactServiceResult addRaContactHelper(RaContactBean raOperatorBean) 
        throws IOException {

        Errors errors = new MapBindingResult(new HashMap<String, String>(), "raContact");
        long raop_cert_key = raOperatorBean.getCert_key();

        log.info("Adding a RA Contact record for: [" + raop_cert_key + "]");
        CertificateRow raopCert = this.certDao.findById(raop_cert_key);

        if (!this.doesRowExist(raopCert)) {
            errors.reject("invalid.revocation.cert.notvalid", "Revocation Failed - Certificate is not VALID or has expired");
            log.warn("RA revocation failed by: [" + raop_cert_key + "] for certificate: [" + raop_cert_key + "] - cert is not valid or has expired");
            return new RaContactServiceResult(errors);
        }
        
        // Certificate Cells
        String loc = uk.ac.ngs.service.CertUtil.extractDnAttribute(raopCert.getDn(), CertUtil.DNAttributeType.L);
        String ou = uk.ac.ngs.service.CertUtil.extractDnAttribute(raopCert.getDn(), CertUtil.DNAttributeType.OU);
        String cn = raopCert.getCn();
        String email = raopCert.getEmail();
        
        String ra = raOperatorBean.getRa();
        int ra_id = 0;
       
        String title = null;
        String name = null ;
        String coneemail = null;
        String phone = null;
        String street = null;
        String city = null;
        String postcode = null;
        String location = null;
        boolean manager = false;
        boolean operator = false;
        Date trainingDate = null;
        String department_hp = null;
        String institute_hp = null;
        int ra_id2 = 0;
        
        if(raOperatorBean.getTitle() != null){
            title = raOperatorBean.getTitle();
        }
        if(raOperatorBean.getName() != null){
            name = raOperatorBean.getName();
        }
        if(raOperatorBean.getEmailAddress() != null){
            coneemail = raOperatorBean.getEmailAddress();
        }
        if(raOperatorBean.getPhone() != null){
            phone = raOperatorBean.getPhone();
        }
        if(raOperatorBean.getStreet() != null){
            street = raOperatorBean.getStreet();
        }
        if(raOperatorBean.getCity() != null){
            city = raOperatorBean.getCity();
        }
        if(raOperatorBean.getPostcode() != null){
            postcode = raOperatorBean.getPostcode();
        }
        if(raOperatorBean.getRaManager() != null){
            manager = raOperatorBean.getRaManager();
        }
        if(raOperatorBean.getRaOperator() != null){
            operator = raOperatorBean.getRaOperator();
        }
        if(raOperatorBean.getTraining() != null){
            trainingDate = convertString(raOperatorBean.getTraining());
        }
     
        
        // Add  
        this.raopService.addRaopContact(loc, ou, name,
                            email, phone, street, city,
                            postcode, cn, manager, operator,
                            trainingDate, title, coneemail, 
                            location, ra_id, department_hp,
                            institute_hp, ra_id2);
        return new RaContactServiceResult(raopCert.getCert_key());
    }
    */
    
    
    /**
     * Perform a update query on a record (Either done by a CA-OP or if the RA-OP is editing their own details)
     * If successful, the changes will be applied to the <tt>raop</tt> row. 
     * 
     * @param raOperatorBean Update data
     * @return 
     * @throws java.io.IOException 
     */
    @Transactional
    @RolesAllowed({"ROLE_RAOP", "ROLE_CAOP"})
    public RaContactServiceResult editRaContact(RaContactBean raOperatorBean) 
            throws IOException {    
        return this.editRaContactHelper(raOperatorBean); 
    }

    private RaContactServiceResult editRaContactHelper (RaContactBean raOperatorBean) 
            throws IOException {

        long raop_cert_key = raOperatorBean.getCert_key();
        
        if(certDao == null){
            log.debug("It is null");
        } else {
            log.debug("It is not null");
        }
        
        log.info("CertID: " + raop_cert_key);
        
        CertificateRow cert = this.certDao.findById(raop_cert_key);
        
        String ou = CertUtil.extractDnAttribute(cert.getDn(), CertUtil.DNAttributeType.OU); //CLRC
        String l = CertUtil.extractDnAttribute(cert.getDn(), CertUtil.DNAttributeType.L); //RAL.;
        
        List<RaopListRow> raops = this.raopDao.findBy(ou, l, cert.getCn(), Boolean.TRUE);
            
        RaopListRow raop = raops.get(0);
        
        RaopListRow updatedRaop = addChanges(raop, raOperatorBean);
        
        // update with new data 
        this.raopService.updateRaopContact(raop, updatedRaop);
        
        return new RaContactServiceResult(raop_cert_key);
    }
    
    private RaopListRow addChanges(RaopListRow raop, RaContactBean changes){

        if(!changes.getTitle().equals(raop.getTitle())){
            raop.setTitle(changes.getTitle());
        }
        if(!changes.getName().equals(raop.getName())){
            raop.setName(changes.getName());
        }
        if(!changes.getEmailAddress().equals(raop.getEmail())){
            raop.setEmail(changes.getEmailAddress());
        }
        if(!changes.getPhone().equals(raop.getPhone())){
            raop.setPhone(changes.getPhone());
        }
        if(!changes.getStreet().equals(raop.getStreet())){
            raop.setStreet(changes.getStreet());
        }
        if(!changes.getCity().equals(raop.getCity())){
            raop.setCity(changes.getCity());
        }
        if(!changes.getPostcode().equals(raop.getPostcode())){
            raop.setPostcode(changes.getPostcode());
        }
        if(!changes.getTraining().equals(raop.getTrainingDate())){         
            raop.setTrainingDate(changes.getTraining());
        }
        
        return raop;
    }
    
    @Inject
    public void setRaopManagerService(RaopManagerService raopService) {
        this.raopService = raopService;
    }
    
    @Inject
    public void setJdbcCertificateDao(JdbcCertificateDao certDao){
        this.certDao = certDao;
    }
    
    @Inject
    public void setJdbcRaopListDao(JdbcRaopListDao raopDao){
        this.raopDao = raopDao;
    }
}