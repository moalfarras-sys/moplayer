import { serve } from "https://deno.land/std@0.224.0/http/server.ts";

serve(async (req) => {
  if (req.method !== "POST") return new Response("Method not allowed", { status: 405 });
  const payload = await req.json();
  const primary = Array.isArray(payload?.primary) ? payload.primary : [];
  const fallback = Array.isArray(payload?.fallback) ? payload.fallback : [];

  const merged = [...primary];
  for (const row of fallback) {
    if (!merged.find((x: { channel_id?: string; start?: string }) => x.channel_id === row.channel_id && x.start === row.start)) {
      merged.push(row);
    }
  }

  return Response.json({ merged, count: merged.length });
});