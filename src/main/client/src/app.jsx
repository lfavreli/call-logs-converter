import "@styles/main.scss"
import React from "react"
import ReactDOM from "react-dom/client"
import CssBaseline from "@mui/material/CssBaseline"
import { RouterProvider, createRouter } from "@tanstack/react-router"
import { routeTree } from "./routes/!routeTree.gen"

const router = createRouter({ routeTree })

const rootElement = document.getElementById("root");
if (!rootElement.innerHTML) {
  ReactDOM.createRoot(rootElement).render(
    <React.StrictMode>
      <CssBaseline />
      <RouterProvider router={router} />
    </React.StrictMode>
  );
}
