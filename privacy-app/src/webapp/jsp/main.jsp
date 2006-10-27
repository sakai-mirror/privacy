<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<% response.setContentType("text/html; charset=UTF-8"); %>

<f:loadBundle basename="org.sakaiproject.tool.privacy.bundle.Messages" var="msgs"/>

<f:view> 
  <sakai:view> 
    <h:form>

	<!-- *********** Tool rendering top of page if on MyWorkspace home page *********** --> 
   <h:panelGroup rendered="#{privacyBean.myWorkspace}" >
     <f:verbatim><div><h3></f:verbatim>
     
     <h:outputText value="#{msgs.privacy_title}" rendered="#{privacyBean.myWorkspace}" />

     <f:verbatim></h3></div><br /></f:verbatim>
	  
	  <%--  TODO: add messages tag if Show All or Hide All has been clicked --%>
	  <f:verbatim><div style="color:green"></f:verbatim>
	  <h:outputText value="#{privacyBean.changeAllMsg}" rendered="#{privacyBean.allChanged}" />
	  <f:verbatim></div><br /></f:verbatim>
			  
	  <h:outputText value="#{msgs.privacy_choose}" />

	  <%-- TODO: add ValueChange event so status info displayed --%>
      <h:selectOneMenu value="#{privacyBean.selectedSite}" immediate="true" onchange="this.form.submit( );"
       valueChangeListener="#{privacyBean.processSiteSelected}">
          <f:selectItems value="#{privacyBean.sites}" />
      </h:selectOneMenu>
	  
	</h:panelGroup>

	<%-- *********** common Tool rendering *********** --%>
	<%-- TODO: only render when specific site selected --%>
	<h:panelGroup rendered="#{!privacyBean.myWorkspace || privacyBean.siteSelected}" >
      <f:verbatim><div class="instruction"></f:verbatim>
      <h:outputText value="#{msgs.privacy_stmt1}" />
 
	  <f:verbatim><b></f:verbatim>
	  <h:outputText value="#{privacyBean.currentStatus}" />
	  <f:verbatim></b></f:verbatim>
 	        
 	  <h:outputText value="#{msgs.privacy_stmt_show} " rendered="#{privacyBean.show}" />
 	  <h:outputText value="#{msgs.privacy_stmt_hide} " rendered="#{! privacyBean.show}" />    
 	     
 	  <f:verbatim><br /></f:verbatim>
 	     
 	  <h:outputText value="#{msgs.privacy_change_directions}" />
 	  <h:outputText value=" #{privacyBean.checkboxText} " />
 	  <h:outputText value="#{msgs.privacy_change_directions2}" />
      <f:verbatim></div></f:verbatim>
     
      <h:selectBooleanCheckbox value="#{privacyBean.changeStatus}" id="statusChange" />
      <h:outputText value="#{privacyBean.checkboxText}" />
      <f:verbatim><br /><br /></f:verbatim>

      <h:outputText value="#{msgs.privacy_note}" />
      <f:verbatim><br /></f:verbatim>
	</h:panelGroup>
         
	  <sakai:button_bar>
        <sakai:button_bar_item action="#{privacyBean.processUpdate}" value="#{msgs.privacy_update}"
            accesskey="u" title="#{msgs.privacy_update_title}" styleClass="active" />

   		<h:panelGroup rendered="#{privacyBean.myWorkspace}" >
   			<sakai:button_bar_item action="#{privacyBean.processShowAll}" value="#{msgs.privacy_show_all}"
   				accesskey="s" title="#{msgs.privacy_show_title}" styleClass="active" />
   			<sakai:button_bar_item action="#{privacyBean.processHideAll}" value="#{msgs.privacy_hide_all}"
   				accesskey="s" title="#{msgs.privacy_hide_title}" styleClass="active" />
	   	</h:panelGroup>

	  </sakai:button_bar>
  
    </h:form>
  </sakai:view>
</f:view>   