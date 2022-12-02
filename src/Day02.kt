fun main() {
    val input = readInput("Day02")
    val rounds = input.map(::mapRound)
    val scores = rounds.map(::roundScore)

    println("Total score for following guide is ${scores.sum()}")

    val outcomeRound = input.map(::mapOutcomeRound)
    val outcomeScores = outcomeRound.map(::outcomeRoundScore)

    println("Total score for following outcomes is ${outcomeScores.sum()}")
}

fun outcomeRoundScore(round: OutcomeRound): Int {
    return (round.outcome.score + handFromOutcome(round.opponent, round.outcome).also {
        println("$round gives outcome $it")
    }.score).also {
        println("$round gives score $it")
    }
}

fun handFromOutcome(opponent: Hand, outcome: Outcome): Hand {
    return when (outcome) {
        Outcome.Win -> opponent.losesTo()
        Outcome.Draw -> opponent
        Outcome.Loss -> opponent.winsAgainst()
    }
}


fun roundScore(round: Round): Int {
    return round.you.score + handOutcome(round.you, round.opponent).also {
//        println("Round $round gives outcome $it")
    }.score
}

fun handOutcome(you: Hand, opponent: Hand): Outcome {
    if (you == opponent) return Outcome.Draw

    val isWin = when (you) {
        Hand.Rock -> opponent == Hand.Scissor
        Hand.Paper -> opponent == Hand.Rock
        Hand.Scissor -> opponent == Hand.Paper
    }

    return if (isWin) {
        Outcome.Win
    } else {
        Outcome.Loss
    }
}

data class OutcomeRound(
    val opponent: Hand,
    val outcome: Outcome
)

data class Round(
    val opponent: Hand,
    val you: Hand
)

enum class Hand(val score: Int) {
    Rock(1), Paper(2), Scissor(3);

    fun losesTo() = when (this) {
        Rock -> Paper
        Paper -> Scissor
        Scissor -> Rock
    }

    fun winsAgainst() = when (this) {
        Rock -> Scissor
        Paper -> Rock
        Scissor -> Paper
    }
}


enum class Outcome(val score: Int) {
    Win(6), Draw(3), Loss(0)
}

fun mapOutcomeRound(round: String): OutcomeRound {
    val (p1, outcome) = round.split(" ")
    return OutcomeRound(mapHand(p1), mapOutcome(outcome))
}

fun mapRound(round: String): Round {
    val (p1, p2) = round.split(" ")
    return Round(mapHand(p1), mapHand(p2))
}

fun mapOutcome(hand: String) = when (hand) {
    "X" -> Outcome.Loss
    "Y" -> Outcome.Draw
    "Z" -> Outcome.Win
    else -> TODO()
}

fun mapHand(hand: String): Hand {
    return when (hand) {
        "A", "X" -> Hand.Rock
        "B", "Y" -> Hand.Paper
        "C", "Z" -> Hand.Scissor
        else -> throw IllegalArgumentException("$hand is not a legal input")
    }
}
