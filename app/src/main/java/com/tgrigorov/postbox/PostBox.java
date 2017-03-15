package com.tgrigorov.postbox;


import android.app.Application;
import android.content.Context;

import com.tgrigorov.postbox.http.data.IModelFactory;
import com.tgrigorov.postbox.http.data.ISerializer;
import com.tgrigorov.postbox.http.data.ModelFactory;
import com.tgrigorov.postbox.http.data.Serializer;
import com.tgrigorov.postbox.data.DbContext;
import com.tgrigorov.postbox.data.IDbContext;
import com.tgrigorov.postbox.data.entities.EntityFactory;
import com.tgrigorov.postbox.data.entities.IEntityFactory;
import com.tgrigorov.postbox.http.IRestClient;
import com.tgrigorov.postbox.http.RestClient;
import com.tgrigorov.postbox.services.ExceptionHandler;
import com.tgrigorov.postbox.services.IExceptionHandler;
import com.tgrigorov.postbox.services.data.IMailService;
import com.tgrigorov.postbox.services.authentication.IOpenAuthenticationProvider;
import com.tgrigorov.postbox.services.data.IPersonService;
import com.tgrigorov.postbox.services.data.IUserService;
import com.tgrigorov.postbox.services.data.MailService;
import com.tgrigorov.postbox.services.authentication.OpenAuthenticationProvider;
import com.tgrigorov.postbox.services.data.PersonService;
import com.tgrigorov.postbox.services.data.UserService;
import com.tgrigorov.postbox.ui.notifications.Alerter;
import com.tgrigorov.postbox.ui.notifications.IAlerter;
import com.tgrigorov.postbox.ui.notifications.IToaster;
import com.tgrigorov.postbox.ui.notifications.Toaster;

public class PostBox extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Context context = getApplicationContext();
        absolutePath = context.getFilesDir().getAbsolutePath();
        exceptionHandler = new ExceptionHandler();
        openAuthenticationProvider = new OpenAuthenticationProvider();
        toaster = new Toaster();
        alerter = new Alerter();

        dbContext = new DbContext(context);
        entityFactory = new EntityFactory();

        modelFactory = new ModelFactory();
        restClient = new RestClient();
        serializer = new Serializer();

        userService = new UserService();
        mailService = new MailService();
        personService = new PersonService();

        dbContext.configure();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        dbContext.disconnect();
    }

    public static String getAbsolutePath() {
        return absolutePath;
    }

    public static IOpenAuthenticationProvider getOpenAuthenticationProvider() {
        return openAuthenticationProvider;
    }

    public static IToaster getToaster() {
        return toaster;
    }

    public static IAlerter getAlerter() {
        return alerter;
    }

    public static IExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public static IDbContext getDbContext() {
        return dbContext;
    }

    public static IEntityFactory getEntityFactory() {
        return entityFactory;
    }

    public static IModelFactory getModelFactory() {
        return modelFactory;
    }

    public static IRestClient getRestClient() {
        return restClient;
    }

    public static ISerializer getSerializer() {
        return serializer;
    }

    public static IUserService getUserService() {
        return userService;
    }

    public static IMailService getMailService() {
        return mailService;
    }

    public static IPersonService getPersonService() {
        return personService;
    }

    private static String absolutePath;
    private static IExceptionHandler exceptionHandler;
    private static IOpenAuthenticationProvider openAuthenticationProvider;
    private static IToaster toaster;
    private static IAlerter alerter;

    private static IDbContext dbContext;
    private static IEntityFactory entityFactory;

    private static IModelFactory modelFactory;
    private static IRestClient restClient;
    private static ISerializer serializer;

    private static IUserService userService;
    private static IMailService mailService;
    private static IPersonService personService;
}
