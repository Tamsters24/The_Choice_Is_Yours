package com.example.thechoiceisyours

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thechoiceisyours.databinding.ActivityVol2ScrollingBinding
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class Vol2ScrollingActivity : AppCompatActivity() {
    private var currentPart = 1
    private var nextPart = 2
    private val storyLines = mutableListOf<String>()
    private var currentChoice = "a"
    private var chapterImageString = ""

    private lateinit var binding: ActivityVol2ScrollingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVol2ScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title
        findViewById<ImageButton>(R.id.option1).setOnClickListener {
            currentPart += 1
            nextPart = currentPart + 1
            currentChoice = "a"
            currentChapter("Part.$currentPart$currentChoice")
            Toast.makeText(baseContext, "Part.$currentPart$currentChoice", Toast.LENGTH_SHORT).show()
            Toast.makeText(baseContext, "Choice.$currentPart$currentChoice.$nextPart", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageButton>(R.id.option2).setOnClickListener {
            currentPart += 1
            nextPart = currentPart + 1
            currentChoice = "b"
            //currentChapter("Part.$currentPart$currentChoice")
            Toast.makeText(baseContext, "Part.$currentPart$currentChoice", Toast.LENGTH_SHORT).show()
            Toast.makeText(baseContext, "Choice.$currentPart$currentChoice.$nextPart", Toast.LENGTH_SHORT).show()
        }

        getStory()
        currentChapter("Part.$currentPart$currentChoice")
        Toast.makeText(baseContext, "Part.$currentPart$currentChoice", Toast.LENGTH_SHORT).show()
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

    // Determine the current Chapter and Display Chapter contents and image
    private fun currentChapter(chapter: String) {
        // Current chapter
        val filteredChapter = storyLines.filter { it.contains(chapter) }
        var storyChapter = filteredChapter[0]
        storyChapter = storyChapter.substring(9)
        storyChapter = storyChapter.replace("\\n", "\n\n")

        // Choice A text
        var choice1Filter = "Choice.$currentPart$currentChoice.$nextPart"
        choice1Filter += "a"
        val filteredOption1 = storyLines.filter { it.contains(choice1Filter) }
        var option1 = filteredOption1[0]
        option1 = option1.substring(13)
        option1 = "Choice 1: $option1"

        // Choice B text
        var choice2Filter = "Choice.$currentPart$currentChoice.$nextPart"
        choice2Filter += "b"
        val filteredOption2 = storyLines.filter { it.contains(choice2Filter) }
        var option2 = filteredOption2[0]
        option2 = option2.substring(13)
        option2 = "Choice 2: $option2"

        // Current Chapter image
        val filteredImage = storyLines.filter { it.contains("p$currentPart$currentChoice") }
        chapterImageString = filteredImage[0]
        chapterImageString = chapterImageString.substring(11)

        // Display text
        val chapterDisplay = findViewById<TextView>(R.id.chapterContents)
        val storyPage = "$storyChapter\n\n$option1\n\n$option2\n\n\n"
        chapterDisplay.text = storyPage

        // Display image
        val imageInputStream: InputStream = assets.open(chapterImageString)
        val image = Drawable.createFromStream(imageInputStream, null)
        val partImage = findViewById<ImageView>(R.id.pageImage)
        partImage.setImageDrawable(image)
    }
}