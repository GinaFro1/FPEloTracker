package com.github.ginafro1.fpelotracker.elotracking;

import com.github.ginafro1.fpelotracker.EloTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.stream.Collectors;

public class Testing {

        @SubscribeEvent
        public void onChat(ClientChatReceivedEvent e) {
            String msg = e.message.getUnformattedText();

            if (msg.toLowerCase().contains("test kill")) {
                sendServerMessage("GumeArtz was killed by Diyansh.");
            }
            if (msg.toLowerCase().contains("test fkill")) {
                sendServerMessage("Watermelon350 was filled full of lead by Diyansh. FINAL KILL!");
            }
            if (msg.toLowerCase().contains("test bedd")) {
                sendServerMessage("BED DESTRUCTION > Green Bed was iced by Diyansh!");
            }
            if (msg.toLowerCase().contains("test bedl")) {
                sendServerMessage("BED DESTRUCTION > Your Bed was destroyed by Watermelon350!");
            }
            if (msg.toLowerCase().contains("test lose")) {
                    sendServerMessage("You have been eliminated!");
            }
            if (msg.toLowerCase().contains("test win")) {
                Minecraft.getMinecraft().ingameGUI.displayTitle("VICTORY", "", 1, 20, 1);
            }
            if (msg.toLowerCase().contains("get elo")) {
                sendServerMessage("Elo: " + EloTracker.relElo);
            }
        }



    private void sendServerMessage(String text) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI()
                    .printChatMessage(new ChatComponentText(text));
        }

}
