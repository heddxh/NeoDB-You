package day.vitayuzu.neodb.ui.library

import day.vitayuzu.neodb.ui.model.Mark
import day.vitayuzu.neodb.util.EntryType
import kotlinx.datetime.isoDayNumber

/**
 * Data for a day in heat map.
 * @param weekIndex: 0-based week index of this year
 * @param dayIndex: 0-based day index of this week
 * @param type: One implementation of [HeatMapBlockType]
 */
data class HeatMapDayData(
    val weekIndex: Int = 0,
    val dayIndex: Int = 0,
    val type: HeatMapBlockType = HeatMapBlockType.DefaultHeatMapEntry
) {
    constructor(
        weekIndex: Int,
        marks: List<Mark>
    ) : this(
        weekIndex = weekIndex,
        dayIndex = marks[0].date.dayOfWeek.isoDayNumber - 1, // isoDayNumber is 1-based
        type = when (marks.groupBy { it.entry.category }.size) {
            0 -> HeatMapBlockType.DefaultHeatMapEntry
            1 -> HeatMapBlockType.SingleHeatMapEntry(marks[0].entry.category)
            2 -> HeatMapBlockType.DoubleHeatMapEntry(
                marks[0].entry.category,
                marks[1].entry.category
            )

            3 -> HeatMapBlockType.TripleHeatMapEntry(
                marks[0].entry.category,
                marks[1].entry.category,
                marks[2].entry.category
            )

            else -> HeatMapBlockType.QuadrupleHeatMapEntry(
                marks[0].entry.category,
                marks[1].entry.category,
                marks[2].entry.category,
                marks[3].entry.category
            )
        }
    )
}

sealed interface HeatMapBlockType {
    data object DefaultHeatMapEntry : HeatMapBlockType
    data class SingleHeatMapEntry(val kind: EntryType) : HeatMapBlockType
    data class DoubleHeatMapEntry(
        val kind1: EntryType,
        val kind2: EntryType
    ) : HeatMapBlockType

    data class TripleHeatMapEntry(
        val kind1: EntryType,
        val kind2: EntryType,
        val kind3: EntryType
    ) : HeatMapBlockType

    data class QuadrupleHeatMapEntry(
        val kind1: EntryType,
        val kind2: EntryType,
        val kind3: EntryType,
        val kind4: EntryType
    ) : HeatMapBlockType
}
