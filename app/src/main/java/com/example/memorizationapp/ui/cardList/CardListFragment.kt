package com.example.memorizationapp.ui.cardList

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.memorizationapp.MainActivity
import com.example.memorizationapp.R
import com.example.memorizationapp.databinding.FragmentCardListBinding
import com.example.memorizationapp.ui.cardList.placeholder.PlaceholderContent

/**
 * A fragment representing a list of Items.
 */
class CardListFragment : Fragment() {

    private var _binding: FragmentCardListBinding? = null
    private val binding get() = _binding!!

    private lateinit var cardListViewModel: CardListViewModel
    private lateinit var _mActivity : MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCardListBinding.inflate(inflater, container, false)
        cardListViewModel = ViewModelProvider(requireActivity())[CardListViewModel::class.java]
        _mActivity = activity as MainActivity
        val view = binding.list

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                adapter = CardListAdapter(PlaceholderContent.ITEMS)
            }
        }

        binding.fabAddCard.setOnClickListener {
        }

        return binding.root
    }
}