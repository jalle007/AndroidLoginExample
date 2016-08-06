package com.bcg.loginexample;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class checkCredentialsTest {


    @Test
    public void check() throws Exception {
        LoginActivity loginActivity =new LoginActivity();
        assertEquals(loginActivity.checkCredentials("jaskobh@hotmail.com", "123456"),"");
    }
}