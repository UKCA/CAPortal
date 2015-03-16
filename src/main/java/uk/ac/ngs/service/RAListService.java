/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ngs.service;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ngs.common.MutableConfigParams;
import uk.ac.ngs.dao.JdbcRalistDao;
import uk.ac.ngs.domain.CertificateRow;
import uk.ac.ngs.domain.RalistRow;
import uk.ac.ngs.service.email.EmailService;
import uk.ac.ngs.security.SecurityContextService;

/**
 * Service class for transactional operations on the <pre>Ralist</pre> table. 
 * 
 * @author Josh Hadley
 * 
 */
@Service
public class RAListService {
    private static final Log log = LogFactory.getLog(RAListService.class);
    private JdbcRalistDao jdbcRaDao;
    private EmailService emailService;
    private SecurityContextService securityContextService;
    private MutableConfigParams mutableConfigParams; 
    private final static int flags = Pattern.CASE_INSENSITIVE | Pattern.MULTILINE;
       
    
    /***
     * Insert new RA Object into <pre>Ralist</pre> table and send email 
     * notifications (if configured to do email).
     * This function updates the Ralist table in various ways:
     * <ul>
     *   <li>Resorts the ID Column</li>
     *   <li>Calculates order_id column based on existing RAs</li>
     *   <li>Sets the Active column to enabled</li>
     * </ul>
     * @param l Location of new RA
     * @param ou Organisation Unit of new RA
     * @return 
     * @throws IOException 
     */
    @Transactional
    public int updateRAList(String l, String ou) 
            throws IOException {
        return this.updateRAListHelper(l, ou); 
    }
    
    
     /***
     * To-Do List:
     * 
     *  1.  Query Ralist using the new 'L' to check to see if it currently exists, if exists, for each 
     *      current RA at the location, extract the order_id to calculate a new id
     *  2.  SQL Insert into Ralist the new RA and update the table so it is sorted correctly.
     *  3.  E-mail CA-OPs to inform the creation of a new RA.
     * 
     * 
     */
    private int updateRAListHelper(String l, String ou) 
            throws IOException {
        
        CertificateRow currentUser = this.securityContextService.getCaUserDetails().getCertificateRow();
        
        String newL = l;
        String newR = ou;
        
        List<RalistRow> currentRAs = this.jdbcRaDao.findAllByLocation(l);
        
        //Check to see if an RA at that Location already exists
        if(currentRAs.isEmpty())
        {
            
        }

        int order_id = 123;
        
        // Send email to the CA-OP of the creation of the new RA (if configured to send email) 
        /*if (Boolean.parseBoolean(
                this.mutableConfigParams.getProperty("email.on.host.cert.email.update"))) {

                this.emailService.sendAdminEmailOnRoleChange(
                        certRow.getDn(), newRole, cert_key, certRow.getEmail());
        }*/
        
        // finally update the ralist table
        return this.jdbcRaDao.insertIntoDB(order_id, l, ou);
    }
    
    @Inject 
    public void setJdbcRaDao(JdbcRalistDao jdbcRaDao){
        this.jdbcRaDao = jdbcRaDao; 
    }
    
    @Inject
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
    
    @Inject
    public void setMutableConfigParams(MutableConfigParams mutableConfigParams) {
        this.mutableConfigParams = mutableConfigParams;
    }
}
