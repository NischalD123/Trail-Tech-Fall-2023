package edu.vt.smarttrail.surveyactivity

import android.Manifest
import android.app.AlarmManager
import android.app.AppOpsManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import edu.vt.smarttrail.AppStatsDBHelper
import edu.vt.smarttrail.DBHelper
import edu.vt.smarttrail.QuestionsApplication
import edu.vt.smarttrail.R
import edu.vt.smarttrail.UidSingleton
import edu.vt.smarttrail.databinding.FragmentQuestionBinding
import edu.vt.smarttrail.db.*
import edu.vt.smarttrail.notifications.NotificationBroadcastReceiver
import edu.vt.smarttrail.notifications.channelID
import edu.vt.smarttrail.notifications.messageExtra
import edu.vt.smarttrail.notifications.notificationID
import edu.vt.smarttrail.notifications.titleExtra
import java.lang.Integer.min
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A [Fragment] subclass for displaying survey questions.
 * Use the [QuestionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class QuestionFragment : Fragment() {

    // User Databases
    private val firebaseCloud = FirebaseDatabase.getInstance().getReference("users")
    private val firebaseCloudSurveyHistory = FirebaseDatabase.getInstance().getReference("history")
//    TODO: some questions are asked every time, other questions are shuffled through
    private var _binding: FragmentQuestionBinding? = null
    // This property is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!
    private val questionViewModel: QuestionViewModel by viewModels {
        QuestionViewModelFactory((activity?.application as QuestionsApplication).repository)
    }
    private val takenSurveyViewModel: TakenSurveyViewModel by viewModels {
        TakenSurveyViewModelFactory((activity?.application as QuestionsApplication).ts_repository)
    }
    private var fb_key = "" // firebase key for current survey entry in firebase

    companion object {
        const val TAG = "QuestionFragment"
    }

    private lateinit var s8q2Radios: Array<RadioButton>
    private var currweektype = 1

    private val data_users = Firebase.database.reference
    // Section 10 vars
    private lateinit var s1001Radios: Array<RadioButton>
    private lateinit var s1002Checks: Array<CheckBox>
    private lateinit var s1003Radios: Array<RadioButton>
    private lateinit var s1004Radios: Array<RadioButton>
    private lateinit var s1005Radios: Array<RadioButton>
    private lateinit var s1006Radios: Array<RadioButton>
    private lateinit var s1007Checks: Array<CheckBox>
    private lateinit var s1008Checks: Array<CheckBox>
    private lateinit var s1009Radios: Array<RadioButton>
    private lateinit var s1010Radios: Array<RadioButton>
    private lateinit var s1011Radios: Array<RadioButton>
    private lateinit var s1012Checks: Array<CheckBox>
    private lateinit var s1013Checks: Array<CheckBox>
    private lateinit var s1014Radios: Array<RadioButton>

    private lateinit var mainScrollView: NestedScrollView
    private lateinit var next_button: AppCompatButton
    private lateinit var prev_button: AppCompatButton
    private lateinit var ivBack: ImageView
    private lateinit var progress_tv: TextView
    private lateinit var tv_section_prompt: TextView
    private lateinit var progressBar: ProgressBar
    private var currSection: Int = 1 // number corresponding to a set of questions
    private var absoluteSectionNumber: Int = 1 // from 1, what number of sections have been traversed
    private var numSections: Int = 10
    private lateinit var adapter: QuestionListAdapter
    private var s1QuestionsArr: ArrayList<Question> = ArrayList()
    private var s2QuestionsArr: ArrayList<Question> = ArrayList()
    private var s3QuestionsArr: ArrayList<Question> = ArrayList()
    private var s4QuestionsArr: ArrayList<Question> = ArrayList()
    private var s5QuestionsArr: ArrayList<Question> = ArrayList()
    private var s6QuestionsArr: ArrayList<Question> = ArrayList()
    private var s7QuestionsArr: ArrayList<Question> = ArrayList()
    private var s8QuestionsArr: ArrayList<Question> = ArrayList()
    private var s9QuestionsArr: ArrayList<Question> = ArrayList()
    private val MAX_PER_SECTION: Int = 30
    private lateinit var recyclerView: RecyclerView
    private lateinit var xmllayout_j: LinearLayout
    private lateinit var xmllayout_k: LinearLayout
    private lateinit var currSurvey: MutableTS
    private var currSurveyId: Long = 0
    private var liveDataSurveyId: LiveData<Long> = MutableLiveData<Long>(0)
    private var latitude = 0.0
    private var longitude = 0.0
    private var compensation_email = ""
    //                            123456789
    private val alphaReference = "abcdefghijklmnopqrstuvwxyz"
    // Define which sections will be showed on subsequent surveys
    //                                   1        2        3        4       5         6       7        8       9        10       11       12      13        14
    private val weeklySections = listOf("abin", "acfik", "aghik", "acik", "adgim", "acik", "aegik", "acik", "ahim", "acgik", "adfik", "acik", "aehim", "acgik")
    // "l" means both j and k
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Obtain compensation email from preliminary screening fragment
            it.getString("COMP_EMAIL")?.let{ value -> compensation_email = value }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()
        _binding = FragmentQuestionBinding.inflate(inflater, container, false)
        val v = binding.root
        // Inflate the layout for this fragment
//        val v = inflater.inflate(R.layout.fragment_question, container, false)
        mainScrollView = v.findViewById(R.id.mainScrollView)
        val utilDate: java.util.Date = Date()
        val sqlCurrDate = java.sql.Date(utilDate.time)
        val extras = requireActivity().intent.extras
        if (extras != null) {
            val tempweektypestr: String? = extras.getString("currweektype")
            if (tempweektypestr != null) {
                currweektype = tempweektypestr.toInt()
            }
        }

        ivBack = v.findViewById(R.id.ivBack)
        ivBack.setOnClickListener {
            activity?.finish()
        }

        var uid = UidSingleton.getUid()
//        Log.d(TAG, "currweektype: ${currweektype}")
        numSections = weeklySections[currweektype-1].length
//        Log.d(TAG, "sections list: ${weeklySections[currweektype-1]}, numsections: ${numSections}")

        // Check if the app has the required permissions
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Get the current location using the Location API
            val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            // Extract the latitude and longitude from the location object
            if (location != null) {
                latitude = location.latitude
            }
            if (location != null) {
                longitude = location.longitude
            }

            if (latitude == 0.0 && longitude == 0.0) {
                // Handle the case where the location is null
                Toast.makeText(requireContext(), "Unable to retrieve location", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Request the necessary permissions from the user
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                FinishFragment.PERMISSIONS_REQUEST_LOCATION
            )
        }
//        val bundle: Bundle? = requireActivity().intent.extras
//        if (bundle != null) {
//            val keys = bundle.keySet()
//            val it: Iterator<String> = keys.iterator()
//            Log.e(TAG, "Dumping Intent start")
//            while (it.hasNext()) {
//                val key = it.next()
//                Log.e(TAG, "[" + key + "=" + bundle[key] + "]")
//            }
//            Log.e(TAG, "Dumping Intent end")
//        }
        currSurvey = MutableTS(uid, sqlCurrDate.toString(), currweektype, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
        adapter = QuestionListAdapter(requireContext())

        // Layouts in xml to display in section 10
        xmllayout_j = v.findViewById(R.id.s5layout_j)
        xmllayout_k = v.findViewById(R.id.s5layout_k)

        // Section prompt setup
        tv_section_prompt = v.findViewById(R.id.tv_section_prompt)
        updateSectionPrompt()

        // Progress bar setup
        progressBar = v.findViewById(R.id.progressBar)
        progressBar.max = numSections
        progressBar.progress = currSection
        progress_tv = v.findViewById(R.id.tv_progress)
        progress_tv.text = "$currSection/${progressBar.getMax()}"

        // Section navigation buttons
        next_button = v.findViewById<AppCompatButton>(R.id.next_btn)
        prev_button = v.findViewById<AppCompatButton>(R.id.prev_btn)
        // Inside your fragment or activity
//        next_button.setOnClickListener {
//            absoluteSectionNumber++
//            if(absoluteSectionNumber > numSections) { // 1-based indexing
//                // Save to local db & to firebase
//                quickUpdateDb()
//
//                Toast.makeText(
//                    requireContext(),
//                    "Thank you for submitting the survey!",
//                    Toast.LENGTH_SHORT
//                ).show()
//
//                // Morva Code - End
//
//                val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
//                val currentWeek = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH)
//
//                // Retrieve survey history for the current user
//                retrieveSurveyHistoryForUser(currSurvey.uid) { surveyHistoryList ->
//                    // Check if the user has already been awarded a badge this week
//                    val badgeAwarded = surveyHistoryList.any { it.weekNumber == currentWeek && it.badgeAwarded }
//
//                    // Create a SurveyHistory object
//                    val surveyHistory = SurveyHistoryItem(
//                        userId = currSurvey.uid,
//                        timestamp = currSurvey.datetime,
//                        weekNumber = currentWeek,
//                        badgeAwarded = badgeAwarded
//                    )
//
//                    // Store survey history in the database
//                    saveSurveyHistory(surveyHistory)
//                }
//
//                // ADD UPLOAD APP STATS HERE
//                if (usagePermission()) {
//                    recordAppStats()
//                }
//
//                createNotificationChannel()
//                // Create notification and set to launch just under a week in the future
//                scheduleNotification()
//                activity?.finish()
//
////                findNavController().navigate(R.id.action_questionFragment_to_finishFragment)
//            } else {
//                if (absoluteSectionNumber == numSections){
//                    next_button.text = "Submit"
//                    next_button.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
//
//                }
//                // Use character corresponding to section in this week's string value to get section number
//                currSection = alphaReference.indexOf(weeklySections[currweektype-1].get(absoluteSectionNumber-1))+1
////                Log.d(TAG, "currsection: ${currSection} section alpha id: ${weeklySections[currweektype-1].get(absoluteSectionNumber-1)}")
//                mainScrollView.smoothScrollTo(0,0);
//                progress_tv.text = "$absoluteSectionNumber/${progressBar.getMax()}"
//                progressBar.progress = absoluteSectionNumber
//                updateSectionPrompt()
//                updateQuestions()
//            }
//        }
        next_button.setOnClickListener {
            absoluteSectionNumber++
            if (absoluteSectionNumber > numSections) { // 1-based indexing
                // Save to local db & to firebase
                quickUpdateDb()

                Toast.makeText(
                    requireContext(),
                    "Thank you for submitting the survey!",
                    Toast.LENGTH_SHORT
                ).show()

                // Morva Code - End

                val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
                val calendar = Calendar.getInstance()
                val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                val currentWeekOfMonth = (currentDayOfMonth - 1) / 7 + 1


                // Retrieve survey history for the current user
                retrieveSurveyHistoryForUser(currSurvey.uid) { surveyHistoryList ->
                    // Check if the user has already been awarded a badge this week
                    var badgeAwarded = false
                    for (surveyHistory in surveyHistoryList) {
                        if (surveyHistory.weekNumber == currentWeekOfMonth && surveyHistory.badgeAwarded) {
                            badgeAwarded = true
                            break
                        }
                    }

                    // If no badge is found for the current week, award the badge
                    if (!badgeAwarded) {
                        val surveyHistory = SurveyHistoryItem(
                            userId = currSurvey.uid,
                            timestamp = currSurvey.datetime,
                            weekNumber = currentWeekOfMonth,
                            badgeAwarded = true
                        )

                        // Store survey history in the database
                        saveSurveyHistory(surveyHistory)
                    }else{
                        val surveyHistory = SurveyHistoryItem(
                            userId = currSurvey.uid,
                            timestamp = currSurvey.datetime,
                            weekNumber = currentWeekOfMonth,
                            badgeAwarded = false
                        )

                        // Store survey history in the database
                        saveSurveyHistory(surveyHistory)
                    }
                }

                // ADD UPLOAD APP STATS HERE
                if (usagePermission()) {
                    recordAppStats()
                }

                createNotificationChannel()
                // Create notification and set to launch just under a week in the future
                scheduleNotification()
                activity?.finish()

            } else {
                if (absoluteSectionNumber == numSections) {
                    next_button.text = "Submit"
                    next_button.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
                }
                // Use character corresponding to section in this week's string value to get section number
                currSection =
                    alphaReference.indexOf(weeklySections[currweektype - 1].get(absoluteSectionNumber - 1)) + 1
                mainScrollView.smoothScrollTo(0, 0)
                progress_tv.text = "$absoluteSectionNumber/${progressBar.getMax()}"
                progressBar.progress = absoluteSectionNumber
                updateSectionPrompt()
                updateQuestions()
            }
        }


        prev_button.setOnClickListener{
            if(absoluteSectionNumber > 1) {
                absoluteSectionNumber--
                currSection = alphaReference.indexOf(weeklySections[currweektype-1].get(absoluteSectionNumber-1))+1
                mainScrollView.smoothScrollTo(0,0)
                progress_tv.text = "$absoluteSectionNumber/${progressBar.getMax()}"
                progressBar.progress = absoluteSectionNumber
                updateSectionPrompt()
                updateQuestions()
            }
        }

        // Questions list
        recyclerView = v.findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : QuestionListAdapter.ClickListener {
            override fun onItemClick(v: View?, qid: Int, response: String) {
                saveResponse(qid, response)
            }
        })

        recyclerView.layoutManager = LinearLayoutManager(context)
        // Add an observer on the LiveData returned by question query
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground
        this.questionViewModel.s1Questions.observe(viewLifecycleOwner, Observer { questions ->
            val temps1qs = questions as ArrayList<Question>
            s1QuestionsArr = ArrayList<Question>()
            val qpool = ArrayList<Question>()
            var i = 0
            while(i < temps1qs.size) {
                val q: Question = temps1qs.get(i)
                if(q.ftype == 0) {
                    s1QuestionsArr.add(q)
                } else {
                    qpool.add(q)
                }
                i++
            }
            val nShuffledQs = min(qpool.size, MAX_PER_SECTION-s1QuestionsArr.size)
            if(nShuffledQs > 0) {
                s1QuestionsArr = (s1QuestionsArr + (qpool.shuffled().take(nShuffledQs))) as ArrayList<Question>
            }
            questions.let { submitCurrentSectionList(s1QuestionsArr) }
        })
        this.questionViewModel.s2Questions.observe(viewLifecycleOwner, Observer { questions ->
            val tempqs = questions as ArrayList<Question>
            s2QuestionsArr = ArrayList<Question>()
            val qpool = ArrayList<Question>()
            var i = 0
            while(i < tempqs.size) {
                val q: Question = tempqs.get(i)
                if(q.ftype == 0) {
                    s2QuestionsArr.add(q)
                } else {
                    qpool.add(q)
                }
                i++
            }
            val nShuffledQs = min(qpool.size, MAX_PER_SECTION-s2QuestionsArr.size)
            if(nShuffledQs > 0) {
                s2QuestionsArr = (s2QuestionsArr + (qpool.shuffled().take(nShuffledQs))) as ArrayList<Question>
            }
        })
        this.questionViewModel.s3Questions.observe(viewLifecycleOwner, Observer { questions ->
            val tempqs = questions as ArrayList<Question>
            s3QuestionsArr = ArrayList<Question>()
            val qpool = ArrayList<Question>()
            var i = 0
            while(i < tempqs.size) {
                val q: Question = tempqs.get(i)
                if(q.ftype == 0) {
                    s3QuestionsArr.add(q)
                } else {
                    qpool.add(q)
                }
                i++
            }
            val nShuffledQs = min(qpool.size, MAX_PER_SECTION-s3QuestionsArr.size)
            if(nShuffledQs > 0) {
                s3QuestionsArr = (s3QuestionsArr + (qpool.shuffled().take(nShuffledQs))) as ArrayList<Question>
            }
        })
        this.questionViewModel.s4Questions.observe(viewLifecycleOwner, Observer { questions ->
            val tempqs = questions as ArrayList<Question>
            s4QuestionsArr = ArrayList<Question>()
            val qpool = ArrayList<Question>()
            var i = 0
            while(i < tempqs.size) {
                val q: Question = tempqs.get(i)
                if(q.ftype == 0) {
                    s4QuestionsArr.add(q)
                } else {
                    qpool.add(q)
                }
                i++
            }
            val nShuffledQs = min(qpool.size, MAX_PER_SECTION-s4QuestionsArr.size)
            if(nShuffledQs > 0) {
                s4QuestionsArr = (s4QuestionsArr + (qpool.shuffled().take(nShuffledQs))) as ArrayList<Question>
            }
        })
        this.questionViewModel.s5Questions.observe(viewLifecycleOwner, Observer { questions ->
            val tempqs = questions as ArrayList<Question>
            s5QuestionsArr = ArrayList<Question>()
            val qpool = ArrayList<Question>()
            var i = 0
            while(i < tempqs.size) {
                val q: Question = tempqs.get(i)
                if(q.ftype == 0) {
                    s5QuestionsArr.add(q)
                } else {
                    qpool.add(q)
                }
                i++
            }
            val nShuffledQs = min(qpool.size, MAX_PER_SECTION-s5QuestionsArr.size)
            if(nShuffledQs > 0) {
                s5QuestionsArr = (s5QuestionsArr + (qpool.shuffled().take(nShuffledQs))) as ArrayList<Question>
            }
        })
        this.questionViewModel.s6Questions.observe(viewLifecycleOwner, Observer { questions ->
            val tempqs = questions as ArrayList<Question>
            s6QuestionsArr = ArrayList<Question>()
            val qpool = ArrayList<Question>()
            var i = 0
            while(i < tempqs.size) {
                val q: Question = tempqs.get(i)
                if(q.ftype == 0) {
                    s6QuestionsArr.add(q)
                } else {
                    qpool.add(q)
                }
                i++
            }
            val nShuffledQs = min(qpool.size, MAX_PER_SECTION-s6QuestionsArr.size)
            if(nShuffledQs > 0) {
                s6QuestionsArr = (s6QuestionsArr + (qpool.shuffled().take(nShuffledQs))) as ArrayList<Question>
            }
        })
        this.questionViewModel.s7Questions.observe(viewLifecycleOwner, Observer { questions ->
            val tempqs = questions as ArrayList<Question>
            s7QuestionsArr = ArrayList<Question>()
            val qpool = ArrayList<Question>()
            var i = 0
            while(i < tempqs.size) {
                val q: Question = tempqs.get(i)
                if(q.ftype == 0) {
                    s7QuestionsArr.add(q)
                } else {
                    qpool.add(q)
                }
                i++
            }
            val nShuffledQs = min(qpool.size, MAX_PER_SECTION-s7QuestionsArr.size)
            if(nShuffledQs > 0) {
                s7QuestionsArr = (s7QuestionsArr + (qpool.shuffled().take(nShuffledQs))) as ArrayList<Question>
            }
        })
        this.questionViewModel.s8Questions.observe(viewLifecycleOwner, Observer { questions ->
            val tempqs = questions as ArrayList<Question>
            s8QuestionsArr = ArrayList<Question>()
            val qpool = ArrayList<Question>()
            var i = 0
            while(i < tempqs.size) {
                val q: Question = tempqs.get(i)
                if(q.ftype == 0) {
                    s8QuestionsArr.add(q)
                } else {
                    qpool.add(q)
                }
                i++
            }
            val nShuffledQs = min(qpool.size, MAX_PER_SECTION-s8QuestionsArr.size)
            if(nShuffledQs > 0) {
                s8QuestionsArr = (s8QuestionsArr + (qpool.shuffled().take(nShuffledQs))) as ArrayList<Question>
            }
        })
        this.questionViewModel.s9Questions.observe(viewLifecycleOwner, Observer { questions ->
            val tempqs = questions as ArrayList<Question>
            s9QuestionsArr = ArrayList<Question>()
            val qpool = ArrayList<Question>()
            var i = 0
            while(i < tempqs.size) {
                val q: Question = tempqs.get(i)
                if(q.ftype == 0) {
                    s9QuestionsArr.add(q)
                } else {
                    qpool.add(q)
                }
                i++
            }
            val nShuffledQs = min(qpool.size, MAX_PER_SECTION-s9QuestionsArr.size)
            if(nShuffledQs > 0) {
                s9QuestionsArr = (s9QuestionsArr + (qpool.shuffled().take(nShuffledQs))) as ArrayList<Question>
            }
        })
        // Section 10 questions (written into xml layout file)
        // Section 10 question 1
        s1001Radios = arrayOf(binding.q1op0, binding.q1op1, binding.q1op2, binding.q1op3)
        for(option in s1001Radios) {
            option.setOnClickListener{ clickedView -> handles10q(clickedView, 1)}
        }

        // Section 10 question 2
        s1002Checks = arrayOf(
            binding.q2op0,
            binding.q2op1,
            binding.q2op2,
            binding.q2op3,
            binding.q2op4,
            binding.q2op5,
            binding.q2op6,
            binding.q2op7,
            binding.q2op8,
            binding.q2op9,
            binding.q2op10,
            binding.q2op11
        )
        for(option in s1002Checks) {
            option.setOnClickListener{ clickedView -> handles10q(clickedView, 2)}
        }

        // Section 10 question 3
        s1003Radios = arrayOf(
            binding.q3op0,
            binding.q3op1,
            binding.q3op2,
            binding.q3op3,
            binding.q3op4,
            binding.q3op5
        )
        for(option in s1003Radios) {
            option.setOnClickListener{ clickedView -> handles10q(clickedView, 3)}
        }

        // Section 10 question 4
        s1004Radios = arrayOf(binding.q4op0, binding.q4op1)
        for(option in s1004Radios) {
            option.setOnClickListener{ clickedView -> handles10q(clickedView, 4)}
        }

        // Section 10 question 5
        s1005Radios = arrayOf(
            binding.q5op0,
            binding.q5op1,
            binding.q5op2,
            binding.q5op3,
            binding.q5op4,
            binding.q5op5,
            binding.q5op6
        )
        for(option in s1005Radios) {
            option.setOnClickListener{ clickedView -> handles10q(clickedView, 5)}
        }

        // Section 10 question 6
        s1006Radios = arrayOf(binding.q6op0, binding.q6op1, binding.q6op2, binding.q6op3)
        for(option in s1006Radios) {
            option.setOnClickListener{ clickedView -> handles10q(clickedView, 6)}
        }

        // Section 10 question 7
        s1007Checks = arrayOf(
            binding.q7op0,
            binding.q7op1,
            binding.q7op2,
            binding.q7op3,
            binding.q7op4,
            binding.q7op5,
            binding.q7op6
        )
        for(option in s1007Checks) {
            option.setOnClickListener{ clickedView -> handles10q(clickedView, 7)}
        }

        // Section 10 question 8
        s1008Checks = arrayOf(
            binding.q8op0,
            binding.q8op1,
            binding.q8op2,
            binding.q8op3,
            binding.q8op4,
            binding.q8op5,
            binding.q8op6,
            binding.q8op7,
            binding.q8op8,
            binding.q8op9,
            binding.q8op10,
            binding.q8op11,
            binding.q8op12,
            binding.q8op13,
            binding.q8op14,
            binding.q8op15,
            binding.q8op16,
            binding.q8op17,
            binding.q8op18
        )
        for(option in s1008Checks) {
            option.setOnClickListener{ clickedView -> handles10q(clickedView, 8)}
        }

        // Section 10 question 9
        s1009Radios = arrayOf(binding.q9op0, binding.q9op1, binding.q9op2, binding.q9op3)
        for(option in s1009Radios) {
            option.setOnClickListener{ clickedView -> handles10q(clickedView, 9)}
        }

        // Section 10 question 10
        s1010Radios = arrayOf(binding.q10op0, binding.q10op1, binding.q10op2, binding.q10op3)
        for(option in s1010Radios) {
            option.setOnClickListener{ clickedView -> handles10q(clickedView, 10)}
        }

        // Section 10 question 11
        s1011Radios = arrayOf(binding.q11op0, binding.q11op1, binding.q11op2, binding.q11op3)
        for(option in s1011Radios) {
            option.setOnClickListener{ clickedView -> handles10q(clickedView, 11)}
        }

        // Section 10 question 12
        s1012Checks = arrayOf(
            binding.q12op0,
            binding.q12op1,
            binding.q12op2,
            binding.q12op3,
            binding.q12op4
        )
        for(option in s1012Checks) {
            option.setOnClickListener{ clickedView -> handles10q(clickedView, 12)}
        }

        // Section 10 question 13
        s1013Checks = arrayOf(
            binding.q13op0,
            binding.q13op1,
            binding.q13op2,
            binding.q13op3,
            binding.q13op4,
            binding.q13op5
        )
        for(option in s1013Checks) {
            option.setOnClickListener{ clickedView -> handles10q(clickedView, 13)}
        }

        // Section 10 question 14
        s1014Radios = arrayOf(binding.q14op0, binding.q14op1)
        for(option in s1014Radios) {
            option.setOnClickListener{ clickedView -> handles10q(clickedView, 14)}
        }
        return v
    }

    // Add this function to your code
    private fun retrieveSurveyHistoryForUser(userId: String, callback: (List<SurveyHistoryItem>) -> Unit) {
        // Assuming you have a database reference or DAO for survey history
        // Replace the following line with your actual database operation
        // For simplicity, I'm using a hypothetical database reference named surveyHistoryDbRef

        firebaseCloudSurveyHistory.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val surveyHistoryList = mutableListOf<SurveyHistoryItem>()
                for (snapshot in dataSnapshot.children) {
                    val history = snapshot.getValue(SurveyHistoryItem::class.java)
                    history?.let { surveyHistoryList.add(it) }
                }
                callback(surveyHistoryList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                // You might want to add error handling here based on your application needs
            }
        })
    }


    /**
     * Run intermediate updates to database after each individual question is answered by user
     */
    private fun quickUpdateDb() {
        // Morva Code - start - storing users' responses on Firebase
        val data = hashMapOf(
            "userid" to currSurvey.uid,
            "timestamp" to currSurvey.datetime,
            "compensation_email" to compensation_email,
            "currweeknum" to currweektype,
            "latitude" to latitude,
            "longitude" to longitude,
            // S1
            "q100" to currSurvey.q100a,
// S2
            "q200" to currSurvey.q200a,
            "q201" to currSurvey.q201a,
            "q202" to currSurvey.q202a,
            "q203" to currSurvey.q203a,
            "q204" to currSurvey.q204a,
            "q205" to currSurvey.q205a,
            "q206" to currSurvey.q206a,
            "q207" to currSurvey.q207a,
            "q208" to currSurvey.q208a,
            "q209" to currSurvey.q209a,
// S3
            "q300" to currSurvey.q300a,
            "q301" to currSurvey.q301a,
            "q302" to currSurvey.q302a,
            "q303" to currSurvey.q303a,
            "q304" to currSurvey.q304a,
            "q305" to currSurvey.q305a,
            "q306" to currSurvey.q306a,
            "q307" to currSurvey.q307a,
            "q308" to currSurvey.q308a,
            "q309" to currSurvey.q309a,
            "q310" to currSurvey.q310a,
            "q311" to currSurvey.q311a,
            "q312" to currSurvey.q312a,
            "q313" to currSurvey.q313a,
            "q314" to currSurvey.q314a,
            "q315" to currSurvey.q315a,
            "q316" to currSurvey.q316a,
// S4
            "q400" to currSurvey.q400a,
            "q401" to currSurvey.q401a,
            "q402" to currSurvey.q402a,
            "q403" to currSurvey.q403a,
            "q404" to currSurvey.q404a,
            "q405" to currSurvey.q405a,
            "q406" to currSurvey.q406a,
            "q407" to currSurvey.q407a,
            "q408" to currSurvey.q408a,
            "q409" to currSurvey.q409a,
            "q410" to currSurvey.q410a,
            "q411" to currSurvey.q411a,
            "q412" to currSurvey.q412a,
            "q413" to currSurvey.q413a,
            "q414" to currSurvey.q414a,
            "q415" to currSurvey.q415a,
// S5
            "q500" to currSurvey.q500a,
            "q501" to currSurvey.q501a,
            "q502" to currSurvey.q502a,
            "q503" to currSurvey.q503a,
            "q504" to currSurvey.q504a,
            "q505" to currSurvey.q505a,
            "q506" to currSurvey.q506a,
            "q507" to currSurvey.q507a,
            "q508" to currSurvey.q508a,
            "q509" to currSurvey.q509a,
            "q510" to currSurvey.q510a,
            "q511" to currSurvey.q511a,
            "q512" to currSurvey.q512a,
            "q513" to currSurvey.q513a,
            "q514" to currSurvey.q514a,
            "q515" to currSurvey.q515a,
// S6
            "q600" to currSurvey.q600a,
            "q601" to currSurvey.q601a,
            "q602" to currSurvey.q602a,
            "q603" to currSurvey.q603a,
            "q604" to currSurvey.q604a,
            "q605" to currSurvey.q605a,
            "q606" to currSurvey.q606a,
            "q607" to currSurvey.q607a,
            "q608" to currSurvey.q608a,
            "q609" to currSurvey.q609a,
            "q610" to currSurvey.q610a,
            "q611" to currSurvey.q611a,
            "q612" to currSurvey.q612a,
            "q613" to currSurvey.q613a,
            "q614" to currSurvey.q614a,
            "q615" to currSurvey.q615a,
// S7
            "q700" to currSurvey.q700a,
            "q701" to currSurvey.q701a,
            "q702" to currSurvey.q702a,
            "q703" to currSurvey.q703a,
            "q704" to currSurvey.q704a,
            "q705" to currSurvey.q705a,
            "q706" to currSurvey.q706a,
            "q707" to currSurvey.q707a,
            "q708" to currSurvey.q708a,
            "q709" to currSurvey.q709a,
            "q710" to currSurvey.q710a,
// S8
            "q800" to currSurvey.q800a,
            "q801" to currSurvey.q801a,
            "q802" to currSurvey.q802a,
            "q803" to currSurvey.q803a,
            "q804" to currSurvey.q804a,
            "q805" to currSurvey.q805a,
            "q806" to currSurvey.q806a,
            "q807" to currSurvey.q807a,
            "q808" to currSurvey.q808a,
            "q809" to currSurvey.q809a,
            "q810" to currSurvey.q810a,
            "q811" to currSurvey.q811a,
            "q812" to currSurvey.q812a,
            "q813" to currSurvey.q813a,
            "q814" to currSurvey.q814a,
            "q815" to currSurvey.q815a,
            "q816" to currSurvey.q816a,
            "q817" to currSurvey.q817a,
            "q818" to currSurvey.q818a,
// S9
            "q900" to currSurvey.q900a,
            "q901" to currSurvey.q901a,
            "q902" to currSurvey.q902a,
// S10
            "q1001" to currSurvey.q1001a,
            "q1002" to currSurvey.q1002a,
            "q1003" to currSurvey.q1003a,
            "q1004" to currSurvey.q1004a,
            "q1005" to currSurvey.q1005a,
            "q1006" to currSurvey.q1006a,
            "q1007" to currSurvey.q1007a,
            "q1008" to currSurvey.q1008a,
            "q1009" to currSurvey.q1009a,
            "q1010" to currSurvey.q1010a,
            "q1011" to currSurvey.q1011a,
            "q1012" to currSurvey.q1012a,
            "q1013" to currSurvey.q1013a,
            "q1014" to currSurvey.q1014a
        )




        if (this.currSurveyId == (0.toLong())) {
//            Log.d(TAG, "IdVal is null, doing first insert")
            liveDataSurveyId = takenSurveyViewModel.insert(currSurvey.createTS())
            liveDataSurveyId.observe(viewLifecycleOwner, Observer<Long> {
                it -> this.currSurveyId = it
            })
            this.fb_key = this.data_users.child("users_responses").push().key.toString();
            this.data_users.child("users_responses").child(currSurvey.uid).child(this.fb_key).setValue(data)
        } else {
//            Log.d(TAG, "IdVal, trying to update: ${this.currSurveyId}")
            takenSurveyViewModel.update(currSurvey.createTS(this.currSurveyId))
            this.data_users.child("users_responses").child(currSurvey.uid).child(this.fb_key).setValue(data)
        }
    }

    /**
     * Constructs string representation of response, with multiple checked responses delimited by
     * semicolon characters
     */
    private fun handles10q(view: View, s10_qnum: Int) {
        var s10qr: String = java.sql.Date(Date().time).toString() + "; "
        // Save response
        when(s10_qnum) {
            1 -> {
                for(op in s1001Radios){
                    if(op.isChecked) {
                        s10qr += "${op.text};"
                    }
                }
                currSurvey.q1001a = s10qr
                quickUpdateDb()
            }
            2 -> {
                for(op in s1002Checks){
                    if(op.isChecked) {
                        s10qr += "${op.text};"
                    }
                }
                currSurvey.q1002a = s10qr
                quickUpdateDb()
            }
            3 -> {
                for(op in s1003Radios){
                    if(op.isChecked) {
                        s10qr += "${op.text};"
                    }
                }
                currSurvey.q1003a = s10qr
                quickUpdateDb()
            }
            4 -> {
                for(op in s1004Radios){
                    if(op.isChecked) {
                        s10qr += "${op.text};"
                    }
                }
                currSurvey.q1004a = s10qr
                quickUpdateDb()
            }
            5 -> {
                for(op in s1005Radios){
                    if(op.isChecked) {
                        s10qr += "${op.text};"
                    }
                }
                currSurvey.q1005a = s10qr
                quickUpdateDb()
            }
            6 -> {
                for(op in s1006Radios){
                    if(op.isChecked) {
                        s10qr += "${op.text};"
                    }
                }
                currSurvey.q1006a = s10qr
                quickUpdateDb()
            }
            7 -> {
                for(op in s1007Checks){
                    if(op.isChecked) {
                        s10qr += "${op.text};"
                    }
                }
                currSurvey.q1007a = s10qr
                quickUpdateDb()
            }
            8 -> {
                for(op in s1008Checks){
                    if(op.isChecked) {
                        s10qr += "${op.text};"
                    }
                }
                currSurvey.q1008a = s10qr
                quickUpdateDb()
            }
            9 -> {
                for(op in s1009Radios){
                    if(op.isChecked) {
                        s10qr += "${op.text};"
                    }
                }
                currSurvey.q1009a = s10qr
                quickUpdateDb()
            }
            10 -> {
                for(op in s1010Radios){
                    if(op.isChecked) {
                        s10qr += "${op.text};"
                    }
                }
                currSurvey.q1010a = s10qr
                quickUpdateDb()
            }
            11 -> {
                for(op in s1011Radios){
                    if(op.isChecked) {
                        s10qr += "${op.text};"
                    }
                }
                currSurvey.q1011a = s10qr
                quickUpdateDb()
            }
            12 -> {
                for(op in s1012Checks){
                    if(op.isChecked) {
                        s10qr += "${op.text};"
                    }
                }
                currSurvey.q1012a = s10qr
                quickUpdateDb()
            }
            13 -> {
                for(op in s1013Checks){
                    if(op.isChecked) {
                        s10qr += "${op.text};"
                    }
                }
                currSurvey.q1013a = s10qr
                quickUpdateDb()
            }
            14 -> {
                for(op in s1014Radios){
                    if(op.isChecked) {
                        s10qr += "${op.text};"
                    }
                }
                currSurvey.q1014a = s10qr
                quickUpdateDb()
            }

        }
    }

    fun saveResponse(questionId: Int, resp: String) {
          val nowString: String = java.sql.Date(Date().time).toString()
//        val nowString = SimpleDateFormat("yyyy.MM.dd G 'at' hh:mm:ss a zzz")
        when(questionId) {
            // S1
            100 -> currSurvey.q100a = "${nowString}; $resp"
// S2
            200 -> currSurvey.q200a = "${nowString}; $resp"
            201 -> currSurvey.q201a = "${nowString}; $resp"
            202 -> currSurvey.q202a = "${nowString}; $resp"
            203 -> currSurvey.q203a = "${nowString}; $resp"
            204 -> currSurvey.q204a = "${nowString}; $resp"
            205 -> currSurvey.q205a = "${nowString}; $resp"
            206 -> currSurvey.q206a = "${nowString}; $resp"
            207 -> currSurvey.q207a = "${nowString}; $resp"
            208 -> currSurvey.q208a = "${nowString}; $resp"
            209 -> currSurvey.q209a = "${nowString}; $resp"
// S3
            300 -> currSurvey.q300a = "${nowString}; $resp"
            301 -> currSurvey.q301a = "${nowString}; $resp"
            302 -> currSurvey.q302a = "${nowString}; $resp"
            303 -> currSurvey.q303a = "${nowString}; $resp"
            304 -> currSurvey.q304a = "${nowString}; $resp"
            305 -> currSurvey.q305a = "${nowString}; $resp"
            306 -> currSurvey.q306a = "${nowString}; $resp"
            307 -> currSurvey.q307a = "${nowString}; $resp"
            308 -> currSurvey.q308a = "${nowString}; $resp"
            309 -> currSurvey.q309a = "${nowString}; $resp"
            310 -> currSurvey.q310a = "${nowString}; $resp"
            311 -> currSurvey.q311a = "${nowString}; $resp"
            312 -> currSurvey.q312a = "${nowString}; $resp"
            313 -> currSurvey.q313a = "${nowString}; $resp"
            314 -> currSurvey.q314a = "${nowString}; $resp"
            315 -> currSurvey.q315a = "${nowString}; $resp"
            316 -> currSurvey.q316a = "${nowString}; $resp"
// S4
            400 -> currSurvey.q400a = "${nowString}; $resp"
            401 -> currSurvey.q401a = "${nowString}; $resp"
            402 -> currSurvey.q402a = "${nowString}; $resp"
            403 -> currSurvey.q403a = "${nowString}; $resp"
            404 -> currSurvey.q404a = "${nowString}; $resp"
            405 -> currSurvey.q405a = "${nowString}; $resp"
            406 -> currSurvey.q406a = "${nowString}; $resp"
            407 -> currSurvey.q407a = "${nowString}; $resp"
            408 -> currSurvey.q408a = "${nowString}; $resp"
            409 -> currSurvey.q409a = "${nowString}; $resp"
            410 -> currSurvey.q410a = "${nowString}; $resp"
            411 -> currSurvey.q411a = "${nowString}; $resp"
            412 -> currSurvey.q412a = "${nowString}; $resp"
            413 -> currSurvey.q413a = "${nowString}; $resp"
            414 -> currSurvey.q414a = "${nowString}; $resp"
            415 -> currSurvey.q415a = "${nowString}; $resp"
// S5
            500 -> currSurvey.q500a = "${nowString}; $resp"
            501 -> currSurvey.q501a = "${nowString}; $resp"
            502 -> currSurvey.q502a = "${nowString}; $resp"
            503 -> currSurvey.q503a = "${nowString}; $resp"
            504 -> currSurvey.q504a = "${nowString}; $resp"
            505 -> currSurvey.q505a = "${nowString}; $resp"
            506 -> currSurvey.q506a = "${nowString}; $resp"
            507 -> currSurvey.q507a = "${nowString}; $resp"
            508 -> currSurvey.q508a = "${nowString}; $resp"
            509 -> currSurvey.q509a = "${nowString}; $resp"
            510 -> currSurvey.q510a = "${nowString}; $resp"
            511 -> currSurvey.q511a = "${nowString}; $resp"
            512 -> currSurvey.q512a = "${nowString}; $resp"
            513 -> currSurvey.q513a = "${nowString}; $resp"
            514 -> currSurvey.q514a = "${nowString}; $resp"
            515 -> currSurvey.q515a = "${nowString}; $resp"
// S6
            600 -> currSurvey.q600a = "${nowString}; $resp"
            601 -> currSurvey.q601a = "${nowString}; $resp"
            602 -> currSurvey.q602a = "${nowString}; $resp"
            603 -> currSurvey.q603a = "${nowString}; $resp"
            604 -> currSurvey.q604a = "${nowString}; $resp"
            605 -> currSurvey.q605a = "${nowString}; $resp"
            606 -> currSurvey.q606a = "${nowString}; $resp"
            607 -> currSurvey.q607a = "${nowString}; $resp"
            608 -> currSurvey.q608a = "${nowString}; $resp"
            609 -> currSurvey.q609a = "${nowString}; $resp"
            610 -> currSurvey.q610a = "${nowString}; $resp"
            611 -> currSurvey.q611a = "${nowString}; $resp"
            612 -> currSurvey.q612a = "${nowString}; $resp"
            613 -> currSurvey.q613a = "${nowString}; $resp"
            614 -> currSurvey.q614a = "${nowString}; $resp"
            615 -> currSurvey.q615a = "${nowString}; $resp"
// S7
            700 -> currSurvey.q700a = "${nowString}; $resp"
            701 -> currSurvey.q701a = "${nowString}; $resp"
            702 -> currSurvey.q702a = "${nowString}; $resp"
            703 -> currSurvey.q703a = "${nowString}; $resp"
            704 -> currSurvey.q704a = "${nowString}; $resp"
            705 -> currSurvey.q705a = "${nowString}; $resp"
            706 -> currSurvey.q706a = "${nowString}; $resp"
            707 -> currSurvey.q707a = "${nowString}; $resp"
            708 -> currSurvey.q708a = "${nowString}; $resp"
            709 -> currSurvey.q709a = "${nowString}; $resp"
            710 -> currSurvey.q710a = "${nowString}; $resp"
// S8
            800 -> currSurvey.q800a = "${nowString}; $resp"
            801 -> currSurvey.q801a = "${nowString}; $resp"
            802 -> currSurvey.q802a = "${nowString}; $resp"
            803 -> currSurvey.q803a = "${nowString}; $resp"
            804 -> currSurvey.q804a = "${nowString}; $resp"
            805 -> currSurvey.q805a = "${nowString}; $resp"
            806 -> currSurvey.q806a = "${nowString}; $resp"
            807 -> currSurvey.q807a = "${nowString}; $resp"
            808 -> currSurvey.q808a = "${nowString}; $resp"
            809 -> currSurvey.q809a = "${nowString}; $resp"
            810 -> currSurvey.q810a = "${nowString}; $resp"
            811 -> currSurvey.q811a = "${nowString}; $resp"
            812 -> currSurvey.q812a = "${nowString}; $resp"
            813 -> currSurvey.q813a = "${nowString}; $resp"
            814 -> currSurvey.q814a = "${nowString}; $resp"
            815 -> currSurvey.q815a = "${nowString}; $resp"
            816 -> currSurvey.q816a = "${nowString}; $resp"
            817 -> currSurvey.q817a = "${nowString}; $resp"
            818 -> currSurvey.q818a = "${nowString}; $resp"
// S9
            900 -> currSurvey.q900a = "${nowString}; $resp"
            901 -> currSurvey.q901a = "${nowString}; $resp"
            902 -> currSurvey.q902a = "${nowString}; $resp"
            // Section 10 dealt with separately
        }
        quickUpdateDb()
    }
    private fun submitCurrentSectionList(sectionQuestions: ArrayList<Question>) {
        this.adapter.submitList(sectionQuestions)
    }
    private fun updateQuestions() {
        xmllayout_j.visibility = View.GONE
        xmllayout_k.visibility = View.GONE
        binding.s5layoutM.visibility = View.GONE
        when (currSection) {
            1 -> {
                tv_section_prompt.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                submitCurrentSectionList(s1QuestionsArr)
            }
            2 -> {
                tv_section_prompt.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                submitCurrentSectionList(s2QuestionsArr)
            }
            3 -> {
                tv_section_prompt.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                submitCurrentSectionList(s3QuestionsArr)
            }
            4 -> {
                tv_section_prompt.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                submitCurrentSectionList(s4QuestionsArr)
            }
            5 -> {
                tv_section_prompt.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                submitCurrentSectionList(s5QuestionsArr)
            }
            6 -> {
                tv_section_prompt.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                submitCurrentSectionList(s6QuestionsArr)
            }
            7 -> {
                tv_section_prompt.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                submitCurrentSectionList(s7QuestionsArr)
            }
            8 -> {
                tv_section_prompt.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                submitCurrentSectionList(s8QuestionsArr)
            }
            9 -> { // i
                tv_section_prompt.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                submitCurrentSectionList(s9QuestionsArr)
            }
            10 -> { // j
                tv_section_prompt.visibility = View.GONE
                recyclerView.visibility = View.GONE
                xmllayout_j.visibility = View.VISIBLE
            }
            11 -> { // k
                tv_section_prompt.visibility = View.GONE
                recyclerView.visibility = View.GONE
                xmllayout_k.visibility = View.VISIBLE
            } 12 -> { // l
                tv_section_prompt.visibility = View.GONE
                recyclerView.visibility = View.GONE
                xmllayout_k.visibility = View.VISIBLE
                xmllayout_j.visibility = View.VISIBLE
            } 13 -> { // m
                tv_section_prompt.visibility = View.GONE
                recyclerView.visibility = View.GONE
                xmllayout_k.visibility = View.VISIBLE
                binding.s5layoutM.visibility = View.VISIBLE
            }
            else -> { // n
                tv_section_prompt.visibility = View.GONE
                recyclerView.visibility = View.GONE
                xmllayout_j.visibility = View.VISIBLE
                xmllayout_k.visibility = View.VISIBLE
                binding.s5layoutM.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Display proper prompt preceding each section of questions
     */
    private fun updateSectionPrompt() {
        tv_section_prompt.visibility = View.VISIBLE
        when(currSection) {
            1 -> {
                tv_section_prompt.visibility = View.GONE
                tv_section_prompt.text = ""
            }

            2 -> {
                tv_section_prompt.text = "How well do the following statements describe your personality?"
            }
            3 -> {
                tv_section_prompt.text = "We would like your opinion on different aspects of your hiking experience.  Specifically, we want to know how desirable or undesirable each of the below aspects are to you.  There or no right or wrong responses.  Please state how you personally feel at this moment about the desirability of the following aspects on the trail."
            }
            4 -> {
                tv_section_prompt.text = "For each of the following statements, please indicate your likelihood of engaging in each activity or behavior."
            }
            5 -> {
                tv_section_prompt.text = "For each of the following statements, please indicate the benefits you would obtain from each situation."
            }
            6 -> {
                tv_section_prompt.text = "For each of the following statements, please indicate how risky you perceive each situation."
            }
            7 -> {
                tv_section_prompt.text = "Please read each word and indicate to what extent you felt this way in the past 7 days."
            }
            8 -> {
                tv_section_prompt.text = "We would like you to tell us how much you are experiencing or feeling each of the following items:"
            }
            9 -> {
                tv_section_prompt.text = ""
            }
            else -> {
//                Log.e(TAG, "Other section number requested: $currSection")
            }
        }
    }

    // Record App Statistic Functions
    private fun recordAppStats() {

        var usageStatsSurvey = ""
        // Creates a list storing all app usage in the specified interval
        val format = SimpleDateFormat("yyyy-MM-dd")
        val takenDate = format.parse(currSurvey.datetime) as Date

        // Creates the format reader for the calender day.
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())

        var daysPassed = Date(takenDate.time)
        Log.e("SIN", "daysPassed: $daysPassed")
        var days = 0

        while (daysPassed.time <= System.currentTimeMillis()) {
            val dayName = dayFormat.format(daysPassed)
            Log.e("SIN", dayName)

            val appStatsLastSurvey = createUsageStatsFromLastTaken(daysPassed, days)
            if (appStatsLastSurvey == null) {
                usageStatsSurvey = "$usageStatsSurvey${dayName.uppercase()}: NONE "
            }
            else {
                val usageListLastSurvey = ArrayList(appStatsLastSurvey!!.values)
                val lastSurveyString = recordUsageList(usageListLastSurvey)
                usageStatsSurvey = "$usageStatsSurvey${dayName.uppercase()}: $lastSurveyString"
                Log.e("SIN", usageStatsSurvey)
            }

            daysPassed = Date(daysPassed.time + 1000 * 3600 * 24)
            days++

            if (days == 7) {
                break
            }
        }
        val login_db = DBHelper(requireActivity())
        val appstats_db = AppStatsDBHelper(requireActivity())
        val user = login_db.getUserFromID(currSurvey.uid)

        login_db.insertUsageStats(user.primaryKey, user.username, user.password, user.email, usageStatsSurvey)
        val reference = firebaseCloud.child(user.primaryKey).child("usageStats").push()
        val key = reference.key
        appstats_db.insertAppStats(user.primaryKey, key, currSurvey.datetime, usageStatsSurvey)
        reference.setValue(usageStatsSurvey)

    }

    private fun saveSurveyHistory(surveyHistory: SurveyHistoryItem) {
        // Assuming you have a database reference or DAO for survey history
        // Replace the following line with your actual database operation
        // For simplicity, I'm using a hypothetical database reference named surveyHistoryDbRef
        firebaseCloudSurveyHistory.push().setValue(surveyHistory)
    }

    // Records a usagestats list.
    private fun recordUsageList(usageList: List<UsageStats>): String {
        var usageStatsString = ""
        for (usageStats in usageList) {
            try {
                val packageName = usageStats.packageName
                val packageNames = packageName.split("\\")
                var appName = packageNames[packageNames.size - 1].trim { it <= ' ' }

                if (isAppInfoAvailable(usageStats)) {
                    val ai = requireActivity().applicationContext.packageManager.getApplicationInfo(packageName, 0)
                    appName = requireActivity().applicationContext.packageManager.getApplicationLabel(ai).toString()
                }
                var usageDuration = getDurationString(usageStats.totalTimeInForeground)
                usageStatsString = "$usageStatsString$appName, $usageDuration; "
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
        return usageStatsString
    }

    // Gets the duration of app foreground time in hh:mm:ss format.
    private fun getDurationString(millis: Long): String {
        var millis = millis
        require(millis >= 0) { "Duration must be greater than zero!" }
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        millis -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        millis -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
        return "$hours h $minutes m $seconds s"
    }

    // Checks if App information is available
    private fun isAppInfoAvailable(usageStats: UsageStats): Boolean {
        return try {
            requireActivity().applicationContext.packageManager.getApplicationInfo(usageStats.packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    // Creates usages stats from last taken survey
    private fun createUsageStatsFromLastTaken(takenDate: Date, dayNeeded: Int): SortedMap<Long, UsageStats>? {
        val dayTimespan = 1000 * 3600 * 24 + 1000

        Log.e("SIN", takenDate.toString())
        Log.e("SIN", Date(takenDate.time + dayTimespan).toString())

        val usm = requireActivity().getSystemService(AppCompatActivity.USAGE_STATS_SERVICE) as UsageStatsManager
        val appList = usm.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            // Begin Time: A amount of days (dayNeeded) after the survey.
            // End Time: A Full day after the survey.
            takenDate.time, takenDate.time + dayTimespan)
        Log.e("SIN", appList.size.toString())
        // Sorts the list by app usage
        if (appList != null && appList.size > 0) {
            var mySortedMap: SortedMap<Long, UsageStats> = TreeMap()
            for (usageStats in appList) {
                mySortedMap[usageStats.totalTimeInForeground] = usageStats
            }
            return mySortedMap
        }
        return null
    }

    // Gets usage permission status
    private fun usagePermission(): Boolean {
        var appOps: AppOpsManager? = null
        var mode: Int = 0
        appOps = requireActivity().getSystemService(AppCompatActivity.APP_OPS_SERVICE)!! as AppOpsManager
        mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), requireActivity().applicationContext.packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun scheduleNotification()
    {
        val intent = Intent(requireActivity().applicationContext, NotificationBroadcastReceiver::class.java)
        val title = "SmarTrail Survey"
        val message = "Time to take a trail survey!"
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            requireActivity().applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        showAlert(time, title, message)
    }

    private fun getTime(): Long
    {
        val MILLIS_IN_WEEK: Long = 604800000 - 1200000 // 20 minutes earlier than exactly in 7 days
        val calendar = Calendar.getInstance()
//    calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis + MILLIS_IN_WEEK
    }

    private fun createNotificationChannel()
    {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelID, name, importance)
            channel.description = desc
            val notificationManager = requireContext().getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        } else {
            TODO("VERSION.SDK_INT < O")
            Log.e(FinishFragment.TAG, "notification channel creation failed")
        }

    }

    private fun showAlert(time: Long, title: String, message: String)
    {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(requireActivity().applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(requireActivity().applicationContext)
        Log.d(
            FinishFragment.TAG, "Title: " + title +
                "\nMessage: " + message +
                "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date))
//        AlertDialog.Builder(requireContext())
//            .setTitle("Notification Scheduled")
//            .setMessage(
//                "Title: " + title +
//                        "\nMessage: " + message +
//                        "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date))
//            .setPositiveButton("Okay"){_,_ ->}
//            .show()
    }

}
