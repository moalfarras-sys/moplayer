import { NextResponse } from "next/server";
import { getSupabaseAdmin } from "@/lib/supabase";

export async function POST(req: Request) {
  const { id, accent_hex, blur_strength, glass_opacity, dark_mode } = (await req.json()) as {
    id?: string;
    accent_hex?: string;
    blur_strength?: number;
    glass_opacity?: number;
    dark_mode?: boolean;
  };

  if (!id) return NextResponse.json({ error: "Theme id is required" }, { status: 400 });

  const supabase = getSupabaseAdmin();
  const { error } = await supabase
    .from("themes")
    .update({ accent_hex, blur_strength, glass_opacity, dark_mode, updated_at: new Date().toISOString() })
    .eq("id", id);

  if (error) return NextResponse.json({ error: error.message }, { status: 500 });
  return NextResponse.json({ ok: true });
}