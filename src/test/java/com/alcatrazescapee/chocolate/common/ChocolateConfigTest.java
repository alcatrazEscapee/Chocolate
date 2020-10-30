package com.alcatrazescapee.chocolate.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class ChocolateConfigTest
{
    @Test
    public void testDefaultConfigOptions()
    {
        // Assert the default config options are enabled. This is required for other tests to function
        assertEquals(ChocolateConfig.Severity.LOG, ChocolateConfig.SERVER.onBiomesRemovedFromChunks.get());
        assertEquals(ChocolateConfig.Severity.LOG, ChocolateConfig.SERVER.onIdsMissingFromPalette.get());
    }
}
