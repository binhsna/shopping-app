package vn.binhnc.manager.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import vn.binhnc.manager.fragment.CategoryFragment;
import vn.binhnc.manager.model.TheLoai;


public class ViewPagerCategoryAdapter extends FragmentStateAdapter {

    private final List<TheLoai> categories;

    public ViewPagerCategoryAdapter(@NonNull FragmentActivity fragmentActivity, List<TheLoai> categories) {
        super(fragmentActivity);
        this.categories = categories;
    }

    @NonNull
    @Override
    public Fragment createFragment(int i) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putInt("EXTRA_DATA_ID_CATEGORY", categories.get(i).getId());
        args.putString("EXTRA_DATA_NAME", categories.get(i).getTentheloai());
        args.putString("EXTRA_DATA_DESC", categories.get(i).getMota());
        args.putString("EXTRA_DATA_IMAGE", categories.get(i).getHinhanh());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
