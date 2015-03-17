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
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ngs.security.SecurityContextService;
import uk.ac.ngs.common.CertUtil;
import uk.ac.ngs.dao.JdbcCertificateDao;
import uk.ac.ngs.dao.JdbcRaopListDao;
import uk.ac.ngs.domain.CertificateRow;
import uk.ac.ngs.domain.RaopListRow;

/**
 * Controller for the <tt>/raop/viewyourra</tt> page. 
 *
 * @author Josh Hadley  
 */
@Controller
@RequestMapping("/raop/viewyourra")
public class ViewYourRA {
    private static final Log log = LogFactory.getLog(ViewYourRA.class);
    private JdbcRaopListDao jdbcRaopListDao; 
    private JdbcCertificateDao certDao; 
    private final Pattern negatedOuPattern = Pattern.compile("[^a-zA-Z0-9\\-]");
    private SecurityContextService securityContextService;

    public static class ViewRaContact {
       private final List<RaopListRow> raoplistRows; 
       private final CertificateRow certRow; 
       private final String loc; 
       private final String ou; 
       public ViewRaContact(CertificateRow certRow, String loc, String ou, 
               List<RaopListRow> raoplistRows){
          this.raoplistRows = raoplistRows; 
          this.certRow = certRow; 
          this.loc = loc; 
          this.ou = ou;
       }

        /**
         * @return the certRow
         */
        public CertificateRow getCertRow() {
            return certRow;
        }

        /**
         * @return the raoplistRows
         */
        public List<RaopListRow> getRaoplistRows() {
            return raoplistRows;
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
    }

    /**
     * ModelAttribute annotations defined on a method in a controller are
     * invoked before RequestMapping methods, within the same controller.
     *
     * @param model
     */
    @ModelAttribute
    public void populateModel(ModelMap model) {
        log.debug("populateMessage"); 
        
        //Grab User's Certificate and extract user's OU and L.
        CertificateRow curCert = this.securityContextService.getCaUserDetails().getCertificateRow();
        String ou = CertUtil.extractDnAttribute(curCert.getDn(), CertUtil.DNAttributeType.OU); 
        String l = CertUtil.extractDnAttribute(curCert.getDn(), CertUtil.DNAttributeType.L); 
        
        //Grab User's RA details
        RaopListRow raop = new RaopListRow();
        List<RaopListRow> raRow = this.jdbcRaopListDao.findBy(ou, l, curCert.getCn(), Boolean.TRUE);
        
        if(!raRow.isEmpty()) {
            raop = raRow.get(0);
        }
        
        log.info("Current RA-OP: " + raop);
        
        if (negatedOuPattern.matcher(ou).find()) {
            // provide an empty list instead
            model.put("ou", "Invalid ou param"); //["+HtmlUtils.htmlEscapeHex(ou)+"]");  
            //model.put("raRows", new ArrayList<RaopListRow>(0));
            model.put("user", null);
            model.put("contacts", new ArrayList<ViewRaContact>(0)); 
        } else {
            // escape reflected untrusted content   
            model.put("ou", ou);   
           
            model.put("user", raop);
            
            List<ViewRaContact> contacts = new ArrayList<ViewRaContact>(0); 
            List<CertificateRow> certRows = this.certDao.findActiveRAsBy(null, ou); 
            for(CertificateRow certRow : certRows){
                String loc = CertUtil.extractDnAttribute(certRow.getDn(), CertUtil.DNAttributeType.L); 
                List<RaopListRow> raoplistRowsWithSameCnOuLoc = this.jdbcRaopListDao.findBy( ou, loc, certRow.getCn(), Boolean.TRUE);
                
                //Check if found cert is not the current user, else it is added to the list
                if(!curCert.getDn().equals(certRow.getDn())){
                   ViewRaContact contact = new ViewRaContact(certRow, loc, ou, raoplistRowsWithSameCnOuLoc); 
                   contacts.add(contact);  
                }
            }
            
            if (contacts.isEmpty())
            {
                model.put("contacts", "Empty"); 
            }
            else{
                model.put("contacts", contacts); 
            }
        }
    }

    /**
     * Select the raop/viewyourra view to render.
     *
     * @return raop/viewyourra
     */
    @RequestMapping(method = RequestMethod.GET)
    public String viewYourRA(Locale locale, Model model) {
        log.debug("Controller /raop/viewyourra");
        return "raop/viewyourra";
    }
    
    @Inject
    public void setSecurityContextService(SecurityContextService securityContextService) {
        this.securityContextService = securityContextService;
    }
    
    
    @Inject
    public void setJdbcRaopListDao(JdbcRaopListDao jdbcRaopListDao){
       this.jdbcRaopListDao = jdbcRaopListDao;  
    }

    
    @Inject
    public void setJdbcCertificateDao(JdbcCertificateDao dao) {
        this.certDao = dao;
    }
}

