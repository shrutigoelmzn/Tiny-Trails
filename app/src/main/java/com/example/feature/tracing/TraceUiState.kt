package com.example.feature.tracing

/**
 * Explicit state machine for the Tracing Screen workflow.
 *
 * Input is strictly accepted ONLY in:
 * - [ReadyToTrace]
 * - [Tracing]
 *
 * Input is strictly blocked in:
 * - [Loading]
 * - [Demo]
 * - [Completed]
 */
sealed interface TraceUiState {
    data object Loading : TraceUiState
    data object Demo : TraceUiState
    data object ReadyToTrace : TraceUiState
    data object Tracing : TraceUiState
    data object Completed : TraceUiState
}

val TraceUiState.isInputAllowed: Boolean
    get() = this is TraceUiState.ReadyToTrace || this is TraceUiState.Tracing
