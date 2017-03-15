package com.tgrigorov.postbox.services.data;


import com.microsoft.aad.adal.AuthenticationResult;
import com.tgrigorov.postbox.PostBox;
import com.tgrigorov.postbox.data.entities.Person;
import com.tgrigorov.postbox.data.entities.User;
import com.tgrigorov.postbox.http.data.MailDetailModel;
import com.tgrigorov.postbox.utils.IPredicate;
import com.tgrigorov.postbox.utils.ListUtils;

import java.util.List;

public class UserService implements IUserService {
    public User loadByAddress(final String address) {
        List<User> users = PostBox.getDbContext().getContext(User.class).list();
        List<User> filtered = ListUtils.filter(users, new IPredicate<User>() {
            public boolean filter(User item) {
                return item.getPerson().getAddress().equals(address);
            }
        });
        return ListUtils.firstOrDefault(filtered);
    }

    public User loadCurrent() {
        List<User> users = PostBox.getDbContext().getContext(User.class).list();
        List<User> filtered = ListUtils.filter(users, new IPredicate<User>() {
            public boolean filter(User item) {
                return item.isCurrent();
            }
        });
        return ListUtils.firstOrDefault(filtered);
    }

    public User create(AuthenticationResult result) {
        String address = result.getUserInfo().getDisplayableId();
        User user = loadByAddress(address);

        if (user == null) {
            MailDetailModel.PersonInfo info = new MailDetailModel().new PersonInfo();
            info.name = result.getUserInfo().getGivenName() + " " + result.getUserInfo().getFamilyName();
            info.address = address;
            Person person = PostBox.getPersonService().create(info);

            user = new User();
            user.setPerson(person);
            user.setToken(result.getAccessToken());
            user.setRefreshToken(result.getRefreshToken());
            user.setCurrent(true);
            user.setExternalId(result.getIdToken());
            user = PostBox.getDbContext().getContext(User.class).create(user);
        } else {
            user.setCurrent(true);
            update(result, user);
        }

        return user;
    }

    public User update(AuthenticationResult result, User user) {
        user.setToken(result.getAccessToken());
        user.setRefreshToken(result.getRefreshToken());
        user = PostBox.getDbContext().getContext(User.class).update(user);
        return user;
    }
}