package com.example.where2surf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.where2surf.model.Spot;
import com.example.where2surf.model.Report;
import com.example.where2surf.model.UserModel;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements ReportsListFragment.Delegate, SpotsListFragment.Delegate {
    NavController navController;
    NavigationView navigationView;
    DrawerLayout drawer;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);

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


        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        drawer.addDrawerListener(toggle);
        navController = Navigation.findNavController(this, R.id.main_nav_host);
        NavigationUI.setupWithNavController(navigationView, navController);
        setOnNavControllerDestinationChanged();

    }

    private void setOnNavControllerDestinationChanged() {
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(destination.getLabel());
                    if (destination.getId() != navController.getGraph().getStartDestination()) {
                        actionBar.setDisplayHomeAsUpEnabled(true);
                        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onBackPressed();
                            }
                        });
                    } else {
                        actionBar.setDisplayHomeAsUpEnabled(false);
                        toggle.syncState();
                        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                drawer.openDrawer(GravityCompat.START);
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
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
    public void OnItemSelected(Spot spot) {
        navController.navigate(SpotsListFragmentDirections.actionSpotsListFragmentToSpotReportListFragment(spot));
    }

    @Override
    public void OnItemSelected(Report report) {
        navController.navigate(SpotReportsListFragmentDirections.actionGlobalReportDetailsFragment(report));
    }


}
