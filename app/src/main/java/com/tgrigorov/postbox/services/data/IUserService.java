package com.tgrigorov.postbox.services.data;


import com.microsoft.aad.adal.AuthenticationResult;
import com.tgrigorov.postbox.data.entities.User;

public interface IUserService {
    User loadByAddress(final String address);

    User loadCurrent();

    User create(AuthenticationResult result);

    User update(AuthenticationResult result, User user);
}
