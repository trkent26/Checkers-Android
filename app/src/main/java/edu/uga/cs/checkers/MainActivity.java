package edu.uga.cs.checkers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;


/**
 * CHECKERS
 *
 * MADE BY:
 * THOMAS KENT
 * HARRISON WEESE
 */

public class MainActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "MainActivity";

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d( DEBUG_TAG, "MainActivity.onCreate()" );

        Button signInButton = findViewById( R.id.button1 );
        Button registerButton = findViewById( R.id.button2 );

        signInButton.setOnClickListener( new SignInButtonClickListener() );
        registerButton.setOnClickListener( new RegisterButtonClickListener() );

        // Check if user is signed in and if signed in, sign the user out before proceeding.
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if( currentUser != null )
            mAuth.signOut();
    }

    private class SignInButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick( View v ) {

            // This is an example of how to use the AuthUI activity for signing in
            // Here, we are just using email/password sign in
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build()
            );

            // Start a sign in activity, but come back with a result
            // This is an example of how to use startActivityForResult
            // The last argument is the code of the result from this call
            // It is used later in the onActivityResult listener to identify
            // the result as a result from this startActivityForResult call.
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(false)
                            .build(),
                    RC_SIGN_IN);
        }
    }

    private class RegisterButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // start the user registration activity
            Intent intent = new Intent(view.getContext(), RegisterActivity.class);
            view.getContext().startActivity(intent);
        }
    }

    // [START auth_fui_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d( DEBUG_TAG, "JobLead: MainActivity.onActivityResult()" );

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                Log.i( "FireBase Test", "Signed in as: " + user.getEmail() );

                // after a successful sign in, start the job leads management activity
                Intent intent = new Intent( this, HomeActivity.class );
                startActivity( intent );

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                Toast.makeText( getApplicationContext(),
                        "Sign in failed",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    // [END auth_fui_result]
}