package es.uam.eps.dadm.cards.fragments.tutorial

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import es.uam.eps.dadm.cards.R

class TutorialFragment : Fragment() {
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tutorial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tutorialListBoard = listOf(
            TutorialBoard(R.color.white, getString(R.string.tutorial_welcome_title), getString(R.string.tutorial_welcome_description)),
            TutorialBoard(R.color.gray, getString(R.string.decks_nav_menu_title), getString(R.string.tutorial_decks_description)),
            TutorialBoard(R.color.white, getString(R.string.study_nav_menu_title), getString(R.string.tutorial_study_description)),
            TutorialBoard(R.color.gray, getString(R.string.statistics_nav_menu_title), getString(R.string.tutorial_statistics_description)),
            TutorialBoard(R.color.white, getString(R.string.tutorial_menu_title), getString(R.string.tutorial_menu_description)),
            TutorialBoard(R.color.gray, getString(R.string.about_nav_menu_title), getString(R.string.tutorial_about_description)),
            TutorialBoard(R.color.white, getString(R.string.tutorial_farewell_title), getString(R.string.tutorial_farewell_description))
        )
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = object : FragmentStateAdapter(this){
            override fun getItemCount(): Int = tutorialListBoard.size

            override fun createFragment(position: Int): Fragment {
                val fragment = TutorialPageFragment()
                fragment.arguments = Bundle().apply {
                    putInt(getString(R.string.arg_background), tutorialListBoard[position].background)
                    putString(getString(R.string.arg_title), tutorialListBoard[position].title)
                    putString(getString(R.string.arg_description), tutorialListBoard[position].description)
                }
                return fragment
            }
        }
    }
}

