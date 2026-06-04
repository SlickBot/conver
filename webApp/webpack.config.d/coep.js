// Cross-origin isolation required by SQLite-WASM OPFS (SharedArrayBuffer).
if (config.devServer) {
  config.devServer.headers = [
    { key: "Cross-Origin-Opener-Policy", value: "same-origin" },
    { key: "Cross-Origin-Embedder-Policy", value: "require-corp" },
  ];
}
