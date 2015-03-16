<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/favicon.ico" type="image/x-icon"/> 
        <title>View Certificate</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <meta name="description" content="Page for viewing an RA-OPs Contact Details" />
        <meta name="author" content="Josh Hadley" />
        <!-- Styles -->
        <%--<jsp:include page="../common/styles.jsp" />--%>
        <%@ include file="../../jspf/styles.jspf" %>
        <link href="${pageContext.request.contextPath}/resources/css/messages/messages.css" rel="stylesheet" />
    </head>
    <body>
        <%--<jsp:include page="../common/header.jsp" />--%>
        <%@ include file="../../jspf/header.jspf" %>


        <%--<ul class="breadcrumb">
            <li><a href="${searchcert}"><<< Search Cert</a> <span class="divider">/</span></li>
        </ul>--%>

        <!-- Wrap all page content here -->
        <div id="wrap">
            <div class="row">
                <div class="col-xs-offset-1">
                    <h2>View RA Contact Details</h2>
                    <c:if test="${errorMessage != null}">
                        <div id="message" class="error">${errorMessage}</div>
                    </c:if>
                    <c:if test="${not empty message}">
                        <div id="message" class="success">${message}</div>
                    </c:if>
                    <!--<div class="col-xs-3 col-lg-4" style="float: right;">
                        <a class="btn btn-md btn-primary" href=""><strong>Edit</strong></a>
                    </div> -->
                    <h4>Last Page Refresh: (${lastViewRefreshDate})</h4>
                    <br/>
                    <div class="col-xs-11 col-lg-10">
                        <table class="table table-hover table-condensed">
                            <thead>
                                <tr>
                                    <th>Ra Contact Details</th>
                                    <th>Value</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td>Organisation Unit (OU)</td>
                                    <td>${raop.ou}</td> 
                                </tr>
                                <tr>
                                    <td>Location (L)</td>
                                    <td>${raop.l}</td>
                                </tr>
                                <tr>
                                    <td>Name</td>
                                    <td>${raop.name}</td> 
                                </tr>
                                <tr>
                                    <td>Email</td>
                                    <td>${raop.email}</td>
                                </tr><tr>
                                    <td>Phone</td>
                                    <td>${raop.phone}</td> 
                                </tr>
                                <tr>
                                    <td>Street</td>
                                    <td>${raop.street}</td>
                                </tr><tr>
                                    <td>City</td>
                                    <td>${raop.city}</td> 
                                </tr>
                                <tr>
                                    <td>Postcode</td>
                                    <td>${raop.postcode}</td>
                                </tr><tr>
                                    <td>Common Name (CN)</td>
                                    <td>${raop.cn}</td> 
                                </tr>
                                <tr>
                                    <td>Title</td>
                                    <td>${raop.title}</td>
                                </tr><tr>
                                    <td>Company Email</td>
                                    <td>${raop.coneemail}</td> 
                                </tr>
                                <tr>
                                    <td>Location</td>
                                    <td>${raop.location}</td>
                                </tr><tr>
                                    <td>RA Manager</td>
                                    <td>${raop.manager}</td> 
                                </tr>
                                <tr>
                                    <td>RA Operator</td>
                                    <td>${raop.operator}</td>
                                </tr><tr>
                                    <td>RA ID</td>
                                    <td>${raop.ra_id}</td> 
                                </tr>
                                <tr>
                                    <td>RA ID2</td>
                                    <td>${raop.ra_id2}</td>
                                </tr><tr>
                                    <td>Training Date</td>
                                    <td>${raop.trainingDate}</td> 
                                </tr>
                                <tr>
                                    <td>Department_hp</td>
                                    <td>${raop.department_hp}</td>
                                </tr><tr>
                                    <td>Institute_hp</td>
                                    <td>${raop.institute_hp}</td> 
                                </tr>
                                <tr>
                                    <td>Active</td>
                                    <td>${raop.active}</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                    
                </div>   
            </div>
        </div>                  
        <%--<jsp:include page="../common/footer.jsp" />--%>
        <%@ include file="../../jspf/footer.jspf" %>
    </body>
</html>
