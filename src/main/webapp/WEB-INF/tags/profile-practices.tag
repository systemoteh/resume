<%@ tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ attribute name="practices" required="true" type="java.util.List" %>
<%@ attribute name="showEdit" required="false" type="java.lang.Boolean" %>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <i class="fa fa-briefcase"></i> Practic Experience
            <c:if test="${showEdit }"><a href="/edit/practices" class="edit-block">Edit</a></c:if>
        </h3>
    </div>
    <div class="panel-body">
        <ul class="timeline">
            <c:forEach var="practice" items="${practices }">
                <li>
                    <div class="timeline-badge danger">
                        <i class="fa fa-briefcase"></i>
                    </div>
                    <div class="timeline-panel">
                        <div class="timeline-heading">
                            <h4 class="timeline-title">${practice.position } at ${practice.company }</h4>
                            <p>
                                <small class="dates"><i class="fa fa-calendar"></i>
                                    <fmt:formatDate value="${practice.beginDate }" pattern="MMM yyyy"/> -
                                    <c:choose>
                                        <c:when test="${practice.finish }">
                                            <fmt:formatDate value="${practice.finishDate }" pattern="MMM yyyy"/>
                                        </c:when>
                                        <c:otherwise>
                                            <strong class="label label-danger">Current</strong>
                                        </c:otherwise>
                                    </c:choose>
                                </small>
                            </p>
                        </div>
                        <div class="timeline-body">
                            <p>
                                <strong>Responsibilities included:</strong> ${practice.responsibilities }
                            </p>
                            <c:if test="${practice.demoUrl != null }">
                                <p>
                                    <strong>Demo: </strong><a href="${practice.demoUrl}">${practice.demoUrl}</a>
                                </p>
                            </c:if>
                            <c:if test="${practice.srcUrl != null }">
                                <p>
                                    <strong>Source code: </strong><a href="${practice.srcUrl}">${practice.srcUrl}</a>
                                </p>
                            </c:if>
                        </div>
                    </div>
                </li>
            </c:forEach>
        </ul>
    </div>
</div>
