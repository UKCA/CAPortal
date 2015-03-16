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

import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.ngs.dao.JdbcCertificateDao;
import uk.ac.ngs.dao.JdbcRaopListDao;
import uk.ac.ngs.domain.RaopListRow;
import uk.ac.ngs.domain.CertificateRow;
import uk.ac.ngs.validation.EmailValidator;

/**
 * Controller for CA operators to view a RA operator's contact details.
 *
 * @author Josh Hadley
 */
@Controller
@RequestMapping("/caop/viewradetails")
public class ViewRaDetails {

    private static final Log log = LogFactory.getLog(ViewRaDetails.class);
    private JdbcCertificateDao certDao;
    private JdbcRaopListDao raopDao;
    private final EmailValidator emailValidator = new EmailValidator();
    
    @ModelAttribute
    public void populateDefaultModel(@RequestParam(required = false) String ou,
            @RequestParam(required = false) String l, @RequestParam(required = false) Integer certKey, ModelMap modelMap) {

        RaopListRow raop = new RaopListRow();
        modelMap.put("raop", raop);
        modelMap.put("lastViewRefreshDate", new Date()); 
        if (certKey != 0) {
            CertificateRow cert = this.certDao.findById(certKey);
            String cn = cert.getCn();
            
            List<RaopListRow> raList = this.raopDao.findBy(ou, l, cn, null);
            if(!raList.isEmpty()){
                raop = raList.get(0);
                modelMap.put("raop", raop);
            }
        } 
    }

    @RequestMapping(method = RequestMethod.GET)
    public void handleViewRaDetails(@RequestParam(required = false) String ou,
            @RequestParam(required = false) String l, @RequestParam(required = false) Integer certKey, ModelMap modelMap) {
        
        //Check if the CN is avaliable
        if (certKey == 0) {
            modelMap.put("errorMessage", "No RA-OP with this CN exists. ");
            return;
        }

        CertificateRow cert = this.certDao.findById(certKey);
        String cn = cert.getCn();
        
        log.info("Found: " + cn);
        
        //Get the specified RA-OP
        List<RaopListRow> raList = this.raopDao.findBy(ou, l, cn, null);
        RaopListRow raop = raList.get(0);
        
        modelMap.put("raop", raop);
    }

    @Inject
    public void setJdbcCertificateDao(JdbcCertificateDao dao) {
        this.certDao = dao;
    }
    
    @Inject
    public void setJdbcRaopListDao(JdbcRaopListDao raopDao) {
        this.raopDao = raopDao;
    }
}