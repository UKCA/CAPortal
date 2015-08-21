<%@page contentType="text/html" pageEncoding="windows-1252"%>
<%--<%@ page session="false"%>--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html>
<html>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=windows-1252"/>
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/favicon.ico" type="image/x-icon"/> 
        <title>RA OP List</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <meta name="description" content="Displays RA-OP Details for their respective RAs" />
        <meta name="author" content="Josh Hadley" />
        <!-- Styles -->
        <%--<jsp:include page="../common/styles.jsp" />--%>
        <%@ include file="../../jspf/styles.jspf" %>
        <link href="${pageContext.request.contextPath}/resources/css/messages/messages.css" rel="stylesheet" />
        <link href="${pageContext.request.contextPath}/resources/jquery/tablesorter/css/theme.blue.css" rel="stylesheet" />
    </head>

    <body>
        <%--<jsp:include page="../common/header.jsp" />--%>      
        <%@ include file="../../jspf/header.jspf" %>
            <div class="modal fade" id="helpModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
            aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content"></div>
                </div>
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                             <h4 class="modal-title" id="helpModalLabel">Viewing the RA Operator Table</h4>
                        </div>
                        <div class="modal-body">
                           Viewing the RA Operator Table, you are able to perform several functions within the table
                           itself.
                           <br/>
                           <br/>
                           These functions include: 
                           <ol>
                                <li><strong>Viewing Current RA Operators</strong> with the ability to search and filter the table as required</li> 
                                <li><strong>View a specific RA Operator</strong> by clicking on the relevant id number.</li>
                                <li><strong>Editing Current RA Operators' Contact Details</strong> by clicking the 'edit' button</li>
                                <li><strong>Adding a new RA Contact</strong> by either: <br/>
                                    <ul>
                                        <li>Clicking the relevant <strong>'Cert Key'</strong> under the <strong>'RA-OPs to be Added'</strong> section</li> 
                                        <li>Or by clicking the 'Add new RA Contact' button</li>
                                    </ul>
                                </li>
                                <li><strong> Adding a new RA </strong> by clicking the 'Add new RA' button</li>
                           </ol>
                        </div>
                        <div class="modal-footer">
                            Click anywhere off this panel to close 
                        </div>
                    </div>
                </div>
            </div>  
        
            <!-- Wrap all page content here -->
            <div id="wrap">
                <div class="row">
                    <div class="col-xs-offset-1">

                    <s:hasBindErrors name="searchRaopFormBean_REQUESTSCOPE">
                           <div id="thesemessages" class="error">Invalid GET request search parameter</div>
                    </s:hasBindErrors>                   

                   <div class="col-xs-7">       
                       <form:form id="form" method="post" action="${pageContext.request.contextPath}/caop/raoplist/"
                                  modelAttribute="searchRaopFormBean" cssClass="form-horizontal">
                           <div class="form-group">
                               <h2 class="form-search-heading">RA-OP Details List</h2>
                               <%--<c:if test="${not empty message}">
                                   <div id="message" class="success">${message}</div>
                               </c:if>--%>
                               <c:if test="${not empty searchOk}">
                                   <div id="message" class="success">${searchOk}</div>
                               </c:if>
                               <s:bind path="*">
                                   <c:if test="${status.error}">
                                       <div id="message" class="error">Form has errors</div>
                                   </c:if>
                               </s:bind>
                               <div class="col-xs-4">
                                   <font class="muted">
                                       _ matches any single char<br/> 
                                       % matches a string
                                   </font>
                               </div>
                           </div>
                           <div class="form-group">
                               <div class="col-xs-3 col-lg-3">
                                   <strong>Common Name Like (CN)</strong>
                               </div>
                               <div class="col-xs-8 col-sm-6 col-md-5 col-lg-3">
                                   <form:input path="name" class="form-control" 
                                       placeholder="A Name" /> <form:errors
                                       path="name" cssClass="text-error" />
                               </div>
                           </div>
                           <div class="form-group">
                               <div class="col-xs-3 col-lg-3">
                                   <strong>RA</strong>
                               </div>
                               <div class="col-xs-8 col-sm-6 col-md-5 col-lg-3">
                                   <form:select path="ra" class="form-control">
                                       <form:options items="${ralistArray}"/>
                                   </form:select> 
                                   <form:errors path="ra" cssClass="text-error" />
                               </div>
                           </div>
                           <div class="form-group">
                               <div class="col-xs-3 col-lg-3">
                                   <strong>Email Address Like</strong>
                               </div>
                               <div class="col-xs-8 col-sm-6 col-md-5 col-lg-3">
                                   <form:input path="emailAddress" class="form-control"
                                       placeholder="someone@world.com" /> <form:errors
                                       path="emailAddress" cssClass="text-error" />
                               </div>
                           </div>
                           <div class="form-group">
                               <div class="col-xs-3 col-lg-3">
                                   <strong>Results per page:</strong>
                               </div>
                               <div class="col-xs-8 col-sm-6 col-md-5 col-lg-3">
                                   <form:select path="showRowCount" class="form-control"> 
                                       <form:option value="20"/>
                                       <form:option value="50"/>
                                       <form:option value="100"/>
                                   </form:select>
                               </div>
                           </div>
                           <div class="form-group">
                               <div class="col-xs-offset-3">
                                   <button type="submit" class="btn btn-md btn-primary">Search</button>
                               </div>
                           </div>
                       </form:form>
                   </div>
                   <br/>

                   <!-- Controls for Adding an RA and an RA Operator -->
                   <div class="col-xs-2">  
                        <h3>RA-OPs To Be Added</h3>
                        <!-- Provide a List of RA-OPs who are not present in the RaopList Database --->
                       <div class="col-xs-11">
                           <!-- Grab the current list of results for the search criteria -->
                            <c:forEach var="raop" items="${sessionScope.raopSearchPageHolder.source}"> 
                                <!-- Check the RA-OPs to see if there is a row present -->
                                <c:if test="${raop.raoplistSize == 0}">
                                    <a href="${pageContext.request.contextPath}/caop/addracontacts?certId=${raop.certRow.cert_key}">${raop.certRow.cert_key}</a>
                                </c:if>
                            </c:forEach>
                        </div>
                        <br/>
                        <br/>
                        <div class="col-xs-9">    
                            <!-- Todo Add new RA-OP Function -->
                            <div class="form-group">
                                <form:form method="get"
                                    action="${pageContext.request.contextPath}/caop/addracontacts">
                                    <div class="col-xs-1 col-lg-1">
                                        <button type="submit" class="btn btn-sm btn-primary" disabled="true">
                                            Add New RA Contact
                                        </button>
                                    </div>
                                </form:form>
                            </div>  
                        </div>
                        <div class="col-xs-3"> 
                            <!-- Todo Add a New RA to the Database -->
                            <div class="form-group">
                                <form:form method="post"
                                    action="${pageContext.request.contextPath}/caop/addnewra">
                                    <div class="col-xs-1 col-lg-1">
                                        <button type="submit" class="btn btn-sm btn-primary" disabled="true">
                                            Add New RA
                                        </button>
                                    </div>
                                </form:form>
                            </div>
                        </div>
                    </div>       

                   <div class="col-xs-1">
                       <a href="#" id="helpMod" style="color: inherit;">
                           <span class="helperIcon glyphicon glyphicon-question-sign" style="font-size: xx-large;"></span>
                       </a>
                   </div> 

                   <div class="col-xs-11">
                       <h4>RA-OP Results (total = ${sessionScope.raopSearchPageHolder.totalRows}
                           <c:if test="${sessionScope.lastRaopSearchDate_session != null}">
                               ,&nbsp;${sessionScope.lastRaopSearchDate_session}
                           </c:if>)
                       </h4>
                   </div>

                   <br/>
                   <div class="col-xs-11">
                       <table id="raopResultsTable" class="tablesorter-blue">
                           <!--  <caption>List of RA-OP rows returned by search</caption> -->
                           <thead>
                               <tr>
                                   <th>Actions</th>
                                   <th class="sorter-false">#</th>
                                   <th>OU</th>
                                   <th>L</th>
                                   <th>Name</th>
                                   <th>Email</th>
                                   <th>Phone</th>
                                   <th>Street</th>
                                   <th>City</th>
                                   <th>Postcode</th>
                                   <th>CN</th>
                                   <th>Title</th>
                                   <th>Coneemail</th>
                                   <th>Location</th>
                                   <th>Manager</th>
                                   <th>Operator</th>
                                   <th>Active Cert</th>
                                   <th>Ra_id</th>
                                   <th>Ra_id2</th>
                                   <th>Training Date</th>
                                   <th>Department_hp</th>
                                   <th>Institute_hp</th>
                                   <th>Active</th>
                               </tr>
                           </thead>
                           <tbody>
                               <c:set var="count" value="0" scope="page" />
                               <c:forEach var="raop" items="${sessionScope.raopSearchPageHolder.source}">
                                   <c:url value="/caop/viewradetails?ou=${raop.ou}&l=${raop.loc}&certKey=${raop.certRow.cert_key}" var="viewraop" />
                                   <c:url value="/raop/editracontactdetails?certId=${raop.certRow.cert_key}" var="editraop" />
                                   <c:set var="count" value="${count + 1}" scope="page"/>
                                   <c:choose>
                                       <c:when test="${raop.raoplistSize == 0}">
                                           <c:set var="isEmpty" value="true" scope="page" />
                                       </c:when>
                                       <c:otherwise>
                                           <c:set var="isEmpty" value="false" scope="page" />
                                       </c:otherwise>   
                                   </c:choose>
                                   <tr>
                                       <c:if test="${isEmpty == 'false'}">
                                            <td>
                                                <a href="${editraop}"><button class="btn btn-sm">Edit</button></a>
                                                
                                                <!-- To Activate/Deactivate an RA-OP from the database. --->
                                                <%--<c:forEach var="detail" items="${raop.raoplistRows}">
                                                    <form:form method="post" action="${pageContext.request.contextPath}/caop/raoplist/goto"
                                                    commandName="raActiveBean" >
                                                        <input hidden value="${detail.active}"/>
                                                        <c:if test="${detail.active == true}">
                                                            <button type="submit" class="btn btn-sm">Dismiss</button>
                                                        </c:if>
                                                        <c:if test="${detail.active == false}">
                                                            <button type="submit" class="btn btn-sm">Activate</button>
                                                        </c:if>
                                                    </form:form>
                                                </c:forEach> --%>
                                            </td>
                                            <td><a href="${viewraop}">${sessionScope.raopSearchPageHolder.row + count}</a></td>
                                            <td>${raop.ou}</td>
                                            <td>${raop.loc}</td>
                                            <c:forEach var="detail" items="${raop.raoplistRows}">
                                                <td>
                                                   ${detail.name}
                                               </td>
                                               <td><a href="mailto:${detail.email}">${detail.email}</a></td>
                                               <td>${detail.phone}</td>
                                               <td>${detail.street}</td>
                                               <td>${detail.city}</td>
                                               <td>${detail.postcode}</td>
                                               <td>${detail.cn}</td>
                                               <td>${detail.title}</td>
                                               <td>${detail.coneemail}</td>
                                               <td>${detail.location}</td>
                                               <td>${detail.manager}</td>
                                               <td>${detail.operator}</td>
                                               <td>${raop.certRow.cert_key}</td>
                                               <td>${detail.ra_id}</td>
                                               <td>${detail.ra_id2}</td>
                                               <td>${detail.trainingDate}</td>
                                               <td>${detail.department_hp}</td>
                                               <td>${detail.institute_hp}</td>
                                               <td>${detail.active}</td>
                                           </c:forEach>
                                       </c:if>
                                   </tr>
                               </c:forEach>
                           </tbody>
                       </table>

                       <table>
                           <tr>
                               <td>
                                   <c:if test="${sessionScope.raopSearchPageHolder.totalRows == 0}">
                                      Showing:&nbsp;[<b>0</b>]  
                                   </c:if>
                                   <c:if test="${sessionScope.raopSearchPageHolder.totalRows > 0}">
                                      Showing:&nbsp;[<b>${sessionScope.raopSearchPageHolder.row+1}</b>] 
                                   </c:if>
                                   to [<b>${sessionScope.raopSearchPageHolder.row + fn:length(sessionScope.certSearchPageHolder.source)}</b>]
                                   of [<b>${sessionScope.raopSearchPageHolder.totalRows}</b>] <!--zero offset list -->
                               </td>
                               <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                               <form:form method="post" action="${pageContext.request.contextPath}/caop/raoplist/goto"
                                          commandName="gotoPageFormBean" >
                                   <td>Go to row:</td>
                                   <td>
                                       <form:input path="gotoPageNumber" cssStyle="width:30px" placeholder="0"/>
                                       <button type="submit" class="btn btn-sm">Go</button>
                                   </td> 
                               </form:form>
                               <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                               <c:if test="${fn:length(sessionScope.raopSearchPageHolder.source) > 0}">
                                   <c:url value="/caop/raoplist/page?page=next" var="pagenextaction" />
                                   <c:url value="/caop/raoplist/page?page=prev" var="pageprevaction" />
                                   <c:url value="/caop/raoplist/page?page=first" var="pagefirstaction" />
                                   <c:url value="/caop/raoplist/page?page=last" var="pagelastaction" />
                                   <td>
                                       <ul class="pager">
                                           <li><a href="${pagefirstaction}">First</a></li>
                                           <li><a href="${pageprevaction}">&laquo; Previous</a></li>
                                           <li><a href="${pagenextaction}">Next &raquo;</a></li>
                                           <li><a href="${pagelastaction}">Last</a></li>
                                       </ul>
                                   </td>
                                </c:if>
                            </tr>
                        </table>
                   </div>
                </div> <!-- /container -->
            </div>
         </div> <!-- /span -->


        <%--<jsp:include page="../common/footer.jsp" />--%>
        <%@ include file="../../jspf/footer.jspf" %>
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/jquery/tablesorter/js/jquery.tablesorter.min.js"></script>
        <%--<script type="text/javascript" src="${pageContext.request.contextPath}/resources/jquery/tablesorter/js/jquery.tablesorter.widgets.min.js"></script>--%>
        
        <script>
            $(function(){
              $("#raopResultsTable").tablesorter();
            });
            
            $(document).ready(function() {
                $("#helpMod").click(function() {
                    $('#helpModal').modal('show');
                });
            });
        </script>
    </body>
</html>
