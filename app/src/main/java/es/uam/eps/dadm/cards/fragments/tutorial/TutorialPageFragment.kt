package es.uam.eps.dadm.cards.fragments.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import es.uam.eps.dadm.cards.R
import es.uam.eps.dadm.cards.databinding.FragmentTutorialPageBinding

class TutorialPageFragment: Fragment() {

    private lateinit var binding: FragmentTutorialPageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_tutorial_page,
            container,
            false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(getString(R.string.arg_background)) && it.containsKey(getString(
            R.string.arg_title)) && it.containsKey(getString(R.string.arg_description))}?.apply {
            binding.background.setBackgroundColor(resources.getColor(getInt(getString(R.string.arg_background))))
            binding.title.text = getString(getString(R.string.arg_title))
            binding.description.text = getString(getString(R.string.arg_description))
        }
    }
}