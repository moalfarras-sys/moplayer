package tv.moplayer.domain.contracts

import kotlinx.coroutines.flow.Flow
import tv.moplayer.domain.model.AppEvent

interface EventBus {
    fun publish(event: AppEvent)
    fun events(): Flow<AppEvent>
}