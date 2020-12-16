package me.wolfyscript.utilities.api.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This config is plain for utility and might change significantly in future updates.
 * This and it's counterpart {@link KeyedJsonConfig} will be changed in the upcoming updates and are planned to be somewhat stable/done in version 1.6.3.x.
 * <p>
 * Everything in this class can also be coded manually using Jackson, but the goal is to summarize it and prevent boilerplate.
 *
 * @param <T> any type you want to save in or load from the config file. See the Jackson documentation for more information about how to use custom serializer.
 */
public class JsonConfig<T> {

    protected File file;
    private final Function<File, T> rootFunction;
    private final Supplier<T> rootSupplier;
    protected T value;

    /**
     * This allows you to configure your own function to load the Object from the file.
     * The function is only called when the file exists.
     * <br/>
     * If you don't want to use a file use {@link #JsonConfig(File, Supplier)} instead.
     *
     * @param file         The file to load the config from. Must not be null!
     * @param rootFunction The function to load the JsonNode from the file. Only called when the file exists!
     */
    public JsonConfig(@NotNull File file, Function<File, T> rootFunction) {
        this.file = file;
        this.rootFunction = rootFunction;
        this.rootSupplier = null;
        try {
            load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This allows you to configure your own supplier. Other than {@link #JsonConfig(File, Function)} the supplier is also called when the file is null.
     * If you want to set the file on save or just want it to use for memory only, then you can use this constructor.
     *
     *
     * @param file         The file to load the config from. Can be null!
     * @param rootSupplier The supplier that supplies the config with the value.
     */
    public JsonConfig(@Nullable File file, @NotNull Supplier<T> rootSupplier) {
        this.file = file;
        this.rootFunction = null;
        this.rootSupplier = rootSupplier;
        this.value = rootSupplier.get();
        Objects.requireNonNull(value, "Can't load config! Value cannot be null!");
    }

    /**
     * Loads the specified type from the file, but only if the config exists!
     *
     * @param file  The file of the config.
     * @param type  The type of the object that should be loaded.
     */
    public JsonConfig(@NotNull File file, Class<T> type) {
        this(file, file1 -> {
            try {
                return JacksonUtil.getObjectMapper().readValue(file1, type);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    /**
     *
     *
     * @param file
     * @param initialValue
     * @throws IOException
     */
    public JsonConfig(@Nullable File file, Class<T> type, @NotNull String initialValue) {
        this(file, () -> {
            try {
                return JacksonUtil.getObjectMapper().readValue(initialValue, type);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    /**
     * Loads a Object of a specific type from a String value.
     * Make sure that the value can be serialized (See Jackson for more documentation).
     *
     * @param initialValue
     * @throws IOException
     */
    public JsonConfig(Class<T> type, @NotNull String initialValue) {
        this(null, type, initialValue);
    }

    /**
     * This creates a memory only config with the passed value.
     * The file is null, but it can be saved to a File later using {@link #save(File)} or similar methods.
     *
     * @param value The value that should be stored.
     * @throws IOException
     */
    public JsonConfig(@NotNull T value) {
        this(null, () -> value);
    }

    public void save(File file, ObjectWriter objectWriter) throws IOException {
        if (file != null) {
            if (file.exists() || file.createNewFile()) {
                objectWriter.writeValue(file, this.value);
            } else {
                throw new IOException("Couldn't create config file on save!");
            }
        }
    }

    public void save(ObjectWriter objectWriter) throws IOException {
        save(this.file, objectWriter);
    }

    public void save(File file, boolean prettyPrint) throws IOException {
        save(file, JacksonUtil.getObjectWriter(prettyPrint));
    }

    public void save(boolean prettyPrint) throws IOException {
        save(JacksonUtil.getObjectWriter(prettyPrint));
    }

    public void save(File file) throws IOException {
        save(file, false);
    }

    public void save() throws IOException {
        save(false);
    }

    public void reload() throws IOException {
        save();
        load();
    }

    public boolean load() throws IOException {
        if (!load(this.rootFunction)) {
            return load(this.rootSupplier);
        }
        return true;
    }

    public boolean load(Supplier<T> rootSupplier) {
        if (rootSupplier != null) {
            this.value = rootSupplier.get();
            return true;
        }
        return false;
    }

    public boolean load(Function<File, T> rootFunction) throws IOException {
        if (rootFunction != null) {
            Objects.requireNonNull(this.file, "Can't load config! File cannot be null!");
            file.getParentFile().mkdirs();
            if (this.file.exists() || this.file.createNewFile()) {
                this.value = rootFunction.apply(this.file);
                return true;
            }
        }
        return false;
    }


}
