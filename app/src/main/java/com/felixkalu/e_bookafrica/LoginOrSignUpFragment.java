package com.felixkalu.e_bookafrica;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class LoginOrSignUpFragment extends android.app.Fragment implements View.OnClickListener, View.OnKeyListener {

    public LoginOrSignUpFragment() {
        // Required empty public constructor
    }

    TextView userNameTextView;
    TextView passwordTextView;
    Button btnLoginOrSignin;
    TextView signupTextView;
    TextView emailTextView;
    ConstraintLayout constraintLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_loginorsignupfragment, container, false);

        userNameTextView = (TextView) v.findViewById(R.id.usernameTextView);
        passwordTextView = (TextView) v.findViewById(R.id.passwordTextView);
        signupTextView = (TextView) v.findViewById(R.id.signupTextView);
        btnLoginOrSignin = (Button) v.findViewById(R.id.btnLoginOrSignin);
        constraintLayout = (ConstraintLayout) v.findViewById(R.id.layoutContraint);
        emailTextView = (TextView) v.findViewById(R.id.emailTextView);

        signupTextView.setOnClickListener(this);
        btnLoginOrSignin.setOnClickListener(this);
        constraintLayout.setOnClickListener(this);



        return v;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.signupTextView:
                if(btnLoginOrSignin.getText().toString().toLowerCase().equals("log in")) {
                    btnLoginOrSignin.setText("Sign Up");
                    signupTextView.setText("Or, Log In");
                    emailTextView.setVisibility(View.VISIBLE);
                    userNameTextView.setHint("Username:");
                }
                else if(btnLoginOrSignin.getText().toString().toLowerCase().equals("sign up")) {
                    btnLoginOrSignin.setText("Log In");
                    signupTextView.setText("Or, Sign Up");
                    emailTextView.setVisibility(View.INVISIBLE);
                    userNameTextView.setHint("UserName/Email Address:");

                }
                break;
            case R.id.btnLoginOrSignin:
                if(btnLoginOrSignin.getText().toString().toLowerCase().equals("log in")) {
                    logIn();
                } else {
                    signUp();
                }
                break;
            case R.id.layoutContraint:
                //shut down the keyboard if the user taps anywhere else on the constraint layout.
                InputMethodManager inputMethodManager = (InputMethodManager) this.getActivity().getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(this.getActivity().getCurrentFocus().getWindowToken(), 0);
                break;
        }

    }

    public void signUp() {

        if(userNameTextView.getText().toString().matches("")
                ||passwordTextView.getText().toString().matches("")
                ||emailTextView.getText().toString().matches("")) {
            Toast.makeText(this.getActivity(), "UserName and Password and Email Address are required", Toast.LENGTH_LONG).show();
        } else {
            ParseUser user = new ParseUser();

            user.setUsername(userNameTextView.getText().toString());
            user.setPassword(passwordTextView.getText().toString());
            user.setEmail(emailTextView.getText().toString());

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null) {
                        Log.i("Signup", "Successful");
                        openMyProfileFragment();
                    } else {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }


    }

    public void logIn() {

        if (userNameTextView.getText().toString().matches("") || passwordTextView.getText().toString().matches("")) {
            Toast.makeText(this.getActivity(), "UserName and Password is required", Toast.LENGTH_LONG).show();

        } else {
            ParseUser.logInInBackground(userNameTextView.getText().toString(), passwordTextView.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {

                    if (user != null) {
                        Log.i("Log in", "Login Successful");
                        openMyProfileFragment();
                    } else {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    //this function is to ensure that the enter key on the onscreen keyboard logs in or signs up the user.
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        //to check if the key pressed was the enter button and ensure that the action was pressed down.
        // without the event.getAction() == KeyEvent.ACTION_DOWN check,
        // the first one calls the function twice because it fires an event when the key is pressed down and
        // repeats the same event when the key is released. Watch section8, Lecture 135 to understand better from 04:50 secs
        if( keyCode == event.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            signUp();
        }
        return false;
    }

    //redirect to the myEbooksFragment
    public void openMyProfileFragment() {
        MyProfileFragment myProfileFragment= new MyProfileFragment();
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.frame, myProfileFragment,"myEbooksFragment")
                .addToBackStack(null)
                .commit();
    }
}
