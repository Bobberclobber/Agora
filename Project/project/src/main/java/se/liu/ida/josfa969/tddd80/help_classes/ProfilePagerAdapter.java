package se.liu.ida.josfa969.tddd80.help_classes;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import se.liu.ida.josfa969.tddd80.fragments.FindFragment;
import se.liu.ida.josfa969.tddd80.fragments.MessagesFragment;
import se.liu.ida.josfa969.tddd80.fragments.PostIdeaFragment;
import se.liu.ida.josfa969.tddd80.fragments.IdeaFeedFragment;
import se.liu.ida.josfa969.tddd80.fragments.ProfileSettingsFragment;

/**
 * Created by Josef on 17/07/14.
 *
 * A FragmentPagerAdapter which returns the correct fragment depending
 * on which screen is currently active in the ProfileActivity
 */
public class ProfilePagerAdapter extends FragmentPagerAdapter {
    private String[] nameList = {"Post", "Idea Feed", "Messages", "Find", "Settings"};
    private Fragment[] fragmentList = {new PostIdeaFragment(), new IdeaFeedFragment(), new MessagesFragment(), new FindFragment(), new ProfileSettingsFragment()};

    public ProfilePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList[i];
    }

    @Override
    public int getCount() {
        return fragmentList.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return nameList[position];
    }
}
