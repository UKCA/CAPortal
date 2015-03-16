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
                    <div class="col-xs-11 col-lg-10">
                        <form:form method="post" action="${pageContext.request.contextPath}/caop/addracontact/add">
                        
                            <input type="text">A</input>
                            <input type="text">B</input>
                            <input type="text">C</input>
                            <input type="text">D</input>
                            <input type="text">E</input>
                            <input type="text">F</input>
                            <input type="text">G</input>
                            <input type="text">H</input>
                            <input type="text">I</input>
                            
                            <input type="submit"></input>
                            
                        </form:form>
                    </div>
                    
                </div>   
            </div>
        </div>                  
        <%--<jsp:include page="../common/footer.jsp" />--%>
        <%@ include file="../../jspf/footer.jspf" %>
    </body>
</html>
