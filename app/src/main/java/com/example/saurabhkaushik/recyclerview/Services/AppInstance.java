package com.example.saurabhkaushik.recyclerview.Services;

import android.content.Context;

/**
 * Created by saurabhkaushik on 12/02/17.
 */

public class AppInstance {

    private static PersistenceService persistenceService;
    public static PersistenceService getPersistenceService(Context context) {
        if (persistenceService == null) {
            synchronized (PersistenceService.class) {
                if (persistenceService == null) {
                    persistenceService = new PersistenceService(context);
                }
            }
        }
        return persistenceService;
    }
}
