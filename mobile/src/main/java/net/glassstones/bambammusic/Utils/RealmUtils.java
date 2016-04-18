package net.glassstones.bambammusic.utils;


import android.content.Context;

import io.realm.Realm;

public class RealmUtils {

    Realm realm;

    public RealmUtils(Context context){
        this.realm = Realm.getInstance(context);
    }

}
