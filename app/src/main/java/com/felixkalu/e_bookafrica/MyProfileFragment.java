package com.felixkalu.e_bookafrica;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends android.app.Fragment implements View.OnClickListener {

    ImageView imageView;
    TextView usernameTextView;
    TextView emailTextView;
    TextView questionTextView;
    TextView currentPassTextView;
    TextView newPassTextView;
    TextView reenterPassTextView;
    Button btnChangePassword;
    Button buttonViewMyBooks;
    ConstraintLayout constraintLayout;



    public MyProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_profile, container, false);

         imageView = (ImageView) v.findViewById(R.id.imageView);
         usernameTextView = (TextView) v.findViewById(R.id.usernameTextView);
         emailTextView = (TextView) v.findViewById(R.id.emailTextView);
         questionTextView = (TextView) v.findViewById(R.id.questionTextView);
         currentPassTextView = (TextView) v.findViewById(R.id.currentPass);
         newPassTextView = (TextView) v.findViewById(R.id.newPass);
         reenterPassTextView = (TextView) v.findViewById(R.id.reNewPass);
         btnChangePassword = (Button) v.findViewById(R.id.btnChangePassword);
         constraintLayout = (ConstraintLayout)v.findViewById(R.id.layoutContraint);

        questionTextView.setOnClickListener(this);
        btnChangePassword.setOnClickListener(this);
        imageView.setOnClickListener(this);


        //get the profile picture
        if(ParseUser.getCurrentUser()!=null) {
            getProfilePicture();
        }


        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.questionTextView:
                if(questionTextView.getText().toString().toLowerCase().matches("tap me to change password")) {
                    questionTextView.setText("Tap to cancel Password Change");
                    currentPassTextView.setVisibility(View.VISIBLE);
                    newPassTextView.setVisibility(View.VISIBLE);
                    reenterPassTextView.setVisibility(View.VISIBLE);
                    btnChangePassword.setVisibility(View.VISIBLE);
                } else {
                    questionTextView.setText("Tap me to change Password");
                    currentPassTextView.setVisibility(View.INVISIBLE);
                    newPassTextView.setVisibility(View.INVISIBLE);
                    reenterPassTextView.setVisibility(View.INVISIBLE);
                    btnChangePassword.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.btnChangePassword:
                //write the code to change the password
                Log.i("INFO: ", "Change Password!");
                break;
            case R.id.imageView:
                Log.i("INFO", "IMAGEVIEW CLICKED!");
                uploadProfilePicture();
                break;
        }

    }

    public void openMyEbooksFragment() {
        MyEbooksFragment myEbooksFragment= new MyEbooksFragment();
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.frame, myEbooksFragment,"myEbooksFragment")
                .addToBackStack(null)
                .commit();
    }

    public void uploadProfilePicture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                getPhoto();
            }
            //if we are not in marshMellow?
        } else {
            getPhoto();
        }
        //write code for what to do if log out button is clicked...
    }

    //when the user has granted us permission,
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }

    //this method handles getting photos from the media storage of the device
    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        //for startActivityForResult, the second parameter, requestCode is used to identify this particular intent
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null) {

            //this gets the link to our image and assigns it to selectedImage Uri
            Uri selectedImage = data.getData();

            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);

                Log.i("Photo", "Recieved");

                //This is the code that uploads the photo to the parse server
                //ByteArrayOutputStream allows us convert our image into a parse file which we can upload as part of a parse object to our parse server
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                //converts the image to png format and sets the quality to 100
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                //byteArray is used to convert the stream which includes the compressed png image into a byteArray
                byte[] byteArray = stream.toByteArray();

                //converting to a parse file. it is important to note that to get a parse file you need to go through a byteArray. that is why it's been used.
                ParseFile file = new ParseFile("image.png", byteArray);

                //this is where we will be storing all our images
                ParseObject object = new ParseObject("ProfilePictures");

                //telling parse server that this is what is to be saved in the image and username columns of the created Image class/object.
                object.put("profilepicture", file);
                object.put("username", ParseUser.getCurrentUser().getUsername());

                //saves everything including image and returns e exception if for any reason it does not save.
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null) {
                            Toast.makeText(getActivity(), "Profile Picture saved!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Could not be uploaded - Please try again later!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
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
                                        Toast.makeText(getActivity().getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        //if the object.size returned from the server is less than or equal to 0, tell the user that the user does not have any pictures
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(),"This User has no Pictures" ,Toast.LENGTH_LONG).show();
                    }
                    //if the exception is not equal to null, tell the user the reason.
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),e.getMessage() ,Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
