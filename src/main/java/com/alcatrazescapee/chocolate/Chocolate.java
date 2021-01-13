/*
 * Part of the Chocolate mod by AlcatrazEscapee.
 * Licensed under the MIT License. See LICENSE.md for details.
 */

package com.alcatrazescapee.chocolate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

import com.alcatrazescapee.chocolate.common.ChocolateConfig;
import com.alcatrazescapee.chocolate.common.Debug;

@Mod(Chocolate.MOD_ID)
public final class Chocolate
{
    public static final String MOD_ID = "chocolate";

    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public Chocolate()
    {
        LOGGER.info("Vanilla is real good, but chocolate is better, let's be honest. :)");

        ChocolateConfig.init();
        Debug.init();
    }
}