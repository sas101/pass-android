package com.simroth.passwordstore;

import com.simroth.passwordstore.ui.PasswordStoreActivity;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for setting up provides statements.
 * Register all of your entry points below.
 */
@Module
(
        complete = false,
        // library = true,

        injects = {
                PasswordStoreApp.class,
                PasswordStoreActivity.class
        }

)
public class PasswordStoreModule {

    @Singleton
    @Provides
    Bus provideOttoBus() {
        return new Bus();
    }

}
