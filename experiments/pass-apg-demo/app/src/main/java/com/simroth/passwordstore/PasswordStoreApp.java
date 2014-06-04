

package com.simroth.passwordstore;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.FROYO;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;

import com.github.kevinsawicki.http.HttpRequest;

import dagger.ObjectGraph;

/**
 * Password Store application
 */
public class PasswordStoreApp extends Application {

    private static PasswordStoreApp instance;
    ObjectGraph objectGraph;

    /**
     * Create main application
     */
    public PasswordStoreApp() {

        // Disable http.keepAlive on Froyo and below
        if (SDK_INT <= FROYO)
            HttpRequest.keepAlive(false);
    }

    /**
     * Create main application
     *
     * @param context
     */
    public PasswordStoreApp(final Context context) {
        this();
        attachBaseContext(context);

    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        // Perform Injection
        objectGraph = ObjectGraph.create(getRootModule());
        objectGraph.inject(this);
        objectGraph.injectStatics();

    }

    private Object getRootModule() {
        return new RootModule();
    }


    /**
     * Create main application
     *
     * @param instrumentation
     */
    public PasswordStoreApp(final Instrumentation instrumentation) {
        this();
        attachBaseContext(instrumentation.getTargetContext());
    }

    public void inject(Object object)
    {
        objectGraph.inject(object);
    }



    public static PasswordStoreApp getInstance() {
        return instance;
    }
}
