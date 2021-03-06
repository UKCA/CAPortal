<%@page contentType="text/html" pageEncoding="windows-1252" %>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=windows-1252"/>
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/favicon.ico" type="image/x-icon"/>
    <title>View Bulk</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <meta name="description" content="Approve individual CSRs from a bulk"/>
    <meta name="author" content="David Meredith"/>
    <meta name="author" content="Sam Worley"/>
    <!-- Styles -->
    <%@ include file="../../jspf/styles.jspf" %>
    <link href="${pageContext.request.contextPath}/resources/css/messages/messages.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/jquery/tablesorter/css/theme.blue.css" rel="stylesheet"/>
</head>
<body>
<%@ include file="../../jspf/header.jspf" %>
<div id="wrap">
    <div class="row">
        <div class="col-xs-offset-1 col-xs-10">
            <h1>Manage Bulk</h1>
            <h4>(bulkId=${bulkView.bulkId} requested by <c:out value="${bulkView.rows['0'].requestRow.email}"/>)</h4>

            <c:if test="${not empty successMessage}">
                <div id="successMessage" class="success">${successMessage}</div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div id="errorMessage" class="error">${errorMessage}</div>
            </c:if>
            <div class="col-xs-offset-10">
                <strong><a href="#" id="selall">Select / Deselect All</a></strong>
            </div>
            <form:form method="post" modelAttribute="bulkView">
                <table id="bulkTab" class="table table-hover tablesorter-blue">
                    <thead>
                    <tr>
                        <th><strong>#</strong></th>
                        <th><strong>Serial:</strong></th>
                        <th><strong>Common Name:</strong></th>
                        <th><strong>Distinguished Name:</strong></th>
                        <th><strong>Status:</strong></th>
                        <th class="sorter-false"><strong>Select/Deselect</strong></th>
                        <!--                                    <th class="sorter-false"><strong>Approve</strong></th>
                                                            <th class="sorter-false"><strong>Delete</strong></th>-->
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="rowWrapper" items="${bulkView.rows}" varStatus="status">
                        <%-- You must bind here the properties you want posting with form:x --%>
                        <tr>
                            <td>${status.index + 1}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/raop/viewcsr?requestId=${rowWrapper.requestRow.req_key}">
                                        ${rowWrapper.requestRow.req_key}</a>
                            </td>
                            <td> ${rowWrapper.requestRow.cn}</td>
                            <td class="vertAlign col-xs-2">
                                <button type="button" class="btn btn-sm dnPop" data-container="body"
                                        data-toggle="popover"
                                        data-placement="top" data-content="${rowWrapper.requestRow.dn}">DN
                                </button>
                            </td>
                            <td> ${rowWrapper.requestRow.status} </td>
                            <form:hidden path="rows[${status.index}].requestRow.req_key"/>
                            <form:hidden path="rows[${status.index}].requestRow.cn"/>
                            <c:choose>
                                <c:when test="${rowWrapper.requestRow.status == 'NEW' ||
                                                            rowWrapper.requestRow.status =='RENEW' || 
                                                            rowWrapper.requestRow.status =='APPROVED'}">
                                    <td style="text-align:center"><form:checkbox
                                            path="rows[${status.index}].checked"/></td>
                                    <!-- <td><input type="submit" class="btn btn-sm btn-primary" value="Approve"
                                    onclick="return confirm('Are you sure you want to approve certificate?');"
                                    formaction="${pageContext.request.contextPath}/raop/viewbulk/approveSingle"/>
                                    </td>
                                    <td><button type="button" class="btn btn-sm btn-danger">Delete</button></td>-->
                                </c:when>
                                <c:otherwise>
                                    <td>No action allowed</td>
                                    <!--<td>No action</td><td>No action</td>-->
                                </c:otherwise>
                            </c:choose>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
                <c:if test="${bulkView.bulkId != null}">
                    <form:hidden path="bulkId"/>
                </c:if>
                <input type="submit" class="btn btn-primary" value="Approve Selected"
                       onclick="return confirm('Are you sure you want to approve all selected requests?');"
                       formaction="${pageContext.request.contextPath}/raop/viewbulk/approveBulks"/>
                <input type="submit" class="btn btn-danger" value="Delete Selected"
                       onclick="return confirm('Are you sure you want to delete all selected requests?');"
                       formaction="${pageContext.request.contextPath}/raop/viewbulk/deleteBulks"/>
                <button type="button" class="btn btn-warning" onclick="history.go(-1)">Cancel</button>
            </form:form>
            <br/>
            <br/>
            <br/>
        </div>
    </div>
</div>
<%@ include file="../../jspf/footer.jspf" %>
<script type="text/javascript"
        src="${pageContext.request.contextPath}/resources/jquery/tablesorter/js/jquery.tablesorter.min.js"></script>
<script type="text/javascript">
    $(document).ready(function () {
        $('#selall').click(function () {
            var checkBoxes = $(":checkbox");
            checkBoxes.prop("checked", !checkBoxes.prop("checked"));
        });
        $("#bulkTab").tablesorter({sortList: [[1, 0], [2, 0]]});
    });
</script>
</body>
</html>