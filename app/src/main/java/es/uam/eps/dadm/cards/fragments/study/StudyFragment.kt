package es.uam.eps.dadm.cards.fragments.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import es.uam.eps.dadm.cards.R
import es.uam.eps.dadm.cards.activities.settings.SettingsActivity
import es.uam.eps.dadm.cards.databinding.FragmentStudyBinding
import java.time.LocalDate
import kotlin.math.min

class StudyFragment: Fragment() {

    lateinit var binding: FragmentStudyBinding
    private val viewModel: StudyViewModel by lazy {
        ViewModelProvider(this)[StudyViewModel::class.java]
    }
    private var deckIntervalModifier: Double? = null
    private var studyCounter = 0
    private var studyLimit = 0

    private val listener = View.OnClickListener { v ->
        val quality = when (v?.id) {
            R.id.easy_button -> 5
            R.id.doubt_button -> 3
            R.id.difficult_button -> 0
            else -> throw Exception("Unavailable quality")
        }
        viewModel.update(quality, deckIntervalModifier!!)
        binding.boardView.resetBoard()
        studyCounter++
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_study,
            container,
            false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.dueCard.observe(viewLifecycleOwner){
            viewModel.card = it
            if (it != null){
                viewModel.loadDeckId(it.deckId)
                viewModel.deck.observe(viewLifecycleOwner){ deck ->
                    if (deck != null) {
                        deckIntervalModifier = deck.intervalModifier
                        binding.invalidateAll()
                    }
                }
            } else {
                binding.invalidateAll()
            }
        }

        binding.answerButton.setOnClickListener {
            viewModel.card?.answered = true
            viewModel.card?.lastPracticeDates += LocalDate.now().toString() + ","
            binding.invalidateAll()
        }
        binding.easyButton.setOnClickListener(listener)
        binding.doubtButton.setOnClickListener(listener)
        binding.difficultButton.setOnClickListener(listener)

        viewModel.nDueCards.observe(viewLifecycleOwner){
            viewModel.limit.observe(viewLifecycleOwner){ limit ->
                if (studyLimit == 0 || studyCounter == 0) {
                    studyLimit = if (limit == "" || limit == "0") {
                        it
                    } else {
                        min(limit!!.toInt(), it)
                    }
                }
                if (studyCounter >= studyLimit && studyCounter != 0) {
                    viewModel.card = null
                    binding.invalidateAll()
                    Snackbar.make(binding.root, getString(R.string.no_cards_left), Snackbar.LENGTH_SHORT).show()
                }
                binding.infoTextView.text = resources.getQuantityString(
                    R.plurals.due_cards_format, studyLimit-studyCounter, studyLimit-studyCounter)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (!SettingsActivity.getBoard(requireContext())){
            binding.boardView.visibility = View.GONE
        } else {
            binding.boardView.visibility = View.VISIBLE
        }
        studyLimit = 0
        viewModel.updateLimit(SettingsActivity.getMaximumNumberOfCards(requireContext())!!)
    }
}