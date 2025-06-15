package org.zqty.blockClicker;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.entity.Player;

public class ActionBarUtil {

    public static void sendActionBar(Player player, String message) {
        // compose un composant texte "brut"
        IChatBaseComponent comp = IChatBaseComponent.ChatSerializer.a(
                "{\"text\":\"" + message + "\"}"
        );
        // packet type 2 = ACTION_BAR
        PacketPlayOutChat packet = new PacketPlayOutChat(comp, (byte) 2);
        // envoi
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
