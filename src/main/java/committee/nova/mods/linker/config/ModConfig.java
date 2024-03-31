package committee.nova.mods.linker.config;

import com.google.gson.annotations.Expose;

/**
 * ModConfig
 *
 * @author cnlimiter
 * @version 1.0
 * @description
 * @date 2024/3/30 22:36
 */
public class ModConfig {
    public String getConfigName(){
        return "config";
    }

    @Expose
    public double pathfindingDistance = 24d;
    @Expose
    public double distance = 24d;
}
