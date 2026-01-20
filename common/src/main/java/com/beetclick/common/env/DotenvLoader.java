package com.beetclick.common.env;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public final class DotenvLoader {

    private DotenvLoader() {}

    public static void loadFromProjectRootIfPresent() {
        Path envFile = findUpwards(".env");
        if (envFile == null) return;

        Map<String, String> map = parse(envFile);

        map.forEach((k, v) -> {
            if (System.getenv(k) != null) return;
            if (System.getProperty(k) != null) return;
            System.setProperty(k, v);
        });
    }

    private static Path findUpwards(String filename) {
        Path cur = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        for (int i = 0; i < 10 && cur != null; i++) {
            Path candidate = cur.resolve(filename);
            if (Files.isRegularFile(candidate)) return candidate;
            cur = cur.getParent();
        }
        return null;
    }

    private static Map<String, String> parse(Path file) {
        Map<String, String> out = new HashMap<>();
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String s = line.trim();
                if (s.isEmpty() || s.startsWith("#")) continue;

                int idx = s.indexOf('=');
                if (idx <= 0) continue;

                String key = s.substring(0, idx).trim();
                String val = s.substring(idx + 1).trim();

                if ((val.startsWith("\"") && val.endsWith("\"")) || (val.startsWith("'") && val.endsWith("'"))) {
                    val = val.substring(1, val.length() - 1);
                }
                out.put(key, val);
            }
        } catch (IOException e) {
        }
        return out;
    }
}
