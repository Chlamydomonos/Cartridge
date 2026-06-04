package xyz.chlamydomonos.cartridge.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.resources.Identifier
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.jvm.optionals.getOrNull

object SkinUtil {
    private val cache = ConcurrentHashMap<UUID, Identifier>()
    private val pendingRequests = ConcurrentHashMap.newKeySet<UUID>()

    suspend fun getSkinAsync(uuid: UUID): Identifier {
        try {
            val services = Minecraft.getInstance().services()
            var profile = services.profileResolver.fetchById(uuid).getOrNull()
            if (profile == null) {
                profile = withContext(Dispatchers.IO) {
                    services.sessionService.fetchProfile(uuid, false)?.profile
                } ?: throw Exception()
            }

            val skin = withContext(Dispatchers.IO) {
                val skinFuture = Minecraft.getInstance().skinManager.get(profile)
                skinFuture.get().getOrNull()
            } ?: throw Exception()

            return skin.body.texturePath()
        } catch (_: Exception) {
            return DefaultPlayerSkin.get(uuid).body.texturePath()
        }
    }

    fun getSkin(uuid: UUID, scope: CoroutineScope): Identifier {
        val cached = cache[uuid]
        if (cached != null) {
            return cached
        }

        if (pendingRequests.add(uuid)) {
            scope.launch {
                cache[uuid] = getSkinAsync(uuid)
                pendingRequests.remove(uuid)
            }
        }

        return DefaultPlayerSkin.get(uuid).body.texturePath()
    }
}