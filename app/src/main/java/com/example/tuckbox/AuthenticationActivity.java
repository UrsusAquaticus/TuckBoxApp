package com.example.tuckbox;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.tuckbox.databinding.ActivityAuthenticationBinding;
import com.example.tuckbox.datamodel.TuckBoxViewModel;
import com.example.tuckbox.fragments.LoginFragment;
import com.example.tuckbox.fragments.SignupFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AuthenticationActivity extends AppCompatActivity {

    //https://www.androidhive.info/2020/01/viewpager2-pager-transformations-intro-slider-pager-animations-pager-transformations/
    //https://developer.android.com/topic/libraries/view-binding#java

    static TuckBoxViewModel viewModel;

    ActivityAuthenticationBinding binding;
    LoginFragment loginFragment;
    SignupFragment signupFragment;

    boolean isOpened = false;

    //Used for generating tabs
    private final String[] titles = new String[]{"Login", "Signup"};

    public static TuckBoxViewModel getViewModel() {
        return viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //set viewmodel
        viewModel = TuckBoxViewModel.getViewModel(getApplication());
        //Set up content
        connectTabsToViewpager();
        setupReactingToScreenKeyboard();
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectLoading();
    }

    private void connectLoading() {
        int colorOn = R.color.color_on_primary;
        int colorOff = R.color.material_on_primary_disabled;
        TuckBoxViewModel.isLoading.observe(this, isCurrentlyLoading -> {
            TabLayout tabs = binding.authTabs;
            int textColor = ContextCompat.getColor(this, (isCurrentlyLoading ? colorOff : colorOn));
            tabs.setTabTextColors(textColor, textColor);
            int indicatorColor = isCurrentlyLoading ?
                    1 :
                    textColor;
            tabs.setSelectedTabIndicatorColor(indicatorColor);
            //set each tab
            tabs.setEnabled(!isCurrentlyLoading);
            for (int i = 0; i < tabs.getTabCount(); i++) {
                TabLayout.Tab tab = tabs.getTabAt(i);
                if (tab != null) tab.view.setEnabled(!isCurrentlyLoading);
            }
            binding.authPager.setUserInputEnabled(!isCurrentlyLoading);
            binding.authProgress.setVisibility(
                    isCurrentlyLoading ? View.VISIBLE : View.INVISIBLE
            );
        });
    }

    public void setupReactingToScreenKeyboard() {
        //https://stackoverflow.com/questions/4312319/how-to-capture-the-virtual-keyboard-show-hide-event-in-android
        View rootView = binding.getRoot();

        LayoutTransition transition = new LayoutTransition();
        transition.setDuration(300);
        binding.authRootLayout.setLayoutTransition(transition);

        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int viewportHeight = rootView.getRootView().getHeight();
            int activityHeight = rootView.getHeight();
            //Check if the height of the activity is less than 2/3s of the screen
            //Most likely because of keyboard

            ImageView authLogo = binding.authLogo;
            float logoHeight = authLogo.getHeight();

            //MaterialButton loginButton = loginFragment.getBinding().loginSubmitButton;

            //Check if activity is less than a 3rd the screen size
            if (activityHeight < viewportHeight / 1.5) {
                if (!isOpened) {
                    //Keyboard open
                    //loginButton.setVisibility(View.GONE);
                    authLogo.setTranslationY(0);
                    authLogo.animate()
                            .alpha(0f)
                            .setDuration(300)
                            .translationY(-logoHeight)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    authLogo.setVisibility(View.GONE);
                                    authLogo.clearAnimation();
                                    //loginButton.setVisibility(View.VISIBLE);
                                }
                            });
                }
                isOpened = true;
            } else if (isOpened) {
                //Keyboard Closed
                //Logo
                authLogo.setTranslationY(-logoHeight);
                authLogo.setVisibility(View.VISIBLE);
                authLogo.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .translationY(0)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                authLogo.clearAnimation();
                            }
                        });
                //Finally
                isOpened = false;
            }
        });
    }

    private void connectTabsToViewpager() {
        binding.authPager.setAdapter(new AuthenticationFragmentAdapter(this));
        //Preload pages
        binding.authPager.setOffscreenPageLimit(2);
        new TabLayoutMediator(
                binding.authTabs,
                binding.authPager,
                (tab, position) -> tab.setText(titles[position])
        ).attach();
    }

    private class AuthenticationFragmentAdapter extends FragmentStateAdapter {

        public AuthenticationFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    LoginFragment lf = new LoginFragment();
                    loginFragment = lf;
                    return lf; //Login
                case 1:
                    SignupFragment rf = new SignupFragment();
                    signupFragment = rf;
                    return rf; //register
                default:
                    Log.d("AUTH", "AuthenticationFragmentAdapter position defaulted");
                    finish();
                    return new Fragment(); //Error
            }
        }

        @Override
        public int getItemCount() {
            return titles.length;
        }
    }
}


