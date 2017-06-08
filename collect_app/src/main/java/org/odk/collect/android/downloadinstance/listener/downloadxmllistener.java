package org.odk.collect.android.downloadinstance.listener;

import org.odk.collect.android.downloadinstance.GetXml;

/**
 * Created by Cloud Walker on 24/02/2016.
 */
public interface downloadxmllistener {
    void ondownloadxmlcomplete(boolean complete, GetXml.DocumentFetchResult doc)throws Exception;
}
