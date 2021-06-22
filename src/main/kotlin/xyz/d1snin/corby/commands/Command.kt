package xyz.d1snin.corby.commands

import net.dv8tion.jda.api.Permission
import xyz.d1snin.corby.model.Category

abstract class Command(
    usage: String,
    description: String,
    category: Category,
    cooldown: Int = 0,
    longDescription: String? = null,
    userPerms: List<Permission> = mutableListOf(),
    botPerms: List<Permission> = mutableListOf()
) : AbstractCommand(
    usage, description, category, cooldown, longDescription, userPerms, botPerms
)