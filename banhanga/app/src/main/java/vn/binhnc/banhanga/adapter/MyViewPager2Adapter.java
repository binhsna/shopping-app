package vn.binhnc.banhanga.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.binhnc.banhanga.fragment.CartFragment;
import vn.binhnc.banhanga.fragment.FavoriteFragment;
import vn.binhnc.banhanga.fragment.OrderFragment;
import vn.binhnc.banhanga.fragment.HomeFragment;
import vn.binhnc.banhanga.fragment.MyProfileFragment;

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