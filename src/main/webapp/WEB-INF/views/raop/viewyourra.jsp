<%@page contentType="text/html" pageEncoding="windows-1252"%>
<%--<%@ page session="false"%>--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec"%>

<!DOCTYPE html>
<html>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=windows-1252"/>
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/favicon.ico" type="image/x-icon"/> 
        <title>View RA List</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <meta name="description" content="Home page for viewing the RAs for given RA, public." />
        <meta name="author" content="Josh Hadley" />
        <!-- Styles -->
        <%--<jsp:include page="../common/styles.jsp" />--%>
        <%@ include file="../../jspf/styles.jspf" %>
        <link href="${pageContext.request.contextPath}/resources/css/messages/messages.css" rel="stylesheet" />
    </head>

    <body>
        <%--<jsp:include page="../common/header.jsp" />--%>      
        <%@ include file="../../jspf/header.jspf" %>
        <!-- Wrap all page content here -->
        <div id="wrap">
            <div class="row">
                <div class="col-xs-offset-1">

                    <h3>Your RA Details</h3> 
                    <p>
                        After your certificate has been promoted to RA operator, 
                        your contact details will need to be added to the  
                        RA operator table. Please make sure your details are  
                        correct below
                    </p>
                    <br/>

                    <div class="col-xs-11"> <%-- Current RA-OP User --%>
                        <h3> Your RA-OP Contact Details </h3>
                        <br/>
                        
                        <!-- Check to see if RA-OP details need to be added to the database --->
                        <c:if test="${user != null}">
                            <table class="table table-hover table-condensed">
                            <thead>
                                <tr>
                                    <th>RA ID</th>
                                    <th>Location</th>
                                    <th>Contact</th>
                                    <th>RA Op</th>
                                    <th>RA Man</th>
                                    <th>(OU) OrgUnit</th>
                                    <th>(L) Location</th>
                                    <th>(CN) CN</th>
                                    <th>Email</th> 
                                    <th>Phone</th>
                                    <th>Street</th>
                                    <th>City</th>
                                    <th>Post Code</th>
                                    <th>Training Date</th>
                                    <th>Active</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td>${user.ra_id}</td>
                                    <td>${user.location}</td>
                                    <td>${user.title} ${user.name}</td>
                                    <c:choose>
                                        <c:when test="${user.operator}">
                                            <td><span class="glyphicon glyphicon-star"/></td>
                                        </c:when>
                                        <c:otherwise>
                                            <td></td>
                                        </c:otherwise>
                                    </c:choose>
                                    <c:choose>
                                        <c:when test="${user.manager}">
                                            <td><span class="glyphicon glyphicon-star"/></td>
                                        </c:when>
                                        <c:otherwise>
                                            <td></td>
                                        </c:otherwise>
                                    </c:choose>
                                    <td>${user.ou}</td>
                                    <td>${user.l}</td>
                                    <td>${user.cn}</td>
                                    <sec:authorize access="hasRole('ROLE_RAOP')">
                                        <td><a href="mailto:${user.email}">${user.email}</a></td>
                                    </sec:authorize>
                                    <td>${user.phone}</td>
                                    <td>${contact.street}</td>
                                    <td>${contact.city}</td>
                                    <td>${contact.postcode}</td>
                                    <td>${contact.trainingDate}</td>
                                    <c:choose>
                                        <c:when test="${user.active}">
                                            <td><span class="glyphicon glyphicon-star"/></td>
                                        </c:when>
                                        <c:otherwise>
                                            <td></td>
                                        </c:otherwise>
                                    </c:choose>
                                </tr> 
                            </tbody>
                        </table>        
                        </c:if>
                        <c:if test="${user == null}">
                            <p>Your RA Contact details have yet to be added to the RA Contacts Table.
                               Please add the relevant information as soon as possible.
                            </p>
                        </c:if>
                    </div>    
                        
                        
                    <%--<h5>RA List last refreshed: &nbsp;(${sessionScope.lastRalistSearchDate_session})</h5>--%>
                    <div class="col-xs-11"> <%-- Current RA OP List --%>
                        
                        <h3>Other RA-OPs in your RA: [${ou}]</h3>
                        
                        <br/>
                        
                        <table class="table table-hover table-condensed">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>RA ID</th>
                                    <th>Location</th>
                                    <th>Contact</th>
                                    <th>RA Op</th>
                                    <th>RA Man</th>
                                    <th>(OU) OrgUnit</th>
                                    <th>(L) Location</th>
                                    <th>(CN) CN</th>
                                    <sec:authorize access="hasRole('ROLE_RAOP')">
                                        <th>Email</th> 
                                    </sec:authorize>
                                    <th>Phone</th>
                                    <%---<th>Street</th>
                                    <th>City</th>
                                    <th>Post Code</th>
                                    <th>Training Date</th>---%>
                                    <th>Active</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:set var="count" value="0" scope="page" />
                                <c:forEach var="contact" items="${contacts}" varStatus="loopVar">
                                    <c:set var="count" value="${count + 1}" scope="page"/>
                                    <tr>
                                        <c:forEach var="raop" items="${contact.raoplistRows}">
                                        <td>${count}</td>
                                        <td>${raop.ra_id}</td>
                                        <td>${raop.location}</td>
                                        <td>${raop.title} ${raop.name}</td>
                                        <c:choose>
                                            <c:when test="${raop.operator}">
                                                <td><span class="glyphicon glyphicon-star"/></td>
                                            </c:when>
                                            <c:otherwise>
                                                <td></td>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:choose>
                                            <c:when test="${raop.manager}">
                                                <td><span class="glyphicon glyphicon-star"/></td>
                                            </c:when>
                                            <c:otherwise>
                                                <td></td>
                                            </c:otherwise>
                                        </c:choose>
                                        <td>${contact.ou}</td>
                                        <td>${contact.loc}</td>
                                        <td>${contact.certRow.cn}</td>
                                        <sec:authorize access="hasRole('ROLE_RAOP')">
                                            <td><a href="mailto:${contact.certRow.email}">${contact.certRow.email}</a></td>
                                        </sec:authorize>
                                        <td>${raop.phone}</td>
                                        <%--<td>${contact.street}</td>
                                        <td>${contact.city}</td>
                                        <td>${contact.postcode}</td>
                                        <td>${contact.trainingdate}</td>--%>
                                        <c:choose>
                                            <c:when test="${raop.active}">
                                                <td><span class="glyphicon glyphicon-star"/></td>
                                            </c:when>
                                            <c:otherwise>
                                                <td></td>
                                            </c:otherwise>
                                        </c:choose>
                                        </c:forEach>
                                    </tr>
                                </c:forEach>    
                            </tbody>
                        </table>        


                    </div>

                </div> <!-- /container -->
            </div>
        </div> <!-- /span -->


        <%--<jsp:include page="../common/footer.jsp" />--%>
        <%@ include file="../../jspf/footer.jspf" %>
    </body>
</html>