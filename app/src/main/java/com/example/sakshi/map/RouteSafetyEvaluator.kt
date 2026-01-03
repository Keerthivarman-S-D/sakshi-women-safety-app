package com.example.sakshi.map

object RouteSafetyEvaluator {

    fun calculateScore(tags: List<String>): Int {
        var score = 50 // neutral base

        if (tags.contains("police")) score += 20
        if (tags.contains("hospital")) score += 15

        val negative =
            tags.count { it == "bar" || it == "pub" || it == "alcohol" }

        score -= negative * 20

        return score.coerceIn(0, 100)
    }

    fun classify(score: Int): SafetyLevel =
        when {
            score >= 70 -> SafetyLevel.SAFE
            score >= 40 -> SafetyLevel.MODERATE
            else -> SafetyLevel.UNSAFE
        }
}

enum class SafetyLevel {
    SAFE, MODERATE, UNSAFE
}
