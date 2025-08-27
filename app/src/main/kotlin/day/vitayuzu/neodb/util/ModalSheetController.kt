package day.vitayuzu.neodb.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

class ModalSheetController {
    var status by mutableStateOf(ModalState.CLOSED)
}

val LocalModalSheetController = staticCompositionLocalOf<ModalSheetController> {
    error("No ModalSheetController provided")
}

enum class ModalState { CLOSED, EDIT, NEW, DES }
