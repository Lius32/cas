package org.apereo.cas.adaptors.generic;

import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.credential.HttpBasedServiceCredential;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.principal.Service;

import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Scott Battaglia
 * @since 3.0.0
 */
@Tag("FileSystem")
public class RejectUsersAuthenticationHandlerTests {
    private final RejectUsersAuthenticationHandler authenticationHandler;

    public RejectUsersAuthenticationHandlerTests() {
        val users = new HashSet<String>();
        users.add("scott");
        users.add("dima");
        users.add("bill");
        authenticationHandler = new RejectUsersAuthenticationHandler(StringUtils.EMPTY, null, null, users);
    }

    @Test
    public void verifySupportsProperUserCredentials() throws Exception {
        val c = new UsernamePasswordCredential();

        c.setUsername("fff");
        c.setPassword("rutgers");
        assertNotNull(authenticationHandler.authenticate(c, mock(Service.class)));
    }

    @Test
    public void verifyDoesntSupportBadUserCredentials() {
        try {
            assertFalse(authenticationHandler
                .supports(new HttpBasedServiceCredential(new URL(
                    "http://www.rutgers.edu"), CoreAuthenticationTestUtils.getRegisteredService())));
        } catch (final MalformedURLException e) {
            throw new AssertionError("Could not resolve URL.");
        }
    }

    @Test
    public void verifyFailsUserInMap() {
        val c = new UsernamePasswordCredential();

        c.setUsername("scott");
        c.setPassword("rutgers");

        assertThrows(FailedLoginException.class, () -> authenticationHandler.authenticate(c, mock(Service.class)));
    }

    @Test
    public void verifyPassesUserNotInMap() throws Exception {
        val c = new UsernamePasswordCredential();

        c.setUsername("fds");
        c.setPassword("rutgers");

        assertNotNull(authenticationHandler.authenticate(c, mock(Service.class)));
    }

    @Test
    public void verifyPassesNullUserName() {
        val c = new UsernamePasswordCredential();

        c.setUsername(null);
        c.setPassword("user");

        assertThrows(AccountNotFoundException.class, () -> authenticationHandler.authenticate(c, mock(Service.class)));
    }

    @Test
    public void verifyPassesNullUserNameAndPassword() {
        assertThrows(AccountNotFoundException.class, () -> authenticationHandler.authenticate(new UsernamePasswordCredential(), mock(Service.class)));
    }
}
