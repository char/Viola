package codes.som.anthony.viola

interface InputState<T> {
    fun advance(): Pair<T, InputState<T>?>
}
