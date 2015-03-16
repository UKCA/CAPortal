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
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ngs.common.Pair;
import uk.ac.ngs.common.PartialPagedListHolder;
import uk.ac.ngs.dao.JdbcCertificateDao;
import uk.ac.ngs.dao.JdbcRalistDao;
import uk.ac.ngs.dao.JdbcRaopListDao;
import uk.ac.ngs.domain.CertificateRow;
import uk.ac.ngs.domain.RalistRow;
import uk.ac.ngs.domain.RaopListRow;
import uk.ac.ngs.forms.GotoPageNumberFormBean;
import uk.ac.ngs.forms.SearchRaopFormBean;
import uk.ac.ngs.security.SecurityContextService;

/**
 * Controller for the "/caop/raoplist" page RA-OP Detail searches. 
 * <p>
 * The controller handles pagination through certificate domain object results.
 * Searches can be submitted via GET or POST - both do the same validation.  
 * On submission of a form POST, posted params are validated and the request
 * is re-directed to the GET handler with the appropriate GET request params 
 * for the search. This way, a similar code path is used to execute DB searches
 * (same apart from the GET/POST entry point). 
 * <p>
 *
 * @author Josh Hadley
 */
@Controller
@RequestMapping("/caop/raoplist")
@SessionAttributes(value = {RaOpList.SEARCH_RAOP_FORM_BEAN_SESSIONSCOPE})
public class RaOpList {

    private static final Log log = LogFactory.getLog(RaOpList.class);
    private SecurityContextService securityContextService;
    private JdbcRaopListDao raopDao;
    private JdbcRalistDao ralistDao;
    private JdbcCertificateDao certDao;
    /**
     * Name of the model attribute used to bind form POSTs.  
     */
    public static final String SEARCH_RAOP_FORM_BEAN_SESSIONSCOPE = "searchRaopFormBean"; 
    /**
     * Name of the model attribute used to bind GET request search parameters. 
     */
    public static final String SEARCH_RAOP_FORM_BEAN_GET_REQUESTSCOPE = "searchRaopFormBean_REQUESTSCOPE"; 
    /**
     * Name of model attribute that stores search results in session. Can be 
     * accessed in JSP using <code>${sessionScope.certSearchPageHolder}</code> 
     */
    public static final String RAOP_PAGE_LIST_HOLDER_SESSIONSCOPE = "raopSearchPageHolder"; 
    
    /**
     * Name of the model attribute that stores the list of RAs.  
     */
    public static final String RA_ARRAY_REQUESTSCOPE = "ralistArray"; 

    /**
     * RA Contact Class.
     */
    public static class ViewRaContact {
       private final List<RaopListRow> raoplistRows;
       private final int raoplistSize;
       private final CertificateRow certRow; 
       private final String loc; 
       private final String ou; 
       public ViewRaContact(CertificateRow certRow, String loc, String ou, List<RaopListRow> raoplistRows){
          this.raoplistRows = raoplistRows; 
          this.certRow = certRow; 
          this.loc = loc; 
          this.ou = ou;
          this.raoplistSize = this.raoplistRows.size();
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
        
        public int getRaoplistSize(){
            return raoplistSize;
        }
    }
    
    
    /**
     * ModelAttribute annotations defined on a method in a controller are
     * invoked before RequestMapping methods, within the same controller.
     *
     * @param model
     * @param session
     */
    @ModelAttribute
    public void populateModel(Model model, HttpSession session) {
        //log.debug("populateModel");
        // Populate model with an empty list if session var is not present (e.g. session expired) 
    	PartialPagedListHolder<ViewRaContact> pagedListHolder; 
    	Object testNotNull = session.getAttribute(RaOpList.RAOP_PAGE_LIST_HOLDER_SESSIONSCOPE);
    	if(testNotNull == null){
    		pagedListHolder = new PartialPagedListHolder<ViewRaContact>(new ArrayList<ViewRaContact>(0));
            session.setAttribute(RaOpList.RAOP_PAGE_LIST_HOLDER_SESSIONSCOPE, pagedListHolder);
    	} else {
    		pagedListHolder = (PartialPagedListHolder<ViewRaContact>)testNotNull; 
    	}	

        // Populate the Location list pull down 
        List<RalistRow> rows = this.ralistDao.findAllByActive(true, null, null);  
        List<String> raArray = new ArrayList<String>(rows.size());
               
        //Add RAs
        raArray.add("All"); 
        
        // then add all other RAs
        for (RalistRow row : rows) {
            // BUG - have had trouble submitting RA values that contain whitespace, 
            // so have replaced whitespace in ra with underscore 
            raArray.add(row.getOu()+"_"+row.getL()); 
        }
        model.addAttribute(RA_ARRAY_REQUESTSCOPE, raArray.toArray()); 
    }

