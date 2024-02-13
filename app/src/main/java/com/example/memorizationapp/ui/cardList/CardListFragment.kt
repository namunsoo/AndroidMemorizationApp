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
    private var _lastItemRow = 0
    private var _firstItemRow = 0

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
                val cardListAdapter = binding.rvCardList.adapter as CardListAdapter

                // 스크롤 밑일때 추가
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    val dbHelper = DBHelper(_mActivity)
                    val items = dbHelper.readCard(cardListViewModel.cardBundleId.value!!,
                        _lastItemRow,
                        _itemsPerBinding)
                    dbHelper.close()
                    if (items.isNotEmpty()) {
                        if (cardListAdapter.getCards().count() > 20) {
                            cardListAdapter.deleteItemsFromFront(_itemsPerBinding)
                        }
                        cardListAdapter.addItems(items)
                        cardListViewModel.setValue(
                            cardListViewModel.cardBundleId.value!!,
                            cardListAdapter.getCards())
                        _lastItemRow = cardListAdapter.getCards().last().row
                        _firstItemRow = cardListAdapter.getCards().first().row
                    }
                }

                // 스크롤 위일때 추가
                if (firstVisibleItemPosition == 0) {
                    val dbHelper = DBHelper(_mActivity)
                    val items: MutableList<CardItem>
                    if (_firstItemRow < 2 ) {
                        items = mutableListOf()
                    }
                    else if (_firstItemRow - _itemsPerBinding < 0) {
                        items = dbHelper.readCard(cardListViewModel.cardBundleId.value!!,
                            0,
                            _firstItemRow)

                    } else {
                        items = dbHelper.readCard(cardListViewModel.cardBundleId.value!!,
                            _firstItemRow - _itemsPerBinding - 1,
                            _itemsPerBinding)
                    }
                    dbHelper.close()
                    if (items.isNotEmpty()) {
                        if (cardListAdapter.getCards().count() > 20) {
                            cardListAdapter.deleteItemsFromLast(_itemsPerBinding)
                        }
                        cardListAdapter.addItemsInFront(items)
                        cardListViewModel.setValue(
                            cardListViewModel.cardBundleId.value!!,
                            cardListAdapter.getCards())
                        _lastItemRow = cardListAdapter.getCards().last().row
                        _firstItemRow = cardListAdapter.getCards().first().row
                    }
                }
            }
        })
    }


    private fun getCards() {
        var items: MutableList<CardItem>
        if (cardListViewModel.cardList.value!!.isEmpty()) {
            val dbHelper = DBHelper(_mActivity)
            items = dbHelper.readCard(cardListViewModel.cardBundleId.value!!, 0, _itemsPerBinding)
            dbHelper.close()
            cardListViewModel.setValue( cardListViewModel.cardBundleId.value!!, items)
        } else {
            items = cardListViewModel.cardList.value!!
        }

        if (items.isNotEmpty()) {
            _lastItemRow = items.last().row
            _firstItemRow = items.first().row
            binding.rvCardList.adapter = CardListAdapter(_mActivity, items)
        }
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