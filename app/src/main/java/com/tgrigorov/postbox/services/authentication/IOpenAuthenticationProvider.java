package com.tgrigorov.postbox.services.authentication;

import android.app.Activity;

import com.microsoft.aad.adal.AuthenticationContext;
import com.tgrigorov.postbox.data.entities.User;

import java.util.concurrent.Callable;

public interface IOpenAuthenticationProvider {
    AuthenticationContext getAuthenticationContext();
    void initContext(Activity context, Callable<Void> authenticationComplete);
    void acquireToken(Activity context);
    void refreshToken(final User user);
}