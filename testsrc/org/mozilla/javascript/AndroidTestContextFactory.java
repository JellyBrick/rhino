package org.mozilla.javascript;

import android.support.test.InstrumentationRegistry;

import com.faendir.rhino_android.AndroidContextFactory;

public class AndroidTestContextFactory extends AndroidContextFactory {

    public AndroidTestContextFactory() {
        super(InstrumentationRegistry.getContext());
    }
}
