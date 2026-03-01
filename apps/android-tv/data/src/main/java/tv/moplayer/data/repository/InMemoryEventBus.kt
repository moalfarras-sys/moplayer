package tv.moplayer.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import tv.moplayer.domain.contracts.EventBus
import tv.moplayer.domain.model.AppEvent

class InMemoryEventBus : EventBus {
    private val stream = MutableSharedFlow<AppEvent>(extraBufferCapacity = 64)

    override fun publish(event: AppEvent) {
        stream.tryEmit(event)
    }

    override fun events(): Flow<AppEvent> = stream
}