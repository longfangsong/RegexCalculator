package regex

abstract class Character(private val name: String) : RegexComponent, Comparable<Character> {
    override fun toString(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        return other is Character && name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun compareTo(other: Character): Int {
        if (other::class != this::class) {
            throw UnsupportedOperationException("Cannot compare two different types of character!!")
        }
        return name.compareTo(other.name)
    }
}