package se.liu.ida.josfa969.tddd80;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import se.liu.ida.josfa969.tddd80.fragments.FindFragment;
import se.liu.ida.josfa969.tddd80.fragments.PostIdeaFragment;
import se.liu.ida.josfa969.tddd80.fragments.ProfileFragment;
import se.liu.ida.josfa969.tddd80.fragments.ProfileSettingsFragment;

/**
 * Created by Josef on 17/07/14.
 *
 * A FragmentPagerAdapter which returns the correct fragment depending
 * on which screen is currently active in the ProfileActivity
 */
public class ProfilePagerAdapter extends FragmentPagerAdapter {
    private final int PAGE_COUNT = 4;
    private String[] nameList = {"Post", "Recent", "Find", "Settings"};
    private Fragment[] fragmentList = {new PostIdeaFragment(), new ProfileFragment(), new FindFragment(), new ProfileSettingsFragment()};

    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public ProfilePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList[i];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return nameList[position];
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
