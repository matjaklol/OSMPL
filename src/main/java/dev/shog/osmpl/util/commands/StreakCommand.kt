package dev.shog.osmpl.util.commands

import dev.shog.osmpl.api.SqlHandler
import dev.shog.osmpl.api.cmd.Command
import dev.shog.osmpl.api.msg.sendMessage
import dev.shog.osmpl.api.msg.sendMessageHandler
import dev.shog.osmpl.fancyDate
import java.util.concurrent.TimeUnit

/**
 * The streak  command.
 */
internal val STREAK_COMMAND = Command.make("streak") {
    //Query db for streak info based on the player username
    val rs = SqlHandler.getConnection(db = "money")
            .prepareStatement("SELECT lastLogin, streak FROM dailyreward WHERE player = ?")
            .apply {
                setString(1, sender.name.toLowerCase())
            }
            .executeQuery()
            
    //if streak info is found send info to player
    if (rs.next()) {
        
        //"Currently at a login streak of XX Days, next reward is $XX*5. In XXtimer"
        //Since reward is capped at $50 just replace that info to max out at $50.
        sendMessageHandler(
                "streak.command.default",
                rs.getInt("streak"),
                if (rs.getInt("streak") * 5 > 50) 50 else rs.getInt("streak") * 5,
                ((TimeUnit.DAYS.toMillis(1) + rs.getLong("lastLogin")) - System.currentTimeMillis()).fancyDate() // not readable lol
        )
    } else {
        sendMessage("yo lol")
    }

    true
}
