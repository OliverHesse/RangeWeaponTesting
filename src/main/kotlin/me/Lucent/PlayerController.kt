package me.Lucent


import me.Lucent.Mechanics.Reloading.ReloadTask
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot

class PlayerController(val plugin:RangedWeaponsTest):Listener  {



    @EventHandler
    fun primaryFireUsedEventCheck(e:PlayerInteractEvent){
        if(e.hand!! == EquipmentSlot.OFF_HAND) return

        if(!(e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK)) return

        plugin.logger.info("player tried to use primary fire")
        val wrappedPlayer = plugin.playerWrapperHandler.getPlayerWrapper(e.player);
        if(!wrappedPlayer.isItemEquip()) return;
        if(plugin.weaponDataHandler.getUniqueId(wrappedPlayer.activeItemData.getItemStack()) == null) return



        if(wrappedPlayer.activeItemData.isReloading()) return

        if(!wrappedPlayer.activeItemData.isCurrentlyFiring() && !wrappedPlayer.activeItemData.isCharging()){
            //logic here


            plugin.logger.info("not currently firing")
            if (wrappedPlayer.activeItemData.getAmmoLeft() == 0){
                wrappedPlayer.activeItemData.reloadTask = ReloadTask(plugin,wrappedPlayer)
                wrappedPlayer.activeItemData.reloadTask!!.runTaskTimer(plugin,0,1);
                plugin.logger.info("RELOADING WEAPON")
                return
            }

            val status = wrappedPlayer.activeItemData.callPrimaryExecutor()

            //used for cooldown checks
            if(status) wrappedPlayer.activeItemData.weaponShot();
        }

        wrappedPlayer.rightClicked();
        e.isCancelled = true;
    }



    //might make 1 for abilities and 1 for primary fire
    @EventHandler
    fun abilityUsedEventCheck(e:PlayerInteractEvent){
        if(e.hand!! == EquipmentSlot.OFF_HAND) return
        if(e.action != Action.LEFT_CLICK_AIR && e.action != Action.LEFT_CLICK_BLOCK) return
        plugin.logger.info("interact event called")
        val wrappedPlayer = plugin.playerWrapperHandler.getPlayerWrapper(e.player);
        if(!wrappedPlayer.isItemEquip()) return;
        val itemStack = wrappedPlayer.activeItemData.getItemStack()


        //weapon does not have an active slot
        if(!wrappedPlayer.activeItemData.hasActiveChipSlot()) return


        if(wrappedPlayer.activeItemData.isActiveChipOnCooldown()) return;

        val status = wrappedPlayer.activeItemData.useActiveChip();

        //used for cooldown checks
        if(status) wrappedPlayer.activeItemData.activeChipUsed();




        e.isCancelled = true
    }
    //stuff is delayed since slot isnt actualy changed till after this event is called
    @EventHandler
    fun onMainHandItemChanged(e:PlayerItemHeldEvent){
        plugin.logger.info("changed hands event called")
        plugin.logger.info("${e.player.inventory.itemInMainHand}")
        plugin.playerWrapperHandler.getPlayerWrapper(e.player).activeItemData.reset();
        plugin.logger.info("new item = ${e.newSlot}")

        //1 tick seems glitchy so 2 for now
        plugin.server.scheduler.runTaskLater(plugin,Runnable {
            plugin.playerWrapperHandler.getPlayerWrapper(e.player).playerUI.updateBoard()
        },2L)
    }

    @EventHandler
    fun onProjectileLaunch(e:ProjectileLaunchEvent){
        plugin.logger.info("Projectile was launched")
    }
    @EventHandler
    fun onPlayerJoin(e:PlayerJoinEvent){

        plugin.playerWrapperHandler.getPlayerWrapper(e.player);

        e.player.gameMode = GameMode.CREATIVE
        e.player.inventory.clear()
        val itemsToGivePlayer = listOf(
            "BasicGrenadeLauncher",
            "BasicAssaultRifle",
            "InstantBeamGun",
            "HitScanRifle",
            "ChargeBeamRifle",)
        for (item in itemsToGivePlayer){

            val itemBase = plugin.weaponDataHandler.generateRangedWeapon(item) ?: continue

            plugin.weaponDataHandler.writeWeaponLore(plugin.playerWrapperHandler.getPlayerWrapper(e.player),itemBase)
            e.player.inventory.addItem(itemBase)
        }



    }
    @EventHandler
    fun onPlayerLeave(e:PlayerQuitEvent){
        plugin.playerWrapperHandler.removeWrapper(e.player);
    }

}