package me.platypus.bumblegum;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import me.platypus.bumblegum.common.CommonEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(Bumblegum.MODID)
public class Bumblegum
{
    public static final String MODID = "bumblegum";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Bumblegum()
    {
        //Register our common event handler
        MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
    }
}
