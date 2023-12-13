package edu.vt.smarttrail.surveyactivity

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.vt.smarttrail.R
import edu.vt.smarttrail.db.Question

/**
 * Renders survey questions in array
 */
class QuestionListAdapter(private val extContext: Context) : ListAdapter<Question, QuestionListAdapter.QuestionViewHolder>(QUESTIONS_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerviewquestionitem, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionItemView: TextView = itemView.findViewById(R.id.tv_qprompt)
        private val r1: RadioButton = itemView.findViewById<RadioButton>(R.id.radio0)
        private val r2: RadioButton = itemView.findViewById<RadioButton>(R.id.radio1)
        private val r3: RadioButton = itemView.findViewById<RadioButton>(R.id.radio2)
        private val r4: RadioButton = itemView.findViewById<RadioButton>(R.id.radio3)
        private val r5: RadioButton = itemView.findViewById<RadioButton>(R.id.radio4)
//        fun create(parent: ViewGroup): QuestionListAdapter.QuestionViewHolder {
//            val view: View = LayoutInflater.from(parent.context)
//                .inflate(R.layout.recyclerviewquestionitem, parent, false)
//            return QuestionViewHolder(view)
//        }
        fun bind(questionObject: Question) {
            questionItemView.text = questionObject.qbody
            r1.setOnClickListener{ onRadioButtonClicked(it, questionObject) }
            r2.setOnClickListener{ onRadioButtonClicked(it, questionObject) }
            r3.setOnClickListener{ onRadioButtonClicked(it, questionObject) }
            r4.setOnClickListener{ onRadioButtonClicked(it, questionObject) }
            r5.setOnClickListener{ onRadioButtonClicked(it, questionObject) }
            // When creating question answers, make them all unchecked to start
            r1.isChecked = false
            r2.isChecked = false
            r3.isChecked = false
            r4.isChecked = false
            r5.isChecked = false
            setQuestionOptions(questionObject.qtype)
        }
        private fun onRadioButtonClicked(view: View, qObject: Question) {
            if (view is RadioButton) {
                // Is the button now checked?
                val checked = view.isChecked
                // Check which radio button was clicked
//                Log.d(TAG, "Radio button selected")
                var strResponse: String = ""
                when (view.getId()) {
                    R.id.radio0 ->
                        if (checked) {
                            strResponse = r1.text as String
                        }
                    R.id.radio1 ->
                        if (checked) {
                            strResponse = r2.text as String
                        }
                    R.id.radio2 ->
                        if (checked) {
                            strResponse = r3.text as String
                        }
                    R.id.radio3 ->
                        if (checked) {
                            strResponse = r4.text as String
                        }
                    R.id.radio4 ->
                        if (checked) {
                            strResponse = r5.text as String
                        }
                }
                clickListener?.onItemClick(view, qObject.id, strResponse)
            }
        }
        fun setQuestionOptions(likert_type: Int) {
            when (likert_type) {
                4 -> {
                    r1.text = "Strongly agree"
                    r2.text = "Agree"
                    r3.text = "Neutral"
                    r4.text = "Disagree"
                    r5.text = "Strongly disagree"
                } 2 -> { // likert type 2 (Likelihood)
                    r1.text = "Very Likely"
                    r2.text = "Likely"
                    r3.text = "Not sure"
                    r4.text = "Unlikely"
                    r5.text = "Very Unlikely"
                } 1 -> { // likert type 1 (Experience)
                    r1.text = "Very Experienced"
                    r2.text = "Experienced"
                    r3.text = "Neither experienced nor inexperienced"
                    r4.text = "Inexperienced"
                    r5.text = "Very Inexperienced"
                } 3 -> { // likert type 3 (Desirability)
                    r1.text = "Very Desirable"
                    r2.text = "Desirable"
                    r3.text = "Not sure"
                    r4.text = "Undesirable"
                    r5.text = "Very Undesirable"
                } 6 -> {
                    r1.text = "Never"
                    r2.text = "Rarely"
                    r3.text = "Occasionally"
                    r4.text = "Very Frequently"
                    r5.text = "Always"
                }
                5 -> { // benefits
                    r1.text = "No benefits at all"
                    r2.text = "Few benefits"
                    r3.text = "Moderate benefits"
                    r4.text = "Significant benefits"
                    r5.text = "Great benefits"
                }
                7 -> { // risk
                    r1.text = "Not at all risky"
                    r2.text = "A little bit risky"
                    r3.text = "Moderately risky"
                    r4.text = "Quite risky"
                    r5.text = "Extremely risky"
                }
                8 -> { // quantity
                    r1.text = "0"
                    r2.text = "1-5"
                    r3.text = "6-10"
                    r4.text = "11-20"
                    r5.text = "More than 20"
                }
                else -> {
                    Log.e(TAG, "Other question type, $likert_type")
                }
            }
        }


    }

    companion object {
        private val QUESTIONS_COMPARATOR = object : DiffUtil.ItemCallback<Question>() {
            override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean {
                return oldItem.id == newItem.id
            }
        }
        private const val TAG = "QuestionListAdapter"
        private var clickListener: ClickListener? = null
    }

    fun setOnItemClickListener(clickListener: ClickListener?) {
        QuestionListAdapter.clickListener = clickListener
    }
    interface ClickListener {
        fun onItemClick(v: View?, qid: Int, response: String)
    }
}
