import { serve } from "https://deno.land/std@0.224.0/http/server.ts";

serve(async (req) => {
  if (req.method !== "POST") return new Response("Method not allowed", { status: 405 });
  const body = await req.text();
  const lines = body.split("\n").filter((l) => l.trim().length > 0);

  const channels = lines
    .filter((line) => !line.startsWith("#"))
    .map((url, index) => ({ id: `live-${index}`, title: `Channel ${index + 1}`, stream_url: url, type: "LIVE" }));

  return Response.json({ channels, count: channels.length });
});