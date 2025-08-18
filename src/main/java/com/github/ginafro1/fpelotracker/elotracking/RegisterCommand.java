package com.github.ginafro1.fpelotracker.elotracking;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class RegisterCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "registerrbw";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) sender;

        String code = generateCode();
        ChatComponentText text = new ChatComponentText(EnumChatFormatting.BLUE + "Your Code for linking is: ");
        ChatComponentText codeText = new ChatComponentText(code);
        codeText.setChatStyle(new ChatStyle()
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,code))
                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ChatComponentText(EnumChatFormatting.GREEN + "Click to put code in chatbox"))));
        text.appendSibling(codeText);

        sendRegisterCode(player.getName(), code);
    }

    private String generateCode() {
        Random rand = new Random();
        int code = rand.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    private void sendRegisterCode(String username, String code) {
        try {
            URL url = new URL("http://ge001.laag.in:4030/register");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = "{\"username\":\"" + username + "\",\"code\":\"" + code + "\"}";
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
}
