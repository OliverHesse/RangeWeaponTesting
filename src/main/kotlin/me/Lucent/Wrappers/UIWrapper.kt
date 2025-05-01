package me.Lucent.Wrappers

import io.papermc.paper.scoreboard.numbers.NumberFormat
import me.Lucent.RangedWeaponsTest
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot

class UIWrapper(val plugin:RangedWeaponsTest,val playerWrapper: PlayerWrapper) {

    val scoreboard = plugin.server.scoreboardManager.newScoreboard
    var lastHealthScoreString = ""
    var lastAmmoScoreString = ""
    val statsObjective = scoreboard.registerNewObjective(
        "stats",
        Criteria.DUMMY,
        Component.text("[USERNAME]").color(TextColor.color(255, 31, 31)))

    init {

        playerWrapper.player.scoreboard = scoreboard

        statsObjective.displaySlot = DisplaySlot.SIDEBAR

        statsObjective.numberFormat(NumberFormat.blank())
        updateBoard()
    }

    //TODO find a way to make this nicer
    fun updateBoard(){
        //TODO
        scoreboard.resetScores(lastHealthScoreString)
        scoreboard.resetScores(lastAmmoScoreString)

        lastHealthScoreString = "♥ ${playerWrapper.health}/${playerWrapper.maxHealth}"
        statsObjective.getScore(lastHealthScoreString).score = 1


        if(playerWrapper.activeItemData.getItemStack().type == Material.AIR) return
        if(playerWrapper.activeItemData.getMaxAmmo() == 0) return
        lastAmmoScoreString = "◆ ${playerWrapper.activeItemData.getAmmoLeft()}/${playerWrapper.activeItemData.getMaxAmmo()}"
        statsObjective.getScore(lastAmmoScoreString).score = 0
    }


}