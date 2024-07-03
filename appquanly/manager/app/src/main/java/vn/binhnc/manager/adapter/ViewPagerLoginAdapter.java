package vn.binhnc.manager.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.binhnc.manager.fragment.LoginEmailTabFragment;
import vn.binhnc.manager.fragment.LoginPhoneTabFragment;

public class ViewPagerLoginAdapter extends FragmentStateAdapter {
    public ViewPagerLoginAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new LoginEmailTabFragment();
        }
        return new LoginPhoneTabFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
