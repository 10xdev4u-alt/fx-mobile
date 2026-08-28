const std = @import("std");

pub fn build(b: *std.Build) void {
    const target = b.standardTargetOptions(.{});
    const optimize = b.standardOptimizeOption(.{});

    const lib = b.addSharedLibrary(.{
        .name = "fx",
        .root_source_file = b.path("src/fx_core.zig"),
        .target = target,
        .optimize = optimize,
    });

    lib.linkLibC();

    // Export C header for JNI interop
    const step = b.step("header", "Generate C header");
    const header = b.addInstallFile(
        b.path("include/fx_core.h"),
        "include/fx_core.h",
    );
    step.dependOn(&header.step);

    b.installArtifact(lib);
}
