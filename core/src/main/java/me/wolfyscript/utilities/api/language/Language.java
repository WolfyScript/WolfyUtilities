package me.wolfyscript.utilities.api.language;

import com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.api.config.JsonConfig;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Language extends JsonConfig<JsonNode> {

    private final HashMap<String, JsonNode> cachedNodes = new HashMap<>();
    private final String lang;

    public Language(Plugin plugin, String lang) {
        super(new File(plugin.getDataFolder(), "lang/" + lang + ".json"), JsonNode.class);
        this.lang = lang;
    }

    public String getName() {
        return lang;
    }

    public String getVersion() {
        return value.path("version").asText();
    }

    public List<String> getAuthors() {
        List<String> list = new ArrayList<>();
        JsonNode node = value.path("author");
        if (node.isTextual()) {
            list.add(node.asText());
        } else {
            node.elements().forEachRemaining(n -> list.add(n.asText()));
        }
        return list;
    }

    public JsonNode getNodeAt(String path) {
        if (cachedNodes.containsKey(path)) {
            return cachedNodes.get(path);
        }
        String[] keys = path.split("\\.");
        JsonNode currentNode = value;
        for (String key : keys) {
            currentNode = currentNode.path(key);
        }
        cachedNodes.put(path, currentNode);
        return currentNode;
    }
}
