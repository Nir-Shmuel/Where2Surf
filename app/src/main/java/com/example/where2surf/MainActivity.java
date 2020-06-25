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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.where2surf.model.Spot;
import com.example.where2surf.model.Report;
import com.example.where2surf.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;

public class MainActivity extends AppCompatActivity implements ReportsListFragment.Delegate, SpotsListFragment.Delegate, NavigationView.OnNavigationItemSelectedListener {
    NavController navController;
    NavigationView navigationView;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.signout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                signout();
                updateUI();
                drawer.closeDrawers();
                navController.navigate(SpotsListFragmentDirections.actionGlobalSpotsListFragment());
                return true;
            }
        });
        navigationView.getMenu().findItem(R.id.userReportsListFragment).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                drawer.closeDrawers();
                navController.navigate(UserReportsListFragmentDirections.actionGlobalUserReportsListFragment());
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
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null)
                    getSupportActionBar().setTitle(destination.getLabel());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }

    public void signIn(String email, String password, final UserModel.Listener<Boolean> listener) {
        UserModel.instance.signIn(email, password, new UserModel.Listener<Boolean>() {
            @Override
            public void onComplete(Boolean data) {
                if (data) {
                    updateUI();
                } else {
                    Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
                listener.onComplete(data);
            }
        });
    }

    public void createAccount(String email, String password, final UserModel.Listener<Boolean> listener) {
        UserModel.instance.signUp(email, password, new UserModel.Listener<Boolean>() {
            @Override
            public void onComplete(Boolean data) {
                if (data) {
                    // Sign in success, update UI with the signed-in user's information
                    updateUI();
                }
                listener.onComplete(data);
            }
        });
    }

    public void signout() {
        UserModel.instance.signOut();
    }

    public void updateUI() {
        boolean isSignedIn = UserModel.instance.isSignedIn();
        navigationView.getMenu().setGroupVisible(R.id.drawer_group_registered, isSignedIn);
        navigationView.getMenu().setGroupVisible(R.id.drawer_group_unregistered, !isSignedIn);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//        switch (menuItem.getItemId()) {
//            case R.id.signout:
//                signout();
//                updateUI();
//                drawer.closeDrawers();
//
//                navController.navigate(SpotsListFragmentDirections.actionGlobalSpotsListFragment());
//                return true;
//
//            case R.id.userReportsListFragment:
//                drawer.setVisibility(View.INVISIBLE);
//                navController.navigate(UserReportsListFragmentDirections.actionGlobalUserReportsListFragment());
//                return true;
//        }
        return true;
    }

    @Override
    public void OnItemSelected(Spot spot) {
        navController.navigate(SpotsListFragmentDirections.actionSpotsListFragmentToSpotReportListFragment(spot));
//        concatToActionBarTitle(spot.getName());
    }

    @Override
    public void OnItemSelected(Report report) {
        navController.navigate(SpotReportsListFragmentDirections.actionGlobalReportDetailsFragment(report));
//        concatToActionBarTitle(report.getSpotName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    public void concatToActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(String.format("%s: %s", actionBar.getTitle(), title));
    }

}
