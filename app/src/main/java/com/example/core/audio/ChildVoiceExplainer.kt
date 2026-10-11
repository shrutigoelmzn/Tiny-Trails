package com.example.core.audio

import com.example.data.model.CategoryType
import com.example.data.model.CharacterContent

/**
 * Provides warm, expressive, child-like explanations and prompts tailored
 * specifically for toddler and preschool learners.
 *
 * Designed with playful sound words (onomatopoeia), cheerful questions,
 * gentle step-by-step guidance, and enthusiastic praise.
 */
object ChildVoiceExplainer {

    fun explainCharacter(character: CharacterContent, isHindi: Boolean = false): String {
        val display = character.displayValue.trim()
        val example = character.exampleWord.trim()
        val emoji = character.exampleEmoji

        if (isHindi) {
            return when (character.category) {
                CategoryType.NUMBERS -> "अरे वाह! यह है नंबर $display! चलो साथ मिलकर $display बनाते हैं!"
                CategoryType.SHAPES -> "देखो! यह है सुंदर आकार $example! चलो उंगली से बनाएं!"
                else -> "अरे वाह! यह है $display! $display से $example! चलो इसे सुंदर बनाएं!"
            }
        }

        // 1. Shapes & Pre-Writing Stroke Patterns
        if (character.category == CategoryType.SHAPES || character.id.startsWith("stroke_") || character.id.startsWith("shape_")) {
            return when (character.id) {
                "stroke_h_line" -> "Look! A straight line! Like a little train track, zoom zoom from left to right! Let's slide our finger across!"
                "stroke_v_line" -> "A tall line! Tall like a big green tree! Whoosh, slide from the top all the way down to the bottom!"
                "stroke_zigzag" -> "Zigzag trail! Zippy zap, like a lightning bolt! Up and down, and up and down, wheee!"
                "stroke_wave" -> "Ocean wave! Swish and splash, like a happy little dolphin jumping in the water! Wheee!"
                "shape_circle" -> "A circle! Round and round like a yummy cookie! Yum yum, no corners at all!"
                "shape_square" -> "A square! Four straight sides, just like a cozy present box! One, two, three, four!"
                "shape_triangle" -> "A triangle! Pointy at the top, like a yummy slice of pizza! Yum yum, three corners!"
                "shape_star" -> "A sparkly star! Twinkle twinkle little star, shining so bright in the sky! Let's trace the points!"
                "shape_heart" -> "A sweet heart! Thump thump, sending you a warm hug and lots of love! Round the hills and down to a point!"
                else -> "Look! It's a fun shape, $example! Let's trace the lines together!"
            }
        }

        // 2. Numbers 0-9
        if (character.category == CategoryType.NUMBERS || display.toIntOrNull() != null) {
            return when (display) {
                "0" -> "Look! Number zero! Round like an empty doughnut! Zero means none at all! Around we go!"
                "1" -> "Look! Number one! One bright shiny sun up in the sky! Trace down from top to bottom, wheee!"
                "2" -> "Quack quack! Number two! Two happy duckies swimming in the pond! Curve around and slide right across!"
                "3" -> "Yippee! Number three! Three yummy cupcakes! Around a tree, around a tree, that's how we make a three!"
                "4" -> "Beep beep! Number four! Four wheels on a speedy red car! Down and over, and down once more!"
                "5" -> "High five, friend! Number five! Five little fingers waving hello! Down and around, and put a little hat on top!"
                "6" -> "Look! Number six! Six pretty ladybugs crawling on a green leaf! Down we go and loop in a circle!"
                "7" -> "Number seven! Seven pretty colors in the rainbow! Slide across the sky and zip down like a slide!"
                "8" -> "Number eight! Eight wiggly legs on a friendly octopus! Make an S and do not wait, race back up to make an eight!"
                "9" -> "Number nine! Nine colorful balloons floating high! Make a loop and line so fine, that makes a number nine!"
                else -> "Look! Number $display! Let's count together and trace number $display!"
            }
        }

        // 3. Uppercase Letters
        if (character.category == CategoryType.ALPHABET_UPPER) {
            return when (display.uppercase()) {
                "A" -> "Yay! Big letter A! A says ah, like a yummy red apple! Crunch crunch! Up, down, and across!"
                "B" -> "Look, look! Big letter B! B is for bouncy ball! Boing boing! Down, and two big round bumps!"
                "C" -> "Meow meow! Letter C is for cute cuddly cat! Purr purr! Let's trace a big round curve!"
                "D" -> "Stomp stomp! Big letter D! D is for dinosaur! Roar! Straight down, and a giant tummy!"
                "E" -> "Toot toot! Letter E is for elephant! Big friendly elephant! Down, top, middle, and bottom!"
                "F" -> "Splish splash! Letter F is for little swimming fish! Swish swish through the water!"
                "G" -> "Look! Letter G is for tall tall giraffe, reaching up to the trees! Round we go and step inside!"
                "H" -> "Gallop gallop! Letter H is for happy horse! Neigh neigh! Two tall poles and a bridge across!"
                "I" -> "Brrr, so cool! Letter I is for cozy little igloo! Straight line down from top to bottom!"
                "J" -> "Giggle giggle! Letter J is for sweet jellyfish floating in the sea! Down and curve up!"
                "K" -> "Flutter flutter! Letter K is for colorful kite flying high in the blue sky!"
                "L" -> "Hear him roar! Letter L is for brave lion! Down and slide right across the floor!"
                "M" -> "Ooh ooh aah aah! Letter M is for silly monkey jumping on the bed! Up, down, up, and down!"
                "N" -> "Chirp chirp! Letter N is for cozy little bird nest! Down, slant, and straight back up!"
                "O" -> "Hoot hoot! Letter O is for wise little owl! Round and round, just like a big shiny circle!"
                "P" -> "Oink oink! Letter P is for cute little piggy! Straight down and a round little head!"
                "Q" -> "So sparkly! Letter Q is for lovely queen with a golden crown! Round like an O with a little walking stick!"
                "R" -> "Hop hop hop! Letter R is for fluffy bunny rabbit with wiggly ears! Down, curve, and slide out!"
                "S" -> "Warm and bright! Letter S is for happy smiling sun! Slither like a friendly snake, sss!"
                "T" -> "Growl! Letter T is for speedy tiger with stripes! Tall line down and a wide hat on top!"
                "U" -> "Pitter patter! Letter U is for rainbow umbrella keeping us dry! Down, under, and back up!"
                "V" -> "Vroom vroom! Letter V is for speedy little van! Slant down and slant straight up!"
                "W" -> "Whoosh! Letter W is for giant friendly whale swimming in the ocean! Down, up, down, up, wheee!"
                "X" -> "Ting ting ting! Letter X is for musical xylophone! Slant cross and slant cross, x marks the spot!"
                "Y" -> "Spin spin spin! Letter Y is for super fun yo-yo dancing on a string!"
                "Z" -> "Zoom zoom! Letter Z is for happy zebra with black and white stripes! Zag, zig, and zag across!"
                else -> "Look! It's letter $display! $display is for $example! Let's trace it together!"
            }
        }

        // 4. Lowercase Letters
        if (character.category == CategoryType.ALPHABET_LOWER) {
            val upper = display.uppercase()
            return when (upper) {
                "A" -> "Yay! Little letter a! A is for tiny busy ant, crawling on a leaf! Around in a circle and straight down!"
                "B" -> "Buzz buzz! Little letter b! B is for busy bumblebee! Tall line down and a round little belly!"
                "C" -> "Purr purr! Little letter c! C is for sweet cuddly kitten! Just a cozy little curve!"
                "D" -> "Woof woof! Little letter d! D is for happy playful puppy dog! Round tummy first, then a tall line up!"
                else -> "Look! Little letter $display! Little $display is for $example! Let's trace little $display together!"
            }
        }

        // Fallback default
        return "Hi friend! Look, it's $display! $display is for $example! Let's trace $display together!"
    }