    /**
     * Invoked initially to add the 'searchRaopFormBean' model attribute. Once
     * created the RaOpList.SEARCH_CERT_FORM_BEAN comes from the HTTP session (see
     * SessionAttributes annotation)
     *
     * @return
     */
    @ModelAttribute(RaOpList.SEARCH_RAOP_FORM_BEAN_SESSIONSCOPE)
    public SearchRaopFormBean createFormBean() {
        return new SearchRaopFormBean();
    }

    @ModelAttribute(SEARCH_RAOP_FORM_BEAN_GET_REQUESTSCOPE) 
    public SearchRaopFormBean createGetRequestFormBean(){
        return new SearchRaopFormBean(); 
    }

    /**
     * Add the 'gotoPageFormBean' to the model if not present.
     * @return
     */
    @ModelAttribute("gotoPageFormBean")
    public GotoPageNumberFormBean createGotoFormBean() {
        return new GotoPageNumberFormBean();
    }


    /**
     * Handle GETs to <pre>/caop/raoplist</pre> for Idempotent page refreshes.
     * @return 
     */
    @RequestMapping(method = RequestMethod.GET)
    public String handleGetRequest() {
        return "caop/raoplist";
    }
    
    /**
     * Handle POSTs to "/caop/raoplist" to do a certificate search.
     * The method processes the form input and then redirects to the "/search" 
     * URL (handled in this controller) with URL encoded GET params that define 
     * the search parameters. 
     * 
     * @return "redirect:/caop/raoplist/search
     */
    @RequestMapping(method = RequestMethod.POST)
    public String submitSearchViaPost(
            @Valid @ModelAttribute(SEARCH_RAOP_FORM_BEAN_SESSIONSCOPE) SearchRaopFormBean searchRaopFormBean, BindingResult result,
            RedirectAttributes redirectAttrs, SessionStatus sessionStatus,
            Model model, HttpSession session) {

        if (result.hasErrors()) {
            log.warn("binding and validation errors");
            return "caop/raoplist";
        }
        // When we submit a new search via post, re-set the start row to zero. 
        searchRaopFormBean.setStartRow(0); 
        
        // Store a success message for rendering on the next request after redirect
        // I have had issues with using flash attributes - if the given 
        // searchRaopFormBean contains spaces in some of its values, then 
        // the flash attribute does not always render after redirecting to the view
        //redirectAttrs.addFlashAttribute("message", "Search Submitted OK");
        
        // Copy our post model attributes to redirect attributes in URL 
        redirectAttrs.addAllAttributes(this.getRedirectParams(searchRaopFormBean)); 
        // Now submit to our GET '/search' handler method to run the query. 
        // Note, I do not manually append request attributes as commented out 
        // in the return redirect: because Spring Views can? be cached based on 
        // the view-name, so if you start appending the parameters to the name 
        // like the code below, each view instance will be cached, see:  
        // http://forum.springsource.org/showthread.php?86633-Spring-3-annotated-controllers-redirection-strings-and-appending-parameters-to-URL
        return "redirect:/caop/raoplist/search";//+"?"+this.buildGetRequestParams(searchRaopFormBean); 
    }
   
