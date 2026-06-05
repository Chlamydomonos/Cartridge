package xyz.chlamydomonos.cartridge.maid

import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager

@LittleMaidExtension
class CartridgeMaidExtension : ILittleMaid {
    override fun addMaidTask(manager: TaskManager) {
        manager.add(BecomeCartridgeTask)
    }
}