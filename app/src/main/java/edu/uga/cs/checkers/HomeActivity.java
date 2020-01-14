package edu.uga.cs.checkers;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import static java.lang.Integer.parseInt;

/**
 * CHECKERS
 *
 * MADE BY:
 * THOMAS KENT
 * HARRISON WEESE
 */

public class HomeActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "HomeActivity";

    EditText friendEmail;
    Button challengeButton;
    TextView checkRec;
    Button logoutButton;

    /**
     * ------- onCreate() ----------------------------------------------------------------------------------
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /* Finding the Needed Views */
        friendEmail = findViewById( R.id.friendEmail );
        challengeButton = findViewById( R.id.challengeButton );
        checkRec = findViewById( R.id.checkRec );
        logoutButton = findViewById( R.id.logoutButton );

        /* Pop-up Challenge Stufffff */ // ------------------------ Start --------------------------
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.popup_challenged, null);
        final View popupView2 = inflater.inflate(R.layout.popup_challenging, null);

        TextView backgroundColor = popupView.findViewById(R.id.challengedBackground);
        backgroundColor.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        backgroundColor = popupView2.findViewById(R.id.textView6);
        backgroundColor.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        final PopupWindow challengedWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        challengedWindow.setOutsideTouchable(false);

        final PopupWindow challengingWindow = new PopupWindow(popupView2, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        challengingWindow.setOutsideTouchable(false);

        final TextView challengingText = popupView2.findViewById(R.id.challengingText);
        final TextView challengedText = popupView.findViewById(R.id.challengedText);
        /* Pop-up Challenge Stufffff */ // ------------------------ End --------------------------

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        /* Sets Email on Home Screen */
        checkRec.setText(currentUser.getEmail());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/" + currentUser.getEmail().substring(0, currentUser.getEmail().length()-4) + "/challenging");
        myRef.setValue("");

        /* Event Listener for Users/Email/challenging */
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                final String value = dataSnapshot.getValue(String.class);
                Log.d(DEBUG_TAG, "Value is: " + value);

                if (value.equals(""))
                {
                    // do nothing
                }
                else if (value.equals("-"))
                {
                    /* - represents "Challenged Accepted" */
                    challengingWindow.dismiss();

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("users/" + currentUser.getEmail().substring(0, currentUser.getEmail().length()-4) + "/challenging");
                    myRef.setValue("");

                    Intent intent = new Intent(getApplicationContext(), CheckersActivity.class);
                    getApplicationContext().startActivity(intent);
                }
                else if (value.equals("--"))
                {
                    /* -- represents "Challenged Declined" */
                    challengingWindow.dismiss();

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("users/" + currentUser.getEmail().substring(0, currentUser.getEmail().length()-4) + "/challenging");
                    myRef.setValue("");

                    Toast.makeText(getApplicationContext(),
                            "Challenge Declined!",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(DEBUG_TAG, "Failed to read value.", databaseError.toException());
            }
        });

        /* Upon Logging In, Initiates Everything to "" */
        myRef = database.getReference("users/" + currentUser.getEmail().substring(0, currentUser.getEmail().length()-4) + "/playingAgainst");
        myRef.setValue("");
        myRef = database.getReference("users/" + currentUser.getEmail().substring(0, currentUser.getEmail().length()-4) + "/currentGameID");
        myRef.setValue("");
        myRef = database.getReference("users/" + currentUser.getEmail().substring(0, currentUser.getEmail().length()-4) + "/updateBoard");
        myRef.setValue("false");
        myRef = database.getReference("users/" + currentUser.getEmail().substring(0, currentUser.getEmail().length()-4) + "/updateScore");
        myRef.setValue("false");
        myRef = database.getReference("users/" + currentUser.getEmail().substring(0, currentUser.getEmail().length()-4) + "/playerColor");
        myRef.setValue("");
        myRef = database.getReference("users/" + currentUser.getEmail().substring(0, currentUser.getEmail().length()-4) + "/challengedBy");
        myRef.setValue("");

        /* Event Listener for challengedBy */
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                final String value = dataSnapshot.getValue(String.class);
                Log.d(DEBUG_TAG, "Value is: " + value);

                if (value.equals(""))
                {
                    // do nothing
                }
                else if (value.equals("-"))
                {
                    /* - Represents Cancelation */
                    Toast.makeText(getApplicationContext(),
                            "Challenge Cancelled!",
                            Toast.LENGTH_LONG).show();

                    challengedWindow.dismiss();

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("users/" + currentUser.getEmail().substring(0, currentUser.getEmail().length()-4) + "/challengedBy");
                    myRef.setValue("");
                }
                else
                {

                    /* CHALLENGED */
                    challengedText.setText("You've been challenged by " + value + "!");
                    challengedText.setTextColor(getResources().getColor(R.color.colorPrimaryLight));

                    /* Landscape Stooof */
                    if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                        challengedWindow.showAtLocation(findViewById(R.id.landHome), Gravity.CENTER, 0, 0);
                    else
                        challengedWindow.showAtLocation(findViewById(R.id.homeLayout), Gravity.CENTER, 0, 0);

                    challengedWindow.getContentView().findViewById( R.id.acceptChallenge ).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            challengedWindow.dismiss();

                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("users/" + currentUser.getEmail().substring(0, currentUser.getEmail().length()-4) + "/playingAgainst");
                            myRef.setValue(value);
                            myRef = database.getReference("users/" + currentUser.getEmail().substring(0, currentUser.getEmail().length()-4) + "/challengedBy");
                            myRef.setValue("");

                            myRef = database.getReference("users/" + value.substring(0, value.length()-4) + "/playingAgainst");
                            myRef.setValue(currentUser.getEmail());
                            myRef = database.getReference("users/" + value.substring(0, value.length()-4) + "/challenging");
                            myRef.setValue("-");

                            myRef = database.getReference("nextID");

                            /* Event Listener For NextID */
                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // This method is called once with the initial value and again
                                    // whenever data at this location is updated.
                                    String value2 = dataSnapshot.getValue(String.class);
                                    int newVal;

                                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                    FirebaseUser currentUser = mAuth.getCurrentUser();

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference("nextID");

                                    if (value2 == null)
                                    {
                                        newVal = 1;
                                    }
                                    else
                                    {
                                        newVal = parseInt(value2)+1;
                                    }

                                    /* Game Setup Junk */
                                    myRef.setValue(Integer.toString(newVal));

                                    myRef = database.getReference("users/" + value.substring(0, value.length()-4) + "/currentGameID");
                                    myRef.setValue(Integer.toString(newVal));

                                    myRef = database.getReference("users/" + currentUser.getEmail().substring(0, currentUser.getEmail().length()-4) + "/currentGameID");
                                    myRef.setValue(Integer.toString(newVal));

                                    myRef = database.getReference("users/" + value.substring(0, value.length()-4) + "/playerColor");
                                    myRef.setValue("red");

                                    myRef = database.getReference("users/" + currentUser.getEmail().substring(0, currentUser.getEmail().length()-4) + "/playerColor");
                                    myRef.setValue("blue");

                                    myRef = database.getReference("games/" + newVal + "/redPlayer/username");
                                    myRef.setValue(value);

                                    /* Game Setup Junk Still */
                                    myRef = database.getReference("games/" + newVal + "/bluePlayer/username");
                                    myRef.setValue(currentUser.getEmail());

                                    myRef = database.getReference("games/" + newVal + "/gameOver");
                                    myRef.setValue("false");

                                    myRef = database.getReference("games/" + newVal + "/bluePlayer/score");
                                    myRef.setValue("0");

                                    myRef = database.getReference("games/" + newVal + "/redPlayer/score");
                                    myRef.setValue("0");

                                    Random rand = new Random();
                                    int randNum = rand.nextInt(20)+1;

                                    myRef = database.getReference("games/" + newVal + "/nextMove");
                                    if (randNum > 10)
                                        myRef.setValue("red");
                                    else
                                        myRef.setValue("blue");

                                    /* Setting Up the Inital Piece Locations in DB */
                                    for (int j = 1; j < 9; j += 1) {
                                        for (int i = 1; i < 9; i += 2) {
                                            if (i == 1 && j % 2 == 1) {
                                                i += 1;
                                            }

                                            final int ii = i;
                                            final int jj = j;

                                            Log.d(DEBUG_TAG, currentUser.getEmail().substring(0, currentUser.getEmail().length()-4));

                                            /* J > 5, implies a Blue Piece */
                                            if (j > 5) {
                                                myRef = database.getReference("games/" + newVal + "/bluePlayer/" + i + j);
                                                myRef.setValue("" + i + j);

                                                myRef.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        String value3 = dataSnapshot.getValue(String.class);

                                                        if (value3.equals("" + ii + jj))
                                                        {
                                                            // do nothing
                                                            // first attachment
                                                        }
                                                        else
                                                        {
                                                            // do the thing that updates the board
                                                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                                            FirebaseUser currentUser = mAuth.getCurrentUser();

                                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                            DatabaseReference myRef = database.getReference("users/" + currentUser.getEmail().substring(0, currentUser.getEmail().length()-4) + "/updateBoard");
                                                            myRef.setValue("true");

                                                            myRef = database.getReference("users/" + value.substring(0, value.length()-4) + "/updateBoard");
                                                            myRef.setValue("true");
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                                /* J > 5, implies a Red Piece */
                                            } else if (j < 4) {
                                                myRef = database.getReference("games/" + newVal + "/redPlayer/" + i + j);
                                                myRef.setValue("" + i + j);

                                                myRef.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        String value4 = dataSnapshot.getValue(String.class);

                                                        if (value4.equals("" + ii + jj))
                                                        {
                                                            //Log.d(DEBUG_TAG, "We're in the if ???????????????????");
                                                        }
                                                        else
                                                        {
                                                            // do the thing that updates the board
                                                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                                            FirebaseUser currentUser = mAuth.getCurrentUser();

                                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                            DatabaseReference myRef = database.getReference("users/" + currentUser.getEmail().substring(0, currentUser.getEmail().length()-4) + "/updateBoard");
                                                            myRef.setValue("true");

                                                            myRef = database.getReference("users/" + value.substring(0, value.length()-4) + "/updateBoard");
                                                            myRef.setValue("true");

                                                            //Log.d(DEBUG_TAG, "Red: users/" + currentUser.getEmail().substring(0, currentUser.getEmail().length()-4) + "/updateBoard");
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }
                                    }

                                    /* TIME FOR CHECKERS BOIZZ */
                                    Intent intent = new Intent(getApplicationContext(), CheckersActivity.class);
                                    getApplicationContext().startActivity(intent);
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value
                                    Log.w(DEBUG_TAG, "Failed to read value.", error.toException());
                                }
                            });
                        }
                    });

                    challengedWindow.getContentView().findViewById( R.id.declineChallenge ).setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            challengedWindow.dismiss();

                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("users/" + currentUser.getEmail().substring(0, currentUser.getEmail().length()-4) + "/challengedBy");
                            myRef.setValue("");

                            myRef = database.getReference("users/" + value.substring(0, value.length()-4) + "/challenging");
                            myRef.setValue("--");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(DEBUG_TAG, "Failed to read value.", error.toException());
            }
        });

        /* Logout Implementation At Very Bottom */
        logoutButton.setOnClickListener( new HomeActivity.LogoutButtonClickListener() );

        challengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = friendEmail.getText().toString();

                try {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("users/" + email.substring(0, email.length() - 4) + "/updateBoard");

                    /* Creation Event for UpdateBoard */
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                // The child does exist

                                final boolean[] busy = new boolean[1];

                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                FirebaseUser currentUser = mAuth.getCurrentUser();

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("users/" + email.substring(0, email.length() - 4) + "/playingAgainst");
                                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        busy[0] = !(snapshot.getValue().toString().equals(""));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                boolean challengedSelf = (email.equals(currentUser.getEmail()));

                                /* Successful Challenge */
                                if (!busy[0] && !challengedSelf) {
                                    myRef = database.getReference("users/" + email.substring(0, email.length() - 4) + "/challengedBy");
                                    myRef.setValue(currentUser.getEmail());

                                    myRef = database.getReference("users/" + currentUser.getEmail().substring(0, currentUser.getEmail().length() - 4) + "/challenging");
                                    myRef.setValue(email);

                                    challengingText.setText("Challenging " + email + "...");
                                    challengingText.setTextColor(getResources().getColor(R.color.colorPrimaryLight));

                                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                                        challengingWindow.showAtLocation(findViewById(R.id.landHome), Gravity.CENTER, 0, 0);
                                    else
                                        challengingWindow.showAtLocation(findViewById(R.id.homeLayout), Gravity.CENTER, 0, 0);


                                    challengingWindow.getContentView().findViewById(R.id.cancelChallenge).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            challengingWindow.dismiss();

                                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                            FirebaseUser currentUser = mAuth.getCurrentUser();

                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            DatabaseReference myRef = database.getReference("users/" + currentUser.getEmail().substring(0, currentUser.getEmail().length() - 4) + "/challenging");
                                            myRef.setValue("");

                                            myRef = database.getReference("users/" + email.substring(0, email.length() - 4) + "/challengedBy");
                                            myRef.setValue("-");
                                        }
                                    });
                                }
                                /* Failed Challenge 1 */
                                else if (busy[0]) {
                                    Toast.makeText(getApplicationContext(),
                                            "User is currently in another game!",
                                            Toast.LENGTH_LONG).show();
                                }
                                /* Failed Challenge 2 */
                                else {
                                    Toast.makeText(getApplicationContext(),
                                            "That wasn't very 'multiplayer' of you.",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                            /* Failed Challenge 3 */
                            else {
                                Toast.makeText(getApplicationContext(),
                                        "User is logged out or does not exist.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),
                            "Improper format!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    /**
     * ------- LogoutButtonClickListener ----------------------------------------------------------------------------------
     */
    private class LogoutButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick( View v ) {
            final View view = v;

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("users/" + currentUser.getEmail().substring(0, currentUser.getEmail().length()-4) + "/updateBoard");
            myRef.setValue(null);

            AuthUI.getInstance()
                    .signOut(view.getContext())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(view.getContext(), MainActivity.class);
                            view.getContext().startActivity(intent);
                        }
                    });
        }
    }
}
