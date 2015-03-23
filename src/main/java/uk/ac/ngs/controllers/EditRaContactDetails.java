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

import java.util.List;
import javax.inject.Inject;
import javax.validation.Valid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
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
import uk.ac.ngs.forms.AddRaOperatorBean;
import uk.ac.ngs.service.CertUtil;

/**
 * Controller for CA operators to add an RA Contact to the RA Operators Table.
 *
 * @author Josh Hadley
 */
@Controller
@RequestMapping("/raop/editracontactdetails")
@SessionAttributes(value = {EditRaContactDetails.ADD_RA_OPERATOR_FORM_BEAN_SESSIONSCOPE})
public class EditRaContactDetails {

    private static final Log log = LogFactory.getLog(ViewCert.class);
    private JdbcCertificateDao certDao;
    private JdbcRaopListDao raopDao;
    //private final EmailValidator emailValidator = new EmailValidator();
    
     /**
     * Name of the model attribute used to bind form POSTs.  
     */
    public static final String ADD_RA_OPERATOR_FORM_BEAN_SESSIONSCOPE = "editRaContactDetails";
    
    @ModelAttribute
    public void populateDefaultModel(ModelMap modelMap) {

        CertificateRow cert = new CertificateRow();
        modelMap.put("cert", cert);
    }

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
        String l = uk.ac.ngs.service.CertUtil.extractDnAttribute(cert.getDn(), CertUtil.DNAttributeType.L);
        String ou = uk.ac.ngs.service.CertUtil.extractDnAttribute(cert.getDn(), CertUtil.DNAttributeType.OU);
        log.info("Found: " + cn);

        //Check if an RA Operator record already exists in the table.
        List<RaopListRow> raList = this.raopDao.findBy(ou, l, cn, null);
        if(raList.isEmpty())
        {
            modelMap.put("errorMessage", "An RA Operator Record does not exist for this RA Operator.");
        }
    }
    
    @ModelAttribute(AddRaContacts.ADD_RA_OPERATOR_FORM_BEAN_SESSIONSCOPE)
    public AddRaOperatorBean createFormBean() {
        return new AddRaOperatorBean();
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
     * @param addRaOperatorBean
     * @param result
     * @param redirectAttrs
     * @return "redirect:/raop/viewcert" on revocation failure, or 
     * "redirect:/raop/viewcert" on successful revocation. 
     */
    @RequestMapping(value="/edit", method=RequestMethod.POST)
    public String editRaContact(@Valid AddRaOperatorBean addRaOperatorBean, BindingResult result,
        RedirectAttributes redirectAttrs) {
        /*long revoke_cert_key = revokeCertFormBean.getCert_key(); 
        if(result.hasErrors()){
            log.warn("binding and validation errors on fullrevokeCertificate");
            redirectAttrs.addFlashAttribute("errorMessage", "Revocation not submitted");
            StringBuilder bindError = new StringBuilder("");
            for (ObjectError error : result.getAllErrors()) {
                bindError.append(error.getDefaultMessage()).append(" ");
            }
            redirectAttrs.addFlashAttribute("formRevokeErrorMessage", bindError); 
            redirectAttrs.addAttribute("certId", revoke_cert_key);
            return "redirect:/raop/viewcert"; 
        } 

        ProcessRevokeService.ProcessRevokeResult revokeResult = processRevokeService.fullRevokeCertificate(
                revokeCertFormBean, securityContextService.getCaUserDetails().getCertificateRow());

        if (!revokeResult.getSuccess()) {
            redirectAttrs.addFlashAttribute("errorMessage", revokeResult.getErrors().getAllErrors().get(0).getDefaultMessage());
            redirectAttrs.addAttribute("certId", revoke_cert_key);
            return "redirect:/raop/viewcert";
        } else {
            redirectAttrs.addFlashAttribute("message", "Certificate SUSPENDED and an APPROVED CRR was created");
            redirectAttrs.addAttribute("requestId", revokeResult.getCrrId());
            return "redirect:/raop/viewcrr";
        }*/
        
        return "redirect:/raop/viewyourra";
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

