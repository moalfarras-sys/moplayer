import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "MoPlayer Admin",
  description: "Admin panel for MoPlayer Supabase-driven TV OS",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}