    /**
     * Handle GETs to "/caop/raoplist/search?search=params&for=dbquery" 
     * to perform a DB search.  
     * <p>
     * Processes the known GET params and submits a query to the DB. Unknown 
     * GET request parameters are ignored. Binding/validation of GET params 
     * is done via Spring @ModelAttribute binding with the given SearchRaopFormBean. 
     * If only a partial query is specified, e.g. "/search?name=some cn", 
     * default values as defined in SearchRaopFormBean are applied. After successful 
     * binding/validation/query, the SEARCH_RAOP_FORM_BEAN_SESSIONSCOPE model attribute 
     * is updated.  
     * 
     * @return "caop/raoplist"
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String submitSearchViaGet(
            @Valid @ModelAttribute(SEARCH_RAOP_FORM_BEAN_GET_REQUESTSCOPE) 
                    SearchRaopFormBean searchRaopFormBeanGetReq, BindingResult result, 
            RedirectAttributes redirectAttrs, SessionStatus sessionStatus,
            Model model, HttpSession session) {
        
        //@RequestMapping(method=RequestMethod.GET, params={"gosearch"}) 
        if (result.hasErrors()) {
            log.warn("binding and validation errors on submitViaGet");
            return "caop/raoplist";
        }
        // Update our session-scoped @ModelAttribute form bean (SEARCH_RAOP_FORM_BEAN_SESSIONSCOPE) 
        // with the request-scoped @ModelAttribute get-request form bean 
        // (SEARCH_CERT_FORM_BEAN_GET_REQUESTSCOPE),  then run the DB search. 
        model.addAttribute(SEARCH_RAOP_FORM_BEAN_SESSIONSCOPE, searchRaopFormBeanGetReq); 
        this.runSearchUpdateResultsInSession(searchRaopFormBeanGetReq, session); 
        model.addAttribute("searchOk", "Search Submitted/Refreshed OK");
        return "caop/raoplist";
    }

    /**
     * Use the given SearchRaopFormBean to submit the DB search and put the results 
     * in session under the CERT_PAGE_LIST_HOLDER session attribute.  
     */
    private void runSearchUpdateResultsInSession(SearchRaopFormBean searchRaopFormBean, HttpSession session){
        // reset the last search date and limit/offset vals. 
        session.setAttribute("lastRaopSearchDate_session", new Date());
        int limit = searchRaopFormBean.getShowRowCount();
        int startRow = searchRaopFormBean.getStartRow(); 
        Pair<List<ViewRaContact>, Integer> results =
                this.submitRASearch(searchRaopFormBean, limit, startRow);
        List<ViewRaContact> rows = results.first;
        Integer totalRows = results.second;
        session.setAttribute("searchRaopTotalRows_session", totalRows);

        // Typically, a PagedListHolder instance will be instantiated with a list of beans, 
        // put into the session, and exported as model (in populateModel as we redirect).
        PartialPagedListHolder<ViewRaContact> pagedListHolder = new PartialPagedListHolder<ViewRaContact>(rows, totalRows);
        pagedListHolder.setPageSize(limit);
        pagedListHolder.setRow(startRow);
        session.setAttribute(RaOpList.RAOP_PAGE_LIST_HOLDER_SESSIONSCOPE, pagedListHolder);
        // If no validation errors, typically you would e.g. save to a db and clear the 
        // "session form bean" attribute from the session via SessionStatus.setComplete().
        //sessionStatus.setComplete(); 
    }

    private Map<String, Object> getRedirectParams(SearchRaopFormBean searchRaopFormBean) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (StringUtils.hasText(searchRaopFormBean.getName())) {
            params.put("name", searchRaopFormBean.getName());
        }
        if (StringUtils.hasText(searchRaopFormBean.getEmailAddress())) {
            params.put("emailAddress", searchRaopFormBean.getEmailAddress());
        }
        if (searchRaopFormBean.getSearchNullEmailAddress() != null) {
            params.put("searchNullEmailAddress", searchRaopFormBean.getSearchNullEmailAddress().toString());
        }
        if (searchRaopFormBean.getStartRow() != null) {
            params.put("startRow", searchRaopFormBean.getStartRow().toString());
        }
        if (searchRaopFormBean.getShowRowCount() != null) {
            params.put("showRowCount", searchRaopFormBean.getShowRowCount().toString());
        }
        if (searchRaopFormBean.getRa() != null) {
            params.put("ra", searchRaopFormBean.getRa());
        }
        return params;
    }
    
