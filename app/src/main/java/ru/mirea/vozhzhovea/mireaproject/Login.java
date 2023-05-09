package ru.mirea.vozhzhovea.mireaproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import ru.mirea.vozhzhovea.mireaproject.databinding.ActivityLoginBinding;


public class Login extends AppCompatActivity {
    private static final String TAG = Login.class.getSimpleName();
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        binding.emailCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(binding.fieldEmail.getText().toString(), binding.fieldPassword.getText().toString());

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
                signIn(binding.fieldEmail.getText().toString(), binding.fieldPassword.getText().toString());

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
            binding.status.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            binding.emailPasswordButtons.setVisibility(View.GONE);
            binding.emailPasswordFields.setVisibility(View.GONE);
            binding.signOutButton.setVisibility(View.VISIBLE);
            binding.verButton.setVisibility(View.VISIBLE);
            binding.verButton.setEnabled(!user.isEmailVerified());
            binding.signOutButton.setVisibility(View.VISIBLE);
            binding.goToAppButton.setVisibility(View.VISIBLE);
        }
        else {
            binding.status.setText(R.string.signed_out);
            binding.emailPasswordButtons.setVisibility(View.VISIBLE);
            binding.emailPasswordFields.setVisibility(View.VISIBLE);
            binding.signOutButton.setVisibility(View.GONE);
            binding.verButton.setVisibility(View.GONE);
            binding.goToAppButton.setVisibility(View.GONE);
        } }
    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure");
                            Toast.makeText(Login.this, "Authentication Failed.",Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        } }
                });

    }
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
// [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                }
                else {
// If sign in fails, display a message to the user. Log.w(TAG, "signInWithEmail:failure", task.getExcep-
                    Toast.makeText(Login.this, "Authentication Failed",
                            Toast.LENGTH_SHORT).show(); updateUI(null);
                }
                // [START_EXCLUDE]
                if (!task.isSuccessful()) {
                    binding.status.setText(R.string.auth_failed); }
                // [END_EXCLUDE]
            }
        });
        // [END sign_in_with_email]
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
// Disable button binding.verifyEmailButton).setEnabled(false);
// Send verification email
// [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        Objects.requireNonNull(user).sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) { // [START_EXCLUDE]
// Re-enable button
                        binding.verButton.setEnabled(true);
                        if (task.isSuccessful()) { Toast.makeText(Login.this,
                                "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(Login.this,
                                    "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    } });
        // [END send_email_verification]
    }

}
