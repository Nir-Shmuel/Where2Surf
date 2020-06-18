package com.example.where2surf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.where2surf.model.Spot;
import com.example.where2surf.model.Report;
import com.example.where2surf.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements SpotsListFragment.Delegate, SpotReportsListFragment.Delegate, NavigationView.OnNavigationItemSelectedListener {
    NavController navController;
    NavigationView navigationView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.signout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                signout();
                updateUI(currentUser);
                drawer.closeDrawers();
                navController.navigate(SpotsListFragmentDirections.actionGlobalSpotsListFragment());
                return true;
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navController = Navigation.findNavController(this, R.id.main_nav_host);
        NavigationUI.setupWithNavController(navigationView, navController);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                getSupportActionBar().setTitle(destination.getLabel());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void signIn(String email, String password, final UserModel.Listener<Boolean> listener) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            currentUser = firebaseAuth.getCurrentUser();
                            updateUI(currentUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        listener.onComplete(task.isSuccessful());
                        // ...
                    }
                });
    }

    public void createAccount(String email, String password, final UserModel.Listener<Boolean> listener) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            currentUser = firebaseAuth.getCurrentUser();
                            updateUI(currentUser);
                        }
                        listener.onComplete(task.isSuccessful());
                    }
                });
    }

    public void signout() {
        firebaseAuth.signOut();
        currentUser = firebaseAuth.getCurrentUser();
    }

    public void updateUI(FirebaseUser user) {
        boolean isLoggedIn = user != null;
        navigationView.getMenu().setGroupVisible(R.id.drawer_group_registered, isLoggedIn);
        navigationView.getMenu().setGroupVisible(R.id.drawer_group_unregistered, !isLoggedIn);


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return true;
    }


    @Override
    public void OnItemSelected(Spot spot) {
        navController.navigate(SpotReportsListFragmentDirections.actionGlobalSpotReportListFragment(spot));
//        concatToActionBarTitle(spot.getName());
    }

    @Override
    public void OnItemSelected(Report report) {
        navController.navigate(ReportDetailsFragmentDirections.actionGlobalReportDetailsFragment(report));
//        concatToActionBarTitle(report.getSpotName());
    }

    public void concatToActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(String.format("%s: %s", actionBar.getTitle(), title));
    }

}
