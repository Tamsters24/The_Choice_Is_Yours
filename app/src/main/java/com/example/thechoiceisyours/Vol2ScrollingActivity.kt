package com.example.thechoiceisyours

import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.example.thechoiceisyours.databinding.ActivityVol2ScrollingBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.BufferedReader
import java.io.InputStreamReader

class Vol2ScrollingActivity : AppCompatActivity() {
    private val storyChapter = "Part 1"
    private val storyLines = mutableListOf<String>()   // list of words from sgb-words.txt

    private lateinit var binding: ActivityVol2ScrollingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVol2ScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title
        binding.fabOption1.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        getStory()
        currentChapter(storyChapter)
    }

    // Retrieve the Story from assets
    private fun getStory() {
        val inStream = assets.open("vol2_journey_under_the_sea.txt")

        // Read each line of the story
        val bufferedReader = BufferedReader(InputStreamReader(inStream))
        var line = bufferedReader.readLine()
        while (line != null) {
            storyLines.add(line)
            line = bufferedReader.readLine()
        }
        bufferedReader.close()
    }

    // Determine the current Chapter and Display Chapter contents
    private fun currentChapter(chapter: String) {
        val filteredLine = storyLines.filter { it.contains(chapter) }
        var storyPage = filteredLine[0]
        storyPage = storyPage.replace("\\n", "\n\n")


        val chapterDisplay = findViewById<TextView>(R.id.chapterContents)
        chapterDisplay.text = storyPage
    }
}