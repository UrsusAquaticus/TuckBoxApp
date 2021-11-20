package com.example.tuckbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.tuckbox.databinding.ActivityMainBinding;
import com.example.tuckbox.datamodel.TuckBoxViewModel;
import com.example.tuckbox.fragments.AccountFragment;
import com.example.tuckbox.fragments.CartFragment;
import com.example.tuckbox.fragments.HistoryFragment;
import com.example.tuckbox.fragments.MenuFragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    //https://stackoverflow.com/questions/58128491/how-to-combine-bottomnavigationview-and-viewpager

    public static final String IS_FROM_ORDER = "IS_FROM_ORDER";
    public static TuckBoxViewModel viewModel;

    ActivityMainBinding binding;
    MenuFragment menuFragment;
    CartFragment cartFragment;
    HistoryFragment historyFragment;
    AccountFragment accountFragment;

    boolean isOpened = false;

    public static TuckBoxViewModel getViewModel() {
        return viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //set viewmodel
        viewModel = TuckBoxViewModel.getViewModel(getApplication());
        //Setup content
        connectNavbarToViewpager();
        setupReactingToScreenKeyboard();
        binding.mainToolbar.setOnMenuItemClickListener((Toolbar.OnMenuItemClickListener) item -> {
                    Log.d("OPTIONS", "Going back");
                    if (item.getItemId() == R.id.appbarSignout) {
                        viewModel.signOut(MainActivity.this);
                    }
                    return true;
                }
        );
    }


    @Override
    protected void onStart() {
        super.onStart();
        connectLoading();
        if (getIntent().getBooleanExtra(IS_FROM_ORDER, false)) {
            //2 is history
            binding.mainPager.setCurrentItem(2, false);
            getIntent().putExtra(IS_FROM_ORDER, false);
        }
    }

    private void connectLoading() {
        TuckBoxViewModel.isLoading.observe(this, isCurrentlyLoading -> {
            binding.mainProgress.setVisibility(
                    isCurrentlyLoading ? View.VISIBLE : View.INVISIBLE
            );
            binding.mainNavigation.setEnabled(!isCurrentlyLoading);
        });
    }


    public void setupReactingToScreenKeyboard() {
        //https://stackoverflow.com/questions/4312319/how-to-capture-the-virtual-keyboard-show-hide-event-in-android
        View rootView = binding.getRoot();

        LayoutTransition transition = new LayoutTransition();

        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int viewportHeight = rootView.getRootView().getHeight();
            int activityHeight = rootView.getHeight();
            //Check if the height of the activity is less than 2/3s of the screen
            //Most likely because of keyboard

            ConstraintLayout layout = binding.mainToolbarLayout;
            Toolbar toolbar = binding.mainToolbar;
            float logoHeight = layout.getHeight();
            BottomNavigationView navbar = binding.mainNavigation;
            MaterialButton saveButton = accountFragment.getBinding().accountSubmitButton;
            MaterialButton deleteButton = accountFragment.getBinding().accountDeleteButton;
            MaterialButton addressButton = accountFragment.getBinding().accountAddressButton;

            //Check if activity is less than a 3rd the screen size
            if (activityHeight < viewportHeight / 1.5) {
                if (!isOpened) {
                    //Keyboard open
                    //Instant
                    transition.setDuration(0);
                    binding.mainRootLayout.setLayoutTransition(transition);
                    saveButton.setVisibility(View.GONE);
                    deleteButton.setVisibility(View.GONE);
                    addressButton.setVisibility(View.GONE);
                    navbar.setVisibility(View.GONE);

                    //delay
                    transition.setDuration(300);
                    binding.mainRootLayout.setLayoutTransition(transition);
                    layout.animate()
                            .alpha(0f)
                            .setDuration(300)
                            .translationY(-logoHeight)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    layout.setVisibility(View.GONE);
                                    layout.clearAnimation();
                                    saveButton.setVisibility(View.VISIBLE);
                                }
                            });
                }
                isOpened = true;
            } else if (isOpened) {
                //Keyboard Closed
                transition.setDuration(300);
                binding.mainRootLayout.setLayoutTransition(transition);

                layout.setTranslationY(0);
                layout.setVisibility(View.VISIBLE);

                saveButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
                addressButton.setVisibility(View.VISIBLE);
                navbar.setVisibility(View.VISIBLE);
                //Finally
                isOpened = false;
            }
        });
    }

    private void connectNavbarToViewpager() { //Set up content
        binding.mainPager.setAdapter(new MainFragmentAdapter(this));
        //preload pages
        binding.mainPager.setOffscreenPageLimit(4);
        binding.mainPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        binding.mainNavigation.getMenu().findItem(R.id.bottomNavMenu).setChecked(true);
                        break;
                    case 1:
                        binding.mainNavigation.getMenu().findItem(R.id.bottomNavCart).setChecked(true);
                        break;
                    case 2:
                        binding.mainNavigation.getMenu().findItem(R.id.bottomNavHistory).setChecked(true);
                        break;
                    case 3:
                        binding.mainNavigation.getMenu().findItem(R.id.bottomNavAccount).setChecked(true);
                        break;
                }
            }
        });
        binding.mainNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottomNavMenu:
                        binding.mainPager.setCurrentItem(0, true);
                        break;
                    case R.id.bottomNavCart:
                        binding.mainPager.setCurrentItem(1, true);
                        break;
                    case R.id.bottomNavHistory:
                        binding.mainPager.setCurrentItem(2, true);
                        break;
                    case R.id.bottomNavAccount:
                        binding.mainPager.setCurrentItem(3, true);
                        break;
                }
                return true;
            }
        });
    }

    public void setCartBadge(int count) {
        BadgeDrawable badge = binding.mainNavigation.getOrCreateBadge(R.id.bottomNavCart);
        badge.setVisible(count > 0);
        badge.setNumber(count);
    }

    private class MainFragmentAdapter extends FragmentStateAdapter {

        public MainFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    MenuFragment mf = new MenuFragment();
                    menuFragment = mf;
                    return mf; //menu
                case 1:
                    CartFragment.CartBadgeSetter setter = MainActivity.this::setCartBadge;
                    CartFragment cf = new CartFragment(setter);
                    cartFragment = cf;
                    return cf; //Cart
                case 2:
                    HistoryFragment hf = new HistoryFragment();
                    historyFragment = hf;
                    return hf; //Order History
                case 3:
                    AccountFragment af = new AccountFragment();
                    accountFragment = af;
                    return af; //Account page

                default:
                    Log.d("MAIN", "MainFragmentAdapter position defaulted");
                    finish();
                    return new Fragment(); //Error
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
}
