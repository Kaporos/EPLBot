package com.github.hokkaydo.eplbot.module.code.command;

import com.github.hokkaydo.eplbot.MessageUtil;
import com.github.hokkaydo.eplbot.Strings;
import com.github.hokkaydo.eplbot.module.code.GlobalRunner;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.net.http.HttpClient;


public class PerformResponse {

    private boolean validateMessageLength(String content){
        return content.length() < 1960;
    }
    private boolean validateHastebinLength(String content){
        return content.length() < MessageUtil.HASTEBIN_MAX_CONTENT_LENGTH;
    }



    /**
     * @param textChannel the channel of the interaction
     * @param input a string with the data to be written in the file
     * @return a File
     */
    private String createUrlFromString(MessageChannel textChannel, String input){
        if (validateMessageLength(input)){
            textChannel.sendMessage(Strings.getString("COMMAND_CODE_EXCEEDED_HASTEBIN_SIZE")).queue();
        }
        HttpClient client = HttpClient.newHttpClient();
        return MessageUtil.hastebinPost(client, input).join();
    }

    /**
     * @param textChannel the channel of the interaction
     * @param code the code that has been submitted
     * @param lang the language submitted
     */
    public void sendSubmittedCode(MessageChannel textChannel, String code, String lang) {
        if (GlobalRunner.safeMentions(code)) {
            textChannel.sendMessage(STR."\{Strings.getString("COMMAND_CODE_UNSAFE_MENTIONS_SUBMITTED")}\n").queue();
            return;
        }
        if (validateMessageLength(code)) {
            textChannel.sendMessage(STR."```\{lang.toLowerCase()}\n\{code}\n```").queue();
            return;
        }
        if (validateHastebinLength(code)) {
            String url = createUrlFromString(textChannel, code);
            textChannel.sendMessage(STR."`The submitted code is available at : `\n<\{url}>").queue();
            return;
        }
        textChannel.sendMessage(STR."`The submitted code is too large \n:\{Strings.getString("COMMAND_CODE_EXCEEDED_HASTEBIN_SIZE")}").queue();
    }
    /**
     * @param textChannel the channel of the interaction
     * @param result the string with the output of the code
     * @param exitCode an int with the exit code of the submitted code
     */
    @SuppressWarnings("unused") //exitCode is not used but could be later
    public void sendResult(MessageChannel textChannel, String result, int exitCode){
        if (validateMessageLength(result)){
            textChannel.sendMessage(STR."`\{result}`").queue();
            return;
        }
        if (validateHastebinLength(result)) {
            String url = createUrlFromString(textChannel, result);
            textChannel.sendMessage(STR."`The result of the code is available at : `\n<\{url}>").queue();
            return;
        }
        textChannel.sendMessage(STR."`The result is too large : \n\{Strings.getString("COMMAND_CODE_EXCEEDED_HASTEBIN_SIZE")}").queue();
    }
}