package io.github.lix3nn53.guardiansofadelia.utilities.config;

import io.github.lix3nn53.guardiansofadelia.GuardiansOfAdelia;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationUtils {

    public static int getChildComponentCount(ConfigurationSection configurationSection, String text) {
        int count = 0;
        while (true) {
            boolean contains = configurationSection.contains(text + (count + 1));
            if (contains) {
                count++;
            } else {
                break;
            }
        }

        return count;
    }

    public static YamlConfiguration createConfig(String filePath, String fileName) {
        File customConfigFile = new File(filePath, fileName);
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            GuardiansOfAdelia.getInstance().saveResource(filePath + File.separator + fileName, false);
        }

        YamlConfiguration fileConfiguration = new YamlConfiguration();
        try {
            fileConfiguration.load(customConfigFile);

            return fileConfiguration;
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<YamlConfiguration> getAllConfigsInFile(String filePath) {
        ArrayList<YamlConfiguration> result = new ArrayList<>();

        File customConfigFile = new File(filePath);
        File[] files = customConfigFile.listFiles();

        if (files == null) return result;

        for (File file : files) {
            if (file.isFile()) {
                YamlConfiguration fileConfiguration = new YamlConfiguration();
                try {
                    fileConfiguration.load(file);

                    result.add(fileConfiguration);
                } catch (IOException | InvalidConfigurationException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }
}
