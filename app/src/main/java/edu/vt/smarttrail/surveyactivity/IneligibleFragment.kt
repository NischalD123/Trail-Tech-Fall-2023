package edu.vt.smarttrail.surveyactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import edu.vt.smarttrail.R

/**
 * A simple [Fragment] subclass to notify the user that they are ineligible for the study based on
 * their screening responses, and allowing them to exit the app gracefully.
 */
class IneligibleFragment : Fragment() {
    companion object {
        val TAG = "IneligibleFragment"
    }
    private lateinit var exit_btn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_ineligible, container, false)
        exit_btn = v.findViewById<Button>(R.id.exit_btn)
        exit_btn.setOnClickListener {
            // Close application
            // TODO: finish issue of returning to main activity
            requireActivity().finishAndRemoveTask()
        }
        return v
    }
}