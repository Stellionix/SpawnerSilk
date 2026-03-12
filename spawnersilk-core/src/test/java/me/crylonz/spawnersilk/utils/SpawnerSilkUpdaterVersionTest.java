package me.crylonz.spawnersilk.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SpawnerSilkUpdaterVersionTest {

    @Test
    void compareVersionPartsTreatsFiveNineZeroAsNewerThanFourTwentyFourZero() {
        int comparison = SpawnerSilkUpdater.compareVersionParts(
                SpawnerSilkUpdater.parseVersionParts("5.9.0"),
                SpawnerSilkUpdater.parseVersionParts("4.24.0")
        );

        assertEquals(1, comparison);
    }

    @Test
    void compareVersionPartsTreatsMultiDigitSegmentsNumerically() {
        int comparison = SpawnerSilkUpdater.compareVersionParts(
                SpawnerSilkUpdater.parseVersionParts("4.10.0"),
                SpawnerSilkUpdater.parseVersionParts("4.9.9")
        );

        assertEquals(1, comparison);
    }

    @Test
    void compareVersionPartsTreatsMissingSegmentsAsZero() {
        int comparison = SpawnerSilkUpdater.compareVersionParts(
                SpawnerSilkUpdater.parseVersionParts("5"),
                SpawnerSilkUpdater.parseVersionParts("5.0.0")
        );

        assertEquals(0, comparison);
    }

    @Test
    void parseVersionPartsSupportsSuffixesAfterNumericSegments() {
        List<Integer> parts = SpawnerSilkUpdater.parseVersionParts("5.0.0-RC1");

        assertEquals(Arrays.asList(5, 0, 0), parts);
    }

    @Test
    void parseVersionPartsRejectsVersionsWithoutNumericSegments() {
        assertThrows(IllegalArgumentException.class, () -> SpawnerSilkUpdater.parseVersionParts("SNAPSHOT"));
    }
}
