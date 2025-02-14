package core.plugin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PluginManifest {

    PluginType type() default PluginType.ACTION;
    String name() default "";
}