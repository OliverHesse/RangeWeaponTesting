package me.Lucent


import me.Lucent.WeaponMechanics.Reloading.ReloadTask
import me.Lucent.WeaponMechanics.Shooting.ActiveExecutors
import org.bukkit.GameMode
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataType
import java.io.File

class PlayerController(val plugin:RangedWeaponsTest):Listener  {



    @EventHandler
    fun primaryFireUsedEventCheck(e:PlayerInteractEvent){
        if(e.hand!! == EquipmentSlot.OFF_HAND) return

        if(!(e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK)) return
        plugin.logger.info("player tried to use primary fire")
        val wrappedPlayer = PlayerWrapperHolder.getPlayerWrapper(plugin,e.player);
        if(!wrappedPlayer.isItemEquip()) return;
        val itemStack = wrappedPlayer.activeItemData.getItemStack()

        val weaponData = wrappedPlayer.activeItemData.getWeaponYamlData() ?: return;



        if(wrappedPlayer.activeItemData.isReloading()) return

        if(!wrappedPlayer.activeItemData.isCurrentlyFiring() && !wrappedPlayer.activeItemData.isCharging()){
            //logic here
            val executor = weaponData.getConfigurationSection("WeaponStats")?.getString("primaryFireExecutor") ?: return
            val args = weaponData.getConfigurationSection("WeaponStats")?.getList("executorArgs") ?: listOf<Any>()



            if (wrappedPlayer.activeItemData.getAmmoLeft() == 0){
                wrappedPlayer.activeItemData.reloadTask = ReloadTask(plugin,wrappedPlayer)
                wrappedPlayer.activeItemData.reloadTask!!.runTaskTimer(plugin,0,1);
                plugin.logger.info("RELOADING WEAPON")
                return
            }

            val status = ActiveExecutors.executorNameToFunction[executor]!!.call(plugin,wrappedPlayer, args.toTypedArray())
            plugin.logger.info(status.toString())
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
        val wrappedPlayer = PlayerWrapperHolder.getPlayerWrapper(plugin,e.player);
        if(!wrappedPlayer.isItemEquip()) return;
        val itemStack = wrappedPlayer.activeItemData.getItemStack()


        //weapon does not have an active slot
        if(!wrappedPlayer.activeItemData.hasActiveChipSlot()) return
        plugin.logger.info("has active chip")
        //TODO bug is here
        if(wrappedPlayer.activeItemData.isActiveChipOnCooldown()) return;
        plugin.logger.info("not on cooldown")
        val status = wrappedPlayer.activeItemData.useActiveChip();

        //used for cooldown checks
        if(status) wrappedPlayer.activeItemData.activeChipUsed();




        e.isCancelled = true
    }
    //stuff is delayed since slot isnt actualy changed till after this event is called
    @EventHandler
    fun onMainHandItemChanged(e:PlayerItemHeldEvent){
        plugin.logger.info("changed hands event called")
        PlayerWrapperHolder.getPlayerWrapper(plugin,e.player).activeItemData.reset();
        plugin.logger.info("new item = ${e.newSlot}")
        PlayerWrapperHolder.getPlayerWrapper(plugin,e.player).playerUI.updateBoard();
        plugin.server.scheduler.runTaskLater(plugin,Runnable {
            PlayerWrapperHolder.getPlayerWrapper(plugin,e.player).playerUI.updateBoard()
        },1L)
    }
    @EventHandler
    fun onProjectileLaunch(e:ProjectileLaunchEvent){
        plugin.logger.info("Projectile was launched")
    }
    @EventHandler
    fun onPlayerJoin(e:PlayerJoinEvent){
        e.player.gameMode = GameMode.CREATIVE
        e.player.inventory.clear()
        val itemsToGivePlayer = listOf(
            "BasicGrenadeLauncher",
            "BasicAssaultRifle",
            "InstantBeamGun",
            "HitScanRifle",
            "ChargeBeamRifle",)
        for (item in itemsToGivePlayer){
            e.player.inventory.addItem(plugin.weaponDataHandler.generateRangedWeapon(item)!!)
        }

        //makes a new wrapper
        PlayerWrapperHolder.getPlayerWrapper(plugin,e.player);


    }
    @EventHandler
    fun onPlayerLeave(e:PlayerQuitEvent){
        PlayerWrapperHolder.removeWrapper(e.player);
    }

}