package utils

enum class MazeTile(val char: Char) {
    EMPTY('.'),
    WALL('#'),
    START('S'),
    END('E');

    companion object {
        fun find(char: Char, def: MazeTile = EMPTY): MazeTile {
            for (entry in entries) {
                if(entry.char == char){
                    return entry
                }
            }

            return def
        }
    }
}