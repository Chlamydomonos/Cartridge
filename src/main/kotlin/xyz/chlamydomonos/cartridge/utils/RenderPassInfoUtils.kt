package xyz.chlamydomonos.cartridge.utils

import com.geckolib.constant.dataticket.DataTicket
import com.geckolib.renderer.base.RenderPassInfo

fun <T: Any> RenderPassInfo<*>.forData(ticket: DataTicket<T>): T {
    return getGeckolibData(ticket) ?: throw RuntimeException("Missing data ticket")
}