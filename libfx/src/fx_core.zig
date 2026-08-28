const std = @import("std");

pub const FxError = error{
    InvalidArgument,
    NotFound,
    PermissionDenied,
    ConnectionFailed,
    Timeout,
    OutOfMemory,
};

pub const FxResult = struct {
    code: i32,
    message: []const u8,
};

pub fn init() FxError!void {
    std.debug.print("fx core initialized\n", .{});
}

pub fn deinit() void {
    std.debug.print("fx core deinitialized\n", .{});
}

pub fn version() []const u8 {
    return "0.1.0";
}

comptime {
    std.testing.refAllDecls(@This());
}
