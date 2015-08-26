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
package uk.ac.ngs.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ngs.dao.JdbcCertificateDao;
import uk.ac.ngs.dao.JdbcCrrDao;
import uk.ac.ngs.dao.JdbcRaopListDao;
import uk.ac.ngs.dao.JdbcRequestDao;
import uk.ac.ngs.domain.CertificateRow;
import uk.ac.ngs.domain.CrrRow;
import uk.ac.ngs.domain.RaopListRow;
import uk.ac.ngs.domain.RequestRow;
import uk.ac.ngs.security.CaUser;
import uk.ac.ngs.security.SecurityContextService;
import uk.ac.ngs.service.CertUtil;

/**
 * 
 * @author Josh Hadley
 * @author David Meredith
 * 
 */
@Controller
@RequestMapping("/caop")
public class CaOpBaseController {
    private static final Log log = LogFactory.getLog(CaOpBaseController.class);
    private SecurityContextService securityContextService;
    private JdbcRaopListDao jdbcRaopListDao;
    private JdbcRequestDao jdbcRequestDao;
    private JdbcCrrDao jdbcCrrDao;
    private JdbcCertificateDao jdbcCertDao;
    
    @ModelAttribute
    public void populateModel(Model model) {
        log.debug("caop populateModel");
        // Fetch the certificateRow entry for display in the model
        CaUser caUser = securityContextService.getCaUserDetails();
        //model.addAttribute("caUser", caUser);


        // Extract the RA value from the user's certificate DN
        String dn = caUser.getCertificateRow().getDn();
        String OU = CertUtil.extractDnAttribute(dn, CertUtil.DNAttributeType.OU); //CLRC
        String L = CertUtil.extractDnAttribute(dn, CertUtil.DNAttributeType.L); //RAL
        String CN = CertUtil.extractDnAttribute(dn, CertUtil.DNAttributeType.CN); //meryem tahar
        String ra = OU + " " + L;
        model.addAttribute("ra", ra);
        log.debug("ra is:[" + ra + "]");

        // Get the current user's RAOP details for display (i.e. when they last 
        // did the training etc). Note, this does not affect their ability to 
        // do raop stuff in the portal. 
        List<RaopListRow> raoprows = this.jdbcRaopListDao.findBy(OU, L, CN, true);
        log.debug("raoprows size: " + raoprows.size());
        model.addAttribute("raoprows", raoprows);

        // Fetch a list of pending CSRs for the RA (NEW and RENEW) 
        // APPROVED
        Map<JdbcRequestDao.WHERE_PARAMS, String> whereParams = new HashMap<JdbcRequestDao.WHERE_PARAMS, String>();
        whereParams.put(JdbcRequestDao.WHERE_PARAMS.RA_EQ, ra);
        whereParams.put(JdbcRequestDao.WHERE_PARAMS.STATUS_EQ, "APPROVED");
        List<RequestRow> newRequestRows = jdbcRequestDao.findBy(whereParams, null, null);
        newRequestRows = jdbcRequestDao.setDataNotBefore(newRequestRows);
        model.addAttribute("approved_reqrows", newRequestRows);

        // Fetch a list of pending CRRs for the RA 
        Map<JdbcCrrDao.WHERE_PARAMS, String> crrWhereParams = new HashMap<JdbcCrrDao.WHERE_PARAMS, String>();
        crrWhereParams.put(JdbcCrrDao.WHERE_PARAMS.STATUS_EQ, "APPROVED"); //NEW,APPROVED,ARCHIVED,DELETED 
        crrWhereParams.put(JdbcCrrDao.WHERE_PARAMS.DN_LIKE, "%L="+L+",OU="+OU+"%"); 
        List<CrrRow> crrRows = jdbcCrrDao.findBy(crrWhereParams, null, null);
        log.debug("crrRows size: ["+crrRows.size()+"]"); 
        crrRows = jdbcCrrDao.setSubmitDateFromData(crrRows); 
        model.addAttribute("crr_reqrows", crrRows);

        // Fetch a list of Raops to be added to the Ra Contact Table
        Map<JdbcCertificateDao.WHERE_PARAMS, String> certWhereParams = new HashMap<JdbcCertificateDao.WHERE_PARAMS, String>();
        certWhereParams.put(JdbcCertificateDao.WHERE_PARAMS.STATUS_LIKE, "VALID");
        List<CertificateRow> certRows = this.jdbcCertDao.findBy(certWhereParams, null, null);

        log.info("Number of certs: " + certRows.size());
        
        certRows = this.addNewRaopsList(certRows);
        
        model.addAttribute("newRAOPs", certRows);
        
        model.addAttribute("lastPageRefreshDate", new Date()); 
    }  

    
    /**
     * Sort the current active operator certificates and check which ones currently
     * exist inside the RaopList Db Table
     * 
     * @param certs Operator Certificate List
     * @return Raops that need to be added
     */
    private List<CertificateRow> addNewRaopsList(List<CertificateRow> certs){
        
        ArrayList<CertificateRow> addRaops = new ArrayList();
        
        boolean check;
        
        for(CertificateRow certRow: certs){
            String certCN = certRow.getCn();
            String certEmail = certRow.getEmail();
            check = true;
            
            // Check if the RA-OP is already present in the list
            if(!addRaops.isEmpty()){
                for(CertificateRow cert: addRaops){
                    if (cert.getCn().equals(certCN)){ // Check if the same cert exists in the 'AddRaopList'
                        check = false;
                    }
                    
                    if (cert.getCn().contains(certCN)) { // Check if a similar cert exists in the 'AddRaopList'
                        check = false;
                    }
                    
                    if (cert.getEmail().equals(certEmail)){ // Check if the certs share the same e-mail address i.e. owner
                        check = false;
                    }
                }
            }
            
            // If not present already, see if an RA-OP Record already exists.
            if(check){
                if("RA Operator".equals(certRow.getRole()) || "CA Operator".equals(certRow.getRole())){
                    List<RaopListRow> raopList = this.jdbcRaopListDao.findBy(certCN, null, null, null);

                    if(raopList.isEmpty()){ // Check if a RA Contact record does not already exist
                        addRaops.add(certRow);
                    }
                }
            }
        }
        
        //Clear and add the sorted list to the original cert list
        certs.clear();
        certs.addAll(addRaops);
        
        // For Debug Use
        log.info("RAOPs to be added: " + certs.size());
        
        for(CertificateRow cert: certs){
            log.info("Cert Information "
                    + "CN: " + cert.getCn()
                    + " Role: " + cert.getRole()
                    + " Status: " + cert.getStatus());
        }
        
        return certs;
    }
    
    
    /**
     * Respond to /caop render by returning its name.
     * 
     * @return caop/home
     */
    @RequestMapping(method = RequestMethod.GET)
    public String caAdminHome(Locale locale, Model model) {
        log.debug("Controller /caop/");
        return "caop/caophome";
    }

    @Inject
    public void setSecurityContextService(SecurityContextService securityContextService) {
        this.securityContextService = securityContextService;
    }

    @Inject
    public void setJdbcRaopListDao(JdbcRaopListDao jdbcRaopListDao) {
        this.jdbcRaopListDao = jdbcRaopListDao;
    }

    @Inject
    public void setJdbcRequestDao(JdbcRequestDao jdbcRequestDao) {
        this.jdbcRequestDao = jdbcRequestDao;
    }

    @Inject
    public void setJdbcCrrDao(JdbcCrrDao jdbcCrrDao) {
        this.jdbcCrrDao = jdbcCrrDao;
    }
    
    @Inject
    public void setJdbcCertificateDao (JdbcCertificateDao jdbcCertDao){
        this.jdbcCertDao = jdbcCertDao;
    }
}
