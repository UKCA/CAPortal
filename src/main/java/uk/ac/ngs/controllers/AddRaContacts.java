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
import javax.inject.Inject;
import javax.validation.Valid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import uk.ac.ngs.dao.JdbcRalistDao;
import uk.ac.ngs.dao.JdbcRaopListDao;
import uk.ac.ngs.domain.CertificateRow;
import uk.ac.ngs.domain.RalistRow;
import uk.ac.ngs.domain.RaopListRow;
import uk.ac.ngs.forms.RaContactBean;
import uk.ac.ngs.service.CertUtil;
import uk.ac.ngs.service.RaContactService;

/**
 * Controller for CA operators to add an RA Contact to the RA Operators Table.
 *
 * @author Josh Hadley
 */
@Controller
@RequestMapping("/caop/addracontacts")
@SessionAttributes(value = {AddRaContacts.ADD_RA_OPERATOR_FORM_BEAN_SESSIONSCOPE})
public class AddRaContacts {

    private static final Log log = LogFactory.getLog(ViewCert.class);
    private JdbcCertificateDao certDao;
    private JdbcRaopListDao raopDao;
    private JdbcRalistDao ralistDao;
    //private final EmailValidator emailValidator = new EmailValidator();
    
     /**
     * Name of the model attribute used to bind form POSTs.  
     */
    public static final String ADD_RA_OPERATOR_FORM_BEAN_SESSIONSCOPE = "addRaOperatorBean";
    
    @ModelAttribute
    public void populateDefaultModel(ModelMap modelMap) {

        CertificateRow cert = new CertificateRow();
        modelMap.put("cert", cert);
        
        List<RalistRow> rows = this.ralistDao.findAllByActive(true, null, null);  
        List<String> raArray = new ArrayList<String>(rows.size());

        // then add all other RAs
        for (RalistRow row : rows) {
            // BUG - have had trouble submitting RA values that contain whitespace, 
            // so have replaced whitespace in ra with underscore 
            raArray.add(row.getOu()+"_"+row.getL()); 
        }
        modelMap.addAttribute("raList", raArray.toArray()); 
    }

    @RequestMapping(method = RequestMethod.GET)
    public void handleAddRaContacts(@RequestParam(required = false) Integer certId, ModelMap modelMap) {
        
        //Check if the certId is avaliable, if not then display an empty model
        if (certId == null) {
            return;
        }  
        
        //Fetch Cert
        CertificateRow cert = this.certDao.findById(certId);
        modelMap.put("cert", cert);

        //Collect cn, l and ou from Cert
        String cn = cert.getCn();
        String l = uk.ac.ngs.service.CertUtil.extractDnAttribute(cert.getDn(), CertUtil.DNAttributeType.L);
        String ou = uk.ac.ngs.service.CertUtil.extractDnAttribute(cert.getDn(), CertUtil.DNAttributeType.OU);
        log.info("Found: " + cn);
        
        //Check if an RA Operator record already exists in the table.
        List<RaopListRow> raList = this.raopDao.findBy(ou, l, cn, null);
        if(!raList.isEmpty())
        {
            modelMap.put("errorMessage", "An RA Operator Record already exists for this RA Operator.");
        }
        
        RaContactBean contact = createFormBean();
        contact.setCert_key(cert.getCert_key());
        contact.setName(cn);
        contact.setEmailAddress(cert.getEmail());
        contact.setRa(ou + "_" + l);
        modelMap.put("addRaOperatorBean", contact);
        
        List<RalistRow> rows = this.ralistDao.findAllByActive(true, null, null);  
        List<String> raArray = new ArrayList<String>(rows.size());

        // then add all other RAs
        for (RalistRow row : rows) {
            // BUG - have had trouble submitting RA values that contain whitespace, 
            // so have replaced whitespace in ra with underscore 
            raArray.add(row.getOu()+"_"+row.getL()); 
        }
        modelMap.addAttribute("raList", raArray.toArray()); 
    }   
    
    @ModelAttribute(AddRaContacts.ADD_RA_OPERATOR_FORM_BEAN_SESSIONSCOPE)
    public RaContactBean createFormBean() {
        return new RaContactBean();
    }
    
    /**
     * Handle POSTs to "/caop/addracontacts/add" to add a new RA Contact to 
     * the RA Operator DB Table.
     * <p>
     * The view is always redirected and redirect attributes added as necessary; 
     * if the insert is successful we redirect to a different 
     * page to view the RA Contact details, if the insert fails we need to re-display the 
     * current details by repopulating the form. 
     * 
     * @param addRaOperatorBean
     * @param result
     * @param redirectAttrs
     * @return "redirect:/raop/viewcert" on revocation failure, or 
     * "redirect:/raop/viewcert" on successful revocation. 
     */
    /*@RequestMapping(value="/add", method=RequestMethod.POST)
    public String addRaContact(@Valid RaContactBean addRaOperatorBean, BindingResult result,
        RedirectAttributes redirectAttrs) {
        String raEmail = addRaOperatorBean.getEmailAddress();
        if(result.hasErrors()){
            log.warn("binding and validation errors on editRaContact");
            redirectAttrs.addFlashAttribute("errorMessage", "Changes not submitted");
            StringBuilder bindError = new StringBuilder("");
            for (ObjectError error : result.getAllErrors()) {
                bindError.append(error.getDefaultMessage()).append(" ");
            }
            redirectAttrs.addFlashAttribute("formRevokeErrorMessage", bindError); 
            redirectAttrs.addAttribute("raop", raEmail);
            return "redirect:/raop/viewcert"; 
        } 

        RaContactService raService = new RaContactService();
        RaContactService.RaContactServiceResult raContactResult = raService.addRaContact(addRaOperatorBean);

        if (!raContactResult.getSuccess()) {
            redirectAttrs.addFlashAttribute("errorMessage", raContactResult.getErrors().getAllErrors().get(0).getDefaultMessage());
                        redirectAttrs.addAttribute("raop", raEmail);
            return "redirect:/raop/viewyourra";
        } else {
            redirectAttrs.addFlashAttribute("message", "Certificate SUSPENDED and an APPROVED CRR was created");
            redirectAttrs.addAttribute("requestId", raContactResult.getCertKey());
            return "redirect:/raop/viewyourra";
        }
    }*/
    
    @Inject
    public void setJdbcCertificateDao(JdbcCertificateDao dao) {
        this.certDao = dao;
    }
    
    @Inject
    public void setJdbcRaopListDao(JdbcRaopListDao raopDao) {
        this.raopDao = raopDao;
    }
    
    @Inject
    public void setJdbcRalistDao(JdbcRalistDao ralistDao) {
        this.ralistDao = ralistDao;
    }
}