    fun getPromptText(key: AudioPromptKey, isHindi: Boolean = false): String {
        if (isHindi) {
            return when (key) {
                AudioPromptKey.WATCH_DEMO -> "देखो देखो! मुझे बनाते हुए देखो!"
                AudioPromptKey.NOW_YOUR_TURN -> "शाबाश! अब तुम्हारी बारी है!"
                AudioPromptKey.START_TRACING -> "चलो उंगली से रेखा बनाओ!"
                AudioPromptKey.GREAT_JOB -> "वाह! बहुत अच्छा किया!"
                AudioPromptKey.SUPERSTAR -> "शाबाश! तुम तो सुपरस्टार हो!"
                AudioPromptKey.TRY_AGAIN -> "कोई बात नहीं! चलो फिर से कोशिश करते हैं!"
                AudioPromptKey.ACTIVITY_COMPLETE -> "अरे वाह! हमने पूरा कर लिया!"
                AudioPromptKey.COUNT_OBJECTS -> "चलो गिनते हैं! एक, दो, तीन!"
                AudioPromptKey.POP_BALLOONS -> "गुब्बारे फोड़ो! फट फट!"
                AudioPromptKey.FIND_OBJECT -> "कहाँ छुपा है? ढूँढो तो जानें!"
                AudioPromptKey.MATCH_CARDS -> "एक जैसी तस्वीरें मिलाओ!"
                AudioPromptKey.FEED_ANIMAL -> "यम यम! चलो प्यारे जानवर को खाना खिलाएं!"
                AudioPromptKey.TAP_CORRECT -> "सही वाले को छुओ!"
                AudioPromptKey.GARDEN_WELCOME -> "हमारे सुंदर बगीचे में स्वागत है!"
                AudioPromptKey.STICKER_COLLECTION -> "स्टिकर को छूकर उसका नाम सुनो!"
            }
        }

        return when (key) {
            AudioPromptKey.WATCH_DEMO -> "Look, look! Watch my magic sparkle finger trace! Wheee!"
            AudioPromptKey.NOW_YOUR_TURN -> "Yay! Now it's your turn, little artist! Follow the sparkly trail!"
            AudioPromptKey.START_TRACING -> "Put your finger on the green star and trace with me!"
            AudioPromptKey.GREAT_JOB -> "Yay! You did it! High five, superstar!"
            AudioPromptKey.SUPERSTAR -> "Yippee! Superstar! Look at that beautiful tracing!"
            AudioPromptKey.TRY_AGAIN -> "Oopsie daisy! That's okay, let's try again together! We can do it!"
            AudioPromptKey.ACTIVITY_COMPLETE -> "Hooray! We finished! You are so super smart!"
            AudioPromptKey.COUNT_OBJECTS -> "Let's tap and count together! One, two, three, count with me!"
            AudioPromptKey.POP_BALLOONS -> "Pop the balloons! Pop, pop, pop, so much bubbly fun!"
            AudioPromptKey.FIND_OBJECT -> "Where is it hiding? Can you spot it with your eagle eyes?"
            AudioPromptKey.MATCH_CARDS -> "Match the cute pictures! Peek-a-boo, let's find the match!"
            AudioPromptKey.FEED_ANIMAL -> "Yum yum yum! The little animal is hungry! Let's feed them yummy treats!"
            AudioPromptKey.TAP_CORRECT -> "Can you find the right one? Give it a tap, tap, tap!"
            AudioPromptKey.GARDEN_WELCOME -> "Welcome to our secret magical garden! Every time we trace, colorful flowers pop up and butterflies flutter!"
            AudioPromptKey.STICKER_COLLECTION -> "Tap any sticker to hear its fun name!"
        }
    }

