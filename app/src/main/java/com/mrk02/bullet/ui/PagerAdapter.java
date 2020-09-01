package com.mrk02.bullet.ui;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.util.Supplier;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class PagerAdapter extends FragmentPagerAdapter {

  private final List<FragmentProvider> providers = new ArrayList<>();

  private final Context context;

  public PagerAdapter(Context context, FragmentManager fm) {
    super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    this.context = context;
  }

  /**
   * @param title    The title of the page.
   * @param provider A function that creates a new instance of the fragment.
   * @return This pager adapter instance.
   */
  public PagerAdapter page(int title, Supplier<Fragment> provider) {
    providers.add(new FragmentProvider(title, provider));
    return this;
  }

  @Override
  public Fragment getItem(int position) {
    return providers.get(position).provider.get();
  }

  @Nullable
  @Override
  public CharSequence getPageTitle(int position) {
    return context.getResources().getString(providers.get(position).title);
  }

  @Override
  public int getCount() {
    return providers.size();
  }

  /**
   *
   */
  private final class FragmentProvider {

    private final int title;
    private final Supplier<Fragment> provider;

    /**
     * @param title
     * @param provider
     */
    private FragmentProvider(int title, Supplier<Fragment> provider) {
      this.title = title;
      this.provider = provider;
    }

  }
}