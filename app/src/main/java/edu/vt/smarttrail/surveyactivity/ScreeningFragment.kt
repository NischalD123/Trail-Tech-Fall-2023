package edu.vt.smarttrail.surveyactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController

import edu.vt.smarttrail.R
import edu.vt.smarttrail.databinding.FragmentScreeningBinding

/**
 * A simple [Fragment] subclass for displaying survey screening questions to determine participant
 * eligibility before continuing on to the other survey sections.
 */
class ScreeningFragment : Fragment() {
    private val screeningSelections = IntArray(4) { i -> -1 }
    private var _binding: FragmentScreeningBinding? = null
    // This property is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!
    companion object {
        const val TAG = "ScreeningFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()
        _binding = FragmentScreeningBinding.inflate(inflater, container, false)
        val v = binding.root
//        val v = inflater.inflate(R.layout.fragment_screening, container, false) // inflate layout
        binding.confirmBtn.setOnClickListener{ confirm_selections() }
        binding.yesAge.setOnClickListener{ it -> onRadioButtonClicked(it) }
        binding.yesParticipate.setOnClickListener{ it -> onRadioButtonClicked(it) }
        binding.yesLongDist.setOnClickListener{ it -> onRadioButtonClicked(it) }
        binding.yesThruHike.setOnClickListener{ it -> onRadioButtonClicked(it) }
        binding.noAge.setOnClickListener{ it -> onRadioButtonClicked(it) }
        binding.noParticipate.setOnClickListener{ it -> onRadioButtonClicked(it) }
        binding.noLongDist.setOnClickListener{ it -> onRadioButtonClicked(it) }
        binding.noThruHike.setOnClickListener{ it -> onRadioButtonClicked(it) }
        binding.notSureThruHike.setOnClickListener{ it -> onRadioButtonClicked(it) }
        binding.ivBack.setOnClickListener {
            activity?.finish()
        }
        return v
    }

    fun confirm_selections() {
        var passedScreening: Boolean = true
        for(i in 0 until screeningSelections.size) {
            if(screeningSelections[i] == -1) { // Some option not selected
                Toast.makeText(context, "Please answer every question", Toast.LENGTH_SHORT).show()
                binding.confirmBtn.startAnimation(AnimationUtils.loadAnimation(context,
                    R.anim.shake
                )) // shake button
                return
            } else if (screeningSelections[i] == 0) {
                // answered "No" to one of the questions
                passedScreening = false
                break
            }
        }
        if(passedScreening) {
            // Go to survey
            findNavController().navigate(R.id.action_screeningFragment_to_questionFragment, Bundle().apply {
                // pass compensation email forward to be saved with survey response
                putString("COMP_EMAIL", binding.emailEntry.text.toString().toLowerCase())
            })
        } else {
            // Failed screening survey
            findNavController().navigate(R.id.action_screeningFragment_to_ineligibleFragment)
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.yes_participate ->
                    if (checked) {
                        screeningSelections[0] = 1
                    }
                R.id.no_participate ->
                    if (checked) {
                        screeningSelections[0] = 0
                        showToast()
                    }
                R.id.yes_age ->
                    if (checked) {
                        screeningSelections[1] = 1
                    }
                R.id.no_age ->
                    if (checked) {
                        screeningSelections[1] = 0
                        showToast()
                    }
                R.id.yes_long_dist ->
                    if (checked) {
                        screeningSelections[2] = 1
                    }
                R.id.no_long_dist ->
                    if (checked) {
                        screeningSelections[2] = 0
                        showToast()
                    }
                R.id.yes_thru_hike ->
                    if (checked) {
                        screeningSelections[3] = 1
                    }
                R.id.no_thru_hike ->
                    if (checked) {
                        screeningSelections[3] = 0
                        showToast()
                    }
                R.id.not_sure_thru_hike ->
                    if (checked) {
                        screeningSelections[3] = 2
                    }
            }
        }
    }

    private fun showToast(){
        Toast.makeText(requireContext(), "You are not eligible for survey.", Toast.LENGTH_LONG).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}