    /**
     * Handle POSTs to "/caop/raoplist/goto" to paginate to a specific row
     * in the results list.
     * @return "redirect:/caop/raoplist/search?search=params&for=dbquery"
     */
    @RequestMapping(value = "/goto", method = RequestMethod.POST)
    public String submitGotoPage(
            @Valid @ModelAttribute("gotoPageFormBean") GotoPageNumberFormBean gotoPageFormBean,
            BindingResult result,
            RedirectAttributes redirectAttrs, SessionStatus sessionStatus,
            Model model, HttpSession session) {

        if (result.hasErrors()) {
            return "caop/raoplist";
        }
        // Get the search data from session. 
        SearchRaopFormBean sessionSearchRaopFormBean =
                (SearchRaopFormBean) session.getAttribute(RaOpList.SEARCH_RAOP_FORM_BEAN_SESSIONSCOPE);
        PartialPagedListHolder<ViewRaContact> pagedListHolder =
                (PartialPagedListHolder<ViewRaContact>) session.getAttribute(RaOpList.RAOP_PAGE_LIST_HOLDER_SESSIONSCOPE);

        int startRow = gotoPageFormBean.getGotoPageNumber() - 1; //zero offset when used in SQL  

        // If requested start row is > than total rows or < 0, just return        
        if (startRow > pagedListHolder.getTotalRows() || startRow < 0) {
            return "caop/raoplist";
        }

        // Update requested start row and re-submit the search as a GET request  
        sessionSearchRaopFormBean.setStartRow(startRow);  
        redirectAttrs.addAllAttributes(this.getRedirectParams(sessionSearchRaopFormBean)); 
        return "redirect:/caop/raoplist/search"; //+"?"+this.buildGetRequestParams(sessionSearchRaopFormBean);  
    }

    /**
     * Handle GETs to '/caop/raoplist/page?page=next|prev|first|last'.  
     * Used for paging through the certificate list when clicking Next,Prev,First,Last links.
     * @param page
     * @param session
     * @return "redirect:/caop/raoplist/search?multiple=search&params=go here"
     */
    @RequestMapping(value = "page", method = RequestMethod.GET)
    public String handlePageRequest(@RequestParam(required = false) String page,
            HttpSession session, RedirectAttributes redirectAttrs) {
        //String page = request.getParameter("page");
        //http://balusc.blogspot.co.uk/2008/10/effective-datatable-paging-and-sorting.html

        // Get the search data from session. 
        SearchRaopFormBean searchRaopFormBean =
                (SearchRaopFormBean) session.getAttribute(RaOpList.SEARCH_RAOP_FORM_BEAN_SESSIONSCOPE);
        PartialPagedListHolder<ViewRaContact> pagedListHolder =
                (PartialPagedListHolder<ViewRaContact>) session.getAttribute(RaOpList.RAOP_PAGE_LIST_HOLDER_SESSIONSCOPE);

        if ("next".equals(page) || "prev".equals(page) || "first".equals(page) || "last".equals(page)) {
            int limit = searchRaopFormBean.getShowRowCount(); //pagedListHolder.getPageSize();
            int startRow = 0;

            if ("next".equals(page)) {
                startRow = pagedListHolder.getRow() + limit;
                if (startRow >= pagedListHolder.getTotalRows()) {
                    startRow = pagedListHolder.getRow();
                }
            }
            if ("prev".equals(page)) {
                startRow = pagedListHolder.getRow() - limit;
                if (startRow < 0) {
                    startRow = 0;
                }
            }
            if ("first".equals(page)) {
                startRow = 0;
            }
            if ("last".equals(page)) {
                startRow = pagedListHolder.getTotalRows() - limit;
                if (startRow > pagedListHolder.getTotalRows()) {
                    startRow = pagedListHolder.getRow();
                }
                if(startRow < 0){
                    startRow = 0;
                } 
            }
            searchRaopFormBean.setStartRow(startRow); 
            redirectAttrs.addAllAttributes(this.getRedirectParams(searchRaopFormBean)); 
            return "redirect:/caop/raoplist/search";//+"?"+this.buildGetRequestParams(searchRaopFormBean);  
        }
        return "caop/raoplist";
    }

