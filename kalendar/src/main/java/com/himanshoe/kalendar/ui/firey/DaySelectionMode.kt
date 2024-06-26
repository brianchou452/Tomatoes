/*
 * Copyright 2023 Kalendar Contributors (https://www.himanshoe.com). All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.himanshoe.kalendar.ui.firey

/**
 * Sealed interface representing the day selection mode in a Kalendar.
 */
sealed interface DaySelectionMode {
    /**
     * Represents the single day selection mode.
     */
    data object Single : DaySelectionMode

    /**
     * Represents the range selection mode.
     */
    data object Range : DaySelectionMode
}

/**
 * Sealed interface representing the range selection errors in a Kalendar.
 */
sealed interface RangeSelectionError {
    /**
     * Represents the error when the end date is before the start date in a range selection.
     */
    data object EndIsBeforeStart : RangeSelectionError
}
