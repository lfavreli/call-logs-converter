import { createLazyFileRoute } from "@tanstack/react-router"
import Contact from "@components/pages/Contact";

export const Route = createLazyFileRoute("/contact")({
  component: Contact,
});
