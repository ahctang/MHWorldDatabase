package com.gatheringhallstudios.mhworlddatabase.features.search

import android.arch.lifecycle.*
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.gatheringhallstudios.mhworlddatabase.MainActivityViewModel
import com.gatheringhallstudios.mhworlddatabase.R
import com.gatheringhallstudios.mhworlddatabase.adapters.common.BasicListDelegationAdapter
import com.gatheringhallstudios.mhworlddatabase.adapters.ItemAdapterDelegate
import com.gatheringhallstudios.mhworlddatabase.adapters.MonsterAdapterDelegate
import com.gatheringhallstudios.mhworlddatabase.common.RecyclerViewFragment
import com.gatheringhallstudios.mhworlddatabase.features.items.ItemDetailPagerFragment
import com.gatheringhallstudios.mhworlddatabase.features.monsters.MonsterDetailPagerFragment
import com.gatheringhallstudios.mhworlddatabase.util.BundleBuilder

class UniversalSearchFragment : RecyclerViewFragment() {

    // Universal Search results handle many types of data.
    // Create an adapter that handles all of them
    val adapter = BasicListDelegationAdapter<Any>(
            MonsterAdapterDelegate({
                findNavController().navigate(
                        R.id.monsterDetailDestination,
                        BundleBuilder().putInt(MonsterDetailPagerFragment.ARG_MONSTER_ID, it.id).build())
            }),
            ItemAdapterDelegate({
                findNavController().navigate(
                        R.id.itemDetailDestination,
                        BundleBuilder().putInt(ItemDetailPagerFragment.ARG_ITEM_ID, it.id).build())
            })
    )

    val activityViewModel by lazy {
        ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java)
    }

    val searchViewModel by lazy {
        ViewModelProviders.of(this).get(UniversalSearchViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setAdapter(adapter)

        // open up the search menu (if not open) if we're on this page
        // If the user hit back and returned to this page, we need to open it again
        activityViewModel.searchActive.value = true

        activityViewModel.filter.observe(this, Observer {
            searchViewModel.searchData(it)
        })

        searchViewModel.searchResults.observe(this, Observer {
            adapter.items = it
            adapter.notifyDataSetChanged()
        })
    }

    override fun onDetach() {
        super.onDetach()

        activityViewModel.searchActive.value = false
    }

    override fun onDestroyView() {
        super.onDestroyView()

        activityViewModel.searchActive.value = false
    }
}