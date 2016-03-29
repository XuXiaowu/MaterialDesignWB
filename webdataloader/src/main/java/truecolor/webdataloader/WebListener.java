/**
 * Copyright 2012 TrueColor Inc.
 */

package truecolor.webdataloader;

import android.os.Bundle;

/**
 * @author cris
 */
public interface WebListener {
    public void onDataLoadFinished(int service, Bundle params, Object result);
}
