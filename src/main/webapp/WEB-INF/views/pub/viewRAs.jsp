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
        <meta name="author" content="David Meredith" />
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

                    <h3>RA Operators for Organisation Unit (OU): [${ou}]</h3> 
                    <p>
                        After you have applied for a certificate, you will need 
                        to contact your institution's RA operator 
                        to show your photo-id before your certificate is 
                        issued. 
                    </p>
                    <p>If you are having problems finding an RA, please contact us on the helpdesk and we'll be happy to help</p>
                    <br/>
                    <sec:authorize access="hasRole('ROLE_RAOP')">
                        <p>
                            As an authenticated RA Operator, email addresses are visible.
                            These are <strong>not</strong> visible to public (unauthenticated) users.
                        </p> <br/>
                    </sec:authorize>

                    <%--<h5>RA List last refreshed: &nbsp;(${sessionScope.lastRalistSearchDate_session})</h5>--%>
                    <div class="col-xs-11">

                        <table class="table table-hover table-condensed">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>RA ID</th>
                                    <th>Location</th>
                                    <th>Contact</th>

                                    <sec:authorize access="hasRole('ROLE_RAOP')">
                                    <th>RA Op</th>
                                    <th>RA Man</th>
                                    <th>(OU) OrgUnit</th>
                                    <th>(L) Location</th>
                                    <th>(CN) CN</th>

                                        <th>Email</th> 

                                    <sec:authorize access="hasRole('ROLE_RAOP')">
                                        <th>Email</th> 
                                    </sec:authorize>

                                    <th>Phone</th>
                                    <%---<th>Street</th>
                                    <th>City</th>
                                    <th>Post Code</th>
                                    <th>Training Date</th>---%>
                                    <th>Active</th>

                                    </sec:authorize>
                                    <%--<sec:authorize access="hasRole('ROLE_CAOP')">
                                        <th>Promote</th>
                                        <th>Demote</th>
                                    </sec:authorize>--%>
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

                                        <sec:authorize access="hasRole('ROLE_RAOP')">
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
                                            <td><a href="mailto:${contact.certRow.email}">${contact.certRow.email}</a></td>
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
                                        </sec:authorize>
                                        </c:forEach>
                                        <%--<sec:authorize access="hasRole('ROLE_CAOP')">
                                            <td>Promote Here</td>
                                            <td>Demote Here</td> 
                                        </sec:authorize>--%>
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
