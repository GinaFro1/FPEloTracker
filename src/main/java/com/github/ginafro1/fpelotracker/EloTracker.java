package com.github.ginafro1.fpelotracker;

import com.github.ginafro1.fpelotracker.elotracking.ChatTracker;
import com.github.ginafro1.fpelotracker.elotracking.RegisterCommand;
import com.github.ginafro1.fpelotracker.elotracking.SetUsernameCommand;
import com.github.ginafro1.fpelotracker.elotracking.Testing;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "fpelotracker", useMetadata=true)
public class EloTracker {

    public static boolean inRanked = false;
    public static String playerName = "Diyansh";
    public static int relElo = 0;
    public static String team = "";
    public static int kills = 0,fkills = 0,fdeaths = 0,bedbreaks = 0,bedloses = 0,wins = 0,loses = 0;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println("Registering Events");
        MinecraftForge.EVENT_BUS.register(new ChatTracker());
        System.out.println("Registered ChatTracker");
        MinecraftForge.EVENT_BUS.register(new Testing());
        System.out.println("Registered Testing");
        ClientCommandHandler.instance.registerCommand(new RegisterCommand());
        ClientCommandHandler.instance.registerCommand(new SetUsernameCommand());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e){
        playerName = Minecraft.getMinecraft().thePlayer != null ? Minecraft.getMinecraft().thePlayer.getGameProfile().getName() : "Name";
    }
}
