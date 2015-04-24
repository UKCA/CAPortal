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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import uk.ac.ngs.common.MutableConfigParams;
import uk.ac.ngs.dao.JdbcCertificateDao;
import uk.ac.ngs.dao.JdbcRaopListDao;
import uk.ac.ngs.domain.CertificateRow;
import uk.ac.ngs.domain.RaopListRow;
import uk.ac.ngs.forms.RaContactBean;
import uk.ac.ngs.security.SecurityContextService;
import uk.ac.ngs.service.email.EmailService;

/**
 * Process RA Operator additions or changes. 
 * Intended to be called from higher level layers, e.g. from Web controllers.
 * TODO: Throw runtime exceptions that are more suitable for business layer, 
 * extract interface once stable. 
 * 
 * @author Josh Hadley
 */
@Service
public class RaContactService {
/*
    private static final Log log = LogFactory.getLog(ProcessRevokeService.class);
    private JdbcCertificateDao certDao;
    private JdbcRaopListDao raopDao;
    private RaopManagerService raopService;
    private SecurityContextService securityContextService;
    private EmailService emailService;
    private MutableConfigParams mutableConfigParams; 

    /**
     * Immutable transfer object that defines the result (success or fail) of a 
     * service layer revocation operation. 
     */
    /*public static class RaContactServiceResult {

        private final Errors errors;
        private final boolean success;
        private final Long certKey;

        /**
         * Construct an instance to signify a <b>success</b>. 
         * @param certKey 
         */
       /* public RaContactServiceResult(Long certKey) {
            this.success = true;
            this.errors = new MapBindingResult(new HashMap<String, String>(), "raContact");
            this.certKey = certKey;
        }
        /**
         * Construct an instance to signify a <b>fail</b>. 
         * @param errors 
         */
        /*public RaContactServiceResult(Errors errors) {
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
    public RaContactServiceResult addRaContact(RaContactBean raOperatorBean) {

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
            trainingDate = raOperatorBean.getTraining();
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

    /**
     * Perform a update query on a record (Either done by a CA-OP or if the RA-OP is editing their own details)
     * If successful, the changes will be applied to the <tt>raop</tt> row. 
     * 
     * @param raOperatorBean Update data 
     * @param clientData Client/calling RA 
     * @return 
     */
   /*@Transactional
    public RaContactServiceResult editRaContact(
            RaContactBean raOperatorBean){

        Errors errors = new MapBindingResult(new HashMap<String, String>(), "raContact");
        long raop_cert_key = raOperatorBean.getCert_key();

        //log.info("RA request revocation by: [" + ra_cert_key + "] for certificate: [" + raop_cert_key + "]");

        CertificateRow revokeCert = this.certDao.findById(raop_cert_key);
        // Check whether this cert can actually be revoked (VALID not expired) 
        if (!this.canUserDoEdit(revokeCert)) {
            errors.reject("invalid.revocation.cert.notvalid", "Revocation Failed - Certificate is not VALID or has expired");
           // log.warn("RA revocation failed by: [" + ra_cert_key + "] for certificate: [" + raop_cert_key + "] - cert is not valid or has expired");
            return new RaContactServiceResult(errors);
        }

        // revoke with status NEW 
        //long crrId = this.crrService.revokeCertificate(raop_cert_key, ra_cert_key,
                //raOperatorBean.getReason(), CrrManagerService.CRR_STATUS.NEW);
     
        // Email the home RAs 
        /*boolean emailRaOnRevoke = Boolean.parseBoolean(this.mutableConfigParams.getProperty("email.ra.on.revoke")); 
        if (emailRaOnRevoke) {
            String loc = CertUtil.extractDnAttribute(revokeCert.getDn(), CertUtil.DNAttributeType.L);
            String ou = CertUtil.extractDnAttribute(revokeCert.getDn(), CertUtil.DNAttributeType.OU);
            Set<String> raEmails = new HashSet<String>(); // use set so duplicates aren't added 
            // Find all the RA email addresses, iterate and send   
            List<CertificateRow> raCerts = this.certDao.findActiveRAsBy(loc, ou);
            for (CertificateRow raCert : raCerts) {
                if (raCert.getEmail() != null) {
                    raEmails.add(raCert.getEmail());
                }
            }

            // if raEmails is empty (possible, since there may not be an RAOP 
            // for this RA anymore), then fallback to email the default list
            if (raEmails.isEmpty()) {
                log.warn("No RAOP exits for [" + loc + " " + ou + "] emailing CA default");
                  String[] allemails = this.mutableConfigParams.getProperty("email.admin.addresses").split(",");  
                  raEmails.addAll(Arrays.asList(allemails));
            }
            this.emailService.sendRaEmailOnRevoke(revokeCert.getDn(), raEmails, raop_cert_key);
        }*/
        
       /* return new RaContactServiceResult(raop_cert_key);
    }

    private boolean canUserDoEdit(CertificateRow cert) {
        if (this.securityContextService.getCaUserDetails().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_CAOP"))) {
            return true;
        } else { //Check if the current user is the same person as the requested raop
            long certKey = cert.getCert_key();
            long raOpKey = this.securityContextService.getCaUserDetails().getCertificateRow().getCert_key();
            if (certKey == raOpKey) {
                return true;
            }
        }
        return false;
    }

    private boolean doesRowExist(CertificateRow cert) {
        String loc = uk.ac.ngs.common.CertUtil.extractDnAttribute(cert.getDn(), uk.ac.ngs.common.CertUtil.DNAttributeType.L);
        String ou = uk.ac.ngs.common.CertUtil.extractDnAttribute(cert.getDn(), uk.ac.ngs.common.CertUtil.DNAttributeType.OU); 
        String cn = uk.ac.ngs.common.CertUtil.extractDnAttribute(cert.getDn(), uk.ac.ngs.common.CertUtil.DNAttributeType.CN); 
        List<RaopListRow> raop = this.raopDao.findBy(loc, ou, cn, null);
        
        return raop.isEmpty();
    }
    
    private boolean notNull() {
        return true;
    }

    @Inject
    public void setRaopManagerService(RaopManagerService raopService) {
        this.raopService = raopService;
    }

    @Inject
    public void setJdbcCertificateDao(JdbcCertificateDao dao) {
        this.certDao = dao;
    }

    @Inject
    public void setSecurityContextService(SecurityContextService securityContextService) {
        this.securityContextService = securityContextService;
    }

    /**
     * @param emailService the emailService to set
     */
    /*@Inject
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Inject
    public void setMutableConfigParams(MutableConfigParams mutableConfigParams){
       this.mutableConfigParams = mutableConfigParams;  
    }
    */
}

