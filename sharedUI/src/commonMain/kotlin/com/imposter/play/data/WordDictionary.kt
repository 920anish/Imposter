package com.imposter.play.data

data class Word(
    val real: String,
    val hint: String = "",
)

object WordDictionary {
    const val CATEGORY_RANDOM = "RANDOM"

    val words: Map<String, Map<Int, List<Word>>> = mapOf(
        "ANIMALS" to byDifficulty(
            easy = listOf(
                Word("Dog", "Pet"),
                Word("Cat", "Whiskers"),
                Word("Lion", "Mane"),
                Word("Elephant", "Trunk"),
                Word("Dolphin", "Ocean"),
            ),
            medium = listOf(
                Word("Kangaroo", "Pouch"),
                Word("Penguin", "Ice"),
                Word("Alligator", "Swamp"),
                Word("Octopus", "Tentacles"),
                Word("Peacock", "Feathers"),
            ),
            hard = listOf(
                Word("Axolotl", "Gills"),
                Word("Aardvark", "Ants"),
                Word("Narwhal", "Tusk"),
                Word("Platypus", "Duck bill"),
                Word("Chameleon", "Camouflage"),
            ),
        ),
        "FOOD" to byDifficulty(
            easy = listOf(
                Word("Pizza", "Slice"),
                Word("Burger", "Bun"),
                Word("Apple", "Fruit"),
                Word("Rice", "Grain"),
                Word("Cake", "Dessert"),
            ),
            medium = listOf(
                Word("Sushi", "Roll"),
                Word("Pancake", "Breakfast"),
                Word("Taco", "Shell"),
                Word("Lasagna", "Layers"),
                Word("Dumpling", "Steamed"),
            ),
            hard = listOf(
                Word("Kimchi", "Fermented"),
                Word("Gnocchi", "Potato"),
                Word("Ratatouille", "Vegetables"),
                Word("Bouillabaisse", "Seafood"),
                Word("Tiramisu", "Coffee"),
            ),
        ),
        "CITIES" to byDifficulty(
            easy = listOf(
                Word("Paris", "Eiffel"),
                Word("Tokyo", "Japan"),
                Word("London", "Big Ben"),
                Word("Dubai", "Skyscraper"),
                Word("Rome", "Colosseum"),
            ),
            medium = listOf(
                Word("Barcelona", "Gaudi"),
                Word("Toronto", "CN Tower"),
                Word("Sydney", "Opera"),
                Word("Istanbul", "Bosporus"),
                Word("Seoul", "Korea"),
            ),
            hard = listOf(
                Word("Reykjavik", "Iceland"),
                Word("Ulaanbaatar", "Mongolia"),
                Word("Guangzhou", "China"),
                Word("Valparaiso", "Hills"),
                Word("Lviv", "Ukraine"),
            ),
        ),
        "MOVIES" to byDifficulty(
            easy = listOf(
                Word("Titanic", "Ship"),
                Word("Avatar", "Blue"),
                Word("Jaws", "Shark"),
                Word("Frozen", "Snow"),
                Word("Inception", "Dream"),
            ),
            medium = listOf(
                Word("Interstellar", "Space"),
                Word("Gladiator", "Arena"),
                Word("Whiplash", "Drums"),
                Word("Shrek", "Ogre"),
                Word("Coco", "Music"),
            ),
            hard = listOf(
                Word("Memento", "Memory"),
                Word("Parasite", "Basement"),
                Word("Amelie", "Paris"),
                Word("Rashomon", "Perspective"),
                Word("Metropolis", "Silent"),
            ),
        ),
        "SPORTS" to byDifficulty(
            easy = listOf(
                Word("Football", "Goal"),
                Word("Cricket", "Bat"),
                Word("Tennis", "Racket"),
                Word("Basketball", "Hoop"),
                Word("Swimming", "Pool"),
            ),
            medium = listOf(
                Word("Baseball", "Home run"),
                Word("Hockey", "Puck"),
                Word("Badminton", "Shuttle"),
                Word("Volleyball", "Net"),
                Word("Boxing", "Ring"),
            ),
            hard = listOf(
                Word("Decathlon", "Ten"),
                Word("Biathlon", "Ski"),
                Word("Lacrosse", "Stick"),
                Word("Curling", "Ice"),
                Word("Fencing", "Foil"),
            ),
        ),
        "SCIENCE" to byDifficulty(
            easy = listOf(
                Word("Gravity", "Fall"),
                Word("Atom", "Tiny"),
                Word("Planet", "Orbit"),
                Word("Energy", "Power"),
                Word("Magnet", "Attract"),
            ),
            medium = listOf(
                Word("Molecule", "Bond"),
                Word("Neutron", "Nucleus"),
                Word("Photosynthesis", "Plants"),
                Word("Velocity", "Speed"),
                Word("Ecosystem", "Nature"),
            ),
            hard = listOf(
                Word("Entanglement", "Quantum"),
                Word("Thermodynamics", "Heat"),
                Word("Chromatography", "Separation"),
                Word("Superposition", "States"),
                Word("Homeostasis", "Balance"),
            ),
        ),
        "TECH" to byDifficulty(
            easy = listOf(
                Word("Laptop", "Portable"),
                Word("Keyboard", "Typing"),
                Word("Mouse", "Cursor"),
                Word("Browser", "Web"),
                Word("Password", "Login"),
            ),
            medium = listOf(
                Word("Database", "Tables"),
                Word("Compiler", "Code"),
                Word("Algorithm", "Steps"),
                Word("Firewall", "Security"),
                Word("Bluetooth", "Wireless"),
            ),
            hard = listOf(
                Word("Containerization", "Docker"),
                Word("Recursion", "Self-call"),
                Word("Polymorphism", "OOP"),
                Word("Asynchronous", "Concurrency"),
                Word("Virtualization", "VM"),
            ),
        ),
        "MUSIC" to byDifficulty(
            easy = listOf(
                Word("Guitar", "Strings"),
                Word("Piano", "Keys"),
                Word("Drums", "Beat"),
                Word("Singer", "Voice"),
                Word("Concert", "Live"),
            ),
            medium = listOf(
                Word("Melody", "Tune"),
                Word("Harmony", "Chords"),
                Word("Rhythm", "Pattern"),
                Word("Violin", "Bow"),
                Word("Microphone", "Stage"),
            ),
            hard = listOf(
                Word("Arpeggio", "Notes"),
                Word("Syncopation", "Off-beat"),
                Word("Crescendo", "Louder"),
                Word("Counterpoint", "Lines"),
                Word("Timbre", "Tone"),
            ),
        ),
        "GEOGRAPHY" to byDifficulty(
            easy = listOf(
                Word("Mountain", "Peak"),
                Word("River", "Flow"),
                Word("Desert", "Sand"),
                Word("Island", "Water"),
                Word("Forest", "Trees"),
            ),
            medium = listOf(
                Word("Peninsula", "Landform"),
                Word("Volcano", "Lava"),
                Word("Glacier", "Ice"),
                Word("Valley", "Lowland"),
                Word("Canyon", "Cliff"),
            ),
            hard = listOf(
                Word("Archipelago", "Many islands"),
                Word("Isthmus", "Narrow land"),
                Word("Tundra", "Cold plain"),
                Word("Plateau", "Flat highland"),
                Word("Estuary", "River mouth"),
            ),
        ),
        "POP_CULTURE" to byDifficulty(
            easy = listOf(
                Word("Superhero", "Cape"),
                Word("Meme", "Viral"),
                Word("TikTok", "Short video"),
                Word("Podcast", "Audio"),
                Word("Influencer", "Followers"),
            ),
            medium = listOf(
                Word("Cosplay", "Costume"),
                Word("Streaming", "Live"),
                Word("Fandom", "Fans"),
                Word("Anime", "Japan"),
                Word("Blockbuster", "Hit"),
            ),
            hard = listOf(
                Word("Cinematography", "Camera"),
                Word("Metaverse", "Virtual"),
                Word("Lore", "Backstory"),
                Word("Canon", "Official"),
                Word("Satire", "Mockery"),
            ),
        ),
        "HISTORY" to byDifficulty(
            easy = listOf(
                Word("Pyramid", "Egypt"),
                Word("Castle", "King"),
                Word("Empire", "Rule"),
                Word("Revolution", "Uprising"),
                Word("Monarchy", "Crown"),
            ),
            medium = listOf(
                Word("Renaissance", "Art"),
                Word("Industrial", "Factories"),
                Word("Treaty", "Agreement"),
                Word("Colonial", "Expansion"),
                Word("Dynasty", "Family rule"),
            ),
            hard = listOf(
                Word("Mesopotamia", "Cradle"),
                Word("Ottoman", "Sultan"),
                Word("Feudalism", "Lords"),
                Word("Bureaucracy", "Administration"),
                Word("Mercantilism", "Trade"),
            ),
        ),
        "DRINKS" to byDifficulty(
            easy = listOf(
                Word("Water", "Hydration"),
                Word("Tea", "Leaves"),
                Word("Coffee", "Caffeine"),
                Word("Juice", "Fruit"),
                Word("Milk", "Dairy"),
            ),
            medium = listOf(
                Word("Smoothie", "Blended"),
                Word("Lemonade", "Citrus"),
                Word("Espresso", "Shot"),
                Word("Cappuccino", "Foam"),
                Word("Mojito", "Mint"),
            ),
            hard = listOf(
                Word("Kombucha", "Fermented"),
                Word("Sangria", "Wine"),
                Word("Americano", "Diluted"),
                Word("Matcha", "Powder"),
                Word("Affogato", "Coffee ice cream"),
            ),
        ),
    )

    private fun byDifficulty(
        easy: List<Word>,
        medium: List<Word>,
        hard: List<Word>,
    ): Map<Int, List<Word>> = mapOf(
        0 to easy,
        1 to medium,
        2 to hard,
    )

    fun resolveCategory(category: String): String {
        val normalized = category.trim().uppercase()
        if (normalized == CATEGORY_RANDOM || normalized !in words.keys) {
            return words.keys.first()
        }
        return normalized
    }
}

class WordDeck(
    private val category: String,
    private val difficulty: Int,
) {
    private val random = kotlin.random.Random.Default
    private var remaining: MutableList<Word> = refill()

    fun nextWord(): Word {
        if (remaining.isEmpty()) {
            remaining = refill()
        }
        return remaining.removeFirst()
    }

    private fun refill(): MutableList<Word> {
        val safeDifficulty = difficulty.coerceIn(0, 2)
        val resolvedCategory = if (category == WordDictionary.CATEGORY_RANDOM) {
            WordDictionary.words.keys.random(random)
        } else {
            WordDictionary.resolveCategory(category)
        }
        return WordDictionary.words.getValue(resolvedCategory)
            .getValue(safeDifficulty)
            .toMutableList()
            .also { it.shuffle(random) }
    }
}

