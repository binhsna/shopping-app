package vn.binhnc.manager.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.binhnc.manager.fragment.CartFragment;
import vn.binhnc.manager.fragment.FavoriteFragment;
import vn.binhnc.manager.fragment.OrderFragment;
import vn.binhnc.manager.fragment.HomeFragment;
import vn.binhnc.manager.fragment.MyProfileFragment;

public class MyViewPager2Adapter extends FragmentStateAdapter {

    public MyViewPager2Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new CartFragment();
            case 2:
                return new FavoriteFragment();
            case 3:
                return new OrderFragment();
            case 4:
                return new MyProfileFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }

}