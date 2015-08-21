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

import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.validation.Valid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ngs.dao.JdbcCertificateDao;
import uk.ac.ngs.dao.JdbcRaopListDao;
import uk.ac.ngs.domain.CertificateRow;
import uk.ac.ngs.domain.RaopListRow;
import uk.ac.ngs.forms.RaContactBean;
import uk.ac.ngs.security.SecurityContextService;
import uk.ac.ngs.service.CertUtil;
import uk.ac.ngs.service.RaContactService;

/**
 * Controller for CA operators to add an RA Contact to the RA Operators Table.
 *
 * @author Josh Hadley
 */
@Controller
@RequestMapping("/raop/editracontactdetails")
@SessionAttributes(value = {EditRaContactDetails.EDIT_RA_OPERATOR_FORM_BEAN_SESSIONSCOPE})
public class EditRaContactDetails {

    private static final Log log = LogFactory.getLog(ViewCert.class);
    private JdbcCertificateDao certDao;
    private JdbcRaopListDao raopDao;
    private SecurityContextService securityContextService;
    private RaContactService raService;
    //private final EmailValidator emailValidator = new EmailValidator();
    
     
    // Name of the model attribute used to bind form POSTs.  
    public static final String EDIT_RA_OPERATOR_FORM_BEAN_SESSIONSCOPE = "editRaContactBean";
    
    /**
     * Populate the default model
     * @param modelMap the current ModelMap
     */
    @ModelAttribute
    public void populateDefaultModel(ModelMap modelMap) {
        CertificateRow cert = new CertificateRow();
        modelMap.put("cert", cert);
    }

    /**
     * Setup the loaded page based on the given certificate id and ModelMap
     * @param certId Raop Certificate ID
     * @param modelMap Current ModelMap
     */
    @RequestMapping(method = RequestMethod.GET)
    public void handleEditRaContactsDetails(@RequestParam(required = true) Integer certId, ModelMap modelMap) {
        
        //Check if the CN is avaliable
        if (certId == null) {
            modelMap.put("errorMessage", "No RA-OP with this CN exists. ");
            return;
        }

        //Fetch Cert
        CertificateRow cert = this.certDao.findById(certId);
        modelMap.put("cert", cert);
        
        //Collect cn, l and ou from Cert
        String cn = cert.getCn();
        log.info("Found: " + cn);
        String l = uk.ac.ngs.service.CertUtil.extractDnAttribute(cert.getDn(), CertUtil.DNAttributeType.L);
        String ou = uk.ac.ngs.service.CertUtil.extractDnAttribute(cert.getDn(), CertUtil.DNAttributeType.OU);
        
        //Check if an RA Operator record already exists in the table.
        List<RaopListRow> raList = this.raopDao.findBy(ou, l, cn, null);
        if(raList.isEmpty())
        {
            modelMap.put("errorMessage", "An RA Operator Record does not exist for this RA Operator.");
        }
        
        RaopListRow raop = raList.get(0);
        
        RaContactBean editRaContactBean = createDefaultFormBean(raop);
        editRaContactBean.setCert_key(certId);
        modelMap.put("editRaContactBean", editRaContactBean);
    }
    
    /***
     * Creates the default form bean
     * @param raop RaopListRow to edit
     * @return Constructed Bean
     */
    private RaContactBean createDefaultFormBean(RaopListRow raop){
        RaContactBean contact = new RaContactBean();
        
        if(raop.getName() != null){
            contact.setName(raop.getName());
        }
        else {
            contact.setName(raop.getCn());
        }
        
        if(raop.getEmail() != null){
            contact.setEmailAddress(raop.getEmail());
        }
        if(raop.getPhone() != null){
            contact.setPhone(raop.getPhone());
        }
        if(raop.getStreet() != null){
            contact.setStreet(raop.getStreet());
        }
        if(raop.getCity() != null){
            contact.setCity(raop.getCity());
        }
        if(raop.getPostcode() != null){
            contact.setPostcode(raop.getPostcode());
        }
        if(raop.getTrainingDate() != null){
            contact.setTraining(raop.getTrainingDate().toString());
        }
        
        return contact;
    }
    
    @ModelAttribute(EditRaContactDetails.EDIT_RA_OPERATOR_FORM_BEAN_SESSIONSCOPE)
    public RaContactBean createFormBean() {
        return new RaContactBean();
    }
    
