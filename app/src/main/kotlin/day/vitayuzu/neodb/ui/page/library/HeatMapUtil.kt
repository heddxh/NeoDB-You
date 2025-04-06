package day.vitayuzu.neodb.ui.page.library

import day.vitayuzu.neodb.ui.model.Mark
import day.vitayuzu.neodb.util.EntryType
import kotlinx.datetime.isoDayNumber

/**
 * Data for a day in heat map.
 * @param weekIndex: 0-based week index of this year
 * @param dayIndex: 0-based day index of this week (Monday = 0, Sunday = 6)
 * @param type: One of [HeatMapBlockType]
 */
data class HeatMapDayData(
    val weekIndex: Int = 0,
    val dayIndex: Int = 0,
    val type: HeatMapBlockType = HeatMapBlockType.None,
) {
    constructor(
        weekIndex: Int,
        marks: List<Mark>, // Assume marks is not empty
    ) : this(
        weekIndex = weekIndex,
        dayIndex =
            marks
                .first()
                .date.dayOfWeek.isoDayNumber - 1,
        // isoDayNumber is 1-based
        type = determineBlockType(marks),
    )

    companion object {
        private fun determineBlockType(marks: List<Mark>): HeatMapBlockType {
            val distinctCategories = marks.map { it.entry.category }.toSet()
            return when (distinctCategories.size) {
                1 -> HeatMapBlockType.Single(distinctCategories.first())
                2 ->
                    HeatMapBlockType.Double(
                        distinctCategories.take(2).component1(),
                        distinctCategories.take(2).component2(),
                    )

                3 ->
                    HeatMapBlockType.Triple(
                        distinctCategories.take(3).component1(),
                        distinctCategories.take(3).component2(),
                        distinctCategories.take(3).component3(),
                    )

                4 ->
                    HeatMapBlockType.Quadruple(
                        distinctCategories.take(4).component1(),
                        distinctCategories.take(4).component2(),
                        distinctCategories.take(4).component3(),
                        distinctCategories.take(4).component4(),
                    )

                else -> HeatMapBlockType.None
            }
        }
    }
}

sealed interface HeatMapBlockType {
    data object None : HeatMapBlockType

    data class Single(
        val kind: EntryType,
    ) : HeatMapBlockType

    data class Double(
        val kind1: EntryType,
        val kind2: EntryType,
    ) : HeatMapBlockType

    data class Triple(
        val kind1: EntryType,
        val kind2: EntryType,
        val kind3: EntryType,
    ) : HeatMapBlockType

    data class Quadruple(
        val kind1: EntryType,
        val kind2: EntryType,
        val kind3: EntryType,
        val kind4: EntryType,
    ) : HeatMapBlockType
}
