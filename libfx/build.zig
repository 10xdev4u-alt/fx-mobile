const std = @import("std");

pub fn build(b: *std.Build) void {
    const target = b.standardTargetOptions(.{});
    const optimize = b.standardOptimizeOption(.{});

    // Shared library for Android JNI
    const lib = b.addSharedLibrary(.{
        .name = "fx",
        .root_source_file = b.path("src/fx_core.zig"),
        .target = target,
        .optimize = optimize,
    });

    lib.linkLibC();

    // Export C header for JNI interop
    const header_step = b.step("header", "Generate C header");
    const header = b.addInstallFile(
        b.path("include/fx_core.h"),
        "include/fx_core.h",
    );
    header_step.dependOn(&header.step);

    b.installArtifact(lib);

    // Test executable for host platform testing
    const tests = b.addTest(.{
        .root_source_file = b.path("src/fx_core.zig"),
        .target = target,
        .optimize = optimize,
    });

    const run_tests = b.addRunArtifact(tests);
    const test_step = b.step("test", "Run unit tests");
    test_step.dependOn(&run_tests.step);
}
