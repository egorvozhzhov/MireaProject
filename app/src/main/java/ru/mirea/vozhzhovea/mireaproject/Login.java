package ru.mirea.vozhzhovea.mireaproject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import ru.mirea.vozhzhovea.mireaproject.databinding.ActivityLoginBinding;



public class Login extends AppCompatActivity {
    private static final String TAG = Login.class.getSimpleName();
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private LocationManager locationManager;
    private String currentUserEmail;
    public double Latitude;
    public double Longitude;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        getLocation();


        binding.emailCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passhashed = SHA256HashAlgorithm.hash(binding.fieldPassword.getText().toString());
                Log.d("Hashed Passsword", passhashed);
                createAccount(binding.fieldEmail.getText().toString(), passhashed);

            }
        });

        binding.signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();

            }
        });

        binding.emailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hashed = SHA256HashAlgorithm.hash("2131212");
                Log.d("Input: " , "2131212");
                Log.d("Hashed: " , hashed);

                String passhashed = SHA256HashAlgorithm.hash(binding.fieldPassword.getText().toString());
                Log.d("Hashed Passsword", passhashed);
                signIn(binding.fieldEmail.getText().toString(), passhashed);

            }
        });

        binding.verButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailVerification();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                updateUI(currentUser);
            }
        });

        binding.goToAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            //binding.status.setText(getString(R.string.emailpassword_status_fmt,
                    //user.getEmail(), user.isEmailVerified()));
            binding.status.setText(getDeviceID());
            binding.emailPasswordButtons.setVisibility(View.GONE);
            binding.emailPasswordFields.setVisibility(View.GONE);
            binding.signOutButton.setVisibility(View.VISIBLE);
            binding.verButton.setVisibility(View.VISIBLE);
            binding.verButton.setEnabled(!user.isEmailVerified());
            binding.signOutButton.setVisibility(View.VISIBLE);
            binding.goToAppButton.setVisibility(View.VISIBLE);
        }
        else {
            //binding.status.setText(R.string.signed_out);
            binding.status.setText(getDeviceID());
            binding.emailPasswordButtons.setVisibility(View.VISIBLE);
            binding.emailPasswordFields.setVisibility(View.VISIBLE);
            binding.signOutButton.setVisibility(View.GONE);
            binding.verButton.setVisibility(View.GONE);
            binding.goToAppButton.setVisibility(View.GONE);
        } }
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            Latitude = location.getLatitude();
            Longitude = location.getLongitude();

            locationManager.removeUpdates(locationListener);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };
    private void FileWrite(){
        File file = new File(getFilesDir(), "user_location.txt");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("User: " + currentUserEmail + "\n");
            writer.write("Latitude: " + Latitude + "\n");
            writer.write("Longitude: " + Longitude + "\n");
            writer.close();
            Log.d("!!!!!!!!", String.valueOf(Latitude));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            currentUserEmail = user.getEmail(); // Сохраняем email зарегистрированного пользователя
                            updateUI(user);

                            getLocation();
                            DBHelper db = new DBHelper(getApplicationContext());
                            db.addLocationData(user.getEmail(), Latitude, Longitude);
                            Log.d("Current lat!!", String.valueOf(Latitude));
                            Log.d("Current long", String.valueOf(Longitude));

                        } else {
                            Log.w(TAG, "createUserWithEmail:failure");
                            Toast.makeText(Login.this, "Authentication Failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void getLocation() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
    }





    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();

                    Log.d("user,getEmail", String.valueOf(user.getEmail()));
                    getLocation();

                    if (checkLocation(user.getEmail())) {

                        updateUI(user);
                    } else {
                        Toast.makeText(Login.this, "Authentication Failed. User is not at the registered location.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                }
                else {
                    Toast.makeText(Login.this, "Authentication Failed",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }


    private boolean checkLocation(String email) {
        DBHelper db = new DBHelper(getApplicationContext());
        double[] val = db.getLocationDataByLogin(email);
        if (distanceMatches(val[0], val[1])) {
            return true;
        } else {
            return false;
        }


    }


    private boolean distanceMatches(double lat, double lng) {

        Log.d("Current lat!!", String.valueOf(Latitude));
        Log.d("Current long", String.valueOf(Longitude));
        Log.d("Registrated Lat!!", String.valueOf(lat));
        Log.d("Registrated Long", String.valueOf(lng));

        return Math.abs(lat - Latitude) < 0.001 && Math.abs(lng - Longitude) < 0.001;
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    public String getDeviceID() {
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return id;
    }

    private void sendEmailVerification() {

        final FirebaseUser user = mAuth.getCurrentUser();
        Objects.requireNonNull(user).sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) { // [START_EXCLUDE]

                        binding.verButton.setEnabled(true);
                        if (task.isSuccessful()) { Toast.makeText(Login.this,
                                "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(Login.this,
                                    "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    } });
        // [END send_email_verification]
    }

}


