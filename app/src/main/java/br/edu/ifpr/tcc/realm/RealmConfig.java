package br.edu.ifpr.tcc.realm;

import android.content.Context;

import io.realm.RealmConfiguration;

/**
 * Created by diego on 08/06/2016.
 */
public class RealmConfig {

    private static RealmConfiguration config;

    private RealmConfig()
    {

    }

    public static RealmConfiguration getConfig(Context context)
    {

        if ( config == null )
        {
            config = new RealmConfiguration.Builder(context).
                    name("br.edu.ifpr.tcc.realm")
                    .schemaVersion(1)
                    .setModules(new Modulo())
                    .deleteRealmIfMigrationNeeded()
                    .build();
        }

        return config;

    }

}
