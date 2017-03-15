package com.tgrigorov.postbox.services.authentication;

import android.app.Activity;

import com.microsoft.aad.adal.AuthenticationCallback;
import com.microsoft.aad.adal.AuthenticationContext;
import com.microsoft.aad.adal.AuthenticationResult;
import com.microsoft.aad.adal.PromptBehavior;
import com.tgrigorov.postbox.PostBox;
import com.tgrigorov.postbox.data.entities.Person;
import com.tgrigorov.postbox.data.entities.User;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.concurrent.Callable;

import javax.crypto.NoSuchPaddingException;

public class OpenAuthenticationProvider implements IOpenAuthenticationProvider {
    public AuthenticationContext getAuthenticationContext() {
        return authenticationContext;
    }

    public void initContext(Activity context, Callable<Void> authenticationComplete) {
        this.authenticationComplete = authenticationComplete;

        try {
            authenticationContext = new AuthenticationContext(context, AUTHORITY, false);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            PostBox.getExceptionHandler().handle(e);
        }
    }

    public void acquireToken(Activity context) {
        authenticationContext.acquireToken(
                context,
                GRAPH_RESOURCE,
                CLIENT_ID,
                REDIRECT_URI,
                PromptBehavior.Always,
                new AuthenticationCallback<AuthenticationResult>() {
                    @Override
                    public void onSuccess(AuthenticationResult result) {
                        onRequestSuccess(result);
                    }

                    @Override
                    public void onError(Exception exception) {
                        PostBox.getExceptionHandler().handle(exception);
                    }
                });
    }

    public void refreshToken(final User user) {
        authenticationContext.acquireTokenByRefreshToken(
                user.getRefreshToken(),
                CLIENT_ID,
                GRAPH_RESOURCE,
                new AuthenticationCallback<AuthenticationResult>() {
                    @Override
                    public void onSuccess(AuthenticationResult result) {
                        onRefreshSuccess(result, user);
                    }

                    @Override
                    public void onError(Exception exception) {
                        PostBox.getExceptionHandler().handle(exception);
                    }
                });
    }

    private void onRequestSuccess(AuthenticationResult result) {
        PostBox.getUserService().create(result);

        try {
            authenticationComplete.call();
        } catch (Exception e) {
            PostBox.getExceptionHandler().handle(e);
        }
    }

    private void onRefreshSuccess(AuthenticationResult result, User user) {
        PostBox.getUserService().update(result, user);

        try {
            authenticationComplete.call();
        } catch (Exception e) {
            PostBox.getExceptionHandler().handle(e);
        }
    }

    private static final String CLIENT_ID = "05682191-1486-46ef-8a48-eb665b89d480";
    private static final String REDIRECT_URI = "http://www.postbox.eu";
    private static final String GRAPH_RESOURCE = "https://graph.microsoft.com";
    private static final String AUTHORITY = "https://login.windows.net/common";

    private AuthenticationContext authenticationContext;
    private Callable<Void> authenticationComplete;
}
