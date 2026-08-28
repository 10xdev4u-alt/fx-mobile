const std = @import("std");

pub fn build(b: *std.Build) void {
    const target = b.standardTargetOptions(.{});
    const optimize = b.standardOptimizeOption(.{});

    // Build as shared library for Android JNI
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

    // Build executable for testing
    const exe = b.addExecutable(.{
        .name = "fx_core",
        .root_source_file = b.path("src/fx_core.zig"),
        .target = target,
        .optimize = optimize,
    });

    const run_cmd = b.addRunArtifact(exe);
    run_cmd.step.dependOn(b.getInstallStep());

    const run_step = b.step("run", "Run the app");
    run_step.dependOn(&run_cmd.step);
}
