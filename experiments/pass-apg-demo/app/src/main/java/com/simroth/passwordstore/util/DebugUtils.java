package com.simroth.passwordstore.util;

import android.content.Intent;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.simroth.passwordstore.BuildConfig;

import java.util.List;

public final class DebugUtils {

    private DebugUtils() { }

    @SuppressWarnings({ "PointlessBooleanExpression", "ConstantConditions" })
    public static String debugIntent(final Intent intent) {

        if (!BuildConfig.DEBUG) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        sb.append(intent);

        if (intent != null && intent.getExtras() != null) {

            sb.append(" | extras: ").append(Joiner.on(", ").join(Iterables.transform(intent.getExtras().keySet(),
                    new Function<String, String>() {
                        @Override
                        public String apply(final String input) {
                            return input + "=" + intent.getExtras().get(input);
                        }
                    })));
            sb.append(" | action: ").append(intent.getAction());
        }

        sb.append(" | flags: ").append(getFlagsString(intent));

        return sb.toString();
    }

    private static String getFlagsString(final Intent intent) {
        if (intent == null) {
            return "<null>";
        }

        final List<String> flagsAsString = Lists.newLinkedList();

        final int flags = intent.getFlags();

        if (hasFlag(flags, Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)) {
            flagsAsString.add("FLAG_ACTIVITY_REORDER_TO_FRONT");
        }

        if (hasFlag(flags, Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)) {
            flagsAsString.add("FLAG_ACTIVITY_BROUGHT_TO_FRONT");
        }

        if (hasFlag(flags, Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY)) {
            flagsAsString.add("FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY");
        }

        if (hasFlag(flags, Intent.FLAG_ACTIVITY_CLEAR_TOP)) {
            flagsAsString.add("FLAG_ACTIVITY_CLEAR_TOP");
        }

        if (hasFlag(flags, Intent.FLAG_ACTIVITY_CLEAR_TASK)) {
            flagsAsString.add("FLAG_ACTIVITY_CLEAR_TASK");
        }

        // TODO add them all

        return flagsAsString.isEmpty() ? "none" : Joiner.on(", ").join(flagsAsString);
    }

    private static boolean hasFlag(final int flags, final int flag) {
        return (flags & flag) != 0;
    }
}
