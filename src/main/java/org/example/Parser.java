package org.example;

import java.util.ArrayList;

public class Parser {
    public static class ParseResult {
        String commandName;
        ArrayList<String> args = new ArrayList<>();
        ArrayList<Character> shorFlags = new ArrayList<>();
        ArrayList<String> longFlags = new ArrayList<>();

        ParseResult(String commandName) {
            this.commandName = commandName;
        }

        public String getCommandName() {
            return commandName;
        }

        public ArrayList<String> getArgs() {
            return args;
        }

        public ArrayList<Character> getShorFlags() {
            return shorFlags;
        }

        public ArrayList<String> getLongFlags() {
            return longFlags;
        }
    }

    public static ParseResult parse(String s) {
        var tokens = tokinize(s);
        if (tokens.isEmpty()) {
            //Что-то надо делать ?
        }
        var res = new ParseResult(tokens.get(0));
        for (int i = 1; i<tokens.size(); i++) {
            var token = tokens.get(i);
            if (token.startsWith("--")) {
                res.longFlags.add(tokens.get(i));
                continue;
            }
            if (token.startsWith("-")) {
                for (var l = 0; l < token.length(); l++) {
                    res.shorFlags.add(token.charAt(l));
                }
                continue;
            }
            res.args.add(token);
        }
        return res;
    }

    private static ArrayList<String> tokinize(String s) {
        ArrayList<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        if(s == null) return tokens;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == '"' && ((i == 0) && !inQuotes)) {
                inQuotes = !inQuotes;
                continue;
            }

            if (Character.isWhitespace(c) && !inQuotes) {
                if(!current.isEmpty()) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
                continue;
            }

            current.append(c);
        }
        tokens.add(current.toString());
        return tokens;
    }
}
