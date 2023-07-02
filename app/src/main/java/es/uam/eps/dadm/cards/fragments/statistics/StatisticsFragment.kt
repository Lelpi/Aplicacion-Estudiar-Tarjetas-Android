package es.uam.eps.dadm.cards.fragments.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import es.uam.eps.dadm.cards.R
import es.uam.eps.dadm.cards.database.CardDatabase
import es.uam.eps.dadm.cards.databinding.FragmentStatisticsBinding
import es.uam.eps.dadm.cards.model.Card
import es.uam.eps.dadm.cards.model.CardType
import es.uam.eps.dadm.cards.model.Deck
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.Executors
import kotlin.random.Random


class StatisticsFragment : Fragment() {

    private lateinit var binding: FragmentStatisticsBinding
    private val weeklyStatistics = mutableListOf(0, 0, 0, 0)
    private val dailyStatistics = mutableListOf(0, 0, 0, 0, 0, 0, 0)
    private val currentDate: LocalDate = LocalDate.now()
    private val executor = Executors.newSingleThreadExecutor()
    private val viewModel by lazy {
        ViewModelProvider(this)[StatisticsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_statistics,
            container,
            false)

        binding.currentDate.text = getString(R.string.fragment_statistics_today, currentDate.toString())

        binding.tabLayout.getTabAt(viewModel.tabLayout.value!!)?.select()

        viewModel.cards.observe(viewLifecycleOwner){
            weeklyStatistics.fill(0)
            dailyStatistics.fill(0)
            it.forEach { card ->
                if (LocalDate.parse(card.nextPracticeDate.substring(0, 10))
                    in currentDate..currentDate.plusDays(6) || card.isDue(LocalDateTime.now())) {
                    weeklyStatistics[0] += 1
                } else if (LocalDate.parse(card.nextPracticeDate.substring(0, 10))
                    in currentDate.plusDays(7)..currentDate.plusDays(13)) {
                    weeklyStatistics[1] += 1
                } else if (LocalDate.parse(card.nextPracticeDate.substring(0, 10))
                    in currentDate.plusDays(14)..currentDate.plusDays(20)) {
                    weeklyStatistics[2] += 1
                } else if (LocalDate.parse(card.nextPracticeDate.substring(0, 10))
                    in currentDate.plusDays(21)..currentDate.plusDays(27)) {
                    weeklyStatistics[3] += 1
                }

                if (card.lastPracticeDates == ""){
                    return@forEach
                }
                card.lastPracticeDates.split(",").dropLast(1).forEach { date ->
                    if (currentDate.minusDays(6) == LocalDate.parse(date)) {
                        dailyStatistics[0] += 1
                    } else if (currentDate.minusDays(5) == LocalDate.parse(date)) {
                        dailyStatistics[1] += 1
                    } else if (currentDate.minusDays(4) == LocalDate.parse(date)) {
                        dailyStatistics[2] += 1
                    } else if (currentDate.minusDays(3) == LocalDate.parse(date)) {
                        dailyStatistics[3] += 1
                    } else if (currentDate.minusDays(2) == LocalDate.parse(date)) {
                        dailyStatistics[4] += 1
                    } else if (currentDate.minusDays(1) == LocalDate.parse(date)) {
                        dailyStatistics[5] += 1
                    } else if (currentDate == LocalDate.parse(date)) {
                        dailyStatistics[6] += 1
                    }
                }
            }
            generateChartData()
        }

        binding.generate.setOnClickListener {
            generateRandomCards()
        }

        binding.tabLayout.addOnTabSelectedListener(object: OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewModel.updateTabLayout(binding.tabLayout.selectedTabPosition)
                generateChartData()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        return binding.root
    }

    private fun generateChartData() {
        if (binding.tabLayout.selectedTabPosition == 0) {
            binding.chart.data = getBarData(getDailyDataSet(), getDailyXAxisValues())
        } else {
            binding.chart.data = getBarData(getWeeklyDataSet(), getWeeklyXAxisValues())
        }
        binding.chart.invalidate()
    }

    private fun getBarData(
        dataSet: BarDataSet,
        xAxisValues: MutableList<String>
    ): BarData {
        val barData = BarData(dataSet)
        barData.setValueTextSize(15f)
        val chart = binding.chart
        chart.description = null
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisValues)
        chart.xAxis.granularity = 1f
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.setDrawGridLines(false)
        chart.xAxis.textSize = 15f
        chart.xAxis.axisLineWidth = 3f
        chart.axisRight.isEnabled = false
        chart.axisLeft.setDrawGridLines(false)
        chart.axisLeft.axisMinimum = 0f
        chart.axisLeft.textSize = 15f
        chart.axisLeft.axisLineWidth = 3f
        chart.setFitBars(true)
        return barData
    }