    /**
     * Query the DB for a list of raop rows and for the total number of
     * rows found that match the search criteria defined by SearchRaopFormBean.
     *
     * @param searchRaopFormBean Used to specify search criteria.
     * @param limit Limits the number of search results.
     * @param offset Offset the results by this many rows.
     * @return <code>Pair.first</code> holds the list of raop rows (from
     * offset to limit), while <code>Pair.second</code> is the total number of
     * matching rows found in the DB.
     */
    private Pair<List<ViewRaContact>, Integer> submitRASearch(
            SearchRaopFormBean searchRaopFormBean, int limit, int offset) {

        //Create RA Contacts List
        List<ViewRaContact> rows = new ArrayList<ViewRaContact>(0);
        List<CertificateRow> certRows;
        String searchRa = searchRaopFormBean.getRa();
        
        //Find Certificate Rows based on RA selection
        if(!searchRa.equals("All"))
        {
            String[] place = searchRa.split("_");
            certRows = this.certDao.findActiveRAsBy(place[1], place[0]);
        }
        else
        {
            certRows = this.certDao.findActiveRAsBy(null, null);
        }
        
        
        //Find and Create Contacts
        for(CertificateRow certRow : certRows){
            String loc = uk.ac.ngs.common.CertUtil.extractDnAttribute(certRow.getDn(), uk.ac.ngs.common.CertUtil.DNAttributeType.L);
            String ou = uk.ac.ngs.common.CertUtil.extractDnAttribute(certRow.getDn(), uk.ac.ngs.common.CertUtil.DNAttributeType.OU); 
            List<RaopListRow> raoplistRowsWithSameCnOuLoc;
            
            if(searchRaopFormBean.getName() == null){
                raoplistRowsWithSameCnOuLoc = this.raopDao.findBy( ou, loc, certRow.getCn(), Boolean.TRUE);
            }
            else {
                String cn = "%"+searchRaopFormBean.getName()+"%";
                raoplistRowsWithSameCnOuLoc = this.raopDao.findBy( ou, loc, cn, Boolean.TRUE);  
            }   
            RaOpList.ViewRaContact contact = new RaOpList.ViewRaContact(certRow, loc, ou, raoplistRowsWithSameCnOuLoc); 
            rows.add(contact);
        }
        
        //Modify the Total based on how many RA Operators are in the raop table
        int totalRows = certRows.size();
        for(ViewRaContact contact : rows){
            if (contact.raoplistRows.isEmpty())
            {
                totalRows = totalRows - 1;
            }
        }
        
        
        log.info("Number of Contacts: " + rows.size());
        for(ViewRaContact ra : rows){
            String cn = ra.certRow.getCn();
            long id = ra.certRow.getCert_key();
            int numRa = ra.raoplistRows.size();
            log.info("RAOP: " + cn + " Cert: " + id + " Number of RA Rows: " + numRa);
        }
                
        log.info("Number of Contacts Displayed: " + totalRows);
        return Pair.create(rows, totalRows);
    }

    @Inject
    public void setJdbcRaopListDao(JdbcRaopListDao dao) {
        this.raopDao = dao;
    }

    @Inject
    public void setJdbcCertificateDao(JdbcCertificateDao dao) {
        this.certDao = dao;
    }
    
    @Inject
    public void setRalistDao(JdbcRalistDao ralistDao) {
        this.ralistDao = ralistDao;
    }
    
    @Inject
    public void setSecurityContextService(SecurityContextService securityContextService) {
        this.securityContextService = securityContextService;
    }
}

