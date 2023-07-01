package com.asmaa.yummy.ui.fragment.recipes.bottomsheet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.get
import com.asmaa.yummy.R
import com.asmaa.yummy.databinding.FragmentRecipesBinding
import com.asmaa.yummy.databinding.RecipesBottomSheetBinding
import com.asmaa.yummy.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.asmaa.yummy.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.asmaa.yummy.viewmodels.RecipesViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import java.util.Locale

class RecipesBottomSheetFragment : BottomSheetDialogFragment(){

    private lateinit var recipesViewModel: RecipesViewModel
    private var _binding: RecipesBottomSheetBinding?= null
    private val binding get() = _binding!!

    private var mealTypeChip = DEFAULT_MEAL_TYPE
    private var mealTypeChipId = 0
    private var dietTypeChip = DEFAULT_DIET_TYPE
    private var dietTypeChipId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipesViewModel = ViewModelProvider(requireActivity()).get(RecipesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = RecipesBottomSheetBinding.inflate(inflater, container, false)

        recipesViewModel.readMealAndDietType.asLiveData().observe(viewLifecycleOwner,{value ->
            mealTypeChip = value.selectedMealType
            dietTypeChip = value.selectedDietType
        })
        binding.mealTypeChipGroup.setOnCheckedChangeListener { group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId)
            val selectedMealType = chip.text.toString().toLowerCase(Locale.ROOT)
            mealTypeChip = selectedMealType
            mealTypeChipId = selectedChipId
        }

            binding.dietTypeChipGroup.setOnCheckedChangeListener{ group,selectedChipId ->
                val chip = group.findViewById<Chip>(selectedChipId)
                val selectedDietType = chip.text.toString().toLowerCase(Locale.ROOT)
                dietTypeChip = selectedDietType
                dietTypeChipId = selectedChipId
        }

        binding.applyBtn.setOnClickListener{
            recipesViewModel.saveMealAndDietType(mealTypeChip,mealTypeChipId,dietTypeChip,dietTypeChipId)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}