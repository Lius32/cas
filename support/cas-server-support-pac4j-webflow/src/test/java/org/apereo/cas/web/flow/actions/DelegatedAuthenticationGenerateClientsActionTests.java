package org.apereo.cas.web.flow.actions;

import org.apereo.cas.authentication.AuthenticationException;
import org.apereo.cas.web.BaseDelegatedAuthenticationTests;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.support.WebUtils;

import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.context.ExternalContextHolder;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockRequestContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link DelegatedAuthenticationGenerateClientsActionTests}.
 *
 * @author Misagh Moayyed
 * @since 6.6.0
 */

@Tag("WebflowAuthenticationActions")
public class DelegatedAuthenticationGenerateClientsActionTests {

    @SpringBootTest(classes = BaseDelegatedAuthenticationTests.SharedTestConfiguration.class,
        properties = "cas.authn.pac4j.core.discovery-selection.selection-type=MENU")
    @Nested
    @SuppressWarnings("ClassCanBeStatic")
    public class MenuSelectionTests {
        @Autowired
        @Qualifier(CasWebflowConstants.ACTION_ID_DELEGATED_AUTHENTICATION_CREATE_CLIENTS)
        private Action delegatedAuthenticationCreateClientsAction;

        @Test
        public void verifyOperation() throws Exception {
            val context1 = getMockRequestContext();
            val result = delegatedAuthenticationCreateClientsAction.execute(context1);
            assertEquals(CasWebflowConstants.TRANSITION_ID_SUCCESS, result.getId());
            assertFalse(WebUtils.getDelegatedAuthenticationProviderConfigurations(context1).isEmpty());
            assertFalse(WebUtils.isDelegatedAuthenticationDynamicProviderSelection(context1));
            assertEquals(HttpStatus.FOUND.value(),
                WebUtils.getHttpServletResponseFromExternalWebflowContext(context1).getStatus());
            val context2 = getMockRequestContext();
            WebUtils.getHttpServletResponseFromExternalWebflowContext(context2).setStatus(HttpStatus.UNAUTHORIZED.value());
            assertThrows(AuthenticationException.class, () -> delegatedAuthenticationCreateClientsAction.execute(context2));
        }
    }

    private static RequestContext getMockRequestContext() {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, response));
        RequestContextHolder.setRequestContext(context);
        ExternalContextHolder.setExternalContext(context.getExternalContext());
        return context;
    }

    @SpringBootTest(classes = BaseDelegatedAuthenticationTests.SharedTestConfiguration.class,
        properties = "cas.authn.pac4j.core.discovery-selection.selection-type=DYNAMIC")
    @Nested
    @SuppressWarnings("ClassCanBeStatic")
    public class DynamicSelectionTests {
        @Autowired
        @Qualifier(CasWebflowConstants.ACTION_ID_DELEGATED_AUTHENTICATION_CREATE_CLIENTS)
        private Action delegatedAuthenticationCreateClientsAction;

        @Test
        public void verifyOperation() throws Exception {
            val context = getMockRequestContext();

            val result = delegatedAuthenticationCreateClientsAction.execute(context);
            assertEquals(CasWebflowConstants.TRANSITION_ID_SUCCESS, result.getId());
            assertTrue(WebUtils.getDelegatedAuthenticationProviderConfigurations(context).isEmpty());
            assertTrue(WebUtils.isDelegatedAuthenticationDynamicProviderSelection(context));
        }
    }
}
