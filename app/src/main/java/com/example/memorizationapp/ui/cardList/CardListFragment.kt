package com.example.memorizationapp.ui.cardList

import android.os.Bundle
import androidx.fragment.app.Fragment
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

class CardListFragment : Fragment() {

    private var _binding: FragmentCardListBinding? = null
    private val binding get() = _binding!!

    private lateinit var cardListViewModel: CardListViewModel
    private lateinit var cardViewModel: CardViewModel
    private lateinit var _mActivity : MainActivity

    private val _itemsPerBinding = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardListViewModel = ViewModelProvider(requireActivity())[CardListViewModel::class.java]
        cardViewModel = ViewModelProvider(requireActivity())[CardViewModel::class.java]
        _mActivity = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardListBinding.inflate(inflater, container, false)

        binding.fabAddCard.setOnClickListener {
            cardViewModel.setValue(0, 0, cardListViewModel.cardBundleId.value!!, null, null, null, CREATE)
            _mActivity.changeFragment(R.id.nav_card)
        }

        getCards()

        cardListViewModel.update.observe(viewLifecycleOwner, Observer { item ->
            (binding.rvCardList.adapter as CardListAdapter).updateItem(item)
            cardListViewModel.setValue(
                cardListViewModel.cardBundleId.value!!,
                (binding.rvCardList.adapter as CardListAdapter).getCards())
        })

        addItemsByScrollEvent()

        return binding.root
    }

    private fun addItemsByScrollEvent(){
        val recyclerView = binding.rvCardList
        val layoutManager = LinearLayoutManager(_mActivity)

        recyclerView.layoutManager = layoutManager

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    val dbHelper = DBHelper(_mActivity)
                    val items = dbHelper.readCard(cardListViewModel.cardBundleId.value!!,
                        binding.rvCardList.adapter!!.itemCount,
                        _itemsPerBinding)
                    dbHelper.close()
                    if (items.isNotEmpty()) {
                        (binding.rvCardList.adapter as CardListAdapter).addItems(items)
                        cardListViewModel.setValue(
                            cardListViewModel.cardBundleId.value!!,
                            (binding.rvCardList.adapter as CardListAdapter).getCards())
                    }

                }
            }
        })
    }


    private fun getCards() {
        var items: MutableList<CardItem>
        if (cardListViewModel.cardList.value!!.isEmpty()) {
            //val startRow = (cardViewModel.cardListRow.value!! / _itemsPerBinding) * _itemsPerBinding
            val dbHelper = DBHelper(_mActivity)
            items = dbHelper.readCard(cardListViewModel.cardBundleId.value!!, 0, _itemsPerBinding)
            dbHelper.close()
            cardListViewModel.setValue( cardListViewModel.cardBundleId.value!!, items)
        } else {
            items = cardListViewModel.cardList.value!!
        }
        binding.rvCardList.adapter = CardListAdapter(_mActivity, items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CREATE = "create"
        private const val UPDATE = "update"
    }
}