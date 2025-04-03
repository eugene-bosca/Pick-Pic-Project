import com.bmexcs.pickpic.data.sources.ImageDataSource
import com.bmexcs.pickpic.data.sources.RealDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CacheImageProxy(
    @RealDataSource private val realImageDataSource: ImageDataSource
) : ImageDataSource {
    private val _cache = MutableStateFlow<Map<String, ByteArray>>(emptyMap())
    private val cache: StateFlow<Map<String, ByteArray>> = _cache.asStateFlow()

    override suspend fun getImage(eventId: String, imageId: String, token: String): ByteArray? {
        // Return cached image if available
        cache.value[imageId]?.let {
            return it
        }

        // Fetch from network and update cache
        val byteArray = realImageDataSource.getImage(eventId, imageId, token)
        byteArray?.let {
            _cache.update { currentCache -> currentCache + (imageId to it) }
        }
        return byteArray
    }

    override suspend fun deleteImage(eventId: String, imageId: String, token: String) {
        // Delete from network and invalidate cache
        realImageDataSource.deleteImage(eventId, imageId, token)
        _cache.update { currentCache -> currentCache - imageId }
    }

    override fun clearCache() {
        _cache.value = emptyMap()
    }

    // Forward addImage to the network (no caching needed here)
    override suspend fun addImage(eventId: String, imageData: ByteArray, token: String) {
        realImageDataSource.addImage(eventId, imageData, token)
    }
}