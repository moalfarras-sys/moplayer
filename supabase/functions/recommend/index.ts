import { serve } from "https://deno.land/std@0.224.0/http/server.ts";

serve(async (req) => {
  if (req.method !== "POST") return new Response("Method not allowed", { status: 405 });
  const payload = await req.json();
  const watched = Array.isArray(payload?.watched) ? payload.watched : [];
  const catalog = Array.isArray(payload?.catalog) ? payload.catalog : [];

  const watchedSet = new Set(watched.map((x: { group?: string }) => x.group).filter(Boolean));
  const recommendations = catalog
    .filter((item: { group?: string }) => item.group && watchedSet.has(item.group))
    .slice(0, 20);

  return Response.json({ recommendations, strategy: "rule_based_group_affinity_v1" });
});