    fun getCelebratoryPraise(
        displayValue: String,
        stars: Int = 3,
        exampleWord: String? = null,
        isHindi: Boolean = false
    ): String {
        val cleanVal = displayValue.trim()
        val withExample = if (!exampleWord.isNullOrBlank()) " as in $exampleWord" else ""

        if (isHindi) {
            return if (stars >= 3) {
                if (cleanVal.isNotEmpty()) {
                    listOf(
                        "शाबाश! पूरे तीन सितारे! तुमने $cleanVal बहुत सुंदर बनाया!",
                        "वाह! तुमने $cleanVal बना कर कमाल कर दिया! तीन सितारे मिले!",
                        "अरे वाह! $cleanVal बहुत बढ़िया बनाया! शाबाश!"
                    ).random()
                } else {
                    listOf(
                        "शाबाश! पूरे तीन सितारे! बहुत सुंदर बनाया!",
                        "वाह! कमाल कर दिया! तीन सितारे मिले!",
                        "अरे वाह! बहुत बढ़िया! शाबाश!"
                    ).random()
                }
            } else {
                if (cleanVal.isNotEmpty()) {
                    listOf(
                        "शाबाश! $cleanVal का बहुत बढ़िया प्रयास!",
                        "वाह! तुमने $cleanVal बहुत अच्छा बनाया!"
                    ).random()
                } else {
                    listOf(
                        "शाबाश! बहुत बढ़िया प्रयास!",
                        "वाह! बहुत अच्छा बनाया!"
                    ).random()
                }
            }
        }

        return if (stars >= 3) {
            if (cleanVal.isNotEmpty()) {
                listOf(
                    "Yippee! Three shiny golden stars for $cleanVal! You are a superstar tracer!",
                    "Hooray! Look at that, you traced $cleanVal! High five, little artist!",
                    "Woohoo! Tracing $cleanVal looks so super-duper amazing! Three shiny stars for you!",
                    "Yay! Fantastic job tracing $cleanVal$withExample! Give me a high five!",
                    "Awesome! Look how pretty your $cleanVal is! You did it!"
                ).random()
            } else {
                listOf(
                    "Yippee! Three shiny golden stars! You're a superstar!",
                    "Hooray! Fantastic job! High five, little artist!",
                    "Woohoo! Look at that, you did it! So proud of you!"
                ).random()
            }
        } else {
            if (cleanVal.isNotEmpty()) {
                listOf(
                    "Yay! Good tracing on $cleanVal! Keep practicing, you're doing so good!",
                    "Hooray! Great try on $cleanVal! Let's trace it again for three shiny stars!",
                    "Well done! You made $cleanVal! High five!"
                ).random()
            } else {
                "Yay! Good try, friend! Let's keep playing together!"
            }
        }
    }

