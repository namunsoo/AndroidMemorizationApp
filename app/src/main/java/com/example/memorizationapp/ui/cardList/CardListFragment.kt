package com.example.memorizationapp.ui.cardList

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.memorizationapp.MainActivity
import com.example.memorizationapp.R
import com.example.memorizationapp.common.database.DBHelper
import com.example.memorizationapp.databinding.FragmentCardListBinding
import com.example.memorizationapp.ui.card.CardViewModel

/**
 * A fragment representing a list of Items.
 */
class CardListFragment : Fragment() {

    private var _binding: FragmentCardListBinding? = null
    private val binding get() = _binding!!

    private lateinit var cardListViewModel: CardListViewModel
    private lateinit var cardViewModel: CardViewModel
    private lateinit var _mActivity : MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardListBinding.inflate(inflater, container, false)
        cardListViewModel = ViewModelProvider(requireActivity())[CardListViewModel::class.java]
        cardViewModel = ViewModelProvider(requireActivity())[CardViewModel::class.java]
        _mActivity = activity as MainActivity

        binding.fabAddCard.setOnClickListener {
            cardViewModel.setValue(cardListViewModel.cardBundleId.value!!, null, null, null, "create")
            _mActivity.changeFragment(R.id.nav_card)
        }

        cardListViewModel.cardList.observe(_mActivity, Observer {
            binding.list.adapter = CardListAdapter(getCards())
        })

        return binding.root
    }

    private fun getCards(): List<CardItem> {
        if (cardListViewModel.cardList.value!!.isEmpty()) {
            val dbHelper = DBHelper(_mActivity)
            val items = dbHelper.readCard(cardListViewModel.cardBundleId.value!!, 0, 10)
            dbHelper.close()
            cardListViewModel.setValue(cardListViewModel.cardBundleId.value!!, items.toMutableList())
            return items
        }
        return cardListViewModel.cardList.value!!.toList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}