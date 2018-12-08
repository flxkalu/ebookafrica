/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.felixkalu.e_bookafrica;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class StarterApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);

    // Add your initialization code here
    Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
            .applicationId("f5a9c3e75b6454cad821a48368724094a17325b2")
            .clientKey("5bbe857765ee95c9a52af73bd5e2db195fea6b28")
            .server("http://18.217.207.51:80/parse/")
            .build()
    );

    //The commented code below is used to check and confirm that the app is correctly connected to parse server. it is commented because we have confirmed that the connection is working as it should.
    // To revise and remember how this whole thing is done, watch section8, Lecture 128.

/*
    ParseObject object = new ParseObject("ExampleObject");
    object.put("Number", "08162607777");
    object.put("Name", "Felix");

    object.saveInBackground(new SaveCallback() {
      @Override
      public void done(ParseException ex) {
        if (ex == null) {
          Log.i("Parse Result", "Successful!");
        } else {
          Log.i("Parse Result", "Failed" + ex.toString());
        }
      }
    });
*/

    //this line of code automatically creates the user sign up but if you want to take control of user sign up, remove the line. Watch section8, Lecture 131 to understand better

    //ParseUser.enableAutomaticUser();
    ParseACL defaultACL = new ParseACL();
    defaultACL.setPublicReadAccess(true);
    defaultACL.setPublicWriteAccess(true);
    ParseACL.setDefaultACL(defaultACL, true);

  }
}
