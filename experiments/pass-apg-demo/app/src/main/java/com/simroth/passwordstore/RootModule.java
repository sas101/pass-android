package com.simroth.passwordstore;

import dagger.Module;

/**
 * Add all the other modules to this one.
 */
@Module
(
    includes = {
            AndroidModule.class,
            PasswordStoreModule.class
    }
)
public class RootModule {
}
