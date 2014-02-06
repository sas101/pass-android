pass-android
============

Android password manager app compatible with [pass](http://www.zx2c4.com/projects/password-store/), the standard unix password manager.

Core Features
-------------

1. Secure Password Storage

    Encrypted container for passwords to hide even their file names.

1. Secure Copy & Paste

    Control the Clipboard (i.e. clear after 45 seconds). Don't let unauthorized apps access the password you want to copy & paste.

1. Browse passwords to copy or show

    Recursively browse the directories with passwords. Allow to copy or show.

1. Built-in keyboard for passphrase entry

    Don't let other apps snoop the passphrase. Be paranoid.

1. Use GnuPG

    Full compatibility with pass and the gnupg toolchain.

1. Import & Update passwords

    One-way push of passwords from computer to device.

1. Secure Passphrase & Password Cache

    Don't leak anything. But don't make the mobile life too uncomfortable.

1. Improve security

    Include PRNG fixes.

1. Be Configurable

    Preferences. Let the user decide. As much as possible.

1. Modern UI and app architecture

    Pleasant to the eye. Easily extensible. Hackable.



Feature Details
---------------

### Secure Password Storage

Encrypted container for passwords

Store gpg encrypted passwords in an encrypted virtual file system / crypto container to avoid leakage of file names (and thus potentially usernames, etc., as well as information, where you have accounts / private information).

* Which encrypted virtual file system / crypto container to use?
 * https://github.com/facebook/conceal
 * https://github.com/guardianproject/IOCipher / https://guardianproject.info/code/iocipher/


### Secure Copy & Paste

* [Android Copy & Paste Guide](http://developer.android.com/guide/topics/text/copy-paste.html)
* watch out for [ClipboardManager.OnPrimaryClipChangedListener](http://developer.android.com/reference/android/content/ClipboardManager.OnPrimaryClipChangedListener.html)!
* probably safer solution: use [ContentProvider](http://developer.android.com/guide/topics/text/copy-paste.html#Provider)
* and control access via [Binder.getCallingUid()](http://developer.android.com/reference/android/os/Binder.html#getCallingUid%28%29)
* then ask the user, if he really wants to paste the current password into this application
* see also [this discussion](https://groups.google.com/forum/.!topic/android-security-discuss/gl9pxKjH6yE)

#### TODO: Write Demo/Test Apps for Secure Copy & Paste via ContentProvider
* Copy App with ContentProvider and Access Control
* Paste App can be anything, but should be a simple app that explores the Clipboard

### Browse passwords directories

* use Fragments with transition
* implement Search functionality later

### Built-in keyboard for passphrase entry

* Optionally (configurable) use a built-in keyboard to increase security.
* Might be a bit paranoid.
* [Example](https://code.google.com/p/android-keyboard-demo/source/browse/trunk/src/foo/bar/DemoActivity.java)

### Use gnupg

But *which gnupg* ?

1. use any available GnuPG-compatible app using DECRYPT Intent

    * TODO find and commit experiment with APG, adjust experiment to work with all below

1. use [guardianproject gnupg](https://github.com/guardianproject/gnupg-for-android)?

    * under heavy development
    * is exactly what we need on mobile
    * Q: can the GNUPGHOME/app_home directory be linked to our secure vault?

1. include own version of gnupg?

    * probably lots of work
    * guardianproject did that already

1. use ICryptoService from [OpenPGP Keychain](https://github.com/openpgp-keychain/openpgp-keychain/)? (allowing the user to choose)

    * follow-up of APG
    * modern UI
    * Java implementation
    * supposedly guardianproject will also support ICryptoService API (TODO REFERENCE)
    * [Discussion about inclusion in K-9 Mail](https://groups.google.com/forum/?fromgroups=.!topic/k-9-dev/0KwkFfuIY_Q)


Possible solution: Use Intent for now, maybe Guardian Project GnuPG in future, or even _also_ support ICryptoService one day?


### Import & Update passwords

Push passwords 1-way to the device

via:

1. internal webserver? i.e. [nanohttpd](https://github.com/guardianproject/nanohttpd)
1. Git?
1. custom Android SyncAdapter ... but fetch from where?
1. ...?

preferably use a commandline tool to update the passwords on the device. we are already using the commandline with pass!

Idea: use webserver that needs to be started explicitly in local Wifi home network, showing the IP and random port number. Then connect with `pass-push 192.168.1.123:46738` tool to this address and send password gpg files to this address as HTTP POST with full directory name.

TODO: How to handle deletes and moves? Consider every password that didn't receive a push (HTTP POST / HEAD?) as "to delete"?

### Secure Passphrase & Password Cache

* i.e. [CacheWord](https://github.com/guardianproject/cacheword)
* in-memory protection?

### Improve overall security

* While we are at it, include PRNG fixes: [Some SecureRandom Thoughts](http://android-developers.blogspot.de/2013/08/some-securerandom-thoughts.html).
* We probably anyways need this.

### Be Configurable

* Preferences
 * Store location
 * Cache password timeout
 * ...?

### Modern UI and app architecture

* use [Android Bootstrap](http://www.androidbootstrap.com/)
* use Android Studio
* use Gradle


Wishlist / Secondary Features
-----------------------------

Maybe one day support:

1. Searching for a password
1. Sharing of a password (TODO: analyse security impact)
1. Creation of passwords
1. Real 2-way sync (with conflict handling / merging)
1. Security tips, like "Did you know..." you can improve your ~/.gnupg/gpg.conf settings? i.e. improve hashing algorithms used, see bettercrypto.org

