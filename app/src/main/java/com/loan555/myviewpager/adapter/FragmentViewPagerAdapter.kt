package com.loan555.myviewpager.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.loan555.myviewpager.fragment.DetailFragment
import com.loan555.myviewpager.fragment.HomeFragment
import com.loan555.myviewpager.fragment.ToDoListFragment

class FragmentViewPagerAdapter(fm: FragmentManager, var behavior: Int) :
    FragmentStatePagerAdapter(fm, behavior) {
    override fun getCount(): Int {
        return behavior
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            1 -> ToDoListFragment()
            2 -> {
                DetailFragment()
            }
            else -> HomeFragment()
        }
    }
}