    private fun getDailyDataSet(): BarDataSet {
        val entries = mutableListOf<BarEntry>()
        entries.add(BarEntry(0f, dailyStatistics[0].toFloat()))
        entries.add(BarEntry(1f, dailyStatistics[1].toFloat()))
        entries.add(BarEntry(2f, dailyStatistics[2].toFloat()))
        entries.add(BarEntry(3f, dailyStatistics[3].toFloat()))
        entries.add(BarEntry(4f, dailyStatistics[4].toFloat()))
        entries.add(BarEntry(5f, dailyStatistics[5].toFloat()))
        entries.add(BarEntry(6f, dailyStatistics[6].toFloat()))
        val barDataSet = BarDataSet(entries, getString(R.string.fragment_statistics_daily))
        barDataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
        barDataSet.valueFormatter = DefaultValueFormatter(0)
        return barDataSet
    }

    private fun getDailyXAxisValues(): MutableList<String> {
        val xAxis = mutableListOf<String>()
        xAxis.add(LocalDate.now().minusDays(6).toString().substring(5, 10))
        xAxis.add(LocalDate.now().minusDays(5).toString().substring(5, 10))
        xAxis.add(LocalDate.now().minusDays(4).toString().substring(5, 10))
        xAxis.add(LocalDate.now().minusDays(3).toString().substring(5, 10))
        xAxis.add(LocalDate.now().minusDays(2).toString().substring(5, 10))
        xAxis.add(LocalDate.now().minusDays(1).toString().substring(5, 10))
        xAxis.add(LocalDate.now().toString().substring(5, 10))
        return xAxis
    }

    private fun getWeeklyDataSet(): BarDataSet {
        val entries = mutableListOf<BarEntry>()
        entries.add(BarEntry(0f, weeklyStatistics[0].toFloat()))
        entries.add(BarEntry(1f, weeklyStatistics[1].toFloat()))
        entries.add(BarEntry(2f, weeklyStatistics[2].toFloat()))
        entries.add(BarEntry(3f, weeklyStatistics[3].toFloat()))
        val barDataSet = BarDataSet(entries, getString(R.string.fragment_statistics_weekly))
        barDataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
        barDataSet.valueFormatter = DefaultValueFormatter(0)
        return barDataSet
    }

    private fun getWeeklyXAxisValues(): MutableList<String> {
        val xAxis = mutableListOf<String>()
        xAxis.add(getString(R.string.between_0_6_days))
        xAxis.add(getString(R.string.between_7_13_days))
        xAxis.add(getString(R.string.between_14_20_days))
        xAxis.add(getString(R.string.between_21_27_days))
        return xAxis
    }

    private fun generateRandomCards() {
        val deck = Deck("Random")
        executor.execute {
            CardDatabase.getInstance(requireContext()).cardDao.add(deck)
            for (i in 1..100){
                CardDatabase.getInstance(requireContext()).cardDao.add(
                    Card(
                    i.toString(),
                    i.toString(),
                    deck.id,
                    nextPracticeDate = LocalDateTime.now().plusDays(Random.nextInt(28).toLong()).toString(),
                    lastPracticeDates = LocalDate.now().minusDays(Random.nextInt(7).toLong()).toString() + ",",
                    type = CardType.MATURE.toString(),
                    timesCorrect = i,
                    timesStudied = i+1
                )
                )
            }
        }
    }
}