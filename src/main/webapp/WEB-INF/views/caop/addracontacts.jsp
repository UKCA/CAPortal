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
        <title>Add New RA Contact</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <meta name="description" content="Page for adding an RA-OPs Contact Details" />
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
                    <h2>Add RA Contact Details</h2>
                    <br/>
                    <c:if test="${errorMessage != null}">
                        <div id="message" class="error">${errorMessage}</div>
                    </c:if>
                    <c:if test="${not empty message}">
                        <div id="message" class="success">${message}</div>
                    </c:if>
                    <div class="col-xs-11 col-lg-10">
                            
                        <form:form id="form" method="post" action="${pageContext.request.contextPath}/caop/addracontact/add"
                                   modelAttribute="addRaOperatorBean" cssClass="form-horizontal">
                            <div class="form-group">
                                <div class="col-xs-5 col-lg-5">
                                    Title
                                </div>
                                <div class="col-xs-6 col-lg-6">
                                    <form:select path="Title" class="form-control"> 
                                        <form:option value="Mr"/>
                                        <form:option value="Mrs"/>
                                        <form:option value="Miss"/>
                                        <form:option value="Dr"/>
                                        <form:option value="Prof"/>
                                    </form:select>
                                </div>
                            </div>
                            
                            <div class="form-group">    
                                <div class="col-xs-5 col-lg-5">
                                    Full Name
                                </div>
                                <div class="col-xs-5 col-lg-5">
                                    <form:input path="name" class="form-control" 
                                    placeholder="A Name" /> <form:errors
                                    path="name" cssClass="text-error" />
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <div class="col-xs-5 col-lg-5">
                                    RA
                                </div>
                                <div class="col-xs-5 col-lg-5">
                                    <form:select path="ra" class="form-control">
                                    <form:options items="${raList}"/>
                                    </form:select> 
                                    <form:errors path="ra" cssClass="text-error" />
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <div class="col-xs-5 col-lg-5">
                                    RA Operator?
                                </div>
                                <div class="col-xs-5 col-lg-5">
                                    <form:checkbox path="raOperator"/>
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <div class="col-xs-5 col-lg-5">
                                    RA Manager?
                                </div>
                                <div class="col-xs-5 col-lg-5">
                                    <form:checkbox path="raManager"/>
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <div class="col-xs-5 col-lg-5">
                                    E-mail
                                </div>
                                <div class="col-xs-5 col-lg-5">
                                    <form:input path="emailAddress" class="form-control"
                                    placeholder="someone@world.com" /> <form:errors
                                    path="emailAddress" cssClass="text-error" />
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <div class="col-xs-5 col-lg-5">
                                    Phone
                                </div>
                                <div class="col-xs-5 col-lg-5">
                                    <form:input path="phone" class="form-control" 
                                    placeholder="Phone Number e.g. 00000 111111" /> <form:errors
                                    path="phone" cssClass="text-error" />
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <div class="col-xs-5 col-lg-5">
                                    Street
                                </div>
                                <div class="col-xs-5 col-lg-5">
                                    <form:input path="street" class="form-control" 
                                    placeholder="Street where you are based" /> <form:errors
                                    path="street" cssClass="text-error" />
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <div class="col-xs-5 col-lg-5">
                                    City
                                </div>
                                <div class="col-xs-5 col-lg-5">
                                     <form:input path="city" class="form-control" 
                                    placeholder="City where you are based" /> <form:errors
                                    path="city" cssClass="text-error" />
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <div class="col-xs-5 col-lg-5">
                                    Postcode
                                </div>
                                <div class="col-xs-5 col-lg-5">
                                    <form:input path="postcode" class="form-control" 
                                    placeholder="Your postocde where you are based" /> <form:errors
                                    path="postcode" cssClass="text-error" />
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <div class="col-xs-5 col-lg-5">
                                    Training Date (DD-MM-YYYY)
                                </div>
                                <div class="col-xs-5 col-lg-5">
                                    <form:input path="training" class="form-control" 
                                    placeholder="The date you passed your RA training." /> <form:errors
                                    path="training" cssClass="text-error" />
                                </div>
                            </div>
                            
                            <div class="form-group">                            
                                <div class="col-xs-5 col-lg-5">
                                    <input type="submit" disabled />
                                </div>
                            </div>
                        </form:form>
                    </div>
                    
                </div>   
            </div>
        </div>                  
        <%--<jsp:include page="../common/footer.jsp" />--%>
        <%@ include file="../../jspf/footer.jspf" %>
    </body>
</html>