    fun getBadgeExplanation(title: String, isUnlocked: Boolean): String {
        return if (isUnlocked) {
            "Hooray! You unlocked the $title badge! You did so much awesome tracing! High five!"
        } else {
            "Ooh, a shiny mystery badge! Keep tracing letters and numbers with me and we'll unlock it together!"
        }
    }

    fun getProgressSummaryAudio(totalStars: Int): String {
        return "Look at all our shiny stars! We collected $totalStars stars! You are doing so super great, little superstar!"
    }

    fun getGardenWelcomeAudio(): String {
        return "Welcome to our sunny secret garden! Every time we trace together, pretty flowers bloom and butterflies dance! Let's help them grow!"
    }

    fun getCertificateAudio(childName: String = "superstar"): String {
        return "Look at your shiny diploma certificate! You are a super smart scholar! Let's show your family!"
    }

    fun getColoringStudioAudio(): String {
        return "Welcome to the magical coloring studio! Pick your favorite rainbow color and let's draw with sparkly glitter!"
    }

    fun getPetReaction(action: String): String {
        return when (action.lowercase()) {
            "tickle" -> listOf(
                "Tee-hee! That tickles my tummy!",
                "Giggle giggle! Pip loves playtime!",
                "Tee-hee! You're making me laugh!"
            ).random()
            "feed" -> listOf(
                "Yum yum yum! Thank you for the yummy star treat! Nom nom!",
                "Mmm, so delicious! Pip loves shiny stars!",
                "Nom nom nom! That was super yummy!"
            ).random()
            "dance" -> listOf(
                "Yay, let's dance! Wiggle wiggle wiggle!",
                "Look at my dino wiggle dance! Wheee!",
                "Dancing is so much fun! Yay!"
            ).random()
            else -> "Pip loves playing with you! You are my best friend!"
        }
    }
}
