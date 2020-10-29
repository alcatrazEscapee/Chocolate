/*
 * Part of the Suck Eggs Mod by AlcatrazEscapee
 * Work under Copyright. See the project LICENSE.md for details.
 */

package com.alcatrazescapee.chocolate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

import com.alcatrazescapee.chocolate.common.ChocolateConfig;
import com.alcatrazescapee.chocolate.common.ChocolateTests;

@Mod(Chocolate.MOD_ID)
public final class Chocolate
{
    public static final String MOD_ID = "chocolate";

    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private static final boolean ENABLE_DEBUG_FEATURES = !FMLEnvironment.production;

    public Chocolate()
    {
        LOGGER.info("Vanilla is real good, but chocolate is better, let's be honest. :)");
        if (ENABLE_DEBUG_FEATURES)
        {
            LOGGER.error("Enabling Chocolate's Debug Features! You should not see this!");
            ChocolateTests.init();
        }

        ChocolateConfig.init();
    }
}