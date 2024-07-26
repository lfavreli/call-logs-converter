import { createLazyFileRoute } from "@tanstack/react-router"
import Home from "@components/pages/Home";

export const Route = createLazyFileRoute("/")({
  component: Home,
});
