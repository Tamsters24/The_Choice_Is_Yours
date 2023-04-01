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

    fun getNextChoices(currentChoice: String): List<String> {
        return when (currentChoice) {
            "1a" -> listOf("2a", "2b")
            "2a" -> listOf("3a", "3b")
            "2b" -> listOf("3c", "3d")
            "3a" -> listOf("4a", "4b")
            "3b" -> listOf("4c", "4d")
            "3c" -> listOf("4e", "4f")
            "3d" -> listOf("4g", "4h")
            "4a" -> listOf("5a", "5b")
            "4b" -> listOf("5c", "5d")
            "4c" -> listOf("THE END")
            "4d" -> listOf("THE END")
            "4e" -> listOf("5e", "5f")
            "4f" -> listOf("5g", "5h")
            "4g" -> listOf("5i", "5j")
            "4h" -> listOf("5k", "5l")
            "5a" -> listOf("THE END")
            "5b" -> listOf("6a", "6b")
            "5c" -> listOf("6c", "6d")
            "5d" -> listOf("THE END")
            "5e" -> listOf("2a")
            "5f" -> listOf("6e", "6f")
            "5g" -> listOf("THE END")
            "5h" -> listOf("6g", "6h")
            "5i" -> listOf("6i", "6j")
            "5j" -> listOf("THE END")
            "5k" -> listOf("THE END")
            "5l" -> listOf("6k", "6l")
            "6a" -> listOf("7a", "7b")
            "6b" -> listOf("6c")
            "6c" -> listOf("3d")
            "6d" -> listOf("THE END")
            "6e" -> listOf("7c", "7d")
            "6f" -> listOf("7e", "7f")
            "6g" -> listOf("7g", "7h")
            "6h" -> listOf("THE END")
            "6i" -> listOf("7i", "7j")
            "6j" -> listOf("7k", "7l")
            "6k" -> listOf("7m", "7n")
            "6l" -> listOf("2a")
            "7a" -> listOf("8a", "8b")
            "7b" -> listOf("5a")
            "7c" -> listOf("8c", "8d")
            "7d" -> listOf("8e", "8f")
            "7e" -> listOf("8g", "8h")
            "7f" -> listOf("8i", "8j")
            "7g" -> listOf("8k", "8l")
            "7h" -> listOf("8m", "8n")
            "7i" -> listOf("8o", "8p")
            "7j" -> listOf("8q", "8r")
            "7k" -> listOf("7l", "8t", "8u")
            "7l" -> listOf("THE END")
            "7m" -> listOf("THE END")
            "7n" -> listOf("THE END")
            "8a" -> listOf("9a", "9b")
            "8b" -> listOf("THE END")
            "8c" -> listOf("9c", "9d")
            "8d" -> listOf("THE END")
            "8e" -> listOf("9e", "9f")
            "8f" -> listOf("THE END")
            "8g" -> listOf("2a")
            "8h" -> listOf("THE END")
            "8i" -> listOf("9g", "9h")
            "8j" -> listOf("9i", "9j")
            "8k" -> listOf("THE END")
            "8l" -> listOf("THE END")
            "8m" -> listOf("THE END")
            "8n" -> listOf("7e")
            "8o" -> listOf("THE END")
            "8p" -> listOf("9k", "9l")
            "8q" -> listOf("9m", "9n")
            "8r" -> listOf("THE END")
            "8s" -> listOf("THE END")
            "8t" -> listOf("THE END")
            "9a" -> listOf("THE END")
            "9b" -> listOf("10a", "THE END")
            "9c" -> listOf("10b", "10c")
            "9d" -> listOf("10d", "10e")
            "9e" -> listOf("THE END")
            "9f" -> listOf("10f", "10g")
            "9g" -> listOf("THE END")
            "9h" -> listOf("THE END")
            "9i" -> listOf("THE END")
            "9j" -> listOf("7c")
            "9k" -> listOf("THE END")
            "9l" -> listOf("THE END")
            "9m" -> listOf("THE END")
            "9n" -> listOf("THE END")

            "10a" -> listOf("THE END")
            "10b" -> listOf("THE END")
            "10c" -> listOf("THE END")
            "10d" -> listOf("THE END")
            "10e" -> listOf("THE END")
            "10f" -> listOf("THE END")
            "10g" -> listOf("THE END")

            else -> emptyList()
        }
    }
}