package com.github.ginafro1.fpelotracker.elotracking;

import com.github.ginafro1.fpelotracker.EloTracker;
import com.github.ginafro1.fpelotracker.EloValues;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;


public class ChatTracker {

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post e){
        if(e.type == RenderGameOverlayEvent.ElementType.ALL){
            FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
            fr.drawString("Kills: " + EloTracker.kills,5,5,-1);
            fr.drawString("FKills: " + EloTracker.fkills,5,15,-1);
            fr.drawString("FDeaths: " + EloTracker.fdeaths,5,25,-1);
            fr.drawString("Bed Breaks: " + EloTracker.bedbreaks,5,35,-1);
            fr.drawString("Wins: " + EloTracker.wins,85,5,-1);
            fr.drawString("Loses: " + EloTracker.loses,85,15,-1);
            fr.drawString("ELO: " + EloTracker.relElo,85,25,-1);
            fr.drawString("Team: " + EloTracker.team,85,35,-1);
        }
    }


    private void sendEloToDB(String playerName, int relElo) {
        try {
            URL url = new URL("http://ge001.laag.in:4030/elo");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = "{\"username\":\"" + playerName + "\",\"relElo\":\"" + relElo + "\"}";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            StringBuilder response = new StringBuilder();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            String respStr = response.toString();
            System.out.println("Response: " + respStr);

            if (responseCode == 200 && respStr.contains("\"status\":\"success\"")) {
                System.out.println("✅ Code sent successfully");
            } else if (respStr.contains("\"error\"")) {
                System.out.println("❌ Failed: " + respStr);
            } else {
                System.out.println("⚠️ Unexpected response: " + respStr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload e){
        EloTracker.team = "";
        if(EloTracker.inRanked){
//            sendEloToDB(EloTracker.playerName,EloTracker.relElo);
            EloTracker.relElo = 0;
        }
        EloTracker.inRanked = false;
        System.out.println("Not in ranked anymore");
    }
    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent e) {
        String s = StringUtils.stripControlCodes(e.message.getUnformattedText());
        String message = s.toLowerCase();
        if(e.message.getUnformattedText().contains("Cross-teaming is not allowed! Report cross-teamers using /report!")){
            EloTracker.inRanked = true;
            System.out.println("In Ranked && checking for team");
            getTeamFromArmor();
        }
        if (!EloTracker.inRanked) return;

        List<String> killMsges = Arrays.asList(Messages.killMessages);
        List<String> killMessagesLower = Arrays.stream(Messages.killMessages)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        if (containsAny(message, killMessagesLower)) {
            System.out.println("Kill Message");
            if (message.replace(".", "").endsWith(EloTracker.playerName.toLowerCase())) {
                EloTracker.relElo += EloValues.KILL.eloValue;
                System.out.println("Added Elo For KILL");
                EloTracker.kills++;
            }if (message.replace(".", "").endsWith("final kill!")) {
                if (message.toLowerCase().contains(EloTracker.playerName.toLowerCase())) {
                    if (message.startsWith(EloTracker.playerName.toLowerCase())) {
                        EloTracker.relElo += EloValues.FDEATH.eloValue;
                        System.out.println("Added ELO for FDEATH");
                    } else {
                        EloTracker.relElo += EloValues.FKILL.eloValue;
                        System.out.println("Added Elo For FKILL");
                    }
                }
            }

        } else if (message.startsWith("bed destruction")) {
            System.out.println("Bed Message");
            if (message.endsWith(EloTracker.playerName.toLowerCase() + "!")) {
                EloTracker.relElo += EloValues.BEDBREAK.eloValue;
                System.out.println("Added Elo For BEDW");
                EloTracker.bedbreaks++;
            }
            if(message.contains(EloTracker.team.toLowerCase())){
                EloTracker.relElo += EloValues.BEDLOSE.eloValue;
                System.out.println("Added ELO for BEDL");
                EloTracker.bedloses++;
            }
        }
        else if(message.startsWith("team eliminated")){
            if(message.contains(EloTracker.team)){
                EloTracker.relElo += EloValues.LOSE.eloValue;
                System.out.println("Added Elo for Lose");
            }
        }
    }

    private boolean winAwarded = false;

    @SubscribeEvent
    public void onRender(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().ingameGUI != null) {
            Minecraft.getMinecraft().fontRendererObj.drawString("ELO: " + EloTracker.relElo, 15,15,-1);
            String title = ReflectionHelper.getPrivateValue(
                    GuiIngame.class,
                    Minecraft.getMinecraft().ingameGUI,
                    "displayedTitle"
            );

            if (title != null){
                if(title.equalsIgnoreCase("VICTORY")) {
                    if (!winAwarded) {
                        EloTracker.relElo += EloValues.WIN.eloValue;
                        winAwarded = true;
                        System.out.println("Added Elo For WIN");
                    }
                }
            }else {
                winAwarded = false;
            }
        }
    }

    private void getTeamFromArmor() {
        EntityPlayerSP sp = Minecraft.getMinecraft().thePlayer;
        ItemStack chest = sp.getCurrentArmor(2);
        if(chest != null && chest.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor) chest.getItem();
            if(armor.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER){
                int color = ((ItemArmor) chest.getItem()).getColor(chest);
                System.out.println("Armor Color: " + String.format("%06X", color));
                String c = String.format("%06X", color);
                switch (c) {
                    case "FF0000": EloTracker.team = "Red"; break;
                    case "FFFF00": EloTracker.team = "Yellow"; break;
                    case "008000": EloTracker.team = "Green"; break;
                    case "0000FF": EloTracker.team = "Blue"; break;
                    case "00FFFF": EloTracker.team = "Aqua"; break;
                    case "800080": EloTracker.team = "Pink"; break;
                    case "FFFFFF": EloTracker.team = "White"; break;
                    case "808080": EloTracker.team = "Gray"; break;
                    default: EloTracker.team = "Unknown"; break;
                }
                System.out.println("Team: " + EloTracker.team);
            }
        }
    }

    private boolean containsAny(String text, List<String> patterns) {
        for (String pattern : patterns) {
            if (text.contains(pattern)) {
                System.out.println("Matches With: " + pattern);
                return true;
            }
        }
        return false;
    }
}