    /**
     * Handle POSTs to "/raop/editracontactdetails/edit" to add a new RA Contact to 
     * the RA Operator DB Table.
     * <p>
     * The view is always redirected and redirect attributes added as necessary; 
     * if the insert is successful we redirect to a different 
     * page to view the RA Contact details, if the insert fails we need to re-display the 
     * current details by repopulating the form. 
     * 
     * @param raContactBean
     * @param result
     * @param redirectAttrs
     * @return "redirect:/raop/viewcert" on revocation failure, or 
     * "redirect:/raop/viewcert" on successful revocation. 
     * @throws java.io.IOException 
     */
    @RequestMapping(value="/edit", method=RequestMethod.POST)
    public String editRaContact(@Valid RaContactBean raContactBean, BindingResult result,
        RedirectAttributes redirectAttrs) throws IOException {
        
        long raCert_key = raContactBean.getCert_key();
        CertificateRow cert = this.certDao.findById(raCert_key);
       
        if(result.hasErrors() || !canUserDoEdit(cert) || ! doesRowExist(cert)){
            log.warn("binding and validation errors on editRaContact");
            log.info(result.getAllErrors());
            redirectAttrs.addFlashAttribute("errorMessage", "Changes not submitted");
            StringBuilder bindError = new StringBuilder("");
            for (ObjectError error : result.getAllErrors()) {
                bindError.append(error.getDefaultMessage()).append(" ");
            }
            redirectAttrs.addFlashAttribute("formRevokeErrorMessage", bindError); 
            redirectAttrs.addAttribute("certId", raCert_key);
            return "redirect:/raop/editracontactdetails"; 
        } 
    
        String loc = uk.ac.ngs.common.CertUtil.extractDnAttribute(cert.getDn(), uk.ac.ngs.common.CertUtil.DNAttributeType.L);
        String ou = uk.ac.ngs.common.CertUtil.extractDnAttribute(cert.getDn(), uk.ac.ngs.common.CertUtil.DNAttributeType.OU); 
        
        List<RaopListRow> raop = this.raopDao.findBy(ou, loc, null, Boolean.TRUE);
              
        if(raop.isEmpty()){
            log.warn("binding and validation errors on editRaContact");
            log.info(result.getAllErrors());
            redirectAttrs.addFlashAttribute("errorMessage", "Raop Row Does Not Exist");
            StringBuilder bindError = new StringBuilder("");
            for (ObjectError error : result.getAllErrors()) {
                bindError.append(error.getDefaultMessage()).append(" ");
            }
            redirectAttrs.addFlashAttribute("formRevokeErrorMessage", bindError); 
            redirectAttrs.addAttribute("certId", raCert_key);
            return "redirect:/raop/editracontactdetails"; 
        } 
                       
        //RaContactService raService = new RaContactService();
        RaContactService.RaContactServiceResult raContactResult = raService.editRaContact(raContactBean);

        if (!raContactResult.getSuccess()) {
            redirectAttrs.addFlashAttribute("errorMessage", raContactResult.getErrors().getAllErrors().get(0).getDefaultMessage());
            redirectAttrs.addAttribute("certId", raCert_key);
            return "redirect:/raop/editracontactdetails";
        } else {
            redirectAttrs.addFlashAttribute("message", "Contact Details Updated");
            redirectAttrs.addAttribute("requestId", raContactResult.getCertKey());
            return "redirect:/raop/editracontactdetails?certId=" + raContactResult.getCertKey();
        }
    }
    
    
    // Check to see if the current user can edit the RA Contact as either a CA-OP, or as a RA-OP in the same RA 
    private boolean canUserDoEdit(CertificateRow cert) {
        if (this.securityContextService.getCaUserDetails().getAuthorities() 
                .contains(new SimpleGrantedAuthority("ROLE_CAOP"))) { //Check if the current user is a CA-OP
            return true;
        } else {  //Check if the current user is the same person as the requested RAOP and have the correct RA-OP privelleges
            long certKey = cert.getCert_key();
            long raOpKey = this.securityContextService.getCaUserDetails().getCertificateRow().getCert_key();
            
            if (this.securityContextService.getCaUserDetails().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_RAOP")))
            {
                if (certKey == raOpKey) {
                    return true;
                } 
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
    
    @Inject
    public void setJdbcCertificateDao(JdbcCertificateDao dao) {
        this.certDao = dao;
    }
    
    @Inject
    public void setJdbcRaopListDao(JdbcRaopListDao raopDao) {
        this.raopDao = raopDao;
    }
    
    @Inject
    public void setSecurityContextService(SecurityContextService securityContextService) {
        this.securityContextService = securityContextService;
    }
    
    @Inject 
    public void setRaContactService(RaContactService raContactService){
        this.raService = raContactService;
    }
}

