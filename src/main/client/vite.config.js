import { defineConfig } from "vite"
import react from "@vitejs/plugin-react-swc"
import { TanStackRouterVite } from "@tanstack/router-vite-plugin"


// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    react(),
    TanStackRouterVite({
      disableTypes: true,
      quoteStyle: "double",
      routeFileIgnorePrefix: "!",
      generatedRouteTree: "./src/routes/!routeTree.gen.js"
    })],
  envDir: "./.env",
  server: {
    port: 5173
  },
  optimizeDeps: {
    include: ["react", "react-dom"],
  },
  build: {
    outDir: "../resources/static/",
    emptyOutDir: true
  },
  resolve: {
    alias: {
      "@": "/src",
      "@assets": "/src/assets",
      "@styles": "/src/styles",
      "@components": "/src/components",
      "@pages": "/src/components/pages"
    },
  },
  css: {
    preprocessorOptions: {
      scss: {
        // Additional Sass/SCSS options
      },
    }
  },
});
