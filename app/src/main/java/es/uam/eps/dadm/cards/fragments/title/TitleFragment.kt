package es.uam.eps.dadm.cards.fragments.title

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import es.uam.eps.dadm.cards.R
import es.uam.eps.dadm.cards.activities.login.EmailPasswordActivity
import es.uam.eps.dadm.cards.activities.settings.SettingsActivity
import es.uam.eps.dadm.cards.databinding.FragmentTitleBinding
import es.uam.eps.dadm.cards.views.DrawerLocker




class TitleFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentTitleBinding>(
            inflater,
            R.layout.fragment_title,
            container,
            false
        )

        (activity as DrawerLocker?)!!.setDrawerEnabled(false)

        binding.cardsTitleTextView.setOnClickListener {
            if (!SettingsActivity.getLoggedIn(requireContext())) {
                startActivity(Intent(context, EmailPasswordActivity::class.java))
            } else if (!SettingsActivity.getSkipTutorial(requireContext())) {
                SettingsActivity.setSkipTutorial(requireContext(), true)
                it.findNavController().navigate(R.id.action_titleFragment_to_tutorialFragment)
            } else {
                it.findNavController().navigate(R.id.action_titleFragment_to_studyFragment)
            }
        }

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        (activity as DrawerLocker?)!!.setDrawerEnabled(true)
    }
}