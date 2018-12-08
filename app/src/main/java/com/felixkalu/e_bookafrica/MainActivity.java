package com.felixkalu.e_bookafrica;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.folioreader.FolioReader;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import static com.felixkalu.e_bookafrica.BookDetails.progress_bar_type;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ImageView imageView;
    SearchView searchView;

    //for the download progress dialog. It is declared as public static so that it can be called from the BookDetails class.
    public static ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.navHeaderTextView1);
        TextView navEmailTextView = (TextView) headerView.findViewById(R.id.navHeaderTextView2);
        imageView = (ImageView)headerView.findViewById(R.id.imageView);

        searchView = (SearchView)findViewById(R.id.searchView);
        searchView.setVisibility(View.INVISIBLE);

        if(ParseUser.getCurrentUser()!=null) {
            navUsername.setText(ParseUser.getCurrentUser().getUsername());
            navEmailTextView.setText(ParseUser.getCurrentUser().getEmail());
            getProfilePicture();
        }

        StoreFragment fragment = new StoreFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "fragment1");
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            ParseUser.logOut();
            Toast.makeText(this, "Logged Out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.StoreFragment) {
            setTitle("E-book Africa Store");
            StoreFragment fragment = new StoreFragment();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment, "storeFragment");
            fragmentTransaction.commit();

        } else if (id == R.id.MyEbooksFragment) {
            setTitle("My E-Books Fragment");
            MyEbooksFragment fragment = new MyEbooksFragment();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment, "myEbooksFragment");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (id == R.id.LoginOrSignUpFragment) {

            if(ParseUser.getCurrentUser()==null) {

                setTitle("Log in/Sign Up");
                LoginOrSignUpFragment fragment = new LoginOrSignUpFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment, "loginOrSignUpFragment");
                fragmentTransaction.commit();
            } else {
                setTitle("Profile");
                MyProfileFragment fragment = new MyProfileFragment();

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment, "myProfileFragment");
                fragmentTransaction.commit();
            }
        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getProfilePicture() {

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("ProfilePictures");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                //if there are no errors...
                if( e == null) {
                    //...and objects returned is greater than 0...
                    if(objects.size() > 0) {
                        //loop through all objects that were returned
                        for(ParseObject object : objects) {
                            //create a parseFile file, get the the object i.e. the current image on the loop, and assign it to the created parseFile file.
                            ParseFile file = (ParseFile) object.get("profilepicture");
                            //convert the image to a bitmap file.
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if(e == null && data != null) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        imageView.setImageBitmap(bitmap);

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        //if the object.size returned from the server is less than or equal to 0, tell the user that the user does not have any pictures
                    } else {
                        Toast.makeText(getApplicationContext(),"This User has no Pictures" ,Toast.LENGTH_LONG).show();
                    }
                    //if the exception is not equal to null, tell the user the reason.
                } else {
                    Toast.makeText(getApplicationContext(),e.getMessage() ,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //This method is here and is used only when a book is downloading.
    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case progress_bar_type:
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please Wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }
}
