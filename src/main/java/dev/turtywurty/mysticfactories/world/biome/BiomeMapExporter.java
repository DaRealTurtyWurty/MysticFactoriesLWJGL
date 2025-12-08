package dev.turtywurty.mysticfactories.world.biome;

import dev.turtywurty.mysticfactories.util.Identifier;
import dev.turtywurty.mysticfactories.world.Chunk;
import dev.turtywurty.mysticfactories.world.ChunkPos;
import dev.turtywurty.mysticfactories.world.World;
import dev.turtywurty.mysticfactories.world.tile.TilePos;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.List;

public final class BiomeMapExporter {
    private BiomeMapExporter() {
    }

    public static void export(World world, Path outputPath) {
        Map<ChunkPos, Chunk> chunks = world.getChunks();
        if (chunks.isEmpty()) {
            System.out.println("Biome map export skipped: no chunks have been generated.");
            return;
        }

        int minChunkX = Integer.MAX_VALUE;
        int minChunkZ = Integer.MAX_VALUE;
        int maxChunkX = Integer.MIN_VALUE;
        int maxChunkZ = Integer.MIN_VALUE;
        for (ChunkPos pos : chunks.keySet()) {
            minChunkX = Math.min(minChunkX, pos.x);
            minChunkZ = Math.min(minChunkZ, pos.y);
            maxChunkX = Math.max(maxChunkX, pos.x);
            maxChunkZ = Math.max(maxChunkZ, pos.y);
        }

        int chunkWidth = maxChunkX - minChunkX + 1;
        int chunkHeight = maxChunkZ - minChunkZ + 1;
        int width = chunkWidth * Chunk.SIZE;
        int height = chunkHeight * Chunk.SIZE;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Map<Biome, Integer> palette = new HashMap<>();

        for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
            for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
                int pixelChunkX = (chunkX - minChunkX) * Chunk.SIZE;
                int pixelChunkZ = (chunkZ - minChunkZ) * Chunk.SIZE;

                for (int localZ = 0; localZ < Chunk.SIZE; localZ++) {
                    int pixelZ = pixelChunkZ + localZ;
                    for (int localX = 0; localX < Chunk.SIZE; localX++) {
                        int pixelX = pixelChunkX + localX;
                        int worldX = chunkX * Chunk.SIZE + localX;
                        int worldZ = chunkZ * Chunk.SIZE + localZ;

                        Optional<Biome> biome = world.getBiome(new TilePos(worldX, worldZ));
                        int color = palette.computeIfAbsent(biome.orElse(null), BiomeMapExporter::colorForBiome);
                        image.setRGB(pixelX, pixelZ, color);
                    }
                }
            }
        }

        try {
            if (outputPath.getParent() != null) {
                Files.createDirectories(outputPath.getParent());
            }

            ImageIO.write(image, "png", outputPath.toFile());
            writeLegend(outputPath, palette);
            System.out.println("Exported biome map to: " + outputPath.toAbsolutePath());
        } catch (IOException ioException) {
            System.err.println("Failed to export biome map: " + ioException.getMessage());
        }
    }

    private static int colorForBiome(Biome biome) {
        if (biome == null)
            return 0xFF000000;

        Identifier id = biome.getId();
        int hash = id != null ? id.toString().hashCode() : biome.hashCode();
        float hue = (hash & 0x00FFFFFF) / (float) 0x1000000;
        float saturation = 0.65f;
        float brightness = 0.95f;
        return Color.HSBtoRGB(hue, saturation, brightness);
    }

    private static void writeLegend(Path imagePath, Map<Biome, Integer> palette) throws IOException {
        if (palette.isEmpty())
            return;

        String baseName = imagePath.getFileName().toString();
        int dotIndex = baseName.lastIndexOf('.');
        if (dotIndex != -1) {
            baseName = baseName.substring(0, dotIndex);
        }

        Path legendPath = imagePath.resolveSibling(baseName + ".txt");
        List<Map.Entry<Biome, Integer>> entries = new ArrayList<>(palette.entrySet());
        entries.sort(Comparator.comparing(entry -> biomeName(entry.getKey())));

        var builder = new StringBuilder("Biome legend for ")
                .append(imagePath.getFileName())
                .append(System.lineSeparator());

        for (Map.Entry<Biome, Integer> entry : entries) {
            String name = biomeName(entry.getKey());
            String color = String.format("#%06X", entry.getValue() & 0xFFFFFF);
            builder.append(name).append(" -> ").append(color).append(System.lineSeparator());
        }

        Files.writeString(
                legendPath,
                builder.toString(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
    }

    private static String biomeName(Biome biome) {
        if (biome == null)
            return "unknown";

        Identifier id = biome.getId();
        if (id != null)
            return id.toString();

        return "unregistered_biome";
    }
}
