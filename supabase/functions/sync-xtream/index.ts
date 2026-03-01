import { serve } from "https://deno.land/std@0.224.0/http/server.ts";

serve(async (req) => {
  if (req.method !== "POST") return new Response("Method not allowed", { status: 405 });
  const payload = await req.json();

  return Response.json({
    status: "queued",
    server: payload?.server ?? null,
    note: "Xtream sync stub for beta scaffold",
